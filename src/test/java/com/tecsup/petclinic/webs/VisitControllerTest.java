package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.domain.VisitTO;
import com.tecsup.petclinic.entities.Visit;
import com.tecsup.petclinic.services.VisitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class VisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VisitService visitService;

    private Visit visit;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    public void setUp() {
        visit = new Visit();
        visit.setPetId(1L);
        visit.setVisitDate(new java.sql.Date(new Date().getTime())); // Evita ClassCastException
        visit.setDescription("Consulta de prueba");
        visitService.create(visit);
    }

    @Test
    public void testGetVisitById() throws Exception {
        String responseContent = mockMvc.perform(get("/api/visits/" + visit.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json")) // Tipo de contenido ajustado
                .andDo(print())  // Imprime la respuesta para inspección
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println("Response Content: " + responseContent);
    }

    @Test
    public void testCreateVisit() throws Exception {
        VisitTO visitTO = new VisitTO(null, 1L, "2024-11-07", "Nueva visita");

        ResultActions mvcActions = mockMvc.perform(post("/api/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(visitTO)))
                .andExpect(status().isCreated())
                .andDo(print()); // Imprime la respuesta para inspección

        String response = mvcActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response Content: " + response);
    }

    @Test
    public void testUpdateVisit() throws Exception {
        visit.setDescription("Consulta actualizada");
        visit.setVisitDate(new java.sql.Date(dateFormat.parse("2024-11-08").getTime())); // Fecha actualizada para la prueba

        mockMvc.perform(put("/api/visits/" + visit.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(visit)))
                .andExpect(status().isNoContent())  // Cambiado para esperar 204 No Content
                .andDo(print());
    }

    @Test
    public void testDeleteVisit() throws Exception {
        mockMvc.perform(delete("/api/visits/" + visit.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testFindVisitNotFound() throws Exception {
        mockMvc.perform(get("/api/visits/9999"))
                .andExpect(status().isNotFound());
    }
}


