package com.restaurante.app.controlador.dto;

import java.math.BigDecimal;
import java.util.List;

import com.restaurante.app.modelo.Ingrediente;
import com.restaurante.app.modelo.Plato;

public record PlatoDTO(Long id, String nombre, String descripcion, BigDecimal precio,
                       boolean disponible, String categoria, List<String> ingredientes) {

    public static PlatoDTO de(Plato p) {
        return new PlatoDTO(
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.isDisponible(),
                p.getCategoria() == null ? null : p.getCategoria().getNombre(),
                p.getIngredientes().stream().map(Ingrediente::getNombre).sorted().toList());
    }
}
