package com.delivery_api.Projeto.Delivery.API.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.delivery_api.Projeto.Delivery.API.entity.Produto;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface ProdutoRepository extends JpaRepository <Produto, Long> {
    List<Produto> findByRestauranteId(Long restauranteId);

    // Apenas produtos disponíveis
    List<Produto> findByDisponivelTrue();

    // Produtos por categoria
    List<Produto> findByCategoria(String categoria);

    // Por faixa de preço (menor ou igual)
    List<Produto> findByPrecoLessThanEqual(BigDecimal preco);
    
}

