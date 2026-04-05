package com.jsebastian.eden.EdenSys.controller;

import com.jsebastian.eden.EdenSys.domain.Mensaje;
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
    public void sendMessage(Mensaje message, Principal principal) {

        String sender = principal.getName(); // email del usuario
        message.setEmisor(sender);

        // guardar en BD
        Mensaje saved = chatService.save(message);

        // enviar al destinatario
        messagingTemplate.convertAndSendToUser(
                message.getReceptor(),
                "/queue/messages",
                saved
        );
    }
}
