// Conversación completa con todos sus mensajes
package com.jsebastian.eden.EdenSys.Dtos.chat;

import java.util.List;

public record ConversacionDetalleDto(
        Long id,
        Long otroUsuarioId,
        String otroUsuarioNombre,
        String otroUsuarioApellido,
        List<MensajeDto> mensajes
) {}
 