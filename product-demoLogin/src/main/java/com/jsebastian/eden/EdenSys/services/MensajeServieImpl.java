package com.jsebastian.eden.EdenSys.services;

import com.jsebastian.eden.EdenSys.domain.Mensaje;
import com.jsebastian.eden.EdenSys.repository.MensajeRepository;
import com.jsebastian.eden.EdenSys.services.interfaces.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MensajeServieImpl implements MensajeService {

    private final MensajeRepository mensajeRepository;

    @Override
    public Mensaje save(Mensaje mensaje) {
        mensaje.setFechaMensaje(LocalDateTime.now());
        return mensajeRepository.save(mensaje);
    }

    @Override
    public List<Mensaje> getChat(String emisor, String receptor) {
        return mensajeRepository
                .findByEmisorAndReceptorOrReceptorAndEmisor(emisor, receptor, receptor, emisor);
    }
}
