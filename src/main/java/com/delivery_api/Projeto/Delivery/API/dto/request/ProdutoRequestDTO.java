package com.delivery_api.Projeto.Delivery.API.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para requisição de criação ou atualização de produto")
public class ProdutoRequestDTO {
    @Schema(description = "ID do produto (opcional para criação)", example = "1")
    private Long id;
    
    @Schema(description = "Nome do produto", example = "Pizza Margherita", required = true)
    @NotBlank(message = "O nome é obrigatório")
    private String nome;
    
    @Schema(description = "Descrição do produto", example = "Molho de tomate, mussarela e manjericão", required = true)
    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;
    
    @Schema(description = "Preço do produto", example = "35.90", required = true)
    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    private BigDecimal preco;
    
    @Schema(description = "Categoria do produto", example = "Pizza", required = true)
    @NotBlank(message = "A categoria é obrigatória")
    private String categoria;
    
    @Schema(description = "Disponibilidade do produto", example = "true")
    private Boolean disponivel;
}

