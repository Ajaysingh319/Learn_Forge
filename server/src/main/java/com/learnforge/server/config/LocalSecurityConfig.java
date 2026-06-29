package com.learnforge.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
@Profile("local")
public class LocalSecurityConfig {

    @Bean
    JwtDecoder jwtDecoder() {
        return token -> Jwt.withTokenValue(token != null && !token.isBlank() ? token : "local-dev-token")
                .header("alg", "none")
                .claim("sub", "local-dev-user")
                .build();
    }
}
