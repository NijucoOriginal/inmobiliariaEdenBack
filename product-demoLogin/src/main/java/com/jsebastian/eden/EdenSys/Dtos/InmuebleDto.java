package com.jsebastian.eden.EdenSys.Dtos;

import com.jsebastian.eden.EdenSys.domain.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class InmuebleDto {
    private double longitud;
    private double latitud;
    private TipoNegocio tipoNegocio;
    private TipoInmueble tipo;
    private double medidas;
    private int habitaciones;
    private int banos;
    private String descripcion;
    private EstadoInmueble estado;
    private double precio;
    private int cantidadParqueaderos;
    private String telefonoContacto;
    private String nombreContacto;
    private String correoContacto;
}

