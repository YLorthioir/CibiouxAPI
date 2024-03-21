package be.ylorth.cibiouxrest.pl.controllers;

import be.ylorth.cibiouxrest.bl.services.AuthService;
import be.ylorth.cibiouxrest.pl.models.Error;
import be.ylorth.cibiouxrest.pl.models.auth.AuthResponse;
import be.ylorth.cibiouxrest.pl.models.auth.LoginForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "log-in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Error.class),
                        examples = @ExampleObject(value = "{\"message\": \"Login ou mot de passe incorrects\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/auth/login\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginForm form){
        return ResponseEntity.ok(authService.login(form));
    }
}
