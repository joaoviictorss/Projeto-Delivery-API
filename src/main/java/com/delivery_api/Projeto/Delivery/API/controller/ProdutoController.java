package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.response.ApiResponse;
import com.delivery_api.Projeto.Delivery.API.dto.response.PagedResponse;
import com.delivery_api.Projeto.Delivery.API.dto.response.ProdutoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Produto;
import com.delivery_api.Projeto.Delivery.API.exceptions.EntityNotFoundException;
import com.delivery_api.Projeto.Delivery.API.service.ProdutoService;
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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
@Tag(name = "Produtos", description = "Operações relacionadas aos produtos")
public class ProdutoController {
    
    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    @Operation(summary = "Cadastrar produto", description = "Cria um novo produto no sistema")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ApiResponse<ProdutoResponseDTO>> cadastrar(@Valid @RequestBody Produto produto) {
        Produto produtoSalvo = produtoService.cadastrar(produto);
        ProdutoResponseDTO response = toResponseDTO(produtoSalvo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/produtos/" + response.getId())
                .body(ApiResponse.success(response, "Produto cadastrado com sucesso"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Retorna um produto específico pelo ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ApiResponse<ProdutoResponseDTO>> buscarPorId(
            @Parameter(description = "ID do produto") @PathVariable Long id) {
        Produto produto = produtoService.buscarPorId(id);
        ProdutoResponseDTO response = toResponseDTO(produto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ApiResponse<ProdutoResponseDTO>> atualizar(
            @Parameter(description = "ID do produto") @PathVariable Long id,
            @Valid @RequestBody Produto produto) {
        Produto atualizado = produtoService.atualizar(id, produto);
        ProdutoResponseDTO response = toResponseDTO(atualizado);
        return ResponseEntity.ok(ApiResponse.success(response, "Produto atualizado com sucesso"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover produto", description = "Remove um produto do sistema")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<Void> remover(
            @Parameter(description = "ID do produto") @PathVariable Long id) {
        produtoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/disponibilidade")
    @Operation(summary = "Alterar disponibilidade do produto", description = "Altera a disponibilidade de um produto (toggle)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Disponibilidade atualizada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ApiResponse<ProdutoResponseDTO>> alterarDisponibilidade(
            @Parameter(description = "ID do produto") @PathVariable Long id) {
        Produto produto = produtoService.buscarPorId(id);
        produto.setDisponivel(!produto.getDisponivel());
        Produto atualizado = produtoService.atualizar(id, produto);
        ProdutoResponseDTO response = toResponseDTO(atualizado);
        return ResponseEntity.ok(ApiResponse.success(response, "Disponibilidade alterada com sucesso"));
    }

    @GetMapping("/restaurantes/{restauranteId}/produtos")
    @Operation(summary = "Listar produtos de um restaurante", description = "Retorna todos os produtos de um restaurante específico")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de produtos retornada")
    })
    public ResponseEntity<ApiResponse<List<ProdutoResponseDTO>>> listarPorRestaurante(
            @Parameter(description = "ID do restaurante") @PathVariable Long restauranteId,
            @Parameter(description = "Filtrar apenas disponíveis") @RequestParam(required = false) Boolean disponivel) {
        List<Produto> produtos = produtoService.buscarPorRestaurante(restauranteId);
        
        if (disponivel != null && disponivel) {
            produtos = produtos.stream()
                    .filter(Produto::getDisponivel)
                    .collect(Collectors.toList());
        }
        
        List<ProdutoResponseDTO> response = produtos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar produtos por categoria", description = "Retorna todos os produtos de uma categoria específica")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de produtos retornada")
    })
    public ResponseEntity<ApiResponse<List<ProdutoResponseDTO>>> buscarPorCategoria(
            @Parameter(description = "Categoria do produto") @PathVariable String categoria) {
        List<Produto> produtos = produtoService.buscarPorCategoria(categoria);
        List<ProdutoResponseDTO> response = produtos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar produtos por nome", description = "Busca produtos cujo nome contém o termo informado")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de produtos encontrados")
    })
    public ResponseEntity<ApiResponse<List<ProdutoResponseDTO>>> buscarPorNome(
            @Parameter(description = "Termo de busca") @RequestParam String nome) {
        // Implementação simplificada - buscar todos e filtrar
        List<Produto> todosProdutos = produtoService.listarTodos().stream()
                .map(dto -> {
                    Produto p = new Produto();
                    p.setId(dto.getId());
                    p.setNome(dto.getNome());
                    p.setDescricao(dto.getDescricao());
                    p.setPreco(dto.getPreco());
                    p.setCategoria(dto.getCategoria());
                    p.setDisponivel(dto.getDisponivel());
                    return p;
                })
                .filter(p -> p.getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
        
        List<ProdutoResponseDTO> response = todosProdutos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
            produto.getId(),
            produto.getNome(),
            produto.getDescricao(),
            produto.getPreco(),
            produto.getCategoria(),
            produto.getDisponivel()
        );
    }
}
