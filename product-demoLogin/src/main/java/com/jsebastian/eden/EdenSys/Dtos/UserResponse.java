package com.jsebastian.eden.EdenSys.Dtos;

import com.jsebastian.eden.EdenSys.domain.Rol;

public record UserResponse(Long id, String email, Rol rol,String nombre,String apellido, String documentoIdentidad, String telefono) {
}
