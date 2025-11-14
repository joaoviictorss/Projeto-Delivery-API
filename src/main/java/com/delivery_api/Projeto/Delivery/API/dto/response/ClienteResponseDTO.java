package com.delivery_api.Projeto.Delivery.API.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO de resposta para Cliente")
public class ClienteResponseDTO {

    @Schema(description = "ID do cliente", example = "1")
    private Long id;

    @Schema(description = "Nome completo do cliente", example = "João da Silva")
    private String nome;

    @Schema(description = "Email do cliente", example = "joao@email.com")
    private String email;

    @Schema(description = "Telefone do cliente", example = "(11) 99999-1111")
    private String telefone;

    @Schema(description = "Endereço do cliente", example = "Rua A, 123 - São Paulo/SP")
    private String endereco;

    @Schema(description = "Status ativo/inativo", example = "true")
    private Boolean ativo;
}

