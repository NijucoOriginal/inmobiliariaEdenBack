package com.jsebastian.eden.EdenSys.controller;

import com.jsebastian.eden.EdenSys.Dtos.*;
import com.jsebastian.eden.EdenSys.domain.Inmueble;
import com.jsebastian.eden.EdenSys.services.interfaces.InmuebleService;
import com.jsebastian.eden.EdenSys.services.interfaces.UserService;
import com.jsebastian.eden.EdenSys.services.JwtService;
import com.jsebastian.eden.EdenSys.exceptions.ValueConflictException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${frontend.url}")
    private String frontendUrl;
    
    @Value("${frontend.local.url}")
    private String frontendLocalUrl;

    private final UserService userService;



    private final InmuebleService inmuebleService;


    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            userService.crearUsuario(request.toCrearUsuarioDto());
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente. Por favor, verifica tu correo para activar tu cuenta.");
        } catch (ValueConflictException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/activate/{codigo}")
    public ResponseEntity<String> activate(@PathVariable String codigo) {
        boolean activated = userService.activarUsuario(codigo);
        if (activated) {
            return ResponseEntity.ok("Cuenta activada exitosamente.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Código de activación inválido o expirado.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            String token = userService.validarCredencialesYGenerarToken(request.email(), request.contrasena());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(e.getMessage()));
        }
    }
    @PostMapping("/recuperar")
    public ResponseEntity<String> solicitarRecuperacion(
            @RequestParam String email
    ) {
        userService.enviarCodigoRecuperacionContrasena(email);

        return ResponseEntity.ok(
                "Si el correo está registrado, recibirás un código de recuperación."
        );
    }

    @PostMapping("/recuperar/cambiar")
    public ResponseEntity<String> cambiarContrasena(
            @RequestBody CambiarContrasenaDto dto
    ) {

        boolean ok = userService.cambiarContrasenaConCodigo(dto);

        if (!ok) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Código inválido o expirado.");
        }

        return ResponseEntity.ok("Contraseña actualizada correctamente.");
    }

    @PostMapping("/contacto")
    public ResponseEntity<String> enviarContacto(
            @Valid @RequestBody ContactoDto contacto) {

        userService.procesarContacto(contacto);

        return ResponseEntity.ok("Correo enviado correctamente");
    }
}
