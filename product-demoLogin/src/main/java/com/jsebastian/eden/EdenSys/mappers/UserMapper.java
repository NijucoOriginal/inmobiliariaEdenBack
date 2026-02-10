package com.jsebastian.eden.EdenSys.mappers;

import com.jsebastian.eden.EdenSys.Dtos.UserResponse;
import com.jsebastian.eden.EdenSys.domain.User;
import com.jsebastian.eden.EdenSys.Dtos.CrearUsuarioDto;
import com.jsebastian.eden.EdenSys.Dtos.UsuarioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    //UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "apellido", source = "apellido")
    @Mapping(target = "documentoIdentidad", source = "documentoIdentidad")
    @Mapping(target = "telefono", source = "telefono")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "contrasena", source = "contrasena")
    @Mapping(target = "rol", source = "rol")
    User toEntity(CrearUsuarioDto dto); // Mapea CrearUsuarioDto a User

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "rol", source = "rol")
    UsuarioResponse toUsuarioResponse(User user); // Mapea User a UsuarioResponse


    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "rol", source = "rol")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "apellido", source = "apellido")
    @Mapping(target = "documentoIdentidad", source = "documentoIdentidad")
    @Mapping(target = "telefono", source = "telefono")
    UserResponse toUserResponse(User user);
}

