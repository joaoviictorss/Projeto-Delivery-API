package com.delivery_api.Projeto.Delivery.API.service;


import com.delivery_api.Projeto.Delivery.API.dto.request.ClienteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.ClienteResponseDTO;

import java.util.List;

public interface ClienteService {

    ClienteResponseDTO cadastrar(ClienteRequestDTO dto);

    ClienteResponseDTO buscarPorId(Long id);

    ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto);

    ClienteResponseDTO ativarDesativar(Long id);

    List<ClienteResponseDTO> listarAtivos();

    List<ClienteResponseDTO> buscarPorNome(String nome);


}

