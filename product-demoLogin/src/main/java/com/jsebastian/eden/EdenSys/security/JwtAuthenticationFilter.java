package com.jsebastian.eden.EdenSys.security;

import com.jsebastian.eden.EdenSys.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        System.out.println("=== JwtAuthenticationFilter ejecutado ===");
        System.out.println("Authorization header: " + authHeader);
        System.out.println("Ruta actual: " + request.getRequestURI());



        // 1️⃣ Si no hay header o no empieza con "Bearer ", continúa sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            System.out.println("No se encontró el token JWT en la solicitud.");
            return;
        }

        // 2️⃣ Extraer el token y el email del usuario
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        // 3️⃣ Validar el token y autenticar si el contexto está vacío
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 🔹 EXTRAER EL ROL DESDE EL TOKEN
                String role = jwtService.extractRole(jwt); // asegúrate de tener este método en JwtService
                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                // 🔹 Crear la autenticación con el rol del token
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 🔹 Guardar en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 4️⃣ Continuar con el siguiente filtro
        filterChain.doFilter(request, response);
    }
}



