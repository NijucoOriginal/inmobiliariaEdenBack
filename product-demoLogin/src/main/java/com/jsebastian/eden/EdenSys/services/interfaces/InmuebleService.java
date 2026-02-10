package com.jsebastian.eden.EdenSys.services.interfaces;

import com.jsebastian.eden.EdenSys.Dtos.InmuebleDto;
import com.jsebastian.eden.EdenSys.Dtos.InmueblePatchDto;
import com.jsebastian.eden.EdenSys.Dtos.InmuebleResponse;
import com.jsebastian.eden.EdenSys.domain.Inmueble;
import com.jsebastian.eden.EdenSys.domain.User;
import com.jsebastian.eden.EdenSys.exceptions.ValueConflictException;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InmuebleService {
    InmuebleResponse crearInmueble(InmuebleDto inmuebleDto) throws ValueConflictException;

    User buscarAgenteConMenorCarga();

    User buscarAsesorConMenorCarga();

    void eliminarInmueble(Long id);
    InmuebleResponse actualizarInmueble(Long id, InmuebleDto inmuebleDto);



    InmuebleResponse actualizarEstadoTransaInmueble(String estadoTransa, Long id);

    InmuebleResponse patchInmueble(Long id, InmueblePatchDto patchDto);
    InmuebleResponse obtenerInmueble(Long id);

    List<InmuebleResponse> buscarInmueblesPorUsuario(String propietarioEmail);

    List<InmuebleResponse> buscarInmueblesPorAgente(String emailAgente);

    InmuebleResponse buscarInmueblePorAgente(String emailAgente);

    Inmueble buscarInmueblePorAgenteSinResponse(String emailAgente);

    List<InmuebleResponse> obtenerListaDeInmuebles();

    InmuebleResponse crearInmueble(@Valid InmuebleDto inmuebleDto, List<MultipartFile> imagenes, List<MultipartFile> documentosImportantes,String correoUsuario) ;
}
