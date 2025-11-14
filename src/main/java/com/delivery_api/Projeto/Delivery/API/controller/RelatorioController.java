package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.response.ApiResponse;
import com.delivery_api.Projeto.Delivery.API.projection.RelatorioVendas;
import com.delivery_api.Projeto.Delivery.API.repository.PedidoRepository;
import com.delivery_api.Projeto.Delivery.API.repository.ProdutoRepository;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteRepository;
import com.delivery_api.Projeto.Delivery.API.repository.ClienteRepository;
import com.delivery_api.Projeto.Delivery.API.service.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*")
@Tag(name = "Relatórios", description = "Endpoints para geração de relatórios e análises")
public class RelatorioController {

    @Autowired
    private RestauranteService restauranteService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/vendas-por-restaurante")
    @Operation(summary = "Vendas por restaurante", description = "Retorna relatório de vendas agrupado por restaurante")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    })
    public ResponseEntity<ApiResponse<List<RelatorioVendas>>> vendasPorRestaurante(
            @Parameter(description = "Data inicial (formato: yyyy-MM-ddTHH:mm:ss)") @RequestParam(required = false) String dataInicio,
            @Parameter(description = "Data final (formato: yyyy-MM-ddTHH:mm:ss)") @RequestParam(required = false) String dataFim) {
        List<RelatorioVendas> relatorio = restauranteService.relatorioVendasPorRestaurante();
        return ResponseEntity.ok(ApiResponse.success(relatorio));
    }

    @GetMapping("/produtos-mais-vendidos")
    @Operation(summary = "Produtos mais vendidos", description = "Retorna ranking dos produtos mais vendidos")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    })
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> produtosMaisVendidos(
            @Parameter(description = "Limite de resultados") @RequestParam(defaultValue = "10") int limite) {
        // Implementação simplificada - em produção, usar consulta nativa
        List<Map<String, Object>> produtos = List.of(
            Map.of("produto", "Pizza Margherita", "quantidade", 150, "totalVendas", 5385.00),
            Map.of("produto", "X-Burger", "quantidade", 120, "totalVendas", 2268.00),
            Map.of("produto", "Combo Sashimi", "quantidade", 80, "totalVendas", 3672.00)
        );
        return ResponseEntity.ok(ApiResponse.success(produtos));
    }

    @GetMapping("/clientes-ativos")
    @Operation(summary = "Clientes mais ativos", description = "Retorna ranking de clientes por número de pedidos")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    })
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> clientesAtivos(
            @Parameter(description = "Limite de resultados") @RequestParam(defaultValue = "10") int limite) {
        // Implementação simplificada - em produção, usar consulta nativa
        List<Map<String, Object>> clientes = List.of(
            Map.of("cliente", "João Silva", "totalPedidos", 25, "valorTotal", 1250.50),
            Map.of("cliente", "Maria Santos", "totalPedidos", 18, "valorTotal", 890.30),
            Map.of("cliente", "Pedro Oliveira", "totalPedidos", 12, "valorTotal", 650.20)
        );
        return ResponseEntity.ok(ApiResponse.success(clientes));
    }

    @GetMapping("/pedidos-por-periodo")
    @Operation(summary = "Pedidos por período", description = "Retorna relatório de pedidos em um período específico")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> pedidosPorPeriodo(
            @Parameter(description = "Data inicial (formato: yyyy-MM-ddTHH:mm:ss)") @RequestParam String dataInicio,
            @Parameter(description = "Data final (formato: yyyy-MM-ddTHH:mm:ss)") @RequestParam String dataFim,
            @Parameter(description = "Status do pedido") @RequestParam(required = false) String status) {
        
        LocalDateTime inicio = LocalDateTime.parse(dataInicio);
        LocalDateTime fim = LocalDateTime.parse(dataFim);
        
        var pedidos = pedidoRepository.findByDataPedidoBetween(inicio, fim);
        
        if (status != null) {
            pedidos = pedidos.stream()
                    .filter(p -> p.getStatus().equals(status))
                    .toList();
        }
        
        long totalPedidos = pedidos.size();
        double valorTotal = pedidos.stream()
                .mapToDouble(p -> p.getValorTotal() != null ? p.getValorTotal().doubleValue() : 0.0)
                .sum();
        
        Map<String, Object> relatorio = Map.of(
            "periodo", Map.of("inicio", dataInicio, "fim", dataFim),
            "totalPedidos", totalPedidos,
            "valorTotal", valorTotal,
            "ticketMedio", totalPedidos > 0 ? valorTotal / totalPedidos : 0.0
        );
        
        return ResponseEntity.ok(ApiResponse.success(relatorio));
    }
}

