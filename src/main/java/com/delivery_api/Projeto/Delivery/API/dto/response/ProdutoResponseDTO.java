package com.delivery_api.Projeto.Delivery.API.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de resposta para Produto")
public class ProdutoResponseDTO {
    @Schema(description = "ID do produto", example = "1")
    private Long id;
    
    @Schema(description = "Nome do produto", example = "Pizza Margherita")
    private String nome;
    
    @Schema(description = "Descrição do produto", example = "Molho de tomate, mussarela e manjericão")
    private String descricao;
    
    @Schema(description = "Preço do produto", example = "35.90")
    private BigDecimal preco;
    
    @Schema(description = "Categoria do produto", example = "Pizza")
    private String categoria;
    
    @Schema(description = "Disponibilidade do produto", example = "true")
    private Boolean disponivel;
}

