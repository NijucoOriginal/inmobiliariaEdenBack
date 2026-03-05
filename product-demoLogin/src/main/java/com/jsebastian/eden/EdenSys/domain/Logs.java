package com.jsebastian.eden.EdenSys.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Logs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaDeEmision;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private User usuario;

    private String descripcion;

}