package com.learnforge.server.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AppProperties.class, Auth0Properties.class, AiProperties.class})
public class AppConfig {
}
