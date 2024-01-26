package be.ylorth.cibiouxrest.pl.integrationTest;

import be.ylorth.cibiouxrest.dal.models.UserRole;
import be.ylorth.cibiouxrest.pl.models.auth.AuthResponse;
import be.ylorth.cibiouxrest.pl.models.auth.LoginForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @BeforeEach
    public void setup() throws Exception {
    }

    @Test
    public void testLogin_OK() throws Exception {
        LoginForm testLoginForm = new LoginForm("Yann", "Test1234=");

        AuthResponse expectedAuthResponse = new AuthResponse("Test token", "Yann", UserRole.ADMIN);

        this.mockMvc.perform(post("/auth/login")
                        .contentType(contentType)
                        .content(objectMapper.writeValueAsString(testLoginForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is(expectedAuthResponse.login())))
                .andExpect(jsonPath("$.role", is(expectedAuthResponse.role().toString())));
    }

    @Test
    public void testLogin_WrongPassword() throws Exception {
        LoginForm testLoginForm = new LoginForm("Yann", "fqzefzeqfezfqezf");

        this.mockMvc.perform(post("/auth/login")
                        .contentType(contentType)
                        .content(objectMapper.writeValueAsString(testLoginForm)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogin_WrongLogin() throws Exception {
        LoginForm testLoginForm = new LoginForm("Test login", "fqzefzeqfezfqezf");

        this.mockMvc.perform(post("/auth/login")
                        .contentType(contentType)
                        .content(objectMapper.writeValueAsString(testLoginForm)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogin_LoginNull() throws Exception {
        LoginForm testLoginForm = new LoginForm(null, "fqzefzeqfezfqezf");

        this.mockMvc.perform(post("/auth/login")
                        .contentType(contentType)
                        .content(objectMapper.writeValueAsString(testLoginForm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLogin_PasswordNull() throws Exception {
        LoginForm testLoginForm = new LoginForm("Yann", null);

        this.mockMvc.perform(post("/auth/login")
                        .contentType(contentType)
                        .content(objectMapper.writeValueAsString(testLoginForm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLogin_LoginFormNull() throws Exception {
        LoginForm testLoginForm = null;

        this.mockMvc.perform(post("/auth/login")
                        .contentType(contentType)
                        .content(objectMapper.writeValueAsString(testLoginForm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testLogin_AlreadyConnected() throws Exception {
        LoginForm testLoginForm = new LoginForm("Yann", "Test1234=");

        this.mockMvc.perform(post("/auth/login")
                        .contentType(contentType)
                        .content(objectMapper.writeValueAsString(testLoginForm)))
                .andExpect(status().isForbidden());
    }
}
