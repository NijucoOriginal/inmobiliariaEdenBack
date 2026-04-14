package com.jsebastian.eden.EdenSys.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/metricas")
@CrossOrigin
public class MetricasController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${prometheus.url:http://prometheus:9090}")
    private String prometheusUrl;

    @GetMapping("/query")
    public ResponseEntity<String> query(@RequestParam String query) {
        try {
            String url = prometheusUrl + "/api/v1/query?query=" + query;
            String response = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok("{\"data\":{\"result\":[]}}");
        }
    }

    // Endpoint para las gráficas históricas (/api/metricas/query_range)
    @GetMapping("/query_range")
    public ResponseEntity<String> queryRange(
            @RequestParam String query,
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam String step
    ) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString(prometheusUrl + "/api/v1/query_range")  // ← fromUriString en vez de fromHttpUrl
                    .queryParam("query", query)
                    .queryParam("start", start)
                    .queryParam("end", end)
                    .queryParam("step", step)
                    .build(false)
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok("{\"data\":{\"result\":[]}}");
        }
    }
}