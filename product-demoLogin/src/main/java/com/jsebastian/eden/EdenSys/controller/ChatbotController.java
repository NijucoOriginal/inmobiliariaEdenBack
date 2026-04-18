package com.jsebastian.eden.EdenSys.controller;

import com.jsebastian.eden.EdenSys.Dtos.ChatbotResponseDto;
import com.jsebastian.eden.EdenSys.Dtos.InmuebleFiltroDto;
import com.jsebastian.eden.EdenSys.domain.TipoInmueble;
import com.jsebastian.eden.EdenSys.domain.TipoNegocio;
import com.jsebastian.eden.EdenSys.repository.InmuebleRepository;
import com.jsebastian.eden.EdenSys.specifications.InmuebleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final InmuebleRepository inmuebleRepository;

    private String limpiar(String valor) {
        if (valor == null) return null;
        String v = valor.trim();
        if (v.isBlank() || v.equals("=") || v.equals("null")
                || v.equals("undefined") || v.equalsIgnoreCase("empty")) return null; // ← agrega esto
        return v;
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String departamento,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String tipoNegocio,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) Integer habitacionesMin,
            @RequestParam(required = false) Integer banosMin,
            @RequestParam(required = false) Integer parqueaderosMin
    ) {
        ciudad       = limpiar(ciudad);
        departamento = limpiar(departamento);
        tipo         = limpiar(tipo);
        tipoNegocio  = limpiar(tipoNegocio);

        InmuebleFiltroDto filtro = new InmuebleFiltroDto();
        filtro.setCiudad(ciudad);
        filtro.setDepartamento(departamento);

        try {
            filtro.setTipo(tipo != null ? TipoInmueble.valueOf(tipo.toUpperCase()) : null);
        } catch (IllegalArgumentException e) {
            filtro.setTipo(null);
        }

        try {
            filtro.setTipoNegocio(tipoNegocio != null ? TipoNegocio.valueOf(tipoNegocio.toUpperCase()) : null);
        } catch (IllegalArgumentException e) {
            filtro.setTipoNegocio(null);
        }

        filtro.setPrecioMin(precioMin);
        filtro.setPrecioMax(precioMax);
        filtro.setHabitacionesMin(habitacionesMin);
        filtro.setBanosMin(banosMin);
        filtro.setParqueaderosMin(parqueaderosMin);

        var spec  = InmuebleSpecification.conFiltros(filtro);
        long total = inmuebleRepository.count(spec);

        Map<String, Object> filtrosAplicados = new HashMap<>();
        if (ciudad       != null) filtrosAplicados.put("ciudad",         ciudad);
        if (departamento != null) filtrosAplicados.put("departamento",   departamento);
        if (filtro.getTipo()        != null) filtrosAplicados.put("tipo",        filtro.getTipo().name());
        if (filtro.getTipoNegocio() != null) filtrosAplicados.put("tipoNegocio", filtro.getTipoNegocio().name());
        if (precioMin       != null) filtrosAplicados.put("precioMin",       precioMin);
        if (precioMax       != null) filtrosAplicados.put("precioMax",       precioMax);
        if (habitacionesMin != null) filtrosAplicados.put("habitacionesMin", habitacionesMin);
        if (banosMin        != null) filtrosAplicados.put("banosMin",        banosMin);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje",          "Se encontraron " + total + " inmuebles");
        response.put("totalResultados",  total);
        response.put("filtrosAplicados", filtrosAplicados);
        response.put("accion",           "VER_CATALOGO");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/destacados")
    public ResponseEntity<?> destacados() {
        InmuebleFiltroDto filtro = new InmuebleFiltroDto();
        var spec  = InmuebleSpecification.conFiltros(filtro);
        long total = inmuebleRepository.count(spec);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje",          "Se encontraron " + total + " inmuebles disponibles");
        response.put("totalResultados",  total);
        response.put("filtrosAplicados", new HashMap<>());
        response.put("accion",           "VER_CATALOGO");

        return ResponseEntity.ok(response);
    }



    @GetMapping("/inmueble/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        var inmueble = inmuebleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inmueble no encontrado"));

        ChatbotResponseDto response = new ChatbotResponseDto();
        response.setDatos(List.of(inmueble));
        response.setMensaje("Detalle del inmueble");
        response.setRedirectUrl("/propiedades/" + id);

        return ResponseEntity.ok(response);
    }
}