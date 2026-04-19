// ─── ChatRestController.java ───────────────────────────────────────────────
// Endpoints REST para carga inicial de datos
package com.jsebastian.eden.EdenSys.controller;

import com.jsebastian.eden.EdenSys.Dtos.chat.*;
import com.jsebastian.eden.EdenSys.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    /**
     * GET /api/chat/conversaciones
     * Devuelve todas las conversaciones del usuario logueado (lista lateral)
     */
    @GetMapping("/conversaciones")
    public ResponseEntity<List<ConversacionDto>> listarConversaciones(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                chatService.listarConversaciones(userDetails.getUsername())
        );
    }

    /**
     * POST /api/chat/conversaciones/iniciar
     * Abre o crea una conversación con otro usuario.
     * Llamado desde el botón "Contactar" del detalle-inmueble.
     */
    @PostMapping("/conversaciones/iniciar")
    public ResponseEntity<ConversacionDetalleDto> iniciarConversacion(
            @RequestBody IniciarConversacionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ConversacionDetalleDto detalle = chatService.obtenerOCrearConversacion(
                userDetails.getUsername(),
                request.otroUsuarioId()
        );
        return ResponseEntity.ok(detalle);
    }

    /**
     * GET /api/chat/conversaciones/{id}
     * Carga el historial completo de una conversación existente
     */
    @GetMapping("/conversaciones/{id}")
    public ResponseEntity<ConversacionDetalleDto> obtenerConversacion(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Reutilizamos el mismo método: si la conversación existe la carga,
        // el otroUsuarioId lo inferimos desde la conversación
        ConversacionDetalleDto detalle = chatService.obtenerConversacionPorId(
                userDetails.getUsername(), id
        );
        return ResponseEntity.ok(detalle);
    }

    /**
     * PUT /api/chat/conversaciones/{id}/leidos
     * Marca todos los mensajes de la conversación como leídos
     */
    @PutMapping("/conversaciones/{id}/leidos")
    public ResponseEntity<Void> marcarLeidos(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        chatService.marcarLeidos(userDetails.getUsername(), id);
        return ResponseEntity.ok().build();
    }
}
 