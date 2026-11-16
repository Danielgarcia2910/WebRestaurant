package com.restaurante.app.servicio;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurante.app.modelo.Categoria;
import com.restaurante.app.repositorio.CategoriaRepository;

@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categorias;

    public CategoriaService(CategoriaRepository categorias) {
        this.categorias = categorias;
    }

    public List<Categoria> listar() {
        return categorias.findAll(Sort.by("id"));
    }

    public Categoria buscarPorId(Long id) {
        return categorias.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe la categoría con id " + id));
    }

    @Transactional
    public Categoria guardar(Categoria datos) {
        if (datos.getNombre() == null || datos.getNombre().isBlank()) {
            throw new ReglaNegocioException("El nombre de la categoría es obligatorio");
        }
        String nombre = datos.getNombre().trim();
        boolean repetida = categorias.findByNombreIgnoreCase(nombre)
                .filter(otra -> !otra.getId().equals(datos.getId()))
                .isPresent();
        if (repetida) {
            throw new ReglaNegocioException("Ya existe la categoría \"" + nombre + "\"");
        }
        Categoria categoria = datos.getId() == null ? new Categoria() : buscarPorId(datos.getId());
        categoria.setNombre(nombre);
        return categorias.save(categoria);
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscarPorId(id);
        int total = categoria.getPlatos().size();
        if (total > 0) {
            throw new ReglaNegocioException("No se puede eliminar \"" + categoria.getNombre()
                    + "\": tiene " + total + (total == 1 ? " plato" : " platos"));
        }
        categorias.delete(categoria);
    }
}
