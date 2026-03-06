package com.jsebastian.eden.EdenSys.services;

import com.jsebastian.eden.EdenSys.config.CloudinaryConfig;
import com.jsebastian.eden.EdenSys.domain.Inmueble;
import com.jsebastian.eden.EdenSys.services.interfaces.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {


    @Autowired
    private final CloudinaryConfig cloudinaryConfig;

    public String subirImagen(MultipartFile archivo) {
        try
        {
            Map resultado = cloudinaryConfig.upload(archivo);
            return resultado.get("secure_url").toString();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error al subir imagen a Cloudinary", e);
        }
    }

    @Override
    public String subirDocumento(MultipartFile archivo) {
        try {
            Map resultado = cloudinaryConfig.uploadRaw(archivo);
            return resultado.get("secure_url").toString();
        }
        catch (IOException e) {
            throw new RuntimeException("Error al subir documento a Cloudinary", e);
        }
    }

    @Override
    public void eliminarArchivo(Inmueble inmueble, String resourceType, int cantidadArchivos) {
        String url=null;
        for(int i = 0; i < cantidadArchivos; i++)
        {
            try
            {
                url=inmueble.getImagenes().get(i).getUrl();
                String publicId = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
                cloudinaryConfig.deleteFile(publicId, resourceType);
            }
            catch (Exception e)
            {
                System.out.println("No se pudo eliminar archivo de Cloudinary: " + url);
            }
        }
    }


}
