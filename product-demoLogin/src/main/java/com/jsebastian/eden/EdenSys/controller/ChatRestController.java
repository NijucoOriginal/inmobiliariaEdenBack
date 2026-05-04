package com.jsebastian.eden.EdenSys.controller;

import com.jsebastian.eden.EdenSys.Dtos.chat.*;
import com.jsebastian.eden.EdenSys.services.ChatMetricasService; // ← NUEVO
import com.jsebastian.eden.EdenSys.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;
    private final ChatMetricasService chatMetricasService; // ← NUEVO

    @GetMapping("/conversaciones")
    public ResponseEntity<List<ConversacionDto>> listarConversaciones(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) return ResponseEntity.status(500).build();
            return ResponseEntity.ok(
                    chatService.listarConversaciones(userDetails.getUsername())
            );
        } catch (Exception e) {
            chatMetricasService.contarError(); // ← NUEVO
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/conversaciones/iniciar")
    public ResponseEntity<ConversacionDetalleDto> iniciarConversacion(
            @RequestBody IniciarConversacionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Instant inicio = Instant.now(); // ← agregar
        try {
            ConversacionDetalleDto detalle = chatService.obtenerOCrearConversacion(
                    userDetails.getUsername(),
                    request.otroUsuarioId()
            );
            chatMetricasService.registrarLatenciaMensaje(inicio, Instant.now()); // ← agregar
            return ResponseEntity.ok(detalle);
        } catch (Exception e) {
            chatMetricasService.contarError();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/conversaciones/{id}")
    public ResponseEntity<ConversacionDetalleDto> obtenerConversacion(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Instant inicio = Instant.now(); // ← agregar
        try {
            ConversacionDetalleDto detalle = chatService.obtenerConversacionPorId(
                    userDetails.getUsername(), id
            );
            chatMetricasService.registrarLatenciaMensaje(inicio, Instant.now()); // ← agregar
            return ResponseEntity.ok(detalle);
        } catch (Exception e) {
            chatMetricasService.contarError();
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/conversaciones/{id}/leidos")
    public ResponseEntity<Void> marcarLeidos(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            chatService.marcarLeidos(userDetails.getUsername(), id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            chatMetricasService.contarError(); // ← NUEVO
            return ResponseEntity.status(500).build();
        }
    }
}