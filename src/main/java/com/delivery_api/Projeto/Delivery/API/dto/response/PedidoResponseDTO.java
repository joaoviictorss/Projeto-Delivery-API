package com.delivery_api.Projeto.Delivery.API.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de resposta para Pedido")
public class PedidoResponseDTO {
    @Schema(description = "ID do pedido", example = "1")
    private Long id;

    @Schema(description = "Número do pedido", example = "PED1234567890")
    private String numeroPedido;

    @Schema(description = "Data do pedido")
    private LocalDateTime dataPedido;

    @Schema(description = "Status do pedido", example = "PENDENTE")
    private String status;

    @Schema(description = "Valor total do pedido", example = "54.80")
    private BigDecimal valorTotal;

    @Schema(description = "Observações do pedido", example = "Sem cebola")
    private String observacoes;

    @Schema(description = "ID do cliente", example = "1")
    private Long clienteId;

    @Schema(description = "ID do restaurante", example = "1")
    private Long restauranteId;

    @Schema(description = "Itens do pedido")
    private String itens;
}
