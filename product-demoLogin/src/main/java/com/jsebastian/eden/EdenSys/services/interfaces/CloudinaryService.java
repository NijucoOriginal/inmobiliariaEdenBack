package com.jsebastian.eden.EdenSys.services.interfaces;

import com.jsebastian.eden.EdenSys.domain.Inmueble;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {


    String subirImagen(MultipartFile archivo);

    String subirDocumento(MultipartFile archivo);

    void eliminarArchivo(Inmueble inmueble, String resourceType, int cantidadArchivos);
}
