package com.restaurante.app.servicio;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurante.app.modelo.Ingrediente;
import com.restaurante.app.repositorio.IngredienteRepository;

@Service
@Transactional(readOnly = true)
public class IngredienteService {

    private final IngredienteRepository ingredientes;

    public IngredienteService(IngredienteRepository ingredientes) {
        this.ingredientes = ingredientes;
    }

    public List<Ingrediente> listar() {
        return ingredientes.findAll(Sort.by("nombre"));
    }

    public Ingrediente buscarPorId(Long id) {
        return ingredientes.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el ingrediente con id " + id));
    }

    @Transactional
    public Ingrediente guardar(Ingrediente datos) {
        if (datos.getNombre() == null || datos.getNombre().isBlank()) {
            throw new ReglaNegocioException("El nombre del ingrediente es obligatorio");
        }
        String nombre = datos.getNombre().trim();
        boolean repetido = ingredientes.findByNombreIgnoreCase(nombre)
                .filter(otro -> !otro.getId().equals(datos.getId()))
                .isPresent();
        if (repetido) {
            throw new ReglaNegocioException("Ya existe el ingrediente \"" + nombre + "\"");
        }
        Ingrediente ingrediente = datos.getId() == null ? new Ingrediente() : buscarPorId(datos.getId());
        ingrediente.setNombre(nombre);
        return ingredientes.save(ingrediente);
    }

    @Transactional
    public void eliminar(Long id) {
        Ingrediente ingrediente = buscarPorId(id);
        int total = ingrediente.getPlatos().size();
        if (total > 0) {
            throw new ReglaNegocioException("No se puede eliminar \"" + ingrediente.getNombre()
                    + "\": lo usan " + total + (total == 1 ? " plato" : " platos"));
        }
        ingredientes.delete(ingrediente);
    }
}
