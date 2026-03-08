package com.jsebastian.eden.EdenSys.mappers;

import com.jsebastian.eden.EdenSys.Dtos.*;
import com.jsebastian.eden.EdenSys.domain.Inmueble;
import com.jsebastian.eden.EdenSys.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.jsebastian.eden.EdenSys.domain.Imagen;

@Mapper(
        componentModel = "spring",
        imports = {com.jsebastian.eden.EdenSys.domain.Imagen.class, java.util.stream.Collectors.class}
)
public interface InmuebleMapper {

    @Mapping(target = "longitud", source = "longitud")
    @Mapping(target = "latitud", source = "latitud")
    @Mapping(target = "tipoNegocio", source = "tipoNegocio")

    @Mapping(target = "tipo", source = "tipo")
    @Mapping(target = "medidas", source = "medidas")
    @Mapping(target = "habitaciones", source = "habitaciones")
    @Mapping(target = "banos", source = "banos")
    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "estado", source = "estado")
    @Mapping(target = "precio", source = "precio")

    @Mapping(target = "cantidadParqueaderos", source = "cantidadParqueaderos")
    @Mapping(target = "telefonoContacto", source = "telefonoContacto")
    @Mapping(target = "nombreContacto", source = "nombreContacto")
    @Mapping(target = "correoContacto", source = "correoContacto")
    Inmueble toEntity(InmuebleDto dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "longitud", source = "longitud")
    @Mapping(target = "latitud", source = "latitud")
    @Mapping(target = "tipoNegocio", source = "tipoNegocio")
    @Mapping(target = "tipo", source = "tipo")
    @Mapping(target = "medidas", source = "medidas")
    @Mapping(target = "habitaciones", source = "habitaciones")
    @Mapping(target = "banos", source = "banos")
    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "estado", source = "estado")
    @Mapping(target = "precio", source = "precio")
    @Mapping(target = "estadoTransa", source = "estadoTransa")
    @Mapping(target = "cantidadParqueaderos", source = "cantidadParqueaderos")
    @Mapping(target = "telefonoContacto", source = "telefonoContacto")
    @Mapping(target = "nombreContacto", source = "nombreContacto")
    @Mapping(target = "correoContacto", source = "correoContacto")

    @Mapping(target = "asesorLegal", source = "asesorLegal")

    @Mapping(target = "agenteAsociado", source = "agenteAsociado")

    @Mapping(target = "propietario", source = "propietario")

    @Mapping(target = "imagenes",
            expression = "java(entity.getImagenes() != null ? entity.getImagenes().stream().map(Imagen::getUrl).toList() : java.util.Collections.emptyList())")

    @Mapping(target = "documentosImportantes",
            expression = "java(entity.getDocumentosImportantes() != null ? entity.getDocumentosImportantes().stream().map(doc -> doc.getRutaArchivo()).toList() : java.util.Collections.emptyList())")

    InmuebleResponse toResponse(Inmueble entity);

    AgenteResponse toAgenteResponse(User agente);
    PropietarioResponse toPropietarioResponse(User propietario);
    AsesorResponse toAsesorResponse(User asesor);


    // Método utilitario para actualizar los campos de una entidad Inmueble existente con los valores de un InmuebleDto.
    void updateEntityFromDto(InmuebleDto dto, @org.mapstruct.MappingTarget com.jsebastian.eden.EdenSys.domain.Inmueble entity);
}
