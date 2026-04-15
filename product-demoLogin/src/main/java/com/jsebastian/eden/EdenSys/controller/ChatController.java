package com.jsebastian.eden.EdenSys.controller;

import com.jsebastian.eden.EdenSys.domain.Mensaje;
import com.jsebastian.eden.EdenSys.Dtos.MensajeDTO;
import com.jsebastian.eden.EdenSys.services.interfaces.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MensajeService chatService;

    // 📩 Enviar mensaje
    @MessageMapping("/chat")
    public void sendMessage(MensajeDTO messageDTO, Principal principal) {

        String sender = principal.getName(); // email del usuario autenticado

        // 1. Mapear DTO a Entidad para guardar en BD
        Mensaje mensaje = new Mensaje();
        mensaje.setEmisor(sender);
        mensaje.setReceptor(messageDTO.getReceptor());
        mensaje.setContenido(messageDTO.getContenido());

        Mensaje saved = chatService.save(mensaje);

        // 2. Preparar el DTO de respuesta con datos generados (fecha y emisor real)
        MensajeDTO respuestaDTO = new MensajeDTO();
        respuestaDTO.setEmisor(saved.getEmisor());
        respuestaDTO.setReceptor(saved.getReceptor());
        respuestaDTO.setContenido(saved.getContenido());
        respuestaDTO.setFechaMensaje(saved.getFechaMensaje());

        // 3. Enviar al destinatario
        messagingTemplate.convertAndSendToUser(
                respuestaDTO.getReceptor(),
                "/queue/messages",
                respuestaDTO
        );
    }
}