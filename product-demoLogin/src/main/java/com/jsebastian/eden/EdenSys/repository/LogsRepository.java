package com.jsebastian.eden.EdenSys.repository;

import com.jsebastian.eden.EdenSys.domain.Logs;
import com.jsebastian.eden.EdenSys.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogsRepository extends JpaRepository<Logs, Long> {

    /**
     * Obtiene todos los logs de un usuario
     */
    List<Logs> findByUsuario(User usuario);

    /**
     * Obtiene todos los logs por id de usuario
     */
    List<Logs> findByUsuarioId(Long usuarioId);

}