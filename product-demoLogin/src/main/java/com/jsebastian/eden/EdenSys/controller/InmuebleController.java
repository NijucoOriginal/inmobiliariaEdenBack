package com.jsebastian.eden.EdenSys.controller;

import com.jsebastian.eden.EdenSys.Dtos.InmuebleDto;
import com.jsebastian.eden.EdenSys.Dtos.InmueblePatchDto;
import com.jsebastian.eden.EdenSys.Dtos.InmuebleResponse;
import com.jsebastian.eden.EdenSys.domain.User;
import com.jsebastian.eden.EdenSys.exceptions.ValueConflictException;
import com.jsebastian.eden.EdenSys.services.interfaces.InmuebleService;
import com.jsebastian.eden.EdenSys.services.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/inmuebles")
public class InmuebleController {


    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${frontend.local.url}")
    private String frontendLocalUrl;

    private final InmuebleService inmuebleService;

    private final UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> crearInmueble(
            @ModelAttribute InmuebleDto inmuebleDto,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes,
            @RequestPart(value = "documentosImportantes", required = false) List<MultipartFile> documentosImportantes,
            @RequestParam("correoUsuario") String correoUsuario
    ) {
        System.out.println("DTO: " + inmuebleDto);
        System.out.println("Imagenes: " + (imagenes != null ? imagenes.size() : "null"));
        System.out.println("Documentos: " + (documentosImportantes != null ? documentosImportantes.size() : "null"));
        System.out.println("Correo: " + correoUsuario);

        try {
            InmuebleResponse inmuebleResponse = inmuebleService.crearInmueble(inmuebleDto, imagenes, documentosImportantes, correoUsuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(inmuebleResponse);
        } catch (ValueConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
        }
    }


    /*@GetMapping("/{id}")
    public ResponseEntity<?> obtenerInmueble(@PathVariable Long id) {
        try {
            InmuebleResponse response = inmuebleService.obtenerInmueble(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

     */

    @GetMapping("/{email}")
    public ResponseEntity<?> obtenerInmuebles(@PathVariable String email) {
        try {
            List<InmuebleResponse> listaInmueblesUsuario = inmuebleService.buscarInmueblesPorUsuario(email);
            return ResponseEntity.ok(listaInmueblesUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<InmuebleResponse>> obtenerListaDeInmuebles() {
        List<InmuebleResponse> lista = inmuebleService.obtenerListaDeInmuebles();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/agente/{email}")
    public ResponseEntity<?> obtenerInmueblesPorAgente(@PathVariable String email) {
        try {
            List<InmuebleResponse> listaInmueblesAgente = inmuebleService.buscarInmueblesPorAgente(email);
            return ResponseEntity.ok(listaInmueblesAgente);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarInmueble(@PathVariable Long id, @Valid @RequestBody InmuebleDto inmuebleDto) {
        try {
            InmuebleResponse response = inmuebleService.actualizarInmueble(id, inmuebleDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/agente/{estadoTransa}/{id}")
    public ResponseEntity<?> actualizarInmuebleEstadoTransa(@PathVariable String estadoTransa, @PathVariable Long id) {
        try {
            System.out.println("Llega hasta aqui");
            InmuebleResponse inmueble = inmuebleService.actualizarEstadoTransaInmueble(estadoTransa, id);
            return ResponseEntity.ok(inmueble);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchInmueble(@PathVariable Long id, @RequestBody InmueblePatchDto patchDto) {
        try {
            InmuebleResponse response = inmuebleService.patchInmueble(id, patchDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarInmueble(@PathVariable Long id) {
        try {
            inmuebleService.eliminarInmueble(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/agentes/transferir/{id}")
    public ResponseEntity<?> transferirInmueble(@PathVariable Long id,@RequestBody InmuebleDto inmueble) {
        User usuarioNormalucho = userService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));



        return ResponseEntity.ok(usuarioNormalucho);
    }



}
