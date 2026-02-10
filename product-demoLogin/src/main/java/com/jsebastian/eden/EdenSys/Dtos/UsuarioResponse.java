package com.jsebastian.eden.EdenSys.Dtos;

import com.jsebastian.eden.EdenSys.domain.Rol;

public record UsuarioResponse(String id, String email, Rol rol) {}
