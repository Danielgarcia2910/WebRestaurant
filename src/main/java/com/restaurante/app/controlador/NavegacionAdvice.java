package com.restaurante.app.controlador;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.restaurante.app.modelo.Categoria;
import com.restaurante.app.repositorio.CategoriaRepository;

/** Pone las categorías en el menú de arriba de las páginas públicas. */
@ControllerAdvice(assignableTypes = {MenuController.class, ReservaController.class, LoginController.class})
public class NavegacionAdvice {

    private final CategoriaRepository categorias;

    public NavegacionAdvice(CategoriaRepository categorias) {
        this.categorias = categorias;
    }

    @ModelAttribute("categoriasMenu")
    public List<Categoria> categoriasMenu() {
        return categorias.findAll(Sort.by("id"));
    }
}
