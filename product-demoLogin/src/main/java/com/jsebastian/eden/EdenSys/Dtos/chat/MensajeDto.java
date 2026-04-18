package com.jsebastian.eden.EdenSys.Dtos.chat;

import java.time.LocalDateTime;

public record MensajeDto(
        Long id,
        Long conversacionId,
        Long emisorId,
        String emisorNombre,
        String emisorApellido,
        Long receptorId,
        String contenido,
        LocalDateTime enviadoEn,
        boolean leido
) {}
