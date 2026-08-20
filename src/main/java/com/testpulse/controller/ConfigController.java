package com.testpulse.controller;

import com.testpulse.service.AppConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final AppConfigService appConfigService;

    public ConfigController(AppConfigService appConfigService) {
        this.appConfigService = appConfigService;
    }

    @GetMapping
    public ResponseEntity<?> getApiRoot() {
        return appConfigService.getConfigValue("apiRoot")
                .map(value -> ResponseEntity.ok(Map.of("apiRoot", value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
