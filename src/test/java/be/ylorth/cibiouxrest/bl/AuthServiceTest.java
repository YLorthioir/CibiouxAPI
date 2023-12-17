package be.ylorth.cibiouxrest.bl;

import be.ylorth.cibiouxrest.bl.servicesImpl.AuthServiceImpl;
import be.ylorth.cibiouxrest.dal.models.UserEntity;
import be.ylorth.cibiouxrest.dal.models.UserRole;
import be.ylorth.cibiouxrest.dal.repositories.UserRepository;
import be.ylorth.cibiouxrest.pl.models.auth.AuthResponse;
import be.ylorth.cibiouxrest.pl.models.auth.LoginForm;
import be.ylorth.cibiouxrest.pl.utils.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginForm form;

    @BeforeEach
    void setUp() {
        form = new LoginForm("testUser","testPass");
    }

    @Test
    void testLogin() {
    }
}
