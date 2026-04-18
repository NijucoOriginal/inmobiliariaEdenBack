// ─── EnviarMensajeRequest.java ─────────────────────────────────────────────
// Lo que manda el frontend al enviar un mensaje por WebSocket
package com.jsebastian.eden.EdenSys.Dtos.chat;

public record EnviarMensajeRequest(
        Long conversacionId,   // null si es conversación nueva
        Long receptorId,       // ID del destinatario
        String contenido
) {}