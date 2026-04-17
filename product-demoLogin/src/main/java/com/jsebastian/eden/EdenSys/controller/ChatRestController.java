package com.jsebastian.eden.EdenSys.controller;

import com.jsebastian.eden.EdenSys.Dtos.MensajeDTO;
import com.jsebastian.eden.EdenSys.services.interfaces.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRestController {

    private final MensajeService chatService;

    @GetMapping("/{user}")
    public List<MensajeDTO> getChat(@PathVariable String user, Principal principal) {
        return chatService.getChat(principal.getName(), user)
                .stream()
                .map(m -> {
                    MensajeDTO dto = new MensajeDTO();
                    dto.setEmisor(m.getEmisor());
                    dto.setReceptor(m.getReceptor());
                    dto.setContenido(m.getContenido());
                    dto.setFechaMensaje(m.getFechaMensaje());
                    return dto;
                })
                .toList();
    }
}