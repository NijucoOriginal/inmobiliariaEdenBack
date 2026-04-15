package com.jsebastian.eden.EdenSys.controller;

import com.jsebastian.eden.EdenSys.domain.Mensaje;
import com.jsebastian.eden.EdenSys.services.interfaces.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MensajeController {

    private final MensajeService mensajeService;

    @GetMapping("/{emisor}/{receptor}")
    public ResponseEntity<List<Mensaje>> getHistorial(@PathVariable String emisor, @PathVariable String receptor) {
        // Nota: Idealmente aquí también devolverías una List<MensajeDTO>,
        // pero para simplificar, usaremos la entidad tal como la devuelve tu servicio.
        return ResponseEntity.ok(mensajeService.getChat(emisor, receptor));
    }
}