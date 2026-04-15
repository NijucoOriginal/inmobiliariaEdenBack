package com.jsebastian.eden.EdenSys.Dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MensajeDTO {
    private String emisor;
    private String receptor;
    private String contenido;
    private LocalDateTime fechaMensaje;
}