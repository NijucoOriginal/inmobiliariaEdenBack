package com.jsebastian.eden.EdenSys.config;

import com.jsebastian.eden.EdenSys.services.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class MessageInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        // 🔥 SOLO intercepta cuando el cliente se conecta
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            var authHeaderList = accessor.getNativeHeader("Authorization");

            if (authHeaderList == null || authHeaderList.isEmpty()) {
                log.warn("❌ No se envió Authorization header");
                return message; // Rechazar silenciosamente
            }

            String authHeader = authHeaderList.get(0);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("❌ Token con formato inválido");
                return message;
            }

            String jwt = authHeader.substring(7);

            try {
                // 📧 Extraer email del token (lanzará excepción si es inválido/expirado)
                String username = jwtService.extractUsername(jwt);
                log.info("✅ Usuario intentando conectar al chat: {}", username);

                var userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                accessor.setUser(auth);
                log.info("🔐 Conexión WebSocket autenticada exitosamente");

            } catch (Exception e) {
                log.error("❌ Token JWT inválido o expirado en WebSocket: {}", e.getMessage());
                // No asignamos el usuario, por lo que la conexión fallará por falta de permisos
            }
        }

        return message;
    }
}