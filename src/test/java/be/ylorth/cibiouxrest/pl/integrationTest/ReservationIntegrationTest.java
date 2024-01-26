package be.ylorth.cibiouxrest.pl.integrationTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReservationIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Test
    public void testGetDateNonDispo() throws Exception {
        this.mockMvc.perform(get("/reservation/dateNonDispo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datesFermetures",
                        is(List.of(LocalDate.now().plusWeeks(1).toString()))));
    }

    @Test
    @WithMockUser( roles = "ADMIN")
    public void testGetOne_OK() throws Exception {        
        mockMvc.perform(get("/reservation/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
                
    }

    @Test
    @WithMockUser( roles = "ADMIN")
    public void testGetOne_NotFound() throws Exception {
        mockMvc.perform(get("/reservation/-99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetOne_UnAuthorized() throws Exception {
        mockMvc.perform(get("/reservation/1"))
                .andExpect(status().isForbidden());
    }
}
