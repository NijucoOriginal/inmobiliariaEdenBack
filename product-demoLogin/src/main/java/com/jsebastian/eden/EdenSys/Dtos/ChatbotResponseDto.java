package com.jsebastian.eden.EdenSys.Dtos;

import lombok.Data;
import java.util.List;

@Data
public class ChatbotResponseDto {
    private String mensaje;
    private String redirectUrl;
    private List<?> datos;
}