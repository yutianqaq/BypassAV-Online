package com.yutian4090.bypass.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "bypassav")
public class ApplicationProperties {
    private String templatesDirectory;

    private String storageDirector;

    private String compilerC;

    private String compilerNIM;

    private final Map<String, String> templateCMapping = new HashMap<>();

    private final Map<String, String> templateNIMMapping = new HashMap<>();

}
