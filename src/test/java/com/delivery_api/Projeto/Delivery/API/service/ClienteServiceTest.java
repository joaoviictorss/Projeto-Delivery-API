package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.dto.request.ClienteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.ClienteResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Cliente;
import com.delivery_api.Projeto.Delivery.API.exceptions.BusinessException;
import com.delivery_api.Projeto.Delivery.API.repository.ClienteRepository;
import com.delivery_api.Projeto.Delivery.API.service.impl.ClienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - ClienteService")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private ClienteRequestDTO clienteRequestDTO;
    private Cliente cliente;
    private ClienteResponseDTO clienteResponseDTO;

    @BeforeEach
    void setUp() {
        // Arrange - Preparar dados de teste
        clienteRequestDTO = new ClienteRequestDTO();
        clienteRequestDTO.setNome("João Silva");
        clienteRequestDTO.setEmail("joao@email.com");
        clienteRequestDTO.setTelefone("(11) 99999-1111");
        clienteRequestDTO.setEndereco("Rua A, 123");

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao@email.com");
        cliente.setTelefone("(11) 99999-1111");
        cliente.setEndereco("Rua A, 123");
        cliente.setAtivo(true);
        cliente.setDataCadastro(LocalDateTime.now());

        clienteResponseDTO = new ClienteResponseDTO();
        clienteResponseDTO.setId(1L);
        clienteResponseDTO.setNome("João Silva");
        clienteResponseDTO.setEmail("joao@email.com");
        clienteResponseDTO.setTelefone("(11) 99999-1111");
        clienteResponseDTO.setEndereco("Rua A, 123");
        clienteResponseDTO.setAtivo(true);
    }

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso quando dados são válidos")
    void deveCadastrarClienteComSucesso() {
        // Arrange
        when(clienteRepository.existsByEmail(clienteRequestDTO.getEmail())).thenReturn(false);
        when(modelMapper.map(clienteRequestDTO, Cliente.class)).thenReturn(cliente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(clienteResponseDTO);

        // Act
        ClienteResponseDTO resultado = clienteService.cadastrar(clienteRequestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertEquals("joao@email.com", resultado.getEmail());
        assertTrue(resultado.getAtivo());

        // Verify
        verify(clienteRepository, times(1)).existsByEmail(clienteRequestDTO.getEmail());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(modelMapper, times(1)).map(clienteRequestDTO, Cliente.class);
        verify(modelMapper, times(1)).map(cliente, ClienteResponseDTO.class);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando email já está cadastrado")
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        // Arrange
        when(clienteRepository.existsByEmail(clienteRequestDTO.getEmail())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            clienteService.cadastrar(clienteRequestDTO);
        });

        assertEquals("Email já cadastrado" + clienteRequestDTO.getEmail(), exception.getMessage());

        // Verify
        verify(clienteRepository, times(1)).existsByEmail(clienteRequestDTO.getEmail());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve definir cliente como ativo e data de cadastro ao criar")
    void deveDefinirClienteAtivoEDataCadastro() {
        // Arrange
        Cliente clienteNovo = new Cliente();
        when(clienteRepository.existsByEmail(clienteRequestDTO.getEmail())).thenReturn(false);
        when(modelMapper.map(clienteRequestDTO, Cliente.class)).thenReturn(clienteNovo);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente clienteSalvo = invocation.getArgument(0);
            clienteSalvo.setId(1L);
            assertTrue(clienteSalvo.getAtivo());
            assertNotNull(clienteSalvo.getDataCadastro());
            return clienteSalvo;
        });
        when(modelMapper.map(any(Cliente.class), eq(ClienteResponseDTO.class))).thenReturn(clienteResponseDTO);

        // Act
        clienteService.cadastrar(clienteRequestDTO);

        // Assert
        verify(clienteRepository, times(1)).save(argThat(c -> 
            c.getAtivo() != null && c.getAtivo() && c.getDataCadastro() != null
        ));
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso quando dados são válidos")
    void deveAtualizarClienteComSucesso() {
        // Arrange
        Long clienteId = 1L;
        ClienteRequestDTO dtoAtualizado = new ClienteRequestDTO();
        dtoAtualizado.setNome("João Silva Atualizado");
        dtoAtualizado.setEmail("joao.novo@email.com");
        dtoAtualizado.setTelefone("(11) 88888-2222");
        dtoAtualizado.setEndereco("Rua B, 456");

        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setId(clienteId);
        clienteAtualizado.setNome("João Silva Atualizado");
        clienteAtualizado.setEmail("joao.novo@email.com");

        ClienteResponseDTO responseAtualizado = new ClienteResponseDTO();
        responseAtualizado.setId(clienteId);
        responseAtualizado.setNome("João Silva Atualizado");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.existsByEmail(dtoAtualizado.getEmail())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteAtualizado);
        when(modelMapper.map(any(Cliente.class), eq(ClienteResponseDTO.class))).thenReturn(responseAtualizado);

        // Act
        ClienteResponseDTO resultado = clienteService.atualizar(clienteId, dtoAtualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals("João Silva Atualizado", resultado.getNome());

        // Verify
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(clienteRepository, times(1)).existsByEmail(dtoAtualizado.getEmail());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao atualizar cliente inexistente")
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        // Arrange
        Long clienteIdInexistente = 999L;
        when(clienteRepository.findById(clienteIdInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            clienteService.atualizar(clienteIdInexistente, clienteRequestDTO);
        });

        assertEquals("Cliente não encontrado com id: " + clienteIdInexistente, exception.getMessage());

        // Verify
        verify(clienteRepository, times(1)).findById(clienteIdInexistente);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao atualizar com email duplicado")
    void deveLancarExcecaoAoAtualizarComEmailDuplicado() {
        // Arrange
        Long clienteId = 1L;
        ClienteRequestDTO dtoComEmailDuplicado = new ClienteRequestDTO();
        dtoComEmailDuplicado.setEmail("email.existente@email.com");
        dtoComEmailDuplicado.setNome("Nome Válido");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.existsByEmail(dtoComEmailDuplicado.getEmail())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            clienteService.atualizar(clienteId, dtoComEmailDuplicado);
        });

        assertEquals("Email já cadastrado" + dtoComEmailDuplicado.getEmail(), exception.getMessage());

        // Verify
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(clienteRepository, times(1)).existsByEmail(dtoComEmailDuplicado.getEmail());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao atualizar com nome vazio")
    void deveLancarExcecaoAoAtualizarComNomeVazio() {
        // Arrange
        Long clienteId = 1L;
        ClienteRequestDTO dtoComNomeVazio = new ClienteRequestDTO();
        dtoComNomeVazio.setNome("");
        dtoComNomeVazio.setEmail("email@email.com");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.existsByEmail(dtoComNomeVazio.getEmail())).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            clienteService.atualizar(clienteId, dtoComNomeVazio);
        });

        assertEquals("Nome não pode ser vazio", exception.getMessage());

        // Verify
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao atualizar com email vazio")
    void deveLancarExcecaoAoAtualizarComEmailVazio() {
        // Arrange
        Long clienteId = 1L;
        ClienteRequestDTO dtoComEmailVazio = new ClienteRequestDTO();
        dtoComEmailVazio.setNome("Nome Válido");
        dtoComEmailVazio.setEmail("");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.existsByEmail(dtoComEmailVazio.getEmail())).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            clienteService.atualizar(clienteId, dtoComEmailVazio);
        });

        assertEquals("Email não pode ser vazio", exception.getMessage());

        // Verify
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve retornar null ao buscar cliente por ID (método não implementado)")
    void deveRetornarNullAoBuscarPorId() {
        // Act
        ClienteResponseDTO resultado = clienteService.buscarPorId(1L);

        // Assert
        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao listar clientes ativos (método não implementado)")
    void deveRetornarListaVaziaAoListarAtivos() {
        // Act
        List<ClienteResponseDTO> resultado = clienteService.listarAtivos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar por nome (método não implementado)")
    void deveRetornarListaVaziaAoBuscarPorNome() {
        // Act
        List<ClienteResponseDTO> resultado = clienteService.buscarPorNome("João");

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar null ao ativar/desativar cliente (método não implementado)")
    void deveRetornarNullAoAtivarDesativar() {
        // Act
        ClienteResponseDTO resultado = clienteService.ativarDesativar(1L);

        // Assert
        assertNull(resultado);
    }
}

