package com.jsebastian.eden.EdenSys.Dtos;

import com.jsebastian.eden.EdenSys.domain.*;
import java.util.List;

public record InmuebleResponse(
        double longitud,
        double latitud,
        TipoNegocio tipoNegocio,
        AgenteResponse agenteAsociado,
        AsesorResponse asesorLegal,
        TipoInmueble tipo,
        double medidas,
        int habitaciones,
        int banos,
        String descripcion,
        EstadoInmueble estado,
        double precio,
        EstadoTransaccion estadoTransa,
        int cantidadParqueaderos,
        String telefonoContacto,
        String nombreContacto,
        String correoContacto,
        List<String> imagenes,
        PropietarioResponse propietario,
        long id,
        List<String> documentosImportantes

) {}