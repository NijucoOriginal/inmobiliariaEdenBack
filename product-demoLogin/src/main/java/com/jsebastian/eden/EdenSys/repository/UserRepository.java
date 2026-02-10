package com.jsebastian.eden.EdenSys.repository;

import com.jsebastian.eden.EdenSys.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import com.jsebastian.eden.EdenSys.domain.Rol;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su email
     *
     * @param email el email del usuario
     * @return Optional<User> el usuario encontrado o vacío
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario por su cédula
     *
     * @param cedula la cédula del usuario
     * @return Optional<User> el usuario encontrado o vacío
     */
    Optional<User> findByDocumentoIdentidad(String cedula);

    /**
     * Verifica si existe un usuario con el email especificado
     *
     * @param email el email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un usuario con la cédula especificada
     *
     * @param cedula la cédula a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByDocumentoIdentidad(String cedula);

    /**
     * Busca usuarios por rol
     *
     * @param rol el rol a buscar
     * @return List<User> lista de usuarios con ese rol
     */
    List<User> findByRol(Rol rol);

    Optional<User> findByCodigoActivacion(String codigo);

    List<User> findByRolNotIn(List<Rol> rols);

}