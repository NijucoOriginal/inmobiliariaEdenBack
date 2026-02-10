package com.jsebastian.eden.EdenSys.services;

import ch.qos.logback.classic.Logger;
import com.jsebastian.eden.EdenSys.Dtos.UserResponse;
import com.jsebastian.eden.EdenSys.Dtos.UsuarioResponse;
import com.jsebastian.eden.EdenSys.domain.Rol;
import com.jsebastian.eden.EdenSys.domain.User;
import com.jsebastian.eden.EdenSys.repository.UserRepository;
import com.jsebastian.eden.EdenSys.Dtos.CrearUsuarioDto;
import com.jsebastian.eden.EdenSys.mappers.UserMapper;
import com.jsebastian.eden.EdenSys.exceptions.ValueConflictException;
// API DE MENSAJES

import com.jsebastian.eden.EdenSys.services.interfaces.UserService;
import com.sendgrid.*;

import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Implementación del servicio para gestionar operaciones relacionadas con usuarios
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SendGrid sendGrid;

    @Value("${sendgrid.from.email}")
    private String fromEmail;
    @Value("${sendgrid.from.name}")
    private String fromName;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(UserServiceImpl.class);

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    /**
     * Guarda un nuevo usuario en la base de datos
     * @param user el usuario a guardar
     * @return el usuario guardado con su ID generado
     */
    @Override
    public User guardarUsuario(User user) {
        return userRepository.save(user);
    }


    /**
     * Crea un nuevo usuario usando un DTO (las validaciones se hacen en el controlador)
     * @param crearUsuarioDto el DTO con los datos del usuario a crear
     * @return el usuario creado con su ID generado
     * @throws ValueConflictException si el email ya existe
     */
    @Override
    public UsuarioResponse crearUsuario(CrearUsuarioDto crearUsuarioDto)  {

        String emailNormalizado = crearUsuarioDto.email().trim().toLowerCase();
        /*if (existePorEmail(emailNormalizado))
        {
            throw new ValueConflictException("Ya existe un usuario con este email: " + emailNormalizado);
        }

         */

        if(usuarioRegistradoPreviamente(emailNormalizado))
        {
            throw new ValueConflictException("Ya existe un usuario con este email: " + emailNormalizado);
        }

        try {
            /*logica para la validación de cuentas por activación
            Crear la entidad User a partir del DTO para asignar una creación temporal mientras se activa la cuenta*/

            var nuevoUsuario = userMapper.toEntity(crearUsuarioDto);
            nuevoUsuario.setContrasena(passwordEncoder.encode(crearUsuarioDto.contrasena()));
            nuevoUsuario.setRol(Rol.PENDIENTE);

            enviarCodigoEmailActivacion(nuevoUsuario);
            userRepository.save(nuevoUsuario);
            return userMapper.toUsuarioResponse(nuevoUsuario);
        } catch (Exception e) {
            logger.error("Error al crear usuario con email: {}", emailNormalizado, e);
            throw new RuntimeException("Error al crear el usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Metodo privado para enviar el código de activación por email usando SendGrid
     * @param nuevoUsuario
     */
    private void enviarCodigoEmailActivacion(User nuevoUsuario) {
        // Generar código alfanumérico de 6 caracteres
        String codigo = java.util.UUID.randomUUID().toString().replaceAll("[^A-Za-z0-9]", "").substring(0, 6);
        nuevoUsuario.setCodigoActivacion(codigo);
        String to = nuevoUsuario.getEmail();
        String subject = "Código de activación de tu cuenta";
        String content = "Hola, " + nuevoUsuario.getNombre() + ". Tu código de activación es: " + codigo;

        Email from = new Email(fromEmail, fromName);
        Email toEmail = new Email(to);
        com.sendgrid.helpers.mail.Mail mail = new com.sendgrid.helpers.mail.Mail(from, subject, toEmail, new com.sendgrid.helpers.mail.objects.Content("text/plain", content));

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sendGrid.api(request);
            logger.info("Correo de activación enviado a: {}", to);
        } catch (Exception ex) {
            logger.error("Error enviando correo de activación: {}", ex.getMessage());
        }
    }


    /**
     * Busca un usuario por su email
     * @param email el email del usuario a buscar
     * @return Optional<User> el usuario encontrado o vacío si no existe
     */
    @Override
    public Optional<User> buscarPorEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Busca un usuario por su ID
     * @param id el ID del usuario a buscar
     * @return Optional<User> el usuario encontrado o vacío si no existe
     */
    @Override
    public Optional<User> buscarPorId(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Busca un usuario por su cédula
     * @param cedula la cédula del usuario a buscar
     * @return Optional<User> el usuario encontrado o vacío si no existe
     */
    @Override
    public Optional<User> buscarPorCedula(String cedula) {
        return userRepository.findByDocumentoIdentidad(cedula);
    }

    /**
     * Obtiene todos los usuarios
     * @return List<User> lista de todos los usuarios
     */
    @Override
    public List<User> obtenerTodosLosUsuarios() {
        return userRepository.findAll();
    }

    @Override
    public List<UserResponse> obtenerTodosLosUsuariosHabilitados() {
        List<Rol> rolesInahabilitados=new ArrayList<>();
        rolesInahabilitados.add(Rol.PENDIENTE);
        rolesInahabilitados.add(Rol.DESVINCULADO);
        rolesInahabilitados.add(Rol.ASESOR_LEGAL);
        rolesInahabilitados.add(Rol.AGENTE);

        List<User> usuarios=userRepository.findByRolNotIn(rolesInahabilitados);


        return usuarios.stream().map(userMapper::toUserResponse).toList();
    }


    /**
     * Elimina un usuario por su ID
     * @param id el ID del usuario a eliminar
     */
    @Override
    public void eliminarUsuario(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Elimina un usuario por su email
     * @param email el email del usuario a eliminar
     */
    @Override
    public void eliminarUsuarioPorEmail(String email) {
        Optional<User> usuario = userRepository.findByEmail(email);
        if (usuario.isPresent()) {
            userRepository.delete(usuario.get());
        }
    }

    @Override
    public Optional<String> desvincularUsuario(String email) {
        return userRepository.findByEmail(email).map(usuarioExistente -> {

            if(usuarioExistente.getRol().equals(Rol.DESVINCULADO))
            {
                return "El usuario ya se encuentra desvinculado";
            }
            usuarioExistente.setRol(Rol.DESVINCULADO);

            userRepository.save(usuarioExistente);
            return "Usuario eliminado correctamente";
        });
    }

    /**
     * Verifica si existe un usuario con el email especificado
     * @param email el email a verificar
     * @return true si existe, false en caso contrario
     */
    @Override
    public boolean existePorEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean usuarioRegistradoPreviamente(String email) {
        Optional<User> usuario = userRepository.findByEmail(email);
        if(usuario.isPresent())
        {
            if(usuario.get().getRol().equals(Rol.DESVINCULADO))
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }


    /**
     * Verifica si existe un usuario con la cédula especificada
     * @param cedula la cédula a verificar
     * @return true si existe, false en caso contrario
     */
    @Override
    public boolean existePorCedula(String cedula) {
        return userRepository.existsByDocumentoIdentidad(cedula);
    }

    /**
     * Actualiza un usuario existente
     * @param user el usuario con los datos actualizados
     * @return el usuario actualizado
     */
    @Override
    public Optional<UsuarioResponse> actualizarUsuario(String id,CrearUsuarioDto user) {
        return Optional.empty();
    }

    /*@Override
    public Optional<String> actualizarUsuarioEmail(String email, CrearUsuarioDto user) {
        return userRepository.findByEmail(email).map(usuarioExistente -> {

            if(!user.nombre().equals(usuarioExistente.getNombre()))
            {
                usuarioExistente.setNombre(user.nombre());
            }
            if (!user.apellido().equals(usuarioExistente.getApellido()))
            {
                usuarioExistente.setApellido(user.apellido());
            }
            if(!user.telefono().equals(usuarioExistente.getTelefono()))
            {
                usuarioExistente.setTelefono(user.telefono());
            }
            if(!user.documentoIdentidad().equals(usuarioExistente.getDocumentoIdentidad()))
            {
                usuarioExistente.setDocumentoIdentidad(user.documentoIdentidad());
            }
            userRepository.save(usuarioExistente);
            return generarToken(userRepository.save(usuarioExistente));
        });
    }

     */

    @Override
    public String actualizarUsuarioEmail(String email, CrearUsuarioDto user) {
        var usuarioExistente = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el email: " + email));

        if (!user.nombre().equals(usuarioExistente.getNombre())) {
            usuarioExistente.setNombre(user.nombre());
        }
        if (!user.apellido().equals(usuarioExistente.getApellido())) {
            usuarioExistente.setApellido(user.apellido());
        }
        if (!user.telefono().equals(usuarioExistente.getTelefono())) {
            usuarioExistente.setTelefono(user.telefono());
        }
        if (!user.documentoIdentidad().equals(usuarioExistente.getDocumentoIdentidad())) {
            usuarioExistente.setDocumentoIdentidad(user.documentoIdentidad());
        }

        var usuarioActualizado = userRepository.save(usuarioExistente);
        return generarToken(usuarioActualizado);
    }


    /**
     * Activa un usuario basado en el código de activación
     * @param codigo el código de activación
     * @return true si la activación fue exitosa, false en caso contrario
     */
    @Override
    public boolean activarUsuario(String codigo) {
        Optional<User> usuarioOptional = userRepository.findByCodigoActivacion(codigo);

        if (usuarioOptional.isPresent()) {
            User usuario = usuarioOptional.get();

            // Verificar si el usuario ya está activado
            if (usuario.getRol() != Rol.PENDIENTE) {
                logger.warn("El usuario con email {} ya está activado.", usuario.getEmail());
                return false;
            }

            // Actualizar el rol del usuario a CLIENTE
            usuario.setRol(Rol.CLIENTE);
            usuario.setCodigoActivacion(null); // Eliminar el código de activación
            userRepository.save(usuario);

            logger.info("Usuario con email {} activado exitosamente.", usuario.getEmail());
            return true;
        } else {
            logger.warn("Código de activación inválido: {}", codigo);
            return false;
        }
    }

    /**
     * Valida las credenciales del usuario y genera un token JWT
     * @param email el email del usuario
     * @param contrasena la contraseña del usuario
     * @return el token JWT generado
     * @throws IllegalArgumentException si las credenciales son inválidas
     */
    @Override
    public String validarCredencialesYGenerarToken(String email, String contrasena) {
        Optional<User> usuarioOptional = userRepository.findByEmail(email);

        System.out.println("Validando credenciales para el email: " + email);
        if (usuarioOptional.isPresent()) {
            User usuario = usuarioOptional.get();
            System.out.println("Usuario encontrado: " + usuario.getEmail());
            if(!usuario.getRol().equals(Rol.DESVINCULADO))
            {
                // Verificar la contraseña
                if (passwordEncoder.matches(contrasena, usuario.getContrasena())) {
                    // Generar el token JWT
                    System.out.println("Contraseña válida, token generado.");
                    return generarToken(usuario);
                } else {
                    throw new IllegalArgumentException("Contraseña incorrecta.");
                }
            }
            else
            {
                System.out.print("El usuario ya se encuentra desvinculado");
                throw new ValueConflictException("El usuario ya se encuentra desvinculado");
            }
        }
        else
        {
            throw new IllegalArgumentException("Usuario no encontrado con el email proporcionado.");
        }
    }

    @Override
    public String generarToken(User usuario) {
        return jwtService.generateToken(usuario);
    }
    
}
