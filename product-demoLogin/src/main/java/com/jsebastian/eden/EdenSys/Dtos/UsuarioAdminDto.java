package com.jsebastian.eden.EdenSys.Dtos;

public record UsuarioAdminDto(
        Long id,
        String nombre,
        String apellido,
        String email,
        String documentoIdentidad,
        String telefono,
        String rol
) {}