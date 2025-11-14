package com.delivery_api.Projeto.Delivery.API.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de resposta para Restaurante")
public class RestauranteResponseDTO {
    @Schema(description = "ID do restaurante", example = "1")
    private Long id;

    @Schema(description = "Nome do restaurante", example = "Pizzaria Bella")
    private String nome;

    @Schema(description = "Categoria do restaurante", example = "Italiana")
    private String categoria;

    @Schema(description = "Endereço do restaurante", example = "Av. Paulista, 1000")
    private String endereco;

    @Schema(description = "Telefone do restaurante", example = "(11) 3333-1111")
    private String telefone;

    @Schema(description = "Taxa de entrega", example = "5.00")
    private BigDecimal taxaEntrega;

    @Schema(description = "Avaliação do restaurante", example = "4.5")
    private BigDecimal avaliacao;

    @Schema(description = "Status ativo/inativo", example = "true")
    private Boolean ativo;
}
