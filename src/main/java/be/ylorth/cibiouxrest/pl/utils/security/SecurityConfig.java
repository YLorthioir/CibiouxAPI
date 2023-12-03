package be.ylorth.cibiouxrest.pl.utils.security;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@ConfigurationProperties("app.security.cors")
public class SecurityConfig {

    @Setter
    private String serverName;

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> corsConfiguration())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement((session) -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(
                    registry -> registry

                    //Authentification
                        .requestMatchers(HttpMethod.POST,"/auth/login").anonymous()
                            
                    //Reservation
                        .requestMatchers(HttpMethod.GET,"/reservation/dateNonDispo").permitAll()
                        .requestMatchers(HttpMethod.GET,"/reservation/all").authenticated()
                        .requestMatchers(HttpMethod.GET,"/reservation/{id:[0-9]+}").authenticated()
                        .requestMatchers(HttpMethod.PUT,"/reservation/{id:[0-9]+}").permitAll()
                        .requestMatchers(HttpMethod.DELETE,"/reservation/{id:[0-9]+}").authenticated()
                        .requestMatchers(HttpMethod.POST,"/reservation/create").authenticated()
                            
                    //Fermeture
                        .requestMatchers(HttpMethod.GET,"/fermeture/all").permitAll()
                        .requestMatchers(HttpMethod.POST,"/fermeture/insert").authenticated()
                        .requestMatchers(HttpMethod.DELETE,"/fermeture/delete").authenticated()
                            
                    //Swagger
                    .anyRequest().permitAll()
                

        );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfiguration() {
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(serverName));

        config.setAllowedHeaders(List.of("*"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
