package com.jsebastian.eden.EdenSys.controller;

import com.jsebastian.eden.EdenSys.Dtos.ContactoChatDTO;
import com.jsebastian.eden.EdenSys.services.interfaces.ContactoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/contactos")
@RequiredArgsConstructor
public class ContactoController {

    private final ContactoService contactoService;

    @GetMapping
    public ResponseEntity<List<ContactoChatDTO>>getContactos(Principal principal) {
        return ResponseEntity.ok(contactoService.getContactos(principal.getName()));
    }

    @PostMapping("/{email}")
    public ResponseEntity<ContactoChatDTO> agregar(@PathVariable String email, Principal principal) {
        return ResponseEntity.ok(contactoService.agregarContacto(principal.getName(), email));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> eliminar(@PathVariable String email, Principal principal) {
        contactoService.eliminarContacto(principal.getName(), email);
        return ResponseEntity.noContent().build();
    }
}