package com.restaurante.app.controlador;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.restaurante.app.modelo.Categoria;
import com.restaurante.app.modelo.Ingrediente;
import com.restaurante.app.modelo.Plato;
import com.restaurante.app.repositorio.CategoriaRepository;
import com.restaurante.app.repositorio.IngredienteRepository;
import com.restaurante.app.servicio.PlatoService;
import com.restaurante.app.servicio.ReglaNegocioException;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/platos")
public class PlatoAdminController {

    private static final String FORMULARIO = "admin/platos/formulario";

    private final PlatoService platoService;
    private final CategoriaRepository categorias;
    private final IngredienteRepository ingredientes;

    public PlatoAdminController(PlatoService platoService, CategoriaRepository categorias,
                                IngredienteRepository ingredientes) {
        this.platoService = platoService;
        this.categorias = categorias;
        this.ingredientes = ingredientes;
    }

    @GetMapping
    public String lista(Model modelo) {
        modelo.addAttribute("platos", platoService.listar());
        return "admin/platos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model modelo) {
        Plato plato = new Plato();
        prepararFormulario(modelo, plato, null, new HashSet<>());
        return FORMULARIO;
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model modelo) {
        Plato plato = platoService.buscarPorId(id);
        Long categoriaId = plato.getCategoria() == null ? null : plato.getCategoria().getId();
        Set<Long> seleccionados = new HashSet<>();
        for (Ingrediente i : plato.getIngredientes()) {
            seleccionados.add(i.getId());
        }
        prepararFormulario(modelo, plato, categoriaId, seleccionados);
        return FORMULARIO;
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("plato") Plato plato,
                          BindingResult resultado,
                          @RequestParam(required = false) Long categoriaId,
                          @RequestParam(required = false) List<Long> ingredienteIds,
                          @RequestParam(value = "archivoFoto", required = false) MultipartFile archivoFoto,
                          Model modelo,
                          RedirectAttributes flash) {

        Set<Long> seleccionados = ingredienteIds == null ? new HashSet<>() : new HashSet<>(ingredienteIds);

        if (resultado.hasErrors()) {
            prepararFormulario(modelo, plato, categoriaId, seleccionados);
            return FORMULARIO;
        }

        try {
            boolean esNuevo = plato.getId() == null;
            platoService.guardar(plato, categoriaId, ingredienteIds, archivoFoto);
            flash.addFlashAttribute("mensaje", esNuevo ? "Plato creado" : "Plato actualizado");
            return "redirect:/admin/platos";
        } catch (ReglaNegocioException e) {
            modelo.addAttribute("error", e.getMessage());
            prepararFormulario(modelo, plato, categoriaId, seleccionados);
            return FORMULARIO;
        }
    }

    @PostMapping("/{id}/disponibilidad")
    public String cambiarDisponibilidad(@PathVariable Long id, RedirectAttributes flash) {
        Plato plato = platoService.cambiarDisponibilidad(id);
        flash.addFlashAttribute("mensaje",
                "\"" + plato.getNombre() + "\" ahora está " + plato.getEstado().toLowerCase());
        return "redirect:/admin/platos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        platoService.eliminar(id);
        flash.addFlashAttribute("mensaje", "Plato eliminado");
        return "redirect:/admin/platos";
    }

    private void prepararFormulario(Model modelo, Plato plato, Long categoriaId, Set<Long> ingredientesSeleccionados) {
        List<Categoria> listaCategorias = categorias.findAll();
        List<Ingrediente> listaIngredientes = ingredientes.findAll();
        modelo.addAttribute("plato", plato);
        modelo.addAttribute("categorias", listaCategorias);
        modelo.addAttribute("todosIngredientes", listaIngredientes);
        modelo.addAttribute("categoriaSeleccionada", categoriaId);
        modelo.addAttribute("ingredientesSeleccionados", ingredientesSeleccionados);
    }
}
