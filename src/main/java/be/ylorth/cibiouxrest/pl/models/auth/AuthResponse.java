package be.ylorth.cibiouxrest.pl.models.auth;

import be.ylorth.cibiouxrest.dal.models.UserRole;

public record AuthResponse (
    String token,
    String login,
    UserRole role
    ){
}
