package com.jsebastian.eden.EdenSys.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record ActivationRequest(
        @NotNull(message = " El código de activación es requerido")
        String codigoActivacion,
        @NotNull(message = " El correo electrónico es requerido")
        @Email (message = " El correo electrónico debe ser válido")
        String email
) {
}
