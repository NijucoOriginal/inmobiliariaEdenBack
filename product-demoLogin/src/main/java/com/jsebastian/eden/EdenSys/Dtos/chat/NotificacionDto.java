// Notificación push por WebSocket al receptor
package com.jsebastian.eden.EdenSys.Dtos.chat;

public record NotificacionDto(
        Long conversacionId,
        Long emisorId,
        String emisorNombre,
        String contenidoPreview,
        long totalNoLeidos
) {}