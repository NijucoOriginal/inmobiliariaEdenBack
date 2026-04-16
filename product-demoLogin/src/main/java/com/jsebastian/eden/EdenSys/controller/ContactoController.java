package com.jsebastian.eden.EdenSys.controller;

import com.jsebastian.eden.EdenSys.Dtos.ContactoChatDTO;
import com.jsebastian.eden.EdenSys.domain.User;
import com.jsebastian.eden.EdenSys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/contactos")
@RequiredArgsConstructor
public class ContactoController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ContactoChatDTO>> getContactos(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow();

        List<ContactoChatDTO> contactos = user.getContactos().stream()
                .map(c -> new ContactoChatDTO(
                        c.getId(),
                        c.getNombre(),
                        c.getApellido(),
                        c.getEmail()
                ))
                .toList();

        return ResponseEntity.ok(contactos);
    }

}