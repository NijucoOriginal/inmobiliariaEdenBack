package com.jsebastian.eden.EdenSys.specifications;

import com.jsebastian.eden.EdenSys.Dtos.InmuebleFiltroDto;
import com.jsebastian.eden.EdenSys.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class InmuebleSpecification {

    public static Specification<Inmueble> conFiltros(InmuebleFiltroDto filtro) {
        return porCiudad(filtro.getCiudad())
                .and(porDepartamento(filtro.getDepartamento()))
                .and(porTipo(filtro.getTipo()))
                .and(porTipoNegocio(filtro.getTipoNegocio()))
                .and(porPrecioMin(filtro.getPrecioMin()))
                .and(porPrecioMax(filtro.getPrecioMax()))
                .and(porHabitaciones(filtro.getHabitacionesMin()))
                .and(porBanos(filtro.getBanosMin()))
                .and(soloDisponibles());
    }

    private static Specification<Inmueble> porCiudad(String ciudad) {
        return (root, query, cb) -> ciudad == null || ciudad.isBlank() ? null
                : cb.like(cb.lower(root.get("ciudad")), "%" + ciudad.toLowerCase() + "%");
    }

    private static Specification<Inmueble> porDepartamento(String departamento) {
        return (root, query, cb) -> departamento == null || departamento.isBlank() ? null
                : cb.like(cb.lower(root.get("departamento")), "%" + departamento.toLowerCase() + "%");
    }

    private static Specification<Inmueble> porTipo(TipoInmueble tipo) {
        return (root, query, cb) -> tipo == null ? null
                : cb.equal(root.get("tipo"), tipo);
    }

    private static Specification<Inmueble> porTipoNegocio(TipoNegocio tipoNegocio) {
        return (root, query, cb) -> tipoNegocio == null ? null
                : cb.equal(root.get("tipoNegocio"), tipoNegocio);
    }

    private static Specification<Inmueble> porPrecioMin(Double precioMin) {
        return (root, query, cb) -> precioMin == null ? null
                : cb.greaterThanOrEqualTo(root.get("precio"), precioMin);
    }

    private static Specification<Inmueble> porPrecioMax(Double precioMax) {
        return (root, query, cb) -> precioMax == null ? null
                : cb.lessThanOrEqualTo(root.get("precio"), precioMax);
    }

    private static Specification<Inmueble> porHabitaciones(Integer min) {
        return (root, query, cb) -> min == null ? null
                : cb.greaterThanOrEqualTo(root.get("habitaciones"), min);
    }

    private static Specification<Inmueble> porBanos(Integer min) {
        return (root, query, cb) -> min == null ? null
                : cb.greaterThanOrEqualTo(root.get("banos"), min);
    }

    // soloDisponibles()
    private static Specification<Inmueble> soloDisponibles() {
        return (root, query, cb) -> root.get("estadoTransa").in(
                EstadoTransaccion.PROCESOALQUIER,
                EstadoTransaccion.PROCESOCOMPRA,
                EstadoTransaccion.PROCESOPERMUTACION
        );
    }

    // Estado inmueble
    private static Specification<Inmueble> porEstadoInmueble(EstadoInmueble estado) {
        return (root, query, cb) -> estado == null ? null
                : cb.equal(root.get("estado"), estado);
    }
}

