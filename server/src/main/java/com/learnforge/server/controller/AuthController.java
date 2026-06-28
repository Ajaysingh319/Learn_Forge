package com.learnforge.server.controller;

import com.learnforge.server.dto.AuthUserResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api-base-path:/api}/auth")
public class AuthController {

    @GetMapping("/me")
    public AuthUserResponse me(@AuthenticationPrincipal Jwt jwt) {
        AuthUserResponse response = new AuthUserResponse();
        response.setSub(jwt.getSubject());
        response.setEmail(jwt.getClaimAsString("email"));
        response.setName(jwt.getClaimAsString("name"));
        return response;
    }
}
