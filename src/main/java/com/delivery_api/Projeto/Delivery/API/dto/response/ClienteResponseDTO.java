package com.delivery_api.Projeto.Delivery.API.dto.response;

import com.delivery_api.Projeto.Delivery.API.entity.Cliente;
import lombok.Data;

@Data
public class ClienteResponseDTO {

    private Long id;

    private String nome;

    private String email;

    private String telefone;

    private String endereco;

    private Boolean ativo;
}

