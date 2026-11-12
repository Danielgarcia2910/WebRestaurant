package com.restaurante.app.servicio;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.restaurante.app.modelo.Plato;

public interface PlatoService {

    List<Plato> listar();

    Plato buscarPorId(Long id);

    /**
     * Crea o actualiza un plato (si datos.getId() es null, crea).
     * Reglas: nombre único, precio mayor que 0.
     */
    Plato guardar(Plato datos, Long categoriaId, List<Long> ingredienteIds, MultipartFile foto);

    void eliminar(Long id);

    /** Cambia el plato entre Disponible y Agotado. */
    Plato cambiarDisponibilidad(Long id);
}
