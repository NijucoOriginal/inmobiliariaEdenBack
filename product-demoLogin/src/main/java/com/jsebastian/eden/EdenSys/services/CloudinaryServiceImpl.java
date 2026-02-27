package com.jsebastian.eden.EdenSys.services;

import com.jsebastian.eden.EdenSys.config.CloudinaryConfig;
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
}
