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

    // --- NUEVO: Agregar un contacto ---
    public void agregarContacto(String emailUsuarioLogueado, String emailContacto) {
        if (emailUsuarioLogueado.equalsIgnoreCase(emailContacto)) {
            throw new IllegalArgumentException("No puedes agregarte a ti mismo.");
        }

        User usuarioLogueado = userRepository.findByEmail(emailUsuarioLogueado)
                .orElseThrow(() -> new RuntimeException("Usuario logueado no encontrado"));

        User nuevoContacto = userRepository.findByEmail(emailContacto)
                .orElseThrow(() -> new RuntimeException("El usuario que intentas agregar no existe"));

        // Verificamos que no esté ya en la lista
        if (usuarioLogueado.getContactos().contains(nuevoContacto)) {
            throw new IllegalArgumentException("Este usuario ya está en tu lista de contactos.");
        }

        usuarioLogueado.getContactos().add(nuevoContacto);
        userRepository.save(usuarioLogueado); // Hibernate inserta en user_contacts
    }

    // --- NUEVO: Eliminar un contacto ---
    public void eliminarContacto(String emailUsuarioLogueado, String emailContacto) {
        User usuarioLogueado = userRepository.findByEmail(emailUsuarioLogueado)
                .orElseThrow(() -> new RuntimeException("Usuario logueado no encontrado"));

        User contactoAEliminar = userRepository.findByEmail(emailContacto)
                .orElseThrow(() -> new RuntimeException("El usuario que intentas eliminar no existe"));

        // Removemos de la lista (el método equals() y hashCode() de tu entidad es vital aquí)
        boolean removido = usuarioLogueado.getContactos().remove(contactoAEliminar);

        if (!removido) {
            throw new IllegalArgumentException("El usuario no pertenece a tu lista de contactos.");
        }

        userRepository.save(usuarioLogueado); // Hibernate borra de user_contacts
    }


}