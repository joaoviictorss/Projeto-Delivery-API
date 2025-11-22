package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.request.RestauranteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - RestauranteController")
class RestauranteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestauranteRepository restauranteRepository;

    private RestauranteRequestDTO restauranteRequestDTO;

    @BeforeEach
    void setUp() {
        restauranteRepository.deleteAll();
        
        restauranteRequestDTO = new RestauranteRequestDTO();
        restauranteRequestDTO.setNome("Pizzaria Bella");
        restauranteRequestDTO.setCategoria("Italiana");
        restauranteRequestDTO.setEndereco("Av. Paulista, 1000");
        restauranteRequestDTO.setTelefone("(11) 3333-1111");
        restauranteRequestDTO.setTaxaEntrega(new BigDecimal("5.00"));
        restauranteRequestDTO.setAvaliacao(new BigDecimal("4.5"));
        restauranteRequestDTO.setAtivo(true);
    }

    @Test
    @DisplayName("POST /api/restaurantes - Deve criar restaurante com dados válidos")
    void deveCriarRestauranteComDadosValidos() throws Exception {
        mockMvc.perform(post("/api/restaurantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restauranteRequestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nome").value("Pizzaria Bella"))
                .andExpect(jsonPath("$.data.categoria").value("Italiana"))
                .andExpect(jsonPath("$.message").value("Restaurante cadastrado com sucesso"));
    }

    @Test
    @DisplayName("GET /api/restaurantes - Deve listar restaurantes")
    void deveListarRestaurantes() throws Exception {
        // Criar restaurantes de teste
        Restaurante restaurante1 = criarRestaurante("Restaurante 1", "Italiana");
        Restaurante restaurante2 = criarRestaurante("Restaurante 2", "Japonesa");
        restauranteRepository.save(restaurante1);
        restauranteRepository.save(restaurante2);

        mockMvc.perform(get("/api/restaurantes")
                .param("page", "0")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(greaterThanOrEqualTo(2)));
    }

    @Test
    @DisplayName("GET /api/restaurantes/{id} - Deve buscar restaurante existente")
    void deveBuscarRestauranteExistente() throws Exception {
        Restaurante restaurante = criarRestaurante("Restaurante Teste", "Italiana");
        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);

        mockMvc.perform(get("/api/restaurantes/{id}", restauranteSalvo.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(restauranteSalvo.getId()))
                .andExpect(jsonPath("$.data.nome").value("Restaurante Teste"));
    }

    @Test
    @DisplayName("GET /api/restaurantes/categoria/{categoria} - Deve buscar restaurantes por categoria")
    void deveBuscarRestaurantesPorCategoria() throws Exception {
        Restaurante restaurante1 = criarRestaurante("Pizzaria", "Italiana");
        Restaurante restaurante2 = criarRestaurante("Sushi Bar", "Japonesa");
        restauranteRepository.save(restaurante1);
        restauranteRepository.save(restaurante2);

        mockMvc.perform(get("/api/restaurantes/categoria/Italiana"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].categoria").value("Italiana"));
    }

    @Test
    @DisplayName("GET /api/restaurantes/relatorio-vendas - Deve gerar relatório de vendas")
    void deveGerarRelatorioVendas() throws Exception {
        mockMvc.perform(get("/api/restaurantes/relatorio-vendas"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    private Restaurante criarRestaurante(String nome, String categoria) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(nome);
        restaurante.setCategoria(categoria);
        restaurante.setEndereco("Rua Teste, 123");
        restaurante.setTelefone("(11) 99999-9999");
        restaurante.setTaxaEntrega(new BigDecimal("5.00"));
        restaurante.setAvaliacao(new BigDecimal("4.5"));
        restaurante.setAtivo(true);
        return restaurante;
    }
}

