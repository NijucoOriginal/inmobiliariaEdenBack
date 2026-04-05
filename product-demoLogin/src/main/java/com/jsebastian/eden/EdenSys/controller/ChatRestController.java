package com.jsebastian.eden.EdenSys.controller;

import com.jsebastian.eden.EdenSys.domain.Mensaje;
import com.jsebastian.eden.EdenSys.services.interfaces.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRestController {

    private final MensajeService chatService;

    @GetMapping("/{user}")
    public List<Mensaje> getChat(@PathVariable String user, Principal principal) {
        return chatService.getChat(principal.getName(), user);
    }
}