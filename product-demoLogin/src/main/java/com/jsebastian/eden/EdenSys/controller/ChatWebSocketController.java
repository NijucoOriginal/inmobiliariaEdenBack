package com.jsebastian.eden.EdenSys.controller;

import com.jsebastian.eden.EdenSys.Dtos.chat.EnviarMensajeRequest;
import com.jsebastian.eden.EdenSys.Dtos.chat.MensajeDto;
import com.jsebastian.eden.EdenSys.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    /**
     * El cliente publica en: /app/chat.enviar
     * El servicio luego enruta el mensaje al receptor por /user/{id}/queue/mensajes
     */
    @MessageMapping("/chat.enviar")
    public void enviarMensaje(@Payload EnviarMensajeRequest request,
                              Principal principal) {
        if (principal == null) return;
        // principal.getName() = email del usuario autenticado por JWT
        chatService.enviarMensaje(principal.getName(), request);
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
