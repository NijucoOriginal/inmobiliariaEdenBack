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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // ─── Obtener o crear conversación entre dos usuarios ──────────────────
    @Transactional
    public ConversacionDetalleDto obtenerOCrearConversacion(String emailActual, Long otroUsuarioId) {
        User yo = obtenerUsuarioPorEmail(emailActual);
        User otro = obtenerUsuarioPorId(otroUsuarioId);

        // Impedir que un usuario hable consigo mismo
        if (yo.getId().equals(otro.getId())) {
            throw new IllegalArgumentException("No puedes iniciar una conversación contigo mismo.");
        }

        Conversacion conversacion = conversacionRepository
                .findEntreUsuarios(yo, otro)
                .orElseGet(() -> crearConversacion(yo, otro));

        // Marcar mensajes dirigidos a mí como leídos
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

    // ─── Lista de conversaciones del usuario actual ───────────────────────
    @Transactional(readOnly = true)
    public List<ConversacionDto> listarConversaciones(String emailActual) {
        User yo = obtenerUsuarioPorEmail(emailActual);
        List<Conversacion> conversaciones = conversacionRepository.findByUsuario(yo);

        return conversaciones.stream().map(c -> {
            User otro = c.getUsuario1().getId().equals(yo.getId())
                    ? c.getUsuario2()
                    : c.getUsuario1();

            // Último mensaje de la conversación
            List<Mensaje> msgs = mensajeRepository
                    .findByConversacionIdOrderByEnviadoEnAsc(c.getId());

            String ultimoContenido = msgs.isEmpty()
                    ? ""
                    : msgs.get(msgs.size() - 1).getContenido();

            // Contar mensajes no leídos dirigidos a mí en esta conversación
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

    // ─── Enviar mensaje (llamado desde WebSocket controller) ─────────────
    @Transactional
    public MensajeDto enviarMensaje(String emailEmisor, EnviarMensajeRequest request) {
        User emisor = obtenerUsuarioPorEmail(emailEmisor);
        User receptor = obtenerUsuarioPorId(request.receptorId());

        // Impedir auto-mensajes
        if (emisor.getId().equals(receptor.getId())) {
            throw new IllegalArgumentException("No puedes enviarte mensajes a ti mismo.");
        }

        // Obtener o crear la conversación
        Conversacion conversacion;
        if (request.conversacionId() != null) {
            conversacion = conversacionRepository.findById(request.conversacionId())
                    .orElseGet(() -> crearConversacion(emisor, receptor));
        } else {
            conversacion = conversacionRepository
                    .findEntreUsuarios(emisor, receptor)
                    .orElseGet(() -> crearConversacion(emisor, receptor));
        }

        // Persistir el mensaje
        Mensaje mensaje = Mensaje.builder()
                .conversacion(conversacion)
                .emisor(emisor)
                .receptor(receptor)
                .contenido(request.contenido())
                .leido(false)
                .build();

        mensaje = mensajeRepository.save(mensaje);

        // Actualizar timestamp de último mensaje en la conversación
        conversacion.setUltimoMensajeEn(LocalDateTime.now());
        conversacionRepository.save(conversacion);

        MensajeDto mensajeDto = toMensajeDto(mensaje);

        // ─── Entrega en tiempo real por WebSocket ─────────────────────────
        // Canal personal del receptor: /user/{receptorId}/queue/mensajes
        messagingTemplate.convertAndSendToUser(
                receptor.getId().toString(),
                "/queue/mensajes",
                mensajeDto
        );

        // Notificación (badge, preview)
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

    // ─── Marcar mensajes como leídos ─────────────────────────────────────
    @Transactional
    public void marcarLeidos(String emailActual, Long conversacionId) {
        User yo = obtenerUsuarioPorEmail(emailActual);
        mensajeRepository.marcarComoLeidos(conversacionId, yo.getId());
    }

    // ─── Helpers privados ─────────────────────────────────────────────────
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

    // ─── Agregar este método dentro de ChatService.java ───────────────────────

    /**
     * Carga una conversación existente por su ID.
     * Verifica que el usuario autenticado sea participante.
     */
    @Transactional
    public ConversacionDetalleDto obtenerConversacionPorId(String emailActual, Long conversacionId) {
        User yo = obtenerUsuarioPorEmail(emailActual);

        Conversacion conversacion = conversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada: " + conversacionId));

        // Verificar que el usuario sea participante
        boolean esParticipante =
                conversacion.getUsuario1().getId().equals(yo.getId()) ||
                        conversacion.getUsuario2().getId().equals(yo.getId());

        if (!esParticipante) {
            throw new SecurityException("No tienes acceso a esta conversación.");
        }

        // Marcar mis mensajes como leídos
        mensajeRepository.marcarComoLeidos(conversacionId, yo.getId());

        List<MensajeDto> mensajes = mensajeRepository
                .findByConversacionIdOrderByEnviadoEnAsc(conversacionId)
                .stream()
                .map(this::toMensajeDto)
                .toList();

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
}