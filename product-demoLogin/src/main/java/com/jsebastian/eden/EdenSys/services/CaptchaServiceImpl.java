package com.jsebastian.eden.EdenSys.services;

import com.jsebastian.eden.EdenSys.domain.RecaptchaResponse;
import com.jsebastian.eden.EdenSys.services.interfaces.CaptchaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Value("${google.recaptcha.secret}")
    private String secretKey;

    private static final String GOOGLE_RECAPTCHA_ENDPOINT = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verificar(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        RestTemplate restTemplate = new RestTemplate();

        // Creamos los parámetros de la petición
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("secret", secretKey);
        requestBody.add("response", token);

        try {
            RecaptchaResponse response = restTemplate.postForObject(
                    GOOGLE_RECAPTCHA_ENDPOINT,
                    requestBody,
                    RecaptchaResponse.class
            );
            return response != null && response.isSuccess();
        } catch (Exception e) {
            return false; // Si falla la conexión, por seguridad no validamos
        }
    }
}
