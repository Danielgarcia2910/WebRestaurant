package com.restaurante.app.controlador.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PlatoEntrada(
        @NotBlank String nombre,
        @Size(max = 500) String descripcion,
        @NotNull @DecimalMin("0.01") BigDecimal precio,
        Boolean disponible,
        Long categoriaId,
        List<Long> ingredienteIds) {
}
