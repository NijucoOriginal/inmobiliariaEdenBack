package com.jsebastian.eden.EdenSys.repository;

import com.jsebastian.eden.EdenSys.domain.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    // Mensajes de una conversación ordenados cronológicamente
    List<Mensaje> findByConversacionIdOrderByEnviadoEnAsc(Long conversacionId);

    // Marcar todos los mensajes de una conversación como leídos para un receptor
    @Modifying
    @Transactional
    @Query("""
        UPDATE Mensaje m SET m.leido = true
        WHERE m.conversacion.id = :conversacionId
          AND m.receptor.id = :receptorId
          AND m.leido = false
    """)
    void marcarComoLeidos(@Param("conversacionId") Long conversacionId,
                          @Param("receptorId") Long receptorId);

    // Contar mensajes no leídos para un usuario
    @Query("""
        SELECT COUNT(m) FROM Mensaje m
        WHERE m.receptor.id = :userId AND m.leido = false
    """)
    long contarNoLeidos(@Param("userId") Long userId);
}
