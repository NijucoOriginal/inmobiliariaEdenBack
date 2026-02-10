package com.jsebastian.eden.EdenSys.Dtos;

import java.util.List;

public record InmueblePatchDto(
    //String departamento,
    String ubicacion,
    String tipoNegocio,
    Long agenteAsociado,
    List<Long> documentosImportantes,
    String tipo,
    String medidas,
    Integer habitaciones,
    Integer banos,
    String descripcion,
    String estado,
    Double precio,
    String estadoTransa,
    //String ciudad,
    String codigoInmueble,
    List<Long> historial,
    Long asesorLegal,
    Integer cantidadParqueaderos,
    String telfonoContacto,
    String nombreContacto,
    String correoContacto,
    List<Long> imagenes
) {}
