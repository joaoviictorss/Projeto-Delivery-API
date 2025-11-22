package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.dto.request.PedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Cliente;
import com.delivery_api.Projeto.Delivery.API.entity.Pedido;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.delivery_api.Projeto.Delivery.API.enums.StatusPedido;
import com.delivery_api.Projeto.Delivery.API.repository.ClienteRepository;
import com.delivery_api.Projeto.Delivery.API.repository.PedidoRepository;
import com.delivery_api.Projeto.Delivery.API.repository.ProdutoRepository;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - PedidoService")
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private RestauranteRepository restauranteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private PedidoRequestDTO pedidoRequestDTO;
    private Cliente cliente;
    private Restaurante restaurante;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        // Arrange - Preparar dados de teste
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao@email.com");
        cliente.setAtivo(true);

        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNome("Pizzaria Bella");
        restaurante.setAtivo(true);

        pedidoRequestDTO = new PedidoRequestDTO();
        pedidoRequestDTO.setNumeroPedido("PED123456");
        pedidoRequestDTO.setDataPedido(LocalDateTime.now());
        pedidoRequestDTO.setValorTotal(new BigDecimal("54.80"));
        pedidoRequestDTO.setObservacoes("Sem cebola");
        pedidoRequestDTO.setClienteId(1L);
        pedidoRequestDTO.setRestauranteId(1L);
        pedidoRequestDTO.setItens("Pizza Margherita, Pizza Calabresa");
        pedidoRequestDTO.setStatus("PENDENTE");

        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setNumeroPedido("PED123456");
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setValorTotal(new BigDecimal("54.80"));
        pedido.setObservacoes("Sem cebola");
        pedido.setClienteId(1L);
        pedido.setRestaurante(restaurante);
        pedido.setStatus(StatusPedido.PENDENTE.name());
        pedido.setItens("Pizza Margherita, Pizza Calabresa");
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso quando dados são válidos")
    void deveCriarPedidoComSucesso() {
        // Arrange
        when(clienteRepository.findById(pedidoRequestDTO.getClienteId())).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(pedidoRequestDTO.getRestauranteId())).thenReturn(Optional.of(restaurante));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Act
        Pedido resultado = pedidoService.criarPedido(pedidoRequestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("PED123456", resultado.getNumeroPedido());
        assertEquals(StatusPedido.PENDENTE.name(), resultado.getStatus());
        assertEquals(new BigDecimal("54.80"), resultado.getValorTotal());
        assertEquals(1L, resultado.getClienteId());

        // Verify
        verify(clienteRepository, times(1)).findById(pedidoRequestDTO.getClienteId());
        verify(restauranteRepository, times(1)).findById(pedidoRequestDTO.getRestauranteId());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando cliente não existe")
    void deveLancarExcecaoQuandoClienteNaoExiste() {
        // Arrange
        when(clienteRepository.findById(pedidoRequestDTO.getClienteId())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.criarPedido(pedidoRequestDTO);
        });

        assertEquals("Cliente não encontrado: " + pedidoRequestDTO.getClienteId(), exception.getMessage());

        // Verify
        verify(clienteRepository, times(1)).findById(pedidoRequestDTO.getClienteId());
        verify(restauranteRepository, never()).findById(anyLong());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando restaurante não existe")
    void deveLancarExcecaoQuandoRestauranteNaoExiste() {
        // Arrange
        when(clienteRepository.findById(pedidoRequestDTO.getClienteId())).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(pedidoRequestDTO.getRestauranteId())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.criarPedido(pedidoRequestDTO);
        });

        assertEquals("Restaurante não encontrado: " + pedidoRequestDTO.getRestauranteId(), exception.getMessage());

        // Verify
        verify(clienteRepository, times(1)).findById(pedidoRequestDTO.getClienteId());
        verify(restauranteRepository, times(1)).findById(pedidoRequestDTO.getRestauranteId());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando cliente está inativo")
    void deveLancarExcecaoQuandoClienteInativo() {
        // Arrange
        cliente.setAtivo(false);
        when(clienteRepository.findById(pedidoRequestDTO.getClienteId())).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(pedidoRequestDTO.getRestauranteId())).thenReturn(Optional.of(restaurante));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.criarPedido(pedidoRequestDTO);
        });

        assertEquals("Cliente inativo não pode fazer pedidos", exception.getMessage());

        // Verify
        verify(clienteRepository, times(1)).findById(pedidoRequestDTO.getClienteId());
        verify(restauranteRepository, times(1)).findById(pedidoRequestDTO.getRestauranteId());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando restaurante está inativo")
    void deveLancarExcecaoQuandoRestauranteInativo() {
        // Arrange
        restaurante.setAtivo(false);
        when(clienteRepository.findById(pedidoRequestDTO.getClienteId())).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(pedidoRequestDTO.getRestauranteId())).thenReturn(Optional.of(restaurante));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.criarPedido(pedidoRequestDTO);
        });

        assertEquals("Restaurante não está disponível", exception.getMessage());

        // Verify
        verify(clienteRepository, times(1)).findById(pedidoRequestDTO.getClienteId());
        verify(restauranteRepository, times(1)).findById(pedidoRequestDTO.getRestauranteId());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve definir status como PENDENTE ao criar pedido")
    void deveDefinirStatusPendenteAoCriarPedido() {
        // Arrange
        when(clienteRepository.findById(pedidoRequestDTO.getClienteId())).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(pedidoRequestDTO.getRestauranteId())).thenReturn(Optional.of(restaurante));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedidoSalvo = invocation.getArgument(0);
            assertEquals(StatusPedido.PENDENTE.name(), pedidoSalvo.getStatus());
            return pedido;
        });

        // Act
        pedidoService.criarPedido(pedidoRequestDTO);

        // Assert
        verify(pedidoRepository, times(1)).save(argThat(p -> 
            StatusPedido.PENDENTE.name().equals(p.getStatus())
        ));
    }

    @Test
    @DisplayName("Deve listar pedidos por cliente com sucesso")
    void deveListarPedidosPorCliente() {
        // Arrange
        Long clienteId = 1L;
        List<Pedido> pedidos = Arrays.asList(pedido);
        when(pedidoRepository.findByClienteIdOrderByDataPedidoDesc(clienteId)).thenReturn(pedidos);

        // Act
        List<Pedido> resultado = pedidoService.listarPorCliente(clienteId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(pedido.getNumeroPedido(), resultado.get(0).getNumeroPedido());

        // Verify
        verify(pedidoRepository, times(1)).findByClienteIdOrderByDataPedidoDesc(clienteId);
    }

    @Test
    @DisplayName("Deve atualizar status do pedido com sucesso")
    void deveAtualizarStatusPedidoComSucesso() {
        // Arrange
        Long pedidoId = 1L;
        StatusPedido novoStatus = StatusPedido.CONFIRMADO;
        
        Pedido pedidoAtualizado = new Pedido();
        pedidoAtualizado.setId(pedidoId);
        pedidoAtualizado.setStatus(novoStatus.name());

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoAtualizado);

        // Act
        Pedido resultado = pedidoService.atualizarStatus(pedidoId, novoStatus);

        // Assert
        assertNotNull(resultado);
        assertEquals(novoStatus.name(), resultado.getStatus());

        // Verify
        verify(pedidoRepository, times(1)).findById(pedidoId);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao atualizar status de pedido inexistente")
    void deveLancarExcecaoAoAtualizarStatusPedidoInexistente() {
        // Arrange
        Long pedidoIdInexistente = 999L;
        when(pedidoRepository.findById(pedidoIdInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.atualizarStatus(pedidoIdInexistente, StatusPedido.CONFIRMADO);
        });

        assertEquals("Pedido não encontrado: " + pedidoIdInexistente, exception.getMessage());

        // Verify
        verify(pedidoRepository, times(1)).findById(pedidoIdInexistente);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao atualizar status de pedido já entregue")
    void deveLancarExcecaoAoAtualizarStatusPedidoEntregue() {
        // Arrange
        Long pedidoId = 1L;
        pedido.setStatus(StatusPedido.ENTREGUE.name());
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.atualizarStatus(pedidoId, StatusPedido.CONFIRMADO);
        });

        assertEquals("Pedido já finalizado: " + pedidoId, exception.getMessage());

        // Verify
        verify(pedidoRepository, times(1)).findById(pedidoId);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve buscar pedidos por cliente com sucesso")
    void deveBuscarPedidosPorCliente() {
        // Arrange
        Long clienteId = 1L;
        List<Pedido> pedidos = Arrays.asList(pedido);
        when(pedidoRepository.findByClienteId(clienteId)).thenReturn(pedidos);

        // Act
        List<Pedido> resultado = pedidoService.buscarPedidosPorCliente(clienteId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        // Verify
        verify(pedidoRepository, times(1)).findByClienteId(clienteId);
    }

    @Test
    @DisplayName("Deve listar pedidos por status com sucesso")
    void deveListarPedidosPorStatus() {
        // Arrange
        StatusPedido status = StatusPedido.PENDENTE;
        List<Pedido> pedidos = Arrays.asList(pedido);
        when(pedidoRepository.findByStatus(status)).thenReturn(pedidos);

        // Act
        List<Pedido> resultado = pedidoService.listarPorStatus(status);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(status.name(), resultado.get(0).getStatus());

        // Verify
        verify(pedidoRepository, times(1)).findByStatus(status);
    }

    @Test
    @DisplayName("Deve listar pedidos recentes com sucesso")
    void deveListarPedidosRecentes() {
        // Arrange
        List<Pedido> pedidos = Arrays.asList(pedido);
        when(pedidoRepository.findTop10ByOrderByDataPedidoDesc()).thenReturn(pedidos);

        // Act
        List<Pedido> resultado = pedidoService.listarRecentes();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        // Verify
        verify(pedidoRepository, times(1)).findTop10ByOrderByDataPedidoDesc();
    }

    @Test
    @DisplayName("Deve listar pedidos por período com sucesso")
    void deveListarPedidosPorPeriodo() {
        // Arrange
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fim = LocalDateTime.now();
        List<Pedido> pedidos = Arrays.asList(pedido);
        when(pedidoRepository.findByDataPedidoBetween(inicio, fim)).thenReturn(pedidos);

        // Act
        List<Pedido> resultado = pedidoService.listarPorPeriodo(inicio, fim);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        // Verify
        verify(pedidoRepository, times(1)).findByDataPedidoBetween(inicio, fim);
    }

    @Test
    @DisplayName("Deve buscar pedido por ID com sucesso")
    void deveBuscarPedidoPorId() {
        // Arrange
        Long pedidoId = 1L;
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        // Act
        Pedido resultado = pedidoService.buscarPorId(pedidoId);

        // Assert
        assertNotNull(resultado);
        assertEquals(pedidoId, resultado.getId());
        assertEquals("PED123456", resultado.getNumeroPedido());

        // Verify
        verify(pedidoRepository, times(1)).findById(pedidoId);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao buscar pedido inexistente")
    void deveLancarExcecaoAoBuscarPedidoInexistente() {
        // Arrange
        Long pedidoIdInexistente = 999L;
        when(pedidoRepository.findById(pedidoIdInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.buscarPorId(pedidoIdInexistente);
        });

        assertEquals("Pedido não encontrado: " + pedidoIdInexistente, exception.getMessage());

        // Verify
        verify(pedidoRepository, times(1)).findById(pedidoIdInexistente);
    }

    @Test
    @DisplayName("Deve listar todos os pedidos com sucesso")
    void deveListarTodosPedidos() {
        // Arrange
        List<Pedido> pedidos = Arrays.asList(pedido);
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        // Act
        List<Pedido> resultado = pedidoService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        // Verify
        verify(pedidoRepository, times(1)).findAll();
    }
}

