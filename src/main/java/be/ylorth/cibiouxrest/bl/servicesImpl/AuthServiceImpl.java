package be.ylorth.cibiouxrest.bl.servicesImpl;


import be.ylorth.cibiouxrest.bl.services.AuthService;
import be.ylorth.cibiouxrest.dal.models.UserEntity;
import be.ylorth.cibiouxrest.dal.repositories.UserRepository;
import be.ylorth.cibiouxrest.pl.models.auth.AuthResponse;
import be.ylorth.cibiouxrest.pl.models.auth.LoginForm;
import be.ylorth.cibiouxrest.pl.utils.security.JwtProvider;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           AuthenticationManager authenticationManager,
                           JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }
    

    /**
     * Authentifie un utilisateur avec les informations de connexion fournies.
     *
     * Cette méthode prend en charge le processus d'authentification en utilisant les informations de connexion fournies dans le formulaire de connexion (LoginForm). Elle commence par afficher les informations de connexion à des fins de débogage, puis utilise le gestionnaire d'authentification (authenticationManager) pour tenter d'authentifier l'utilisateur en utilisant les informations de connexion fournies. Si l'authentification réussit, elle génère un jeton JWT (JSON Web Token) pour l'utilisateur authentifié, puis renvoie une réponse d'authentification (AuthResponse) contenant le jeton JWT, le login de l'utilisateur et son rôle.
     *
     * @param form Le formulaire de connexion contenant les informations de connexion de l'utilisateur.
     * @return Une réponse d'authentification (AuthResponse) contenant le jeton JWT, le login de l'utilisateur et son rôle.
     */
    @Override
    public AuthResponse login(LoginForm form) {

        Specification<UserEntity> spec = (((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("login"),form.login())));

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(form.login(), form.password()));
        }catch (AuthenticationException e) {
            throw new BadCredentialsException("Login ou mot de passe incorrects");
        }

        UserEntity user = userRepository.findOne(spec)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String token = jwtProvider.generateToken(user.getUsername(), user.getUserRole() );

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getUserRole());
    }

}
