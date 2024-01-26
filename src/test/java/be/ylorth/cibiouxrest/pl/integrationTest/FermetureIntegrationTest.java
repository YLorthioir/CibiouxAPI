package be.ylorth.cibiouxrest.pl.integrationTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FermetureIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreate_Ok() throws Exception {
        String datesAsJson = "[\"" + LocalDate.now().plusDays(20) + "\"]";

        this.mockMvc.perform(post("/fermeture/insert")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(datesAsJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreate_Null() throws Exception {
        String datesAsJson = "";

        this.mockMvc.perform(post("/fermeture/insert")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(datesAsJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreate_Past() throws Exception {
        
        String datesAsJson = "[\"" + LocalDate.now().minusDays(20) + "\"]";

        this.mockMvc.perform(post("/fermeture/insert")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(datesAsJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreate_Unnauthorized() throws Exception {
        String datesAsJson = "[\"" + LocalDate.now().plusDays(20) + "\"]";

        this.mockMvc.perform(post("/fermeture/insert")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(datesAsJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDelete_OK() throws Exception {
        LocalDate dateToDelete = LocalDate.now();

        this.mockMvc.perform(delete("/fermeture/delete")
                        .param("date", dateToDelete.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDelete_Null() throws Exception {

        this.mockMvc.perform(delete("/fermeture/delete")
                        .param("date", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDelete_Unauthorized() throws Exception {
        LocalDate dateToDelete = LocalDate.now();

        this.mockMvc.perform(delete("/fermeture/delete")
                        .param("date", dateToDelete.toString()))
                .andExpect(status().isForbidden());
    }
}
