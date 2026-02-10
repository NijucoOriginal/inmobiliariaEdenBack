package com.jsebastian.eden.EdenSys.Dtos;

import com.jsebastian.eden.EdenSys.domain.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * DTO para la creación de usuarios con validaciones Jakarta
 */
public record CrearUsuarioDto(
                                @NotBlank(message = "El nombre es obligatorio")
                                @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
                                String nombre,

                                @NotBlank(message = "El apellido es obligatorio")
                                @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
                                String apellido,

                                @NotBlank(message = "El documento de identidad es obligatorio")
                                @Size(max = 20, message = "El documento de identidad no puede exceder 20 caracteres")
                                String documentoIdentidad,

                                @NotBlank(message = "El teléfono es obligatorio")
                                @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
                                String telefono,

                                @NotBlank(message = "El email es obligatorio")
                                @Email(message = "El formato del email no es válido")
                                @Size(max = 150, message = "El email no puede exceder 150 caracteres")
                                String email,

                                @NotBlank(message = "La contraseña es obligatoria")
                                @Pattern(
                                    regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                                    message = "La contraseña debe tener al menos una mayúscula, un número, un carácter especial y mínimo 8 caracteres")
                                String contrasena,
                                Rol rol


) {
    public CrearUsuarioDto {
        rol = Objects.requireNonNullElse(rol, Rol.USER);
    }
}
