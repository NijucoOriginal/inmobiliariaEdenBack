package com.jsebastian.eden.EdenSys.services;

import com.jsebastian.eden.EdenSys.Dtos.chat.*;
import com.jsebastian.eden.EdenSys.domain.Conversacion;
import com.jsebastian.eden.EdenSys.domain.Mensaje;
import com.jsebastian.eden.EdenSys.domain.User;
import com.jsebastian.eden.EdenSys.repository.ConversacionRepository;
import com.jsebastian.eden.EdenSys.repository.MensajeRepository;
import com.jsebastian.eden.EdenSys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;        // ← NUEVO
import java.time.LocalDateTime;
import java.time.ZoneOffset;     // ← NUEVO
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMetricasService chatMetricasService; // ← NUEVO

    @Transactional
    public ConversacionDetalleDto obtenerOCrearConversacion(String emailActual, Long otroUsuarioId) {
        User yo = obtenerUsuarioPorEmail(emailActual);
        User otro = obtenerUsuarioPorId(otroUsuarioId);

        if (yo.getId().equals(otro.getId())) {
            throw new IllegalArgumentException("No puedes iniciar una conversación contigo mismo.");
        }

        Conversacion conversacion = conversacionRepository
                .findEntreUsuarios(yo, otro)
                .orElseGet(() -> crearConversacion(yo, otro));

        mensajeRepository.marcarComoLeidos(conversacion.getId(), yo.getId());

        List<MensajeDto> mensajes = mensajeRepository
                .findByConversacionIdOrderByEnviadoEnAsc(conversacion.getId())
                .stream()
                .map(this::toMensajeDto)
                .toList();

        User otroParticipante = conversacion.getUsuario1().getId().equals(yo.getId())
                ? conversacion.getUsuario2()
                : conversacion.getUsuario1();

        return new ConversacionDetalleDto(
                conversacion.getId(),
                otroParticipante.getId(),
                otroParticipante.getNombre(),
                otroParticipante.getApellido(),
                mensajes
        );
    }

    @Transactional(readOnly = true)
    public List<ConversacionDto> listarConversaciones(String emailActual) {
        User yo = obtenerUsuarioPorEmail(emailActual);
        List<Conversacion> conversaciones = conversacionRepository.findByUsuario(yo);

        return conversaciones.stream().map(c -> {
            User otro = c.getUsuario1().getId().equals(yo.getId())
                    ? c.getUsuario2()
                    : c.getUsuario1();

            List<Mensaje> msgs = mensajeRepository
                    .findByConversacionIdOrderByEnviadoEnAsc(c.getId());

            String ultimoContenido = msgs.isEmpty()
                    ? ""
                    : msgs.get(msgs.size() - 1).getContenido();

            long noLeidos = msgs.stream()
                    .filter(m -> m.getReceptor().getId().equals(yo.getId()) && !m.isLeido())
                    .count();

            return new ConversacionDto(
                    c.getId(),
                    otro.getId(),
                    otro.getNombre(),
                    otro.getApellido(),
                    ultimoContenido,
                    c.getUltimoMensajeEn(),
                    noLeidos
            );
        }).toList();
    }

    @Transactional
    public MensajeDto enviarMensaje(String emailEmisor, EnviarMensajeRequest request) {
        User emisor = obtenerUsuarioPorEmail(emailEmisor);
        User receptor = obtenerUsuarioPorId(request.receptorId());

        if (emisor.getId().equals(receptor.getId())) {
            throw new IllegalArgumentException("No puedes enviarte mensajes a ti mismo.");
        }

        Conversacion conversacion;
        if (request.conversacionId() != null) {
            conversacion = conversacionRepository.findById(request.conversacionId())
                    .orElseGet(() -> crearConversacion(emisor, receptor));
        } else {
            conversacion = conversacionRepository
                    .findEntreUsuarios(emisor, receptor)
                    .orElseGet(() -> crearConversacion(emisor, receptor));
        }

        // ── MÉTRICA: contar mensaje ──────────────────────────────────── NUEVO
        chatMetricasService.contarMensaje();

        // ── MÉTRICA: tiempo de respuesta del agente ────────────────────── NUEVO
        // Si hay mensajes previos y el emisor actual NO fue quien mandó el primero,
        // significa que es el agente respondiendo al cliente por primera vez
        List<Mensaje> mensajesPrevios = mensajeRepository
                .findByConversacionIdOrderByEnviadoEnAsc(conversacion.getId());

        if (!mensajesPrevios.isEmpty()) {
            Mensaje primerMensaje = mensajesPrevios.get(0);
            System.out.println("=== METRICA DEBUG ===");
            System.out.println("Primer emisor ID: " + primerMensaje.getEmisor().getId());
            System.out.println("Emisor actual ID: " + emisor.getId());
            System.out.println("Son diferentes: " + !primerMensaje.getEmisor().getId().equals(emisor.getId()));

            if (!primerMensaje.getEmisor().getId().equals(emisor.getId())) {
                chatMetricasService.registrarTiempoRespuesta(
                        primerMensaje.getEnviadoEn().toInstant(ZoneOffset.UTC),
                        Instant.now()
                );
                System.out.println("=== TIEMPO RESPUESTA REGISTRADO ===");
            }
        }
        // ── FIN MÉTRICA ───────────────────────────────────────────────────

        Mensaje mensaje = Mensaje.builder()
                .conversacion(conversacion)
                .emisor(emisor)
                .receptor(receptor)
                .contenido(request.contenido())
                .leido(false)
                .build();

        mensaje = mensajeRepository.save(mensaje);

        conversacion.setUltimoMensajeEn(LocalDateTime.now());
        conversacionRepository.save(conversacion);

        MensajeDto mensajeDto = toMensajeDto(mensaje);

        messagingTemplate.convertAndSendToUser(
                receptor.getId().toString(),
                "/queue/mensajes",
                mensajeDto
        );

        long totalNoLeidos = mensajeRepository.contarNoLeidos(receptor.getId());
        NotificacionDto notificacion = new NotificacionDto(
                conversacion.getId(),
                emisor.getId(),
                emisor.getNombre() + " " + emisor.getApellido(),
                request.contenido().length() > 50
                        ? request.contenido().substring(0, 50) + "..."
                        : request.contenido(),
                totalNoLeidos
        );
        messagingTemplate.convertAndSendToUser(
                receptor.getId().toString(),
                "/queue/notificaciones",
                notificacion
        );

        return mensajeDto;
    }

    @Transactional
    public void marcarLeidos(String emailActual, Long conversacionId) {
        User yo = obtenerUsuarioPorEmail(emailActual);
        mensajeRepository.marcarComoLeidos(conversacionId, yo.getId());
    }

    @Transactional
    public ConversacionDetalleDto obtenerConversacionPorId(String emailActual, Long conversacionId) {
        User yo = obtenerUsuarioPorEmail(emailActual);

        Conversacion conversacion = conversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada: " + conversacionId));

        boolean esParticipante =
                conversacion.getUsuario1().getId().equals(yo.getId()) ||
                        conversacion.getUsuario2().getId().equals(yo.getId());

        if (!esParticipante) {
            throw new SecurityException("No tienes acceso a esta conversación.");
        }

        mensajeRepository.marcarComoLeidos(conversacionId, yo.getId());

        List<MensajeDto> mensajes = mensajeRepository
                .findByConversacionIdOrderByEnviadoEnAsc(conversacionId)
                .stream()
                .map(this::toMensajeDto)
                .toList();

        // ── MÉTRICA: duración de conversación ─────────────────────────── NUEVO
        List<Mensaje> todos = mensajeRepository
                .findByConversacionIdOrderByEnviadoEnAsc(conversacionId);

        if (todos.size() >= 2) {
            Instant inicio = todos.get(0).getEnviadoEn().toInstant(ZoneOffset.UTC);
            Instant fin    = todos.get(todos.size() - 1).getEnviadoEn().toInstant(ZoneOffset.UTC);
            chatMetricasService.registrarDuracionConversacion(inicio, fin);
        }
        // ── FIN MÉTRICA ───────────────────────────────────────────────────

        User otro = conversacion.getUsuario1().getId().equals(yo.getId())
                ? conversacion.getUsuario2()
                : conversacion.getUsuario1();

        return new ConversacionDetalleDto(
                conversacion.getId(),
                otro.getId(),
                otro.getNombre(),
                otro.getApellido(),
                mensajes
        );
    }

    private Conversacion crearConversacion(User u1, User u2) {
        Conversacion nueva = Conversacion.builder()
                .usuario1(u1)
                .usuario2(u2)
                .build();
        return conversacionRepository.save(nueva);
    }

    private User obtenerUsuarioPorEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
    }

    private User obtenerUsuarioPorId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    private MensajeDto toMensajeDto(Mensaje m) {
        return new MensajeDto(
                m.getId(),
                m.getConversacion().getId(),
                m.getEmisor().getId(),
                m.getEmisor().getNombre(),
                m.getEmisor().getApellido(),
                m.getReceptor().getId(),
                m.getContenido(),
                m.getEnviadoEn(),
                m.isLeido()
        );
    }
}