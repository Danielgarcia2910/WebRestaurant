package com.restaurante.app.servicio;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.restaurante.app.modelo.Plato;

public interface PlatoService {

    List<Plato> listar();

    Plato buscarPorId(Long id);

    
    Plato guardar(Plato datos, Long categoriaId, List<Long> ingredienteIds, MultipartFile foto);

    void eliminar(Long id);

    Plato cambiarDisponibilidad(Long id);
}
