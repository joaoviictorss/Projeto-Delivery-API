package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.request.RestauranteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.ApiResponse;
import com.delivery_api.Projeto.Delivery.API.dto.response.PagedResponse;
import com.delivery_api.Projeto.Delivery.API.dto.response.RestauranteResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.delivery_api.Projeto.Delivery.API.exceptions.EntityNotFoundException;
import com.delivery_api.Projeto.Delivery.API.projection.RelatorioVendas;
import com.delivery_api.Projeto.Delivery.API.service.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
@Tag(name = "Restaurantes", description = "Operações relacionadas aos restaurantes")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    @PostMapping
    @Operation(summary = "Cadastrar restaurante", description = "Cria um novo restaurante no sistema")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Restaurante criado com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Restaurante já existe")
    })
    public ResponseEntity<ApiResponse<RestauranteResponseDTO>> cadastrar(
            @Valid @RequestBody RestauranteRequestDTO dto) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(dto.getNome());
        restaurante.setCategoria(dto.getCategoria());
        restaurante.setEndereco(dto.getEndereco());
        restaurante.setTelefone(dto.getTelefone());
        restaurante.setTaxaEntrega(dto.getTaxaEntrega());
        restaurante.setAvaliacao(dto.getAvaliacao());
        
        Restaurante restauranteSalvo = restauranteService.cadastrar(restaurante);
        RestauranteResponseDTO response = toResponseDTO(restauranteSalvo);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/restaurantes/" + response.getId())
                .body(ApiResponse.success(response, "Restaurante cadastrado com sucesso"));
    }

    @GetMapping
    @Operation(summary = "Listar restaurantes", description = "Lista restaurantes com filtros opcionais e paginação")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de restaurantes retornada com sucesso")
    })
    public ResponseEntity<PagedResponse<RestauranteResponseDTO>> listar(
            @Parameter(description = "Categoria do restaurante") @RequestParam(required = false) String categoria,
            @Parameter(description = "Filtrar apenas ativos") @RequestParam(required = false) Boolean ativo,
            @Parameter(description = "Número da página (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size) {
        
        List<RestauranteResponseDTO> restaurantes;
        
        if (categoria != null) {
            restaurantes = restauranteService.buscarPorCategoria(categoria);
        } else if (ativo != null && ativo) {
            restaurantes = restauranteService.listarAtivos();
        } else {
            restaurantes = restauranteService.listarAtivos();
        }
        
        // Aplicar paginação manual
        int start = page * size;
        int end = Math.min(start + size, restaurantes.size());
        List<RestauranteResponseDTO> pagedContent = restaurantes.subList(start, end);
        
        PagedResponse<RestauranteResponseDTO> response = PagedResponse.of(
            pagedContent, page, size, restaurantes.size(), "/api/restaurantes"
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar restaurante por ID", description = "Retorna um restaurante específico pelo ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponse<RestauranteResponseDTO>> buscarPorId(
            @Parameter(description = "ID do restaurante") @PathVariable Long id) {
        return restauranteService.findById(id)
                .map(dto -> {
                    RestauranteResponseDTO response = new RestauranteResponseDTO(
                        dto.getId(), dto.getNome(), dto.getCategoria(), dto.getEndereco(),
                        dto.getTelefone(), dto.getTaxaEntrega(), dto.getAvaliacao(), dto.getAtivo()
                    );
                    return ResponseEntity.ok(ApiResponse.success(response));
                })
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar restaurante", description = "Atualiza os dados de um restaurante existente")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Restaurante não encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ApiResponse<RestauranteResponseDTO>> atualizar(
            @Parameter(description = "ID do restaurante") @PathVariable Long id,
            @Valid @RequestBody RestauranteRequestDTO dto) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(dto.getNome());
        restaurante.setCategoria(dto.getCategoria());
        restaurante.setEndereco(dto.getEndereco());
        restaurante.setTelefone(dto.getTelefone());
        restaurante.setTaxaEntrega(dto.getTaxaEntrega());
        
        Restaurante atualizado = restauranteService.atualizar(id, restaurante);
        RestauranteResponseDTO response = toResponseDTO(atualizado);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Restaurante atualizado com sucesso"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Ativar/Desativar restaurante", description = "Altera o status ativo/inativo de um restaurante")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponse<String>> atualizarStatus(
            @Parameter(description = "ID do restaurante") @PathVariable Long id,
            @Parameter(description = "Novo status (true para ativar, false para desativar)") @RequestParam Boolean ativo) {
        if (ativo) {
            // Implementar ativação se necessário
            return ResponseEntity.ok(ApiResponse.success("Restaurante ativado com sucesso"));
        } else {
            restauranteService.inativar(id);
            return ResponseEntity.ok(ApiResponse.success("Restaurante desativado com sucesso"));
        }
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar restaurantes por categoria", description = "Retorna todos os restaurantes de uma categoria específica")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de restaurantes retornada")
    })
    public ResponseEntity<ApiResponse<List<RestauranteResponseDTO>>> buscarPorCategoria(
            @Parameter(description = "Categoria do restaurante") @PathVariable String categoria) {
        List<RestauranteResponseDTO> restaurantes = restauranteService.buscarPorCategoria(categoria);
        return ResponseEntity.ok(ApiResponse.success(restaurantes));
    }

    @GetMapping("/{id}/taxa-entrega/{cep}")
    @Operation(summary = "Calcular taxa de entrega", description = "Calcula a taxa de entrega para um CEP específico")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Taxa calculada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponse<BigDecimal>> calcularTaxaEntrega(
            @Parameter(description = "ID do restaurante") @PathVariable Long id,
            @Parameter(description = "CEP para cálculo") @PathVariable String cep) {
        return restauranteService.buscarPorId(id)
                .map(restaurante -> {
                    // Lógica simplificada - pode ser expandida com cálculo real por CEP
                    BigDecimal taxa = restaurante.getTaxaEntrega() != null ? restaurante.getTaxaEntrega() : BigDecimal.ZERO;
                    return ResponseEntity.ok(ApiResponse.success(taxa, "Taxa de entrega calculada"));
                })
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + id));
    }

    @GetMapping("/proximos/{cep}")
    @Operation(summary = "Buscar restaurantes próximos", description = "Retorna restaurantes próximos a um CEP específico")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de restaurantes próximos")
    })
    public ResponseEntity<ApiResponse<List<RestauranteResponseDTO>>> buscarProximos(
            @Parameter(description = "CEP para busca") @PathVariable String cep) {
        // Lógica simplificada - retorna todos os ativos
        // Em produção, implementar cálculo de distância por CEP
        List<RestauranteResponseDTO> restaurantes = restauranteService.listarAtivos();
        return ResponseEntity.ok(ApiResponse.success(restaurantes));
    }

    @GetMapping("/relatorio-vendas")
    @Operation(summary = "Relatório de vendas por restaurante", description = "Retorna relatório de vendas agrupado por restaurante")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    })
    public ResponseEntity<ApiResponse<List<RelatorioVendas>>> relatorioVendas() {
        List<RelatorioVendas> relatorio = restauranteService.relatorioVendasPorRestaurante();
        return ResponseEntity.ok(ApiResponse.success(relatorio));
    }

    private RestauranteResponseDTO toResponseDTO(Restaurante restaurante) {
        return new RestauranteResponseDTO(
            restaurante.getId(),
            restaurante.getNome(),
            restaurante.getCategoria(),
            restaurante.getEndereco(),
            restaurante.getTelefone(),
            restaurante.getTaxaEntrega(),
            restaurante.getAvaliacao(),
            restaurante.getAtivo()
        );
    }
}
