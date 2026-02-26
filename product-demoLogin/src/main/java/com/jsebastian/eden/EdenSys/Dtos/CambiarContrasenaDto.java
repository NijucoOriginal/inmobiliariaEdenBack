package com.jsebastian.eden.EdenSys.Dtos;
import jakarta.validation.constraints.*;

public record CambiarContrasenaDto(

        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 6, max = 6)
        String codigo,

        @NotBlank
        @Size(min = 8, max = 100)
        String nuevaContrasena
) {}
