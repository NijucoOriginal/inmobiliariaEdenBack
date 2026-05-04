package com.jsebastian.eden.EdenSys.services;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;

@Service
public class ChatMetricasService {

    private final Timer tiempoRespuestaAgente;
    private final Timer duracionConversacion;
    private final Counter totalMensajes;
    private final Counter totalErrores;

    public ChatMetricasService(MeterRegistry registry) {

        this.tiempoRespuestaAgente = Timer.builder("chat_message_latency_seconds")
                .description("Tiempo que tarda el backend en procesar y responder un mensaje del chat")
                .register(registry);



        this.duracionConversacion = Timer.builder("chat_conversation_duration_seconds")
                .description("Duración total de la conversación")
                .register(registry);

        this.totalMensajes = Counter.builder("chat_messages_total")
                .description("Total de mensajes enviados")
                .register(registry);

        this.totalErrores = Counter.builder("chat_errors_total")
                .description("Fallos técnicos en el chat")
                .register(registry);


    }
    public void registrarLatenciaMensaje(Instant inicio, Instant fin) {
        tiempoRespuestaAgente.record(Duration.between(inicio, fin));
    }

    public void registrarTiempoRespuesta(Instant primerMensaje, Instant respuestaAgente) {
        tiempoRespuestaAgente.record(Duration.between(primerMensaje, respuestaAgente));
    }

    public void registrarDuracionConversacion(Instant inicio, Instant fin) {
        duracionConversacion.record(Duration.between(inicio, fin));
    }

    public void contarMensaje() {
        totalMensajes.increment();
    }

    public void contarError() {
        totalErrores.increment();
    }
}