package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.request.PedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.ApiResponse;
import com.delivery_api.Projeto.Delivery.API.dto.response.PedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Pedido;
import com.delivery_api.Projeto.Delivery.API.enums.StatusPedido;
import com.delivery_api.Projeto.Delivery.API.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
@Tag(name = "Pedidos", description = "Operações relacionadas aos pedidos")
public class PedidoController {
    
    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    @Operation(summary = "Criar pedido", description = "Cria um novo pedido no sistema")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente ou Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> criarPedido(@Valid @RequestBody PedidoRequestDTO dto) {
        Pedido pedido = pedidoService.criarPedido(dto);
        PedidoResponseDTO response = toResponseDTO(pedido);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/pedidos/" + response.getId())
                .body(ApiResponse.success(response, "Pedido criado com sucesso"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna um pedido completo pelo ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> buscarPorId(
            @Parameter(description = "ID do pedido") @PathVariable Long id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        PedidoResponseDTO response = toResponseDTO(pedido);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Listar pedidos", description = "Lista pedidos com filtros opcionais (status, data)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de pedidos retornada")
    })
    public ResponseEntity<ApiResponse<List<PedidoResponseDTO>>> listar(
            @Parameter(description = "Status do pedido") @RequestParam(required = false) StatusPedido status,
            @Parameter(description = "Data inicial (formato: yyyy-MM-ddTHH:mm:ss)") @RequestParam(required = false) String dataInicio,
            @Parameter(description = "Data final (formato: yyyy-MM-ddTHH:mm:ss)") @RequestParam(required = false) String dataFim) {
        
        List<Pedido> pedidos;
        
        if (status != null) {
            pedidos = pedidoService.listarPorStatus(status);
        } else if (dataInicio != null && dataFim != null) {
            LocalDateTime inicio = LocalDateTime.parse(dataInicio);
            LocalDateTime fim = LocalDateTime.parse(dataFim);
            pedidos = pedidoService.listarPorPeriodo(inicio, fim);
        } else {
            pedidos = pedidoService.listarTodos();
        }
        
        List<PedidoResponseDTO> response = pedidos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do pedido", description = "Atualiza o status de um pedido")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Status inválido")
    })
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> atualizarStatus(
            @Parameter(description = "ID do pedido") @PathVariable Long id,
            @Parameter(description = "Novo status") @RequestParam StatusPedido status) {
        Pedido pedido = pedidoService.atualizarStatus(id, status);
        PedidoResponseDTO response = toResponseDTO(pedido);
        return ResponseEntity.ok(ApiResponse.success(response, "Status atualizado com sucesso"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar pedido", description = "Cancela um pedido (soft delete)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Pedido não pode ser cancelado")
    })
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> cancelar(
            @Parameter(description = "ID do pedido") @PathVariable Long id) {
        Pedido pedido = pedidoService.atualizarStatus(id, StatusPedido.CANCELADO);
        PedidoResponseDTO response = toResponseDTO(pedido);
        return ResponseEntity.ok(ApiResponse.success(response, "Pedido cancelado com sucesso"));
    }

    @GetMapping("/clientes/{clienteId}/pedidos")
    @Operation(summary = "Histórico de pedidos do cliente", description = "Retorna todos os pedidos de um cliente específico")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de pedidos retornada")
    })
    public ResponseEntity<ApiResponse<List<PedidoResponseDTO>>> historicoCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId) {
        List<Pedido> pedidos = pedidoService.buscarPedidosPorCliente(clienteId);
        List<PedidoResponseDTO> response = pedidos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/restaurantes/{restauranteId}/pedidos")
    @Operation(summary = "Pedidos de um restaurante", description = "Retorna todos os pedidos de um restaurante específico")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de pedidos retornada")
    })
    public ResponseEntity<ApiResponse<List<PedidoResponseDTO>>> pedidosRestaurante(
            @Parameter(description = "ID do restaurante") @PathVariable Long restauranteId) {
        List<Pedido> todosPedidos = pedidoService.listarTodos();
        List<Pedido> pedidos = todosPedidos.stream()
                .filter(p -> p.getRestaurante() != null && p.getRestaurante().getId().equals(restauranteId))
                .collect(Collectors.toList());
        
        List<PedidoResponseDTO> response = pedidos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/calcular")
    @Operation(summary = "Calcular total do pedido", description = "Calcula o valor total de um pedido sem salvá-lo")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Total calculado com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ApiResponse<BigDecimal>> calcularTotal(@Valid @RequestBody PedidoRequestDTO dto) {
        // Lógica simplificada - usar valorTotal do DTO
        BigDecimal total = dto.getValorTotal() != null ? dto.getValorTotal() : BigDecimal.ZERO;
        return ResponseEntity.ok(ApiResponse.success(total, "Total calculado com sucesso"));
    }

    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setNumeroPedido(pedido.getNumeroPedido());
        dto.setDataPedido(pedido.getDataPedido());
        dto.setStatus(pedido.getStatus());
        dto.setValorTotal(pedido.getValorTotal());
        dto.setObservacoes(pedido.getObservacoes());
        dto.setClienteId(pedido.getClienteId());
        dto.setRestauranteId(pedido.getRestaurante() != null ? pedido.getRestaurante().getId() : null);
        dto.setItens(pedido.getItens());
        return dto;
    }
}
