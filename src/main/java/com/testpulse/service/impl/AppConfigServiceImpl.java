package com.testpulse.service.impl;

import com.testpulse.repository.AppConfigRepository;
import com.testpulse.service.AppConfigService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppConfigServiceImpl implements AppConfigService {

    private final AppConfigRepository appConfigRepository;

    public AppConfigServiceImpl(AppConfigRepository appConfigRepository) {
        this.appConfigRepository = appConfigRepository;
    }

    @Override
    @Cacheable(value = "appConfig", key = "#configKey")
    public Optional<String> getConfigValue(String configKey) {
        return appConfigRepository.findByConfigKey(configKey)
                .map(config -> config.getConfigValue());
    }
}
