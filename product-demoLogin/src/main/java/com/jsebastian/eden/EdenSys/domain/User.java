package com.jsebastian.eden.EdenSys.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class User extends Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String codigoActivacion;

    private LocalDateTime fechaCreacionCodigo;

    // --- NUEVA LISTA DE CONTACTOS ---
    @ManyToMany
    @JoinTable(
            name = "user_contacts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "contact_id")
    )
    private List<User> contactos = new ArrayList<>();
}