package com.jsebastian.eden.EdenSys.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inmueble")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inmueble {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoNegocio tipoNegocio;

    @ManyToOne
    @JoinColumn(name = "agente_asociado_id")
    private User agenteAsociado;

    @OneToMany(mappedBy = "inmueble", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentoImportante> documentosImportantes= new ArrayList<>();;

    @Enumerated(EnumType.STRING)
    private TipoInmueble tipo;

    @Positive
    private double medidas;

    @Positive
    private int habitaciones;

    @Positive
    private int banos;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoInmueble estado;


    @Positive
    private double precio;

    @Enumerated(EnumType.STRING)
    private EstadoTransaccion estadoTransa;


    @OneToMany(mappedBy = "inmueble", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialInmueble> historial= new ArrayList<>();;

    @ManyToOne
    @JoinColumn(name = "asesor_legal_id")
    private User asesorLegal;

    @Positive
    private int cantidadParqueaderos;

    private String telefonoContacto;

    private String nombreContacto;

    private String correoContacto;

    @OneToMany(mappedBy = "inmueble", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Imagen> imagenes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "propietario_id")
    private User propietario;

    @Min(-90)
    @Max(90)
    private double latitud;
    @Min(-180)
    @Max(180)
    private double longitud;
}