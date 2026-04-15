package com.jsebastian.eden.EdenSys.Dtos;

import com.jsebastian.eden.EdenSys.domain.EstadoInmueble;
import com.jsebastian.eden.EdenSys.domain.TipoInmueble;
import com.jsebastian.eden.EdenSys.domain.TipoNegocio;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InmuebleFiltroDto {
    private String ciudad;
    private String departamento;
    private TipoInmueble tipo;
    private TipoNegocio tipoNegocio;
    private Double precioMin;
    private Double precioMax;
    private Integer habitacionesMin;
    private Integer banosMin;
    private int pagina = 0;
    private int tamano = 9;
    private EstadoInmueble estado;
    private Integer parqueaderosMin;
}
