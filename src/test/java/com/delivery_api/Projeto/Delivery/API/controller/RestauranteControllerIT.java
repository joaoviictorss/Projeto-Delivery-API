package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.request.RestauranteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RestauranteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCriarRestaurante() throws Exception {
        RestauranteRequestDTO dto = new RestauranteRequestDTO();
        dto.setNome("Restaurante Teste");
        dto.setCategoria("Brasileira");
        dto.setEndereco("Rua Teste, 456");
        dto.setTelefone("(11) 3333-4444");
        dto.setTaxaEntrega(new BigDecimal("4.50"));
        dto.setAvaliacao(new BigDecimal("4.0"));

        mockMvc.perform(post("/api/restaurantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nome").value("Restaurante Teste"));
    }

    @Test
    public void testListarRestaurantes() throws Exception {
        mockMvc.perform(get("/api/restaurantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    public void testListarRestaurantesPorCategoria() throws Exception {
        mockMvc.perform(get("/api/restaurantes")
                .param("categoria", "Italiana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    public void testBuscarRestaurantePorId() throws Exception {
        mockMvc.perform(get("/api/restaurantes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    public void testBuscarRestauranteInexistente() throws Exception {
        mockMvc.perform(get("/api/restaurantes/9999"))
                .andExpect(status().isNotFound());
    }
}

