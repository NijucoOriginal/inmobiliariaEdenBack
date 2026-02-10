package com.jsebastian.eden.EdenSys.services.interfaces;

import com.jsebastian.eden.EdenSys.Dtos.UserResponse;
import com.jsebastian.eden.EdenSys.Dtos.UsuarioResponse;
import com.jsebastian.eden.EdenSys.domain.User;
import com.jsebastian.eden.EdenSys.Dtos.CrearUsuarioDto;
import com.jsebastian.eden.EdenSys.exceptions.ValueConflictException;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para gestionar operaciones relacionadas con usuarios
 */
public interface UserService {



    /**
     * Guarda un nuevo usuario en la base de datos
     * @param user el usuario a guardar
     * @return el usuario guardado con su ID generado
     */
    User guardarUsuario(User user);

    /**
     * Crea un nuevo usuario usando un DTO (las validaciones se hacen en el DTO al ser mapeadas)
     * @param usuarioDTO el DTO con los datos del usuario a crear
     * @return el usuario creado con su ID generado
     * @throws ValueConflictException si el email ya existe
     */
    UsuarioResponse crearUsuario(CrearUsuarioDto usuarioDTO);// we gonna create a user catching a DTO that are requesting , and return a DTO that are resolving

    /**
     * Busca un usuario por su email
     * @param email el email del usuario a buscar
     * @return Optional<User> el usuario encontrado o vacío si no existe
     */
    Optional<User> buscarPorEmail(String email);

    /**
     * Busca un usuario por su ID
     * @param id el ID del usuario a buscar
     * @return Optional<User> el usuario encontrado o vacío si no existe
     */
    Optional<User> buscarPorId(Long id);

    /**
     * Busca un usuario por su cédula
     * @param cedula la cédula del usuario a buscar
     * @return Optional<User> el usuario encontrado o vacío si no existe
     */
    Optional<User> buscarPorCedula(String cedula);

    /**
     * Obtiene todos los usuarios
     * @return List<User> lista de todos los usuarios
     */
    List<User> obtenerTodosLosUsuarios();

    List<UserResponse> obtenerTodosLosUsuariosHabilitados();

    /**
     * Elimina un usuario por su ID
     * @param id el ID del usuario a eliminar
     */
    void eliminarUsuario(Long id);

    /**
     * Elimina un usuario por su email
     * @param email el email del usuario a eliminar
     */
    void eliminarUsuarioPorEmail(String email);


    Optional<String> desvincularUsuario(String email);

    /**
     * Verifica si existe un usuario con el email especificado
     * @param email el email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existePorEmail(String email);

    boolean usuarioRegistradoPreviamente(String email);

    /**
     * Verifica si existe un usuario con la cédula especificada
     * @param cedula la cédula a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existePorCedula(String cedula);

   Optional<UsuarioResponse>actualizarUsuario(String id, CrearUsuarioDto usuarioDTO)throws ValueConflictException;

    String actualizarUsuarioEmail(String email, CrearUsuarioDto user);

    /**
     * Activa un usuario basado en el código de activación
     * @param codigo el código de activación
     * @return true si la activación fue exitosa, false en caso contrario
     */
    boolean activarUsuario(String codigo);

    /**
     * Valida las credenciales del usuario y genera un token JWT
     * @param email el email del usuario
     * @param contrasena la contraseña del usuario
     * @return el token JWT generado
     * @throws IllegalArgumentException si las credenciales son inválidas
     */
    String validarCredencialesYGenerarToken(String email, String contrasena);

    String generarToken(User usuario);
}
