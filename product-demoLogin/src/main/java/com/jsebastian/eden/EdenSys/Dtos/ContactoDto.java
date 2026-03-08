package com.jsebastian.eden.EdenSys.Dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record ContactoDto(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombre,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^[0-9]{7,15}$", message = "El teléfono debe tener entre 7 y 15 dígitos")
        String telefono,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Debe ser un correo válido")
        String correo,

        @NotBlank(message = "El asunto es obligatorio")
        @Size(max = 150, message = "El asunto no puede superar 150 caracteres")
        String asunto,

        @NotBlank(message = "El mensaje es obligatorio")
        @Size(max = 500, message = "El mensaje no puede superar 500 caracteres")
        String mensaje,
        @NotBlank String recaptchaToken

) {}