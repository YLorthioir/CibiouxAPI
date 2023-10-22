package be.ylorth.cibiouxrest.bl.services;

import be.ylorth.cibiouxrest.pl.models.auth.AuthResponse;
import be.ylorth.cibiouxrest.pl.models.auth.LoginForm;

public interface AuthService {
    AuthResponse login(LoginForm form);
}
