package com.restaurante.app.servicio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurante.app.modelo.Categoria;
import com.restaurante.app.modelo.Plato;
import com.restaurante.app.repositorio.CategoriaRepository;
import com.restaurante.app.repositorio.PlatoRepository;

@Service
@Transactional(readOnly = true)
public class MenuService {

    private final PlatoRepository platos;
    private final CategoriaRepository categorias;

    public MenuService(PlatoRepository platos, CategoriaRepository categorias) {
        this.platos = platos;
        this.categorias = categorias;
    }

    public List<Plato> buscar(String texto, Long categoriaId) {
        String criterio = texto == null ? "" : texto.trim();
        if (categoriaId == null) {
            return platos.buscarPorTexto(criterio);
        }
        return platos.buscarPorTextoYCategoria(criterio, categoriaId);
    }

    public Map<String, List<Plato>> menu(String texto, Long categoriaId) {
        Comparator<Plato> orden = Comparator.comparingLong((Plato p) -> ordenCategoria(p))
                .thenComparing(Plato::getNombre, String.CASE_INSENSITIVE_ORDER);
        Map<String, List<Plato>> grupos = new LinkedHashMap<>();
        buscar(texto, categoriaId).stream()
                .sorted(orden)
                .forEach(p -> grupos.computeIfAbsent(nombreGrupo(p), n -> new ArrayList<>()).add(p));
        return grupos;
    }

    public List<Categoria> categorias() {
        return categorias.findAll(Sort.by("id"));
    }

    public Plato plato(Long id) {
        return platos.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el plato con id " + id));
    }

    public long totalPlatos() {
        return platos.count();
    }

    public long totalCategorias() {
        return categorias.count();
    }

    public List<Plato> ultimos() {
        return platos.findTop5ByOrderByIdDesc();
    }

    private static long ordenCategoria(Plato plato) {
        return plato.getCategoria() == null ? Long.MAX_VALUE : plato.getCategoria().getId();
    }

    private static String nombreGrupo(Plato plato) {
        return plato.getCategoria() == null ? "Otros" : plato.getCategoria().getNombre();
    }
}
