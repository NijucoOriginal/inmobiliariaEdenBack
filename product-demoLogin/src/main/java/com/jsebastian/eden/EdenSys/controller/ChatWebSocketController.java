package com.jsebastian.eden.EdenSys.controller;

import com.jsebastian.eden.EdenSys.Dtos.chat.EnviarMensajeRequest;
import com.jsebastian.eden.EdenSys.Dtos.chat.MensajeDto;
import com.jsebastian.eden.EdenSys.services.ChatMetricasService;
import com.jsebastian.eden.EdenSys.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final ChatMetricasService chatMetricasService;

    /**
     * El cliente publica en: /app/chat.enviar
     * El servicio luego enruta el mensaje al receptor por /user/{id}/queue/mensajes
     */
    @MessageMapping("/chat.enviar")
    public void enviarMensaje(@Payload EnviarMensajeRequest request, Principal principal) {
        Instant inicio = Instant.now(); // ← aquí
        System.out.println("=== WEBSOCKET RECIBIDO ===");
        System.out.println("Principal: " + (principal != null ? principal.getName() : "NULL"));
        if (principal == null) return;
        chatService.enviarMensaje(principal.getName(), request);
        chatMetricasService.registrarLatenciaMensaje(inicio, Instant.now()); // ← aquí
    }

    /**
     * El cliente publica en: /app/chat.leidos
     * Marca mensajes de una conversación como leídos
     */
    @MessageMapping("/chat.leidos")
    public void marcarLeidos(@Payload Long conversacionId,
                             Principal principal) {
        if (principal == null) return;
        chatService.marcarLeidos(principal.getName(), conversacionId);
    }
}
