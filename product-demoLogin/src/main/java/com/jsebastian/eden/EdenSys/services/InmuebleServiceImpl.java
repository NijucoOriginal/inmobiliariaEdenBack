package com.jsebastian.eden.EdenSys.services;

import com.jsebastian.eden.EdenSys.Dtos.InmuebleDto;
import com.jsebastian.eden.EdenSys.Dtos.InmueblePatchDto;
import com.jsebastian.eden.EdenSys.Dtos.InmuebleResponse;
import com.jsebastian.eden.EdenSys.domain.*;
import com.jsebastian.eden.EdenSys.exceptions.ResourceNotFoundException;
import com.jsebastian.eden.EdenSys.mappers.InmuebleMapper;
import com.jsebastian.eden.EdenSys.repository.*;
import com.jsebastian.eden.EdenSys.services.interfaces.ImagenService;
import com.jsebastian.eden.EdenSys.services.interfaces.InmuebleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InmuebleServiceImpl implements InmuebleService {

    @Autowired
    private  InmuebleRepository inmuebleRepository;
    @Autowired
    private  InmuebleMapper inmuebleMapper;

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ImagenService imagenService;


    @Override
    public InmuebleResponse crearInmueble(InmuebleDto inmuebleDto,
                                          List<MultipartFile> imagenes,
                                          List<MultipartFile> documentosImportantes,
                                          String correoUsuario) {
        try
        {
            User usuarioPropietario = userRepository.findByEmail(correoUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Inmueble nuevoInmueble = inmuebleMapper.toEntity(inmuebleDto);
            nuevoInmueble.setPropietario(usuarioPropietario);
            nuevoInmueble.setImagenes(new ArrayList<>());
            nuevoInmueble.setDocumentosImportantes(new ArrayList<>());
            nuevoInmueble.setHistorial(new ArrayList<>());

            /*User agente=new User();
            agente.setEmail("diegonicolaspenarincon@gmail.com");
            agente.setRol(Rol.AGENTE);
            String contrasena="password123";
            agente.setContrasena(passwordEncoder.encode(contrasena));
            agente.setNombre("Diego Penar");
            agente.setApellido("Rincon");
            agente.setTelefono("1234567890");
            agente.setCodigoActivacion(null);
            agente.setDocumentoIdentidad("0326479618668");
            userRepository.save(agente);



            User asesor=new User();
            asesor.setEmail("hola@gmail.com");
            asesor.setRol(Rol.ASESOR_LEGAL);
            asesor.setContrasena(passwordEncoder.encode(contrasena));
            asesor.setNombre("Diego Rinconp");
            asesor.setApellido("Penar");
            asesor.setTelefono("123456789011");
            asesor.setCodigoActivacion(null);
            asesor.setDocumentoIdentidad("55554444777");
            userRepository.save(asesor);

             */


            User agenteMenorCarga = buscarAgenteConMenorCarga();
            User asesorMenorCarga = buscarAsesorConMenorCarga();

            nuevoInmueble.setEstadoTransa(EstadoTransaccion.PENDIENTE);
            nuevoInmueble.setAgenteAsociado(agenteMenorCarga);
            nuevoInmueble.setAsesorLegal(asesorMenorCarga);


            // 4️⃣ Guardar las imágenes si existen
            if (imagenes != null && !imagenes.isEmpty())
             {
                for (MultipartFile imagen : imagenes)
                {
                    try
                    {
                        String ruta = imagenService.subirImagen(imagen);
                        Imagen img = new Imagen();
                        img.setUrl(ruta);
                        img.setInmueble(nuevoInmueble);
                        nuevoInmueble.getImagenes().add(img);
                        System.out.println("Imagen agregada: " + ruta);
                    }
                    catch (Exception ex)
                    {
                        System.err.println("Error al guardar imagen: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }

            // 5️⃣ Guardar los documentos importantes si existen
            if (documentosImportantes != null && !documentosImportantes.isEmpty())
            {
                for (MultipartFile doc : documentosImportantes)
                 {
                     try
                     {
                         String ruta = guardarArchivo(doc, "src/main/resources/documentosImportantes", nuevoInmueble.getCorreoContacto());
                         DocumentoImportante documento = new DocumentoImportante();
                         documento.setRutaArchivo(ruta);
                         documento.setNombreDocumento(doc.getOriginalFilename());
                         documento.setFechaExpedicion(LocalDateTime.now());
                         documento.setInmueble(nuevoInmueble);
                         documento.setCliente(usuarioPropietario);
                         nuevoInmueble.getDocumentosImportantes().add(documento);
                     }
                     catch (Exception ex) {
                            System.err.println("Error al guardar documento importante: " + ex.getMessage());
                            ex.printStackTrace();
                     }

                }
            }

            HistorialInmueble historial=new HistorialInmueble();
            historial.setInmueble(nuevoInmueble);
            historial.setPropietario(usuarioPropietario);
            historial.setFechaInicio(LocalDateTime.now());
            historial.setTipoNegocio(nuevoInmueble.getTipoNegocio());
            historial.setPrecio(nuevoInmueble.getPrecio());
            historial.setDescripcion(nuevoInmueble.getDescripcion());

            nuevoInmueble.getHistorial().add(historial);
            System.out.println( nuevoInmueble.getHistorial().getFirst().getFechaInicio());
            System.out.println( nuevoInmueble.getHistorial().getFirst().getFechaFin());
            System.out.println(nuevoInmueble.getImagenes().getFirst().getUrl());


            nuevoInmueble = inmuebleRepository.save(nuevoInmueble);

            return inmuebleMapper.toResponse(nuevoInmueble);

        }
        catch (Exception e)
        {
            throw new RuntimeException("Error al crear inmueble: " + e.getMessage(), e);
        }
    }

    private String guardarArchivo(MultipartFile archivo, String carpetaDestino,String correoUsuario) throws IOException {
        // Crear carpeta si no existe
        Path carpeta = Paths.get(carpetaDestino);
        if (!Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
        }

        // Generar nombre único
        String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename()+"_"+correoUsuario;
        Path rutaArchivo = carpeta.resolve(nombreArchivo);

        // Guardar físicamente
        Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

        return rutaArchivo.toString(); // O devolver una ruta relativa si prefieres
    }


    @Override
    public InmuebleResponse crearInmueble(InmuebleDto inmuebleDto) {
        try{
            var nuevoInmueble = inmuebleMapper.toEntity(inmuebleDto);
            User agenteMenorCarga = buscarAgenteConMenorCarga();
            nuevoInmueble.setEstadoTransa(EstadoTransaccion.PENDIENTE);
            nuevoInmueble.setAgenteAsociado(agenteMenorCarga);
            nuevoInmueble.setAsesorLegal(agenteMenorCarga);
            inmuebleRepository.save(nuevoInmueble);
            return inmuebleMapper.toResponse(nuevoInmueble);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User buscarAgenteConMenorCarga() {
        int menor=0;
        User usuarioMenorCarga=null;
        List<User> users=userRepository.findByRol(Rol.AGENTE);
        for(int i=0;i<users.size();i++)
        {
            User user=users.get(i);
            if(i==0)
            {
                List<Inmueble> inmueblesAgente=inmuebleRepository.findByAgenteAsociado(user);
                menor=inmueblesAgente.size();
                usuarioMenorCarga=user;
            }
            else
            {
                List<Inmueble> inmueblesAgente=inmuebleRepository.findByAgenteAsociado(user);
                if(inmueblesAgente.size()<menor)
                {
                    menor=inmueblesAgente.size();
                    usuarioMenorCarga=user;
                }
            }
        }
        return usuarioMenorCarga;
    }

    @Override
    public User buscarAsesorConMenorCarga() {
        int menor=0;
        User usuarioMenorCarga=null;
        List<User> users=userRepository.findByRol(Rol.ASESOR_LEGAL);
        for(int i=0;i<users.size();i++)
        {
            User user=users.get(i);
            if(i==0)
            {
                List<Inmueble> inmueblesAsesor=inmuebleRepository.findByAsesorLegal(user);
                menor=inmueblesAsesor.size();
                usuarioMenorCarga=user;
            }
            else
            {
                List<Inmueble> inmueblesAgente=inmuebleRepository.findByAsesorLegal(user);
                if(inmueblesAgente.size()<menor)
                {
                    menor=inmueblesAgente.size();
                    usuarioMenorCarga=user;
                }
            }
        }
        return usuarioMenorCarga;
    }



    @Override
    public void eliminarInmueble(Long id) {
        try {
            var inmueble = inmuebleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Inmueble no encontrado con id: " + id));
            inmuebleRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el inmueble: " + e.getMessage(), e);
        }
    }

    @Override
    public InmuebleResponse actualizarInmueble(Long id, InmuebleDto inmuebleDto) {
        try {
            var inmuebleExistente = inmuebleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Inmueble no encontrado con id: " + id));
            inmuebleMapper.updateEntityFromDto(inmuebleDto, inmuebleExistente);
            inmuebleRepository.save(inmuebleExistente);
            return inmuebleMapper.toResponse(inmuebleExistente);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el inmueble: " + e.getMessage(), e);
        }
    }

    @Override
    public InmuebleResponse actualizarEstadoTransaInmueble(String estadoTransa, Long id) {
        try
        {
            Inmueble inmuebleMandar=inmuebleRepository.findInmuebleById(id);

            inmuebleMandar.setEstadoTransa(EstadoTransaccion.valueOf(estadoTransa));

            System.out.println(inmuebleMandar.getEstadoTransa());

            inmuebleRepository.save(inmuebleMandar);

            return inmuebleMapper.toResponse(inmuebleMandar);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public InmuebleResponse patchInmueble(Long id, InmueblePatchDto patchDto) {
        /*
        try {
            var inmueble = inmuebleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Inmueble no encontrado con id: " + id));
            if (patchDto.departamento() != null) inmueble.setDepartamento(patchDto.departamento());
            // Ubicacion: no es un enum, es un objeto. No se puede actualizar por PATCH con un String.
            if (patchDto.tipoNegocio() != null) {
                try {
                    inmueble.setTipoNegocio(TipoNegocio.valueOf(patchDto.tipoNegocio()));
                } catch (Exception e) {
                    throw new RuntimeException("Tipo de negocio inválido: " + patchDto.tipoNegocio());
                }
            }
            if (patchDto.tipo() != null) {
                try {
                    inmueble.setTipo(TipoInmueble.valueOf(patchDto.tipo()));
                } catch (Exception e) {
                    throw new RuntimeException("Tipo de inmueble inválido: " + patchDto.tipo());
                }
            }
            if (patchDto.medidas() != null) inmueble.setMedidas(Double.parseDouble(patchDto.medidas()));
            if (patchDto.habitaciones() != null) inmueble.setHabitaciones(patchDto.habitaciones());
            if (patchDto.banos() != null) inmueble.setBanos(patchDto.banos());
            if (patchDto.descripcion() != null) inmueble.setDescripcion(patchDto.descripcion());
            if (patchDto.estado() != null) {
                try {
                    inmueble.setEstado(EstadoInmueble.valueOf(patchDto.estado()));
                } catch (Exception e) {
                    throw new RuntimeException("Estado de inmueble inválido: " + patchDto.estado());
                }
            }
            if (patchDto.precio() != null) inmueble.setPrecio(patchDto.precio());
            if (patchDto.estadoTransa() != null) {
                try {
                    inmueble.setEstadoTransa(EstadoTransaccion.valueOf(patchDto.estadoTransa()));
                } catch (Exception e) {
                    throw new RuntimeException("Estado de transacción inválido: " + patchDto.estadoTransa());
                }
            }
            if (patchDto.ciudad() != null) inmueble.setCiudad(patchDto.ciudad());
            if (patchDto.codigoInmueble() != null) inmueble.setCodigoInmueble(Integer.parseInt(patchDto.codigoInmueble()));
            if (patchDto.cantidadParqueaderos() != null) inmueble.setCantidadParqueaderos(patchDto.cantidadParqueaderos());
            if (patchDto.telfonoContacto() != null) inmueble.setTelfonoContacto(patchDto.telfonoContacto());
            if (patchDto.nombreContacto() != null) inmueble.setNombreContacto(patchDto.nombreContacto());
            if (patchDto.correoContacto() != null) inmueble.setCorreoContacto(patchDto.correoContacto());
            if (patchDto.imagenes() != null) {
                // Aquí deberías mapear los IDs a entidades Imagen si es necesario
                // inmueble.setImagenes(listaDeImagenes);
            }
            // No se actualizan: agenteAsociado, asesorLegal, historial, documentosImportantes, ubicacion
            inmuebleRepository.save(inmueble);
            return inmuebleMapper.toResponse(inmueble);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar parcialmente el inmueble: " + e.getMessage(), e);
        }

         */
        return null;
    }

    @Override
    public InmuebleResponse obtenerInmueble(Long id) {
        try {
            var inmueble = inmuebleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Inmueble no encontrado con id: " + id));
            return inmuebleMapper.toResponse(inmueble);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el inmueble: " + e.getMessage(), e);
        }
    }

    @Override
    public List<InmuebleResponse> buscarInmueblesPorUsuario(String propietarioEmail) {
        try
        {
            Optional<User> usuario=userRepository.findByEmail(propietarioEmail);

            User propietario=usuario.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Inmueble> inmuebles = inmuebleRepository.findAllByPropietario(propietario);

            if (inmuebles.isEmpty())
            {
                throw new ResourceNotFoundException("No se encontraron inmuebles para el usuario con id: " + propietario.getId());
            }
            return inmuebles.stream().map(inmuebleMapper::toResponse).toList();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error al buscar inmuebles por propietario: " + e.getMessage(), e);
        }
    }


    @Override
    public List<InmuebleResponse> buscarInmueblesPorAgente(String emailAgente) {
        try
        {
            Optional<User> usuario=userRepository.findByEmail(emailAgente);

            User agenteAsociado=usuario.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Inmueble> inmuebles = inmuebleRepository.findInmueblesByAgenteAsociado(agenteAsociado);

            if (inmuebles.isEmpty())
            {
                throw new ResourceNotFoundException("No se encontraron inmuebles para el usuario con id: " + agenteAsociado.getId());
            }
            return inmuebles.stream().map(inmuebleMapper::toResponse).toList();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error al buscar inmuebles por agente: " + e.getMessage(), e);
        }
    }

    @Override
    public InmuebleResponse buscarInmueblePorAgente(String emailAgente) {
        try
        {
            Optional<User> usuario=userRepository.findByEmail(emailAgente);

            User agenteAsociado=usuario.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Optional<Inmueble> inmuebleAsociado=inmuebleRepository.findInmuebleByAgenteAsociado(agenteAsociado);

            Inmueble inmuebleMandar=inmuebleAsociado.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            return inmuebleMapper.toResponse(inmuebleMandar);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error al buscar inmueble por agente: " + e.getMessage(), e);
        }

    }

    @Override
    public Inmueble buscarInmueblePorAgenteSinResponse(String emailAgente) {
        try
        {

            Optional<User> usuario=userRepository.findByEmail(emailAgente);

            User agenteAsociado=usuario.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


            Optional<Inmueble> inmuebleAsociado=inmuebleRepository.findInmuebleByAgenteAsociado(agenteAsociado);

            System.out.println("Llega hasta aqui 2");

            return inmuebleAsociado.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error al buscar inmueble por agente: " + e.getMessage(), e);
        }

    }


    @Override
    public List<InmuebleResponse> obtenerListaDeInmuebles() {
        try
        {
            List<EstadoTransaccion> estados=new ArrayList<>();
            estados.add(EstadoTransaccion.ALQUILADO);
            estados.add(EstadoTransaccion.PERMUTADO);
            estados.add(EstadoTransaccion.VENDIDO);
            estados.add(EstadoTransaccion.PENDIENTE);
            var lista = inmuebleRepository.findByEstadoTransaNotIn(estados);
            return lista.stream().map(inmuebleMapper::toResponse).toList();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error al obtener la lista de inmuebles: " + e.getMessage(), e);
        }
    }

}
