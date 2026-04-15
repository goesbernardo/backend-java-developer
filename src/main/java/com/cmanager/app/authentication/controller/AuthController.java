package com.cmanager.app.authentication.controller;

import com.cmanager.app.authentication.data.LoginRequest;
import com.cmanager.app.authentication.data.TokenResponse;
import com.cmanager.app.authentication.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "AuthController",
        description = "API de controle autenticações"
)
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(
            summary = "login",
            description = "Login de um usuário",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
        final var response = authenticationService.login(req.username(), req.password());
        return ResponseEntity.ok(response);
    }
}
