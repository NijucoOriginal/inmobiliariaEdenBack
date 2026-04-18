package com.jsebastian.eden.EdenSys.repository;

import com.jsebastian.eden.EdenSys.domain.Conversacion;
import com.jsebastian.eden.EdenSys.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversacionRepository extends JpaRepository<Conversacion, Long> {

    // Buscar conversación entre dos usuarios (en cualquier orden)
    @Query("""
        SELECT c FROM Conversacion c
        WHERE (c.usuario1 = :u1 AND c.usuario2 = :u2)
           OR (c.usuario1 = :u2 AND c.usuario2 = :u1)
    """)
    Optional<Conversacion> findEntreUsuarios(@Param("u1") User u1, @Param("u2") User u2);

    // Todas las conversaciones de un usuario, ordenadas por último mensaje
    @Query("""
        SELECT c FROM Conversacion c
        WHERE c.usuario1 = :usuario OR c.usuario2 = :usuario
        ORDER BY c.ultimoMensajeEn DESC NULLS LAST
    """)
    List<Conversacion> findByUsuario(@Param("usuario") User usuario);
}