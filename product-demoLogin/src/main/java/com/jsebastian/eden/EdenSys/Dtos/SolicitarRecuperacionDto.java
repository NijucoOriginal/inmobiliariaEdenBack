package com.jsebastian.eden.EdenSys.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitarRecuperacionDto(
        @NotBlank @Email String email,
        @NotBlank String recaptchaToken
) {}
