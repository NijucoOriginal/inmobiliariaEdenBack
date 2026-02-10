package com.jsebastian.eden.EdenSys.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.File;
import java.time.LocalDateTime;

@Entity
@Table(name = "documento_importante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoImportante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @PastOrPresent
    private LocalDateTime fechaExpedicion;
    private String nombreDocumento;
    
    @ManyToOne
    @JoinColumn(name = "inmueble_id")
    private Inmueble inmueble;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private User cliente;

    private String rutaArchivo;
}