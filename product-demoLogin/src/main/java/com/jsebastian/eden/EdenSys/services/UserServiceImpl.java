package com.jsebastian.eden.EdenSys.services;

import ch.qos.logback.classic.Logger;
import com.jsebastian.eden.EdenSys.Dtos.*;
import com.jsebastian.eden.EdenSys.domain.Rol;
import com.jsebastian.eden.EdenSys.domain.User;
import com.jsebastian.eden.EdenSys.repository.LogsRepository;
import com.jsebastian.eden.EdenSys.repository.UserRepository;
import com.jsebastian.eden.EdenSys.mappers.UserMapper;
import com.jsebastian.eden.EdenSys.exceptions.ValueConflictException;
// API DE MENSAJES

import com.jsebastian.eden.EdenSys.services.interfaces.LogsService;
import com.jsebastian.eden.EdenSys.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private LogsService logsService;

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
    public UsuarioResponse crearUsuario(CrearUsuarioDto crearUsuarioDto) {

        String emailNormalizado = crearUsuarioDto.email().trim().toLowerCase();

        if (!esContrasenaSegura(crearUsuarioDto.contrasena())) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener mínimo 8 caracteres, " +
                            "al menos una mayúscula, una minúscula, un número y un símbolo."
            );
        }

        try {

            Optional<User> usuarioOpt = userRepository.findByEmail(emailNormalizado);

            //SI EL USUARIO YA EXISTE

            if (usuarioOpt.isPresent()) {

                User usuario = usuarioOpt.get();

                // DESVINCULADO  permitir registro nuevo
                if (usuario.getRol() == Rol.DESVINCULADO) {

                    if (existePorCedula(crearUsuarioDto.documentoIdentidad())) {
                        throw new ValueConflictException(
                                "Ya existe un usuario con esta cédula: "
                                        + crearUsuarioDto.documentoIdentidad()
                        );
                    }

                    userRepository.delete(usuario);
                }

                // PENDIENTE  actualizar datos + contraseña + reenviar código
                else if (usuario.getRol() == Rol.PENDIENTE) {

                    usuario.setNombre(crearUsuarioDto.nombre());
                    usuario.setApellido(crearUsuarioDto.apellido());
                    usuario.setTelefono(crearUsuarioDto.telefono());
                    usuario.setDocumentoIdentidad(crearUsuarioDto.documentoIdentidad());

                    usuario.setContrasena(
                            passwordEncoder.encode(crearUsuarioDto.contrasena())
                    );

                    enviarCodigoEmailActivacion(usuario);
                    userRepository.save(usuario);

                    logsService.registrarLog("Usuario vinculado nuevamente correctamente",usuario.getId());

                    logger.info("Usuario pendiente actualizado y código reenviado: {}", emailNormalizado);

                    return userMapper.toUsuarioResponse(usuario);
                }

                // YA ACTIVO
                else {
                    throw new ValueConflictException(
                            "Ya existe un usuario activo con este email: " + emailNormalizado
                    );
                }
            }

            // USUARIO NUEVO

            if (existePorCedula(crearUsuarioDto.documentoIdentidad())) {
                throw new ValueConflictException(
                        "Ya existe un usuario con esta cédula: "
                                + crearUsuarioDto.documentoIdentidad()
                );
            }

            var nuevoUsuario = userMapper.toEntity(crearUsuarioDto);

            nuevoUsuario.setEmail(emailNormalizado);
            nuevoUsuario.setContrasena(
                    passwordEncoder.encode(crearUsuarioDto.contrasena())
            );
            nuevoUsuario.setRol(Rol.PENDIENTE);

            enviarCodigoEmailActivacion(nuevoUsuario);
            userRepository.save(nuevoUsuario);


            logsService.registrarLog("Usuario creado correctamente, esperando validación",nuevoUsuario.getId());
            logger.info("Nuevo usuario creado con email: {}", emailNormalizado);

            return userMapper.toUsuarioResponse(nuevoUsuario);

        } catch (Exception e) {

            logger.error("Error al crear usuario con email: {}", emailNormalizado, e);

            throw new RuntimeException(
                    "Error al crear el usuario: " + e.getMessage(), e
            );
        }
    }

    /**
     * Metodo privado para enviar el código de activación por email usando SendGrid
     * @param nuevoUsuario
     */
    private void enviarCodigoEmailActivacion(User nuevoUsuario) {
        // Generar código numérico de 6 caracteres
        java.security.SecureRandom random = new java.security.SecureRandom();
        String codigo = String.format("%06d", random.nextInt(1_000_000));
        nuevoUsuario.setFechaCreacionCodigo(LocalDateTime.now());

        // Guardar código en texto plano (está bien para tu caso)
        nuevoUsuario.setCodigoActivacion(codigo);
        String to = nuevoUsuario.getEmail();
        String subject = "Código de activación de tu cuenta";
        String content = "Hola, " + nuevoUsuario.getNombre() + ". Tu código de activación es: " + codigo;

        enviarCorreo(to, subject, content);
    }

    public void enviarCorreo(String destino, String asunto, String mensaje) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(destino);
        mail.setSubject(asunto);
        mail.setText(mensaje);
        mail.setFrom(fromEmail);

        System.out.println(mail.getFrom());


        mailSender.send(mail);
    }


    public void procesarContacto(ContactoDto contacto) {

        String mensajeFinal =
                "Nuevo mensaje de contacto\n\n" +
                        "Nombre: " + contacto.nombre() + "\n" +
                        "Teléfono: " + contacto.telefono() + "\n" +
                        "Correo: " + contacto.correo() + "\n\n" +
                        "Mensaje:\n" + contacto.mensaje();

       enviarCorreo(
                "inmobiliariaedenco@gmail.com",
                "Nuevo contacto - " + contacto.asunto(),
                mensajeFinal
        );

        enviarCorreo(contacto.correo(),"Recibimos su correo","Pronto te responderemos tus inquietudes");
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
        Optional<User> usuario=userRepository.findById(id);
        userRepository.deleteById(id);
        logsService.registrarLog("Usuario eliminado correctamente",usuario.get().getId());
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
            logsService.registrarLog("Usuario eliminado correctamente",usuario.get().getId());
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
            logsService.registrarLog("Usuario desvinculado correctamente",usuarioExistente.getId());
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
        logsService.registrarLog("Usuario actualizado correctamente",usuarioExistente.getId());
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

            if (usuario.getFechaCreacionCodigo().isBefore(LocalDateTime.now().minusMinutes(10))) { //10 minutos
                logsService.registrarLog("El codigo del usuario ha expirado correctamente",usuario.getId());
                logger.warn("Código expirado para usuario {}", usuario.getEmail());
                usuario.setRol(Rol.PENDIENTE);
                usuario.setCodigoActivacion(null);
                usuario.setFechaCreacionCodigo(null);
                userRepository.save(usuario);
                return false;
            }


            // Verificar si el usuario ya está activado
            if (usuario.getRol() != Rol.PENDIENTE) {
                logger.warn("El usuario con email {} ya está activado.", usuario.getEmail());
                return false;
            }

            // Actualizar el rol del usuario a CLIENTE
            usuario.setRol(Rol.CLIENTE);
            logsService.registrarLog("El usuario ha pasado a ser un cliente de la aplicación",usuario.getId());
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
                    logsService.registrarLog("Inicio de sesion exitoso ",usuario.getId());
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

    private boolean esContrasenaSegura(String contrasena) {
        if (contrasena == null) return false;

        String patron = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$";

        return contrasena.matches(patron);
    }
    public void enviarCodigoRecuperacionContrasena(SolicitarRecuperacionDto solicitarRecuperacionDto) {

        String emailNormalizado =solicitarRecuperacionDto.email().trim().toLowerCase();

        Optional<User> usuarioOpt = userRepository.findByEmail(emailNormalizado);

        // No revelar si el usuario existe (práctica profesional)
        if (usuarioOpt.isEmpty()) {
            logger.warn("Solicitud de recuperación para email inexistente: {}", emailNormalizado);
            return;
        }

        User usuario = usuarioOpt.get();

        if (usuario.getRol() == Rol.DESVINCULADO) {
            logger.warn("Intento de recuperación para usuario desvinculado: {}", emailNormalizado);
            return;
        }

        // Generar código seguro
        java.security.SecureRandom random = new java.security.SecureRandom();
        String codigo = String.format("%06d", random.nextInt(1_000_000));

        usuario.setCodigoActivacion(codigo);
        usuario.setFechaCreacionCodigo(LocalDateTime.now());

        userRepository.save(usuario);

        String subject = "Recuperación de contraseña";
        String mensaje = "Hola " + usuario.getNombre() +
                ", tu código para restablecer la contraseña es: " + codigo +
                "\nEste código expira en 10 minutos.";

        enviarCorreo(usuario.getEmail(), subject, mensaje);

        logger.info("Código de recuperación enviado a {}", emailNormalizado);
    }

    public boolean cambiarContrasenaConCodigo(CambiarContrasenaDto dto) {

        String emailNormalizado = dto.email().trim().toLowerCase();

        Optional<User> usuarioOpt = userRepository.findByEmail(emailNormalizado);

        if (usuarioOpt.isEmpty()) {
            logger.warn("Intento de cambio de contraseña con email inexistente: {}", emailNormalizado);
            return false;
        }

        User usuario = usuarioOpt.get();

        // Validar código
        if (usuario.getCodigoActivacion() == null ||
                !usuario.getCodigoActivacion().equals(dto.codigo())) {

            logger.warn("Código incorrecto para {}", emailNormalizado);
            return false;
        }

        // ⏱ Validar expiración
        if (usuario.getFechaCreacionCodigo() == null ||
                usuario.getFechaCreacionCodigo().isBefore(
                        LocalDateTime.now().minusMinutes(10))) {

            logger.warn("Código expirado para {}", emailNormalizado);

            usuario.setCodigoActivacion(null);
            usuario.setFechaCreacionCodigo(null);
            userRepository.save(usuario);

            return false;
        }

        // Validar seguridad de contraseña
        if (!esContrasenaSegura(dto.nuevaContrasena())) {
            throw new IllegalArgumentException(
                    "La contraseña no cumple los requisitos de seguridad,debe contener minimo 8 caracteres,una minuscula,mayuscula y un signo"
            );
        }

        // Encriptar nueva contraseña
        usuario.setContrasena(passwordEncoder.encode(dto.nuevaContrasena()));

        // Invalidar código
        usuario.setCodigoActivacion(null);
        usuario.setFechaCreacionCodigo(null);

        userRepository.save(usuario);

        logsService.registrarLog("Contraseña del usuario actualizada correctamente",usuario.getId());
        logger.info("Contraseña actualizada correctamente para {}", emailNormalizado);

        return true;
    }
    @Override
    public String generarToken(User usuario) {
        return jwtService.generateToken(usuario);
    }
    
}
