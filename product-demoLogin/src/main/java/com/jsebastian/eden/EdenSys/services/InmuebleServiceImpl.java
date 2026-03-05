package com.jsebastian.eden.EdenSys.services;

import com.jsebastian.eden.EdenSys.Dtos.InmuebleDto;
import com.jsebastian.eden.EdenSys.Dtos.InmuebleResponse;
import com.jsebastian.eden.EdenSys.domain.*;
import com.jsebastian.eden.EdenSys.exceptions.ResourceNotFoundException;
import com.jsebastian.eden.EdenSys.mappers.InmuebleMapper;
import com.jsebastian.eden.EdenSys.repository.*;
import com.jsebastian.eden.EdenSys.services.interfaces.CloudinaryService;
import com.jsebastian.eden.EdenSys.services.interfaces.InmuebleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
    private CloudinaryService cloudinaryService;

    @Autowired
    private LogsServiceImpl logsService;


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
                        String ruta = cloudinaryService.subirImagen(imagen);
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
                         String ruta = cloudinaryService.subirDocumento(doc);
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
            logsService.registrarLog("Inmueble creado correctamente, a continuación el id del inmueble recien creado "+nuevoInmueble.getId(),nuevoInmueble.getPropietario().getId());

            return inmuebleMapper.toResponse(nuevoInmueble);

        }
        catch (Exception e)
        {
            throw new RuntimeException("Error al crear inmueble: " + e.getMessage(), e);
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
            logsService.registrarLog("Inmueble eliminado correctamente",inmueble.getPropietario().getId());
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
            logsService.registrarLog("Inmueble actualizado"+inmuebleExistente.getId()+"correctamente",inmuebleExistente.getPropietario().getId());
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

            logsService.registrarLog("El estado del inmueble"+inmuebleMandar.getId() +"ha sido moficiado por: "+estadoTransa,inmuebleMandar.getAgenteAsociado().getId());
            return inmuebleMapper.toResponse(inmuebleMandar);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage(), e);
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

            System.out.println (inmuebleAsociado.get().getImagenes().getFirst().getUrl());

            Inmueble inmuebleMandar=inmuebleAsociado.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            return inmuebleMapper.toResponse(inmuebleMandar);
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

          //  System.out.println ("Enviando todo: "+lista.get(0).get);
            return lista.stream().map(inmuebleMapper::toResponse).toList();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error al obtener la lista de inmuebles: " + e.getMessage(), e);
        }
    }
}
