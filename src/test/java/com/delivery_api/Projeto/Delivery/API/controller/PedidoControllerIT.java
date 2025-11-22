package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.request.PedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Cliente;
import com.delivery_api.Projeto.Delivery.API.entity.Pedido;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.delivery_api.Projeto.Delivery.API.enums.StatusPedido;
import com.delivery_api.Projeto.Delivery.API.repository.ClienteRepository;
import com.delivery_api.Projeto.Delivery.API.repository.PedidoRepository;
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
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - PedidoController")
class PedidoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    private Cliente cliente;
    private Restaurante restaurante;
    private PedidoRequestDTO pedidoRequestDTO;

    @BeforeEach
    void setUp() {
        pedidoRepository.deleteAll();
        clienteRepository.deleteAll();
        restauranteRepository.deleteAll();

        // Criar cliente de teste
        cliente = new Cliente();
        cliente.setNome("Cliente Teste");
        cliente.setEmail("cliente@teste.com");
        cliente.setTelefone("(11) 99999-9999");
        cliente.setEndereco("Rua Teste, 123");
        cliente.setAtivo(true);
        cliente.setDataCadastro(LocalDateTime.now());
        cliente = clienteRepository.save(cliente);

        // Criar restaurante de teste
        restaurante = new Restaurante();
        restaurante.setNome("Restaurante Teste");
        restaurante.setCategoria("Italiana");
        restaurante.setEndereco("Rua Restaurante, 456");
        restaurante.setTelefone("(11) 88888-8888");
        restaurante.setTaxaEntrega(new BigDecimal("5.00"));
        restaurante.setAvaliacao(new BigDecimal("4.5"));
        restaurante.setAtivo(true);
        restaurante = restauranteRepository.save(restaurante);

        // Preparar DTO de pedido
        pedidoRequestDTO = new PedidoRequestDTO();
        pedidoRequestDTO.setNumeroPedido("PED123456");
        pedidoRequestDTO.setDataPedido(LocalDateTime.now());
        pedidoRequestDTO.setValorTotal(new BigDecimal("54.80"));
        pedidoRequestDTO.setObservacoes("Sem cebola");
        pedidoRequestDTO.setClienteId(cliente.getId());
        pedidoRequestDTO.setRestauranteId(restaurante.getId());
        pedidoRequestDTO.setItens("Pizza Margherita, Pizza Calabresa");
        pedidoRequestDTO.setStatus("PENDENTE");
    }

    @Test
    @DisplayName("POST /api/pedidos - Deve criar pedido com dados válidos")
    void deveCriarPedidoComDadosValidos() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.numeroPedido").value("PED123456"))
                .andExpect(jsonPath("$.data.status").value("PENDENTE"))
                .andExpect(jsonPath("$.data.valorTotal").value(54.80))
                .andExpect(jsonPath("$.data.clienteId").value(cliente.getId()))
                .andExpect(jsonPath("$.data.restauranteId").value(restaurante.getId()))
                .andExpect(jsonPath("$.message").value("Pedido criado com sucesso"));
    }

    @Test
    @DisplayName("POST /api/pedidos - Deve retornar 400 quando cliente não existe")
    void deveRetornar400QuandoClienteNaoExiste() throws Exception {
        pedidoRequestDTO.setClienteId(999L);

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/pedidos - Deve retornar 400 quando restaurante não existe")
    void deveRetornar400QuandoRestauranteNaoExiste() throws Exception {
        pedidoRequestDTO.setRestauranteId(999L);

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/pedidos - Deve retornar 400 quando cliente está inativo")
    void deveRetornar400QuandoClienteInativo() throws Exception {
        cliente.setAtivo(false);
        clienteRepository.save(cliente);

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/pedidos - Deve retornar 400 quando restaurante está inativo")
    void deveRetornar400QuandoRestauranteInativo() throws Exception {
        restaurante.setAtivo(false);
        restauranteRepository.save(restaurante);

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/pedidos/{id} - Deve buscar pedido existente")
    void deveBuscarPedidoExistente() throws Exception {
        // Criar pedido
        Pedido pedido = criarPedido();
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        mockMvc.perform(get("/api/pedidos/{id}", pedidoSalvo.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(pedidoSalvo.getId()))
                .andExpect(jsonPath("$.data.numeroPedido").value("PED123456"))
                .andExpect(jsonPath("$.data.status").value("PENDENTE"));
    }

    @Test
    @DisplayName("GET /api/pedidos/{id} - Deve retornar 404 quando pedido não existe")
    void deveRetornar404QuandoPedidoNaoExiste() throws Exception {
        mockMvc.perform(get("/api/pedidos/9999"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/pedidos - Deve listar todos os pedidos")
    void deveListarTodosPedidos() throws Exception {
        // Criar pedidos de teste
        Pedido pedido1 = criarPedido();
        pedido1.setNumeroPedido("PED001");
        Pedido pedido2 = criarPedido();
        pedido2.setNumeroPedido("PED002");
        pedidoRepository.save(pedido1);
        pedidoRepository.save(pedido2);

        mockMvc.perform(get("/api/pedidos"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/pedidos?status=PENDENTE - Deve filtrar pedidos por status")
    void deveFiltrarPedidosPorStatus() throws Exception {
        // Criar pedidos com diferentes status
        Pedido pedidoPendente = criarPedido();
        pedidoPendente.setStatus(StatusPedido.PENDENTE.name());
        pedidoPendente.setNumeroPedido("PED001");
        
        Pedido pedidoConfirmado = criarPedido();
        pedidoConfirmado.setStatus(StatusPedido.CONFIRMADO.name());
        pedidoConfirmado.setNumeroPedido("PED002");
        
        pedidoRepository.save(pedidoPendente);
        pedidoRepository.save(pedidoConfirmado);

        mockMvc.perform(get("/api/pedidos")
                .param("status", "PENDENTE"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].status").value("PENDENTE"));
    }

    @Test
    @DisplayName("PATCH /api/pedidos/{id}/status - Deve atualizar status do pedido")
    void deveAtualizarStatusPedido() throws Exception {
        Pedido pedido = criarPedido();
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoSalvo.getId())
                .param("status", "CONFIRMADO"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CONFIRMADO"))
                .andExpect(jsonPath("$.message").value("Status atualizado com sucesso"));
    }

    @Test
    @DisplayName("PATCH /api/pedidos/{id}/status - Deve retornar erro ao atualizar pedido inexistente")
    void deveRetornarErroAoAtualizarStatusPedidoInexistente() throws Exception {
        mockMvc.perform(patch("/api/pedidos/9999/status")
                .param("status", "CONFIRMADO"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/pedidos/{id}/status - Deve retornar erro ao atualizar pedido já entregue")
    void deveRetornarErroAoAtualizarStatusPedidoEntregue() throws Exception {
        Pedido pedido = criarPedido();
        pedido.setStatus(StatusPedido.ENTREGUE.name());
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoSalvo.getId())
                .param("status", "CONFIRMADO"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/pedidos/{id} - Deve cancelar pedido")
    void deveCancelarPedido() throws Exception {
        Pedido pedido = criarPedido();
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        mockMvc.perform(delete("/api/pedidos/{id}", pedidoSalvo.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CANCELADO"))
                .andExpect(jsonPath("$.message").value("Pedido cancelado com sucesso"));
    }

    @Test
    @DisplayName("GET /api/pedidos/clientes/{clienteId}/pedidos - Deve listar pedidos do cliente")
    void deveListarPedidosDoCliente() throws Exception {
        // Criar pedidos para o cliente
        Pedido pedido1 = criarPedido();
        pedido1.setNumeroPedido("PED001");
        Pedido pedido2 = criarPedido();
        pedido2.setNumeroPedido("PED002");
        pedidoRepository.save(pedido1);
        pedidoRepository.save(pedido2);

        mockMvc.perform(get("/api/pedidos/clientes/{clienteId}/pedidos", cliente.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(greaterThanOrEqualTo(2)));
    }

    @Test
    @DisplayName("GET /api/pedidos/restaurantes/{restauranteId}/pedidos - Deve listar pedidos do restaurante")
    void deveListarPedidosDoRestaurante() throws Exception {
        // Criar pedidos para o restaurante
        Pedido pedido1 = criarPedido();
        pedido1.setNumeroPedido("PED001");
        Pedido pedido2 = criarPedido();
        pedido2.setNumeroPedido("PED002");
        pedidoRepository.save(pedido1);
        pedidoRepository.save(pedido2);

        mockMvc.perform(get("/api/pedidos/restaurantes/{restauranteId}/pedidos", restaurante.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(greaterThanOrEqualTo(2)));
    }

    @Test
    @DisplayName("POST /api/pedidos/calcular - Deve calcular total do pedido")
    void deveCalcularTotalPedido() throws Exception {
        mockMvc.perform(post("/api/pedidos/calcular")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(54.80))
                .andExpect(jsonPath("$.message").value("Total calculado com sucesso"));
    }

    private Pedido criarPedido() {
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido("PED123456");
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE.name());
        pedido.setValorTotal(new BigDecimal("54.80"));
        pedido.setObservacoes("Sem cebola");
        pedido.setClienteId(cliente.getId());
        pedido.setRestaurante(restaurante);
        pedido.setItens("Pizza Margherita, Pizza Calabresa");
        return pedido;
    }
}

