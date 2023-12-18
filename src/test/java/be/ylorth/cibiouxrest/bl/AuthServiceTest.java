package be.ylorth.cibiouxrest.bl;

import be.ylorth.cibiouxrest.bl.exception.NotFoundException;
import be.ylorth.cibiouxrest.bl.servicesImpl.AuthServiceImpl;
import be.ylorth.cibiouxrest.dal.models.UserEntity;
import be.ylorth.cibiouxrest.dal.models.UserRole;
import be.ylorth.cibiouxrest.dal.repositories.UserRepository;
import be.ylorth.cibiouxrest.pl.models.auth.AuthResponse;
import be.ylorth.cibiouxrest.pl.models.auth.LoginForm;
import be.ylorth.cibiouxrest.pl.utils.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginForm form;
    private UserEntity userEntity;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        form = new LoginForm("testlogin","password");

        userEntity = new UserEntity();
        userEntity.setLogin("testlogin");
        userEntity.setUserRole(UserRole.ADMIN);
    }

    @Test
    public void whenValidLogin_thenAuthenticates() {
        when(userRepository.findOne(any(Specification.class)))
                .thenReturn(Optional.of(userEntity));
        when(jwtProvider.generateToken("testlogin", UserRole.ADMIN))
                .thenReturn("token");

        AuthResponse response = authService.login(form);

        assertEquals(response.token(),"token");
        assertEquals(response.login(),"testlogin");
        assertEquals(response.role(),UserRole.ADMIN);

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void whenInvalidLogin_thenThrowsException() {
        when(userRepository.findOne(any(Specification.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(form))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void whenBadCredentials_thenThrowsException() {
        doThrow(new AuthenticationException("Bad credentials") { })
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThatThrownBy(() -> authService.login(form))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Login ou mot de passe incorrects");
    }
}