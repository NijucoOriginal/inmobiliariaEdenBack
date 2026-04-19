package com.jsebastian.eden.EdenSys.Dtos.chat;

import java.time.LocalDateTime;

public record ConversacionDto(
        Long id,
        Long otroUsuarioId,
        String otroUsuarioNombre,
        String otroUsuarioApellido,
        String ultimoMensaje,
        LocalDateTime ultimoMensajeEn,
        long noLeidosPorMi
) {}
