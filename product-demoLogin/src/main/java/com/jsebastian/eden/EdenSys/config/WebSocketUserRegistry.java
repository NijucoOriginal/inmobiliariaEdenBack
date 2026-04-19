// ─── WebSocketUserRegistry.java ───────────────────────────────────────────
// Permite mapear userId → sesión WebSocket para entrega a usuarios offline/online
package com.jsebastian.eden.EdenSys.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class WebSocketUserRegistry {

    // email → userId (se llena al conectar)
    private final Map<String, Long> emailToId = new ConcurrentHashMap<>();

    public void registrar(String email, Long userId) {
        emailToId.put(email, userId);
    }

    public void desconectar(String email) {
        emailToId.remove(email);
    }

    public boolean estaConectado(Long userId) {
        return emailToId.containsValue(userId);
    }
}
 