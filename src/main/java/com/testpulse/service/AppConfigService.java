package com.testpulse.service;

import java.util.Optional;

public interface AppConfigService {
    Optional<String> getConfigValue(String configKey);
}
