package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.request.ClienteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Cliente;
import com.delivery_api.Projeto.Delivery.API.repository.ClienteRepository;
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

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - ClienteController")
class ClienteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    private ClienteRequestDTO clienteRequestDTO;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
        
        clienteRequestDTO = new ClienteRequestDTO();
        clienteRequestDTO.setNome("João Silva");
        clienteRequestDTO.setEmail("joao@email.com");
        clienteRequestDTO.setTelefone("(11) 99999-1111");
        clienteRequestDTO.setEndereco("Rua A, 123");
    }

    @Test
    @DisplayName("POST /api/clientes - Deve criar cliente com dados válidos")
    void deveCriarClienteComDadosValidos() throws Exception {
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nome").value("João Silva"))
                .andExpect(jsonPath("$.data.email").value("joao@email.com"))
                .andExpect(jsonPath("$.data.ativo").value(true))
                .andExpect(jsonPath("$.message").value("Cliente cadastrado com sucesso"));
    }

    @Test
    @DisplayName("POST /api/clientes - Deve retornar 400 quando nome está vazio")
    void deveRetornar400QuandoNomeVazio() throws Exception {
        clienteRequestDTO.setNome("");

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/clientes - Deve retornar 400 quando email é inválido")
    void deveRetornar400QuandoEmailInvalido() throws Exception {
        clienteRequestDTO.setEmail("email-invalido");

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/clientes - Deve retornar 400 quando email já está cadastrado")
    void deveRetornar400QuandoEmailJaCadastrado() throws Exception {
        // Criar primeiro cliente
        Cliente clienteExistente = new Cliente();
        clienteExistente.setNome("Cliente Existente");
        clienteExistente.setEmail("joao@email.com");
        clienteExistente.setTelefone("(11) 99999-9999");
        clienteExistente.setEndereco("Rua B, 456");
        clienteExistente.setAtivo(true);
        clienteExistente.setDataCadastro(LocalDateTime.now());
        clienteRepository.save(clienteExistente);

        // Tentar criar com mesmo email
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/clientes - Deve listar clientes com sucesso")
    void deveListarClientes() throws Exception {
        // Criar clientes de teste
        Cliente cliente1 = criarCliente("Cliente 1", "cliente1@email.com");
        Cliente cliente2 = criarCliente("Cliente 2", "cliente2@email.com");
        clienteRepository.save(cliente1);
        clienteRepository.save(cliente2);

        mockMvc.perform(get("/api/clientes")
                .param("page", "0")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /api/clientes/{id} - Deve buscar cliente existente")
    void deveBuscarClienteExistente() throws Exception {
        Cliente cliente = criarCliente("Cliente Teste", "teste@email.com");
        Cliente clienteSalvo = clienteRepository.save(cliente);

        mockMvc.perform(get("/api/clientes/{id}", clienteSalvo.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(clienteSalvo.getId()))
                .andExpect(jsonPath("$.data.nome").value("Cliente Teste"));
    }

    @Test
    @DisplayName("GET /api/clientes/{id} - Deve retornar null quando cliente não existe (método não implementado)")
    void deveRetornarNullQuandoClienteNaoExiste() throws Exception {
        mockMvc.perform(get("/api/clientes/9999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("PUT /api/clientes/{id} - Deve atualizar cliente com sucesso")
    void deveAtualizarClienteComSucesso() throws Exception {
        Cliente cliente = criarCliente("Cliente Original", "original@email.com");
        Cliente clienteSalvo = clienteRepository.save(cliente);

        ClienteRequestDTO dtoAtualizado = new ClienteRequestDTO();
        dtoAtualizado.setNome("Cliente Atualizado");
        dtoAtualizado.setEmail("atualizado@email.com");
        dtoAtualizado.setTelefone("(11) 88888-2222");
        dtoAtualizado.setEndereco("Rua Nova, 789");

        mockMvc.perform(put("/api/clientes/{id}", clienteSalvo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoAtualizado)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nome").value("Cliente Atualizado"))
                .andExpect(jsonPath("$.message").value("Cliente atualizado com sucesso"));
    }

    @Test
    @DisplayName("PUT /api/clientes/{id} - Deve retornar erro quando cliente não existe")
    void deveRetornarErroAoAtualizarClienteInexistente() throws Exception {
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setNome("Cliente");
        dto.setEmail("email@email.com");
        dto.setTelefone("(11) 99999-9999");
        dto.setEndereco("Rua Teste");

        mockMvc.perform(put("/api/clientes/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/clientes/{id} - Deve inativar cliente")
    void deveInativarCliente() throws Exception {
        Cliente cliente = criarCliente("Cliente para Inativar", "inativar@email.com");
        Cliente clienteSalvo = clienteRepository.save(cliente);

        mockMvc.perform(delete("/api/clientes/{id}", clienteSalvo.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cliente inativado com sucesso"));
    }

    @Test
    @DisplayName("GET /api/clientes/buscar?nome=João - Deve buscar clientes por nome")
    void deveBuscarClientesPorNome() throws Exception {
        Cliente cliente1 = criarCliente("João Silva", "joao@email.com");
        Cliente cliente2 = criarCliente("João Santos", "joao.santos@email.com");
        clienteRepository.save(cliente1);
        clienteRepository.save(cliente2);

        mockMvc.perform(get("/api/clientes/buscar")
                .param("nome", "João"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    private Cliente criarCliente(String nome, String email) {
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setEmail(email);
        cliente.setTelefone("(11) 99999-9999");
        cliente.setEndereco("Rua Teste, 123");
        cliente.setAtivo(true);
        cliente.setDataCadastro(LocalDateTime.now());
        return cliente;
    }
}

