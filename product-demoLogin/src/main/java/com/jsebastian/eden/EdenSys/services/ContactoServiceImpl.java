package com.jsebastian.eden.EdenSys.services;

import com.jsebastian.eden.EdenSys.Dtos.ContactoChatDTO;
import com.jsebastian.eden.EdenSys.domain.User;
import com.jsebastian.eden.EdenSys.repository.UserRepository;
import com.jsebastian.eden.EdenSys.services.interfaces.ContactoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactoServiceImpl implements ContactoService {

    private final UserRepository userRepository;

    @Override
    public List<ContactoChatDTO> getContactos(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return user.getContactos().stream()
                .map(c -> new ContactoChatDTO(c.getId(), c.getNombre(), c.getApellido(), c.getEmail(),c.getRol().toString()))
                .toList();
    }

    @Override
    public ContactoChatDTO agregarContacto(String userEmail, String contactoEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        User contacto = userRepository.findByEmail(contactoEmail).orElseThrow();

        boolean yaExiste = user.getContactos().stream()
                .anyMatch(c -> c.getEmail().equals(contactoEmail));

        if (!yaExiste) {
            user.getContactos().add(contacto);
            userRepository.save(user);
        }

        return new ContactoChatDTO(contacto.getId(), contacto.getNombre(), contacto.getApellido(), contacto.getEmail(),contacto.getRol().toString());
    }

    @Override
    public void eliminarContacto(String userEmail, String contactoEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        user.getContactos().removeIf(c -> c.getEmail().equals(contactoEmail));
        userRepository.save(user);
    }
}
