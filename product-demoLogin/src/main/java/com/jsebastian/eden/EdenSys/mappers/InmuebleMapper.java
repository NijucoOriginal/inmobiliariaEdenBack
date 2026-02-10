package com.jsebastian.eden.EdenSys.mappers;

import com.jsebastian.eden.EdenSys.domain.Inmueble;
import com.jsebastian.eden.EdenSys.Dtos.InmuebleDto;
import com.jsebastian.eden.EdenSys.Dtos.InmuebleResponse;
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
    @Mapping(target = "asesorLegal", expression = "java(entity.getAsesorLegal() != null ? entity.getAsesorLegal().getId() : null)")
    @Mapping(target = "agenteAsociado", expression = "java(entity.getAgenteAsociado() != null ? entity.getAgenteAsociado().getId() : null)")
    @Mapping(target = "propietario", expression = "java(entity.getPropietario() != null ? entity.getPropietario().getId() : null)")
    @Mapping(target = "cantidadParqueaderos", source = "cantidadParqueaderos")
    @Mapping(target = "telefonoContacto", source = "telefonoContacto")
    @Mapping(target = "nombreContacto", source = "nombreContacto")
    @Mapping(target = "correoContacto", source = "correoContacto")
    @Mapping(target = "imagenes", expression = "java(entity.getImagenes().stream().map(Imagen::getUrl).toList())")
    @Mapping(target = "id", source = "id")
    InmuebleResponse toResponse(Inmueble entity);


    // Método utilitario para actualizar los campos de una entidad Inmueble existente con los valores de un InmuebleDto.
    void updateEntityFromDto(InmuebleDto dto, @org.mapstruct.MappingTarget com.jsebastian.eden.EdenSys.domain.Inmueble entity);
}
