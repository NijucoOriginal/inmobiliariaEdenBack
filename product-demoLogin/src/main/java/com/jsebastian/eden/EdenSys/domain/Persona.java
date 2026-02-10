package com.jsebastian.eden.EdenSys.domain;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Superclase mapeada para credenciales básicas.
 * Provee correo, contraseña y rol por defecto USER.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class Persona {

    private String email;

    private String contrasena;

    @Enumerated(EnumType.STRING)
    private Rol rol ;

    private String nombre;

    private String apellido;

    private String documentoIdentidad;

    private String telefono;
}