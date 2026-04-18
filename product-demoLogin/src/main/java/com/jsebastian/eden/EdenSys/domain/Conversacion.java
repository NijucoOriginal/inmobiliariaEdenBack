package com.jsebastian.eden.EdenSys.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Participante 1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario1_id", nullable = false)
    private User usuario1;

    // Participante 2
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario2_id", nullable = false)
    private User usuario2;

    @Column(nullable = false)
    private LocalDateTime creadaEn;

    @Column
    private LocalDateTime ultimoMensajeEn;

    @OneToMany(mappedBy = "conversacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("enviadoEn ASC")
    private List<Mensaje> mensajes = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.creadaEn = LocalDateTime.now();
    }
}