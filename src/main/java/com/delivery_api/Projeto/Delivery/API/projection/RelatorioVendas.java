package com.delivery_api.Projeto.Delivery.API.projection;

import java.math.BigDecimal;

// Interface de Projeção
public interface RelatorioVendas {
    String getNomeRestaurante();
    BigDecimal getTotalVendas();
    Long getQuantidePedidos();
}

