package com.jsebastian.eden.EdenSys.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Component
public class CloudinaryConfig {

    private final Cloudinary cloudinary;

    public CloudinaryConfig(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret
    ) {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public Map upload(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload", file.getOriginalFilename());
        file.transferTo(tempFile);
        return cloudinary.uploader().upload(tempFile, ObjectUtils.emptyMap());
    }

    public Map uploadRaw(MultipartFile file) throws IOException {

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new RuntimeException("El archivo no tiene nombre válido");
        }

        String baseName = originalFilename.contains(".")
                ? originalFilename.substring(0, originalFilename.lastIndexOf("."))
                : originalFilename;

        // 🔥 LIMPIAR NOMBRE
        String safeBaseName = baseName.replaceAll("[^a-zA-Z0-9-_]", "_");
        String publicId = safeBaseName + "_" + System.currentTimeMillis();

        File tempFile = File.createTempFile("upload-", ".tmp");
        file.transferTo(tempFile);

        Map result = cloudinary.uploader().upload(
                tempFile,
                ObjectUtils.asMap(
                        "resource_type", "raw",
                        "type", "upload",
                        "access_mode", "public",
                        "public_id", publicId,
                        "filename_override", originalFilename,
                        "use_filename", false,
                        "unique_filename", false,
                        "overwrite", false
                )
        );

        tempFile.delete();

        return result;
    }
}
