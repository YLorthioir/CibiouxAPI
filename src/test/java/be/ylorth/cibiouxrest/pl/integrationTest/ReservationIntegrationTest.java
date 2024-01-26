package be.ylorth.cibiouxrest.pl.integrationTest;

import be.ylorth.cibiouxrest.dal.models.ReservationEntity;
import be.ylorth.cibiouxrest.dal.models.ReservationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetPendings_OK() throws Exception {
        
        ReservationEntity reservationAttente = new ReservationEntity();
        reservationAttente.setNom("NomTest");
        reservationAttente.setPrenom("PrenomTest");
        reservationAttente.setStatus(ReservationStatus.EN_ATTENTE);
        reservationAttente.setNbPersonne(2);
        reservationAttente.setPremierJour(LocalDate.now().plusDays(3));
        reservationAttente.setDernierJour(LocalDate.now().plusDays(4));

        mockMvc.perform(get("/reservation/getPendings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].nom", is(reservationAttente.getNom())))
                .andExpect(jsonPath("$.[0].prenom", is(reservationAttente.getPrenom())))
                .andExpect(jsonPath("$.[0].status", is(reservationAttente.getStatus().toString())))
                .andExpect(jsonPath("$.[0].nbPersonne", is(reservationAttente.getNbPersonne())))
                .andExpect(jsonPath("$.[0].premierJour", is(reservationAttente.getPremierJour().toString())))
                .andExpect(jsonPath("$.[0].dernierJour", is(reservationAttente.getDernierJour().toString())));
    }

    @Test
    public void testGetPendings_Unauthorized() throws Exception {

        ReservationEntity reservationAttente = new ReservationEntity();
        reservationAttente.setNom("NomTest");
        reservationAttente.setPrenom("PrenomTest");
        reservationAttente.setStatus(ReservationStatus.EN_ATTENTE);
        reservationAttente.setNbPersonne(2);
        reservationAttente.setPremierJour(LocalDate.now().plusDays(3));
        reservationAttente.setDernierJour(LocalDate.now().plusDays(4));

        mockMvc.perform(get("/reservation/getPendings"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetOneByDate_OK() throws Exception {

        ReservationEntity reservationAttente = new ReservationEntity();
        reservationAttente.setNom("NomTest3");
        reservationAttente.setPrenom("PrenomTest3");
        reservationAttente.setStatus(ReservationStatus.REFUSE);
        reservationAttente.setNbPersonne(2);
        reservationAttente.setPremierJour(LocalDate.now().plusDays(8));
        reservationAttente.setDernierJour(LocalDate.now().plusDays(9));
        

        mockMvc.perform(get("/reservation/getByDate/" + LocalDate.now().plusDays(8)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenom", is(reservationAttente.getPrenom())))
                .andExpect(jsonPath("$.status", is(reservationAttente.getStatus().toString())))
                .andExpect(jsonPath("$.nbPersonne", is(reservationAttente.getNbPersonne())))
                .andExpect(jsonPath("$.premierJour", is(reservationAttente.getPremierJour().toString())))
                .andExpect(jsonPath("$.dernierJour", is(reservationAttente.getDernierJour().toString())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetOneByDate_NotFound() throws Exception {
        
        mockMvc.perform(get("/reservation/getByDate/" + LocalDate.now().minusDays(100)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetOneByDate_Unauthorized() throws Exception {

        mockMvc.perform(get("/reservation/getByDate/" + LocalDate.now().minusDays(100)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testSearch_OKWith3() throws Exception {

        String formJson = "{ \"nom\": \"NomTest\", \"dateDebut\": null, \"dateFin\": null, \"status\": null, \"email\": null, \"telephone\": null }";
        
        mockMvc.perform(post("/reservation/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].nom", is("NomTest")))
                .andExpect(jsonPath("$[1].nom", is("NomTest2")))
                .andExpect(jsonPath("$[2].nom", is("NomTest3")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testSearch_OKWith1() throws Exception {

        String formJson = "{ \"nom\": \"NomTest\", \"dateDebut\": null, \"dateFin\": null, \"prenom\": \"PrenomTest2\", \"email\": null, \"telephone\": null }";

        mockMvc.perform(post("/reservation/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nom", is("NomTest2")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testSearch_OKWith0() throws Exception {

        String formJson = "{ \"nom\": \"NomTest\", \"dateDebut\": null, \"dateFin\": null, \"prenom\": \"PrenomTest2\", \"email\": \"test@test.com\", \"telephone\": null }";

        mockMvc.perform(post("/reservation/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testSearch_OKWithNull() throws Exception {

        String formJson = "{ }";

        mockMvc.perform(post("/reservation/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void testSearch_Unauthorized() throws Exception {

        String formJson = "{ \"nom\": \"NomTest\", \"dateDebut\": null, \"dateFin\": null, \"prenom\": \"PrenomTest2\", \"email\": \"test@test.com\", \"telephone\": null }";

        mockMvc.perform(post("/reservation/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateVisitor_OK() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateVisitor_LastNameNull() throws Exception {

        String formJson = "{ " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must not be blank")));
    }

    @Test
    public void testCreateVisitor_FirstNameNull() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must not be blank")));
    }

    @Test
    public void testCreateVisitor_FirstDayNull() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Le premier jour ou le dernier jour ne peuvent pas être null")));
    }

    @Test
    public void testCreateVisitor_LastDayNull() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Le premier jour ou le dernier jour ne peuvent pas être null")));
    }

    @Test
    public void testCreateVisitor_LastDayBeforeFirstDay() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true, \"" + LocalDate.now().plusDays(1) + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",allOf(containsString("La date de sortie doit être après celle d'arrivée"), containsString("Repas invalides"))));
    }

    @Test
    public void testCreateVisitor_EmailNull() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("L'email ne peut pas être null")));
    }

    @Test
    public void testCreateVisitor_PhoneNull() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must not be blank")));
    }

    @Test
    public void testCreateVisitor_NbPersonNull() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must be greater than or equal to 1")));
    }

    @Test
    public void testCreateVisitor_NbPersonneTooSmall() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": -1, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must be greater than or equal to 1")));
    }

    @Test
    public void testCreateVisitor_NbPersonTooHigh() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 5, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must be less than or equal to 4")));
    }

    @Test
    public void testCreateVisitor_RepasNull() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\" " +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Les repas ne peuvent pas être null")));
    }

    @Test
    public void testCreateVisitor_RepasTooSmall() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Repas invalides")));
    }

    @Test
    public void testCreateVisitor_RepasTooBig() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true, \"" + LocalDate.now().plusDays(1) + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Repas invalides")));
    }

    @Test
    public void testCreateVisitor_RepasWrongDays() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Repas invalides")));
    }

    @Test
    public void testCreateVisitor_InPast() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().minusDays(1) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().minusDays(1) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now().minusDays(1) + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must be a date in the present or in the future, must be a date in the present or in the future")));
    }

    @Test
    public void testCreateVisitor_DateAlreadyBookedAfter() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(2) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now().plusDays(1) + "\": true, \"" + LocalDate.now().plusDays(2) + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Une ou plusieurs dates de l'intervalle ne sont pas disponibles pour la réservation")));
    }

    @Test
    public void testCreateVisitor_DateAlreadyBookedBefore() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(3) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(4) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now().plusDays(3) + "\": true, \"" + LocalDate.now().plusDays(4) + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Une ou plusieurs dates de l'intervalle ne sont pas disponibles pour la réservation")));
    }

    @Test
    public void testCreateVisitor_DateAlreadyBookedBetween() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(4) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now().plusDays(1) + "\": true, \"" + LocalDate.now().plusDays(2) + "\": true, \"" + LocalDate.now().plusDays(3) + "\": true, \"" + LocalDate.now().plusDays(4) + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Une ou plusieurs dates de l'intervalle ne sont pas disponibles pour la réservation")));
    }

    @Test
    public void testCreateVisitor_DateWhileClosedDate() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(6) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(7) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now().plusDays(6) + "\": true, \"" + LocalDate.now().plusDays(7) + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Une ou plusieurs dates de l'intervalle ne sont pas disponibles pour la réservation")));
    }

    @Test
    public void testCreateVisitor_Null() throws Exception {

        mockMvc.perform(post("/reservation/createVisitor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_OK() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_LastNameNull() throws Exception {

        String formJson = "{ " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must not be blank")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_FirstNameNull() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must not be blank")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_FirstDayNull() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Le premier jour ou le dernier jour ne peuvent pas être null")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_LastDayNull() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Le premier jour ou le dernier jour ne peuvent pas être null")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_LastDayBeforeFirstDay() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true, \"" + LocalDate.now().plusDays(1) + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",allOf(containsString("La date de sortie doit être après celle d'arrivée"), containsString("Repas invalides"))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_PhoneNull() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must not be blank")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_NbPersonNull() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must be greater than or equal to 1")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_NbPersonneTooSmall() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": -1, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must be greater than or equal to 1")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_NbPersonTooHigh() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 5, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must be less than or equal to 4")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_RepasNull() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\" " +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Les repas ne peuvent pas être null")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_RepasTooSmall() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Repas invalides")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_RepasTooBig() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true, \"" + LocalDate.now().plusDays(1) + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Repas invalides")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_RepasWrongDays() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Repas invalides")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_InPast() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().minusDays(1) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().minusDays(1) + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now().minusDays(1) + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must be a date in the present or in the future, must be a date in the present or in the future")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_DateAlreadyBookedAfter() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(2) + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now().plusDays(1) + "\": true, \"" + LocalDate.now().plusDays(2) + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Une ou plusieurs dates de l'intervalle ne sont pas disponibles pour la réservation")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_DateAlreadyBookedBefore() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(3) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(4) + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now().plusDays(3) + "\": true, \"" + LocalDate.now().plusDays(4) + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Une ou plusieurs dates de l'intervalle ne sont pas disponibles pour la réservation")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_DateAlreadyBookedBetween() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(4) + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now().plusDays(1) + "\": true, \"" + LocalDate.now().plusDays(2) + "\": true, \"" + LocalDate.now().plusDays(3) + "\": true, \"" + LocalDate.now().plusDays(4) + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Une ou plusieurs dates de l'intervalle ne sont pas disponibles pour la réservation")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_DateWhileClosedDate() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(6) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(7) + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now().plusDays(6) + "\": true, \"" + LocalDate.now().plusDays(7) + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Une ou plusieurs dates de l'intervalle ne sont pas disponibles pour la réservation")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDirection_Null() throws Exception {
        
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateDirection_Unauthorized() throws Exception {

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(post("/reservation/createDirection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    public void testUpdateReservationStatus_Accepte() throws Exception {
        long id = 3L;
        String status = "ACCEPTE";

        mockMvc.perform(put("/reservation/" + id + "/updateStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    public void testUpdateReservationStatus_Refuse() throws Exception {
        long id = 1L;
        String status = "REFUSE";

        mockMvc.perform(put("/reservation/" + id + "/updateStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    public void testUpdateReservationStatus_EnAttente() throws Exception {
        long id = 1L;
        String status = "EN_ATTENTE";

        mockMvc.perform(put("/reservation/" + id + "/updateStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateReservationStatus_Null() throws Exception {
        long id = 1L;
        String status = null;

        mockMvc.perform(put("/reservation/" + id + "/updateStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateReservationStatus_WrongRole() throws Exception {
        long id = 1L;
        String status = "COUCOU";

        mockMvc.perform(put("/reservation/" + id + "/updateStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateReservationStatus_NotFound() throws Exception {
        long id = -99L;
        String status = "EN_ATTENTE";

        mockMvc.perform(put("/reservation/" + id + "/updateStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateReservationStatus_Unauthorized() throws Exception {
        long id = 1L;
        String status = "EN_ATTENTE";

        mockMvc.perform(put("/reservation/" + id + "/updateStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    public void testUpdate_OK() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true, \"" + LocalDate.now().plusDays(1) + "\": true}" +
                " }";

        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_LastNameNull() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must not be blank")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_FirstNameNull() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must not be blank")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_FirstDayNull() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Le premier jour ou le dernier jour ne peuvent pas être null")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_LastDayNull() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Le premier jour ou le dernier jour ne peuvent pas être null")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_LastDayBeforeFirstDay() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true, \"" + LocalDate.now().plusDays(1) + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",allOf(containsString("La date de sortie doit être après celle d'arrivée"), containsString("Repas invalides"))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_EmailNull() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("L'email existant ne peut être supprimé")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_PhoneNull() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must not be blank")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_NbPersonNull() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must be greater than or equal to 1")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_NbPersonneTooSmall() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": -1, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must be greater than or equal to 1")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_NbPersonTooHigh() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 5, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must be less than or equal to 4")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_RepasNull() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\" " +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Les repas ne peuvent pas être null")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_RepasTooSmall() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Repas invalides")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_RepasTooBig() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now() + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true, \"" + LocalDate.now().plusDays(1) + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Repas invalides")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_RepasWrongDays() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Repas invalides")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_InPast() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().minusDays(1) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().minusDays(1) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now().minusDays(1) + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("must be a date in the present or in the future, must be a date in the present or in the future")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_DateAlreadyBookedAfter() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(2) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now().plusDays(1) + "\": true, \"" + LocalDate.now().plusDays(2) + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Une ou plusieurs dates de l'intervalle ne sont pas disponibles pour la réservation")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_DateAlreadyBookedBefore() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(3) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(4) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now().plusDays(3) + "\": true, \"" + LocalDate.now().plusDays(4) + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Une ou plusieurs dates de l'intervalle ne sont pas disponibles pour la réservation")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_DateAlreadyBookedBetween() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(4) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now().plusDays(1) + "\": true, \"" + LocalDate.now().plusDays(2) + "\": true, \"" + LocalDate.now().plusDays(3) + "\": true, \"" + LocalDate.now().plusDays(4) + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Une ou plusieurs dates de l'intervalle ne sont pas disponibles pour la réservation")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_DateWhileClosedDate() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusWeeks(1) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusWeeks(1).plusDays(1) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now().plusWeeks(1) + "\": true, \"" + LocalDate.now().plusWeeks(1).plusDays(1) + "\": true}" +
                " }";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Une ou plusieurs dates de l'intervalle ne sont pas disponibles pour la réservation")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_Null() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";
        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdate_Unauthorized() throws Exception {
        long id = 1L;
        String status = "ACCEPTE";

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";

        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_ExtendsDateAlreadyBooked() throws Exception {
        long id = 2L;
        String status = "ACCEPTE";

        String repas = "";

        for(int i = 2; i <= 8; i++){
            repas += "\"" + LocalDate.now().plusDays(i) + "\": true";
            if (i < 8) {
                repas += ", ";
            }
        }

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now().plusDays(2) + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(8) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {" + repas + "}" +
                " }";

        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Une ou plusieurs dates de l'intervalle ne sont pas disponibles pour la réservation")));;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_WhenStatusNull() throws Exception {
        long id = 1L;
        String status = null;

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";

        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_WhenStatusNotExist() throws Exception {
        long id = 1L;
        String status = "COUCOU";

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";

        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdate_WhenNotFound() throws Exception {
        long id = -99L;
        String status = null;

        String formJson = "{ " +
                "\"nom\": \"Test\", " +
                "\"prenom\": \"User\", " +
                "\"premierJour\": \"" + LocalDate.now() + "\", " +
                "\"dernierJour\": \"" + LocalDate.now().plusDays(1) + "\", " +
                "\"email\": \"test.user@example.com\", " +
                "\"telephone\": \"0123456789\", " +
                "\"nbPersonne\": 3, " +
                "\"commentaire\": \"Commentaire de test\", " +
                "\"repas\": {\"" + LocalDate.now() + "\": true}" +
                " }";

        mockMvc.perform(put("/reservation/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status)
                        .content(formJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles="ADMIN")
    @Transactional
    public void testDelete_OK() throws Exception {
        long id = 1L;

        mockMvc.perform(delete("/reservation/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void testDelete_Unauthorized() throws Exception {
        long id = 1L;

        mockMvc.perform(delete("/reservation/" + id))
                .andExpect(status().isForbidden());
    }

}
