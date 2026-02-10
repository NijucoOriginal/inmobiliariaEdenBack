package com.jsebastian.eden.EdenSys.repository;

import com.jsebastian.eden.EdenSys.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InmuebleRepository extends JpaRepository<Inmueble, Long> {

    /**
     * Busca un inmueble por su código único
     */
    //Optional<Inmueble> findByCodigoInmueble(int codigoInmueble);

    /**
     * Verifica si existe un inmueble con el código especificado
     */
    //boolean existsByCodigoInmueble(int codigoInmueble);

    /**
     * Busca inmuebles por ciudad
     */
    //List<Inmueble> findByCiudad(String ciudad);

    /**
     * Busca inmuebles por tipo de negocio
     */
    List<Inmueble> findByTipoNegocio(TipoNegocio tipoNegocio);

    /**
     * Busca inmuebles por estado
     */
    List<Inmueble> findByEstado(EstadoInmueble estado);

    /**
     * Busca inmuebles por agente asociado
     */
    List<Inmueble> findByAgenteAsociado(User agenteAsociado);

    List<Inmueble> findByAsesorLegal(User asesorLegal);

    /**
     * Busca inmuebles por tipo
     */
    List<Inmueble> findByTipo(TipoInmueble tipo);

    /**
     * Busca inmuebles en un rango de precio
     */
    List<Inmueble> findByPrecioBetween(double min, double max);

    Optional<Inmueble> findInmuebleByPropietario(User propietario);

    Optional<Inmueble> findInmuebleByAgenteAsociado(User agenteAsociado);

    List<Inmueble> findAllByPropietario(User propietario);

    List<Inmueble> findByEstadoTransaNotIn(List<EstadoTransaccion> estados);

    List<Inmueble> findInmueblesByAgenteAsociado(User agenteAsociado);

    Inmueble findInmuebleById(Long id);
}

