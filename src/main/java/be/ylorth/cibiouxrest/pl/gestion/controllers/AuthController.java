package be.ylorth.cibiouxrest.pl.gestion.controllers;

import be.ylorth.cibiouxrest.bl.services.AuthService;
import be.ylorth.cibiouxrest.pl.models.auth.AuthResponse;
import be.ylorth.cibiouxrest.pl.models.auth.LoginForm;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginForm form){
        return ResponseEntity.ok(authService.login(form));
    }
}
