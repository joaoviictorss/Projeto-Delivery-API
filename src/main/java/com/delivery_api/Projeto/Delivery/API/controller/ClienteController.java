package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.request.ClienteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.ApiResponse;
import com.delivery_api.Projeto.Delivery.API.dto.response.ClienteResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.delivery_api.Projeto.Delivery.API.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
@Tag(name = "Clientes", description = "Operações relacionadas aos clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    @Operation(summary = "Cadastrar cliente", description = "Cria um novo cliente na plataforma")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Cliente já cadastrado")
    })
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> cadastrar(@Valid @RequestBody ClienteRequestDTO dto) {
        ClienteResponseDTO cliente = clienteService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/clientes/" + cliente.getId())
                .body(ApiResponse.success(cliente, "Cliente cadastrado com sucesso"));
    }

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Lista todos os clientes ativos com paginação opcional")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de clientes retornada")
    })
    public ResponseEntity<ApiResponse<List<ClienteResponseDTO>>> listar(
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size) {
        List<ClienteResponseDTO> clientes = clienteService.listarAtivos();
        
        // Aplicar paginação manual
        int start = page * size;
        int end = Math.min(start + size, clientes.size());
        List<ClienteResponseDTO> pagedContent = clientes.subList(start, end);
        
        PagedResponse<ClienteResponseDTO> response = PagedResponse.of(
            pagedContent, page, size, clientes.size(), "/api/clientes"
        );
        
        return ResponseEntity.ok(ApiResponse.success(response.getContent()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna um cliente específico pelo ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> buscarPorId(
            @Parameter(description = "ID do cliente") @PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponse.success(cliente));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> atualizar(
            @Parameter(description = "ID do cliente") @PathVariable Long id,
            @Validated @RequestBody ClienteRequestDTO dto) {
        ClienteResponseDTO cliente = clienteService.atualizar(id, dto);
        return ResponseEntity.ok(ApiResponse.success(cliente, "Cliente atualizado com sucesso"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar cliente", description = "Inativa um cliente (soft delete)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente inativado com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> inativar(
            @Parameter(description = "ID do cliente") @PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.ativarDesativar(id);
        return ResponseEntity.ok(ApiResponse.success(cliente, "Cliente inativado com sucesso"));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar clientes por nome", description = "Busca clientes cujo nome contém o termo informado")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de clientes encontrados")
    })
    public ResponseEntity<ApiResponse<List<ClienteResponseDTO>>> buscarPorNome(
            @Parameter(description = "Termo de busca") @RequestParam String nome) {
        List<ClienteResponseDTO> clientes = clienteService.buscarPorNome(nome);
        return ResponseEntity.ok(ApiResponse.success(clientes));
    }
}
