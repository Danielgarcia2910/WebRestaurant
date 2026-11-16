package com.restaurante.app.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.restaurante.app.modelo.Categoria;
import com.restaurante.app.servicio.CategoriaService;
import com.restaurante.app.servicio.ReglaNegocioException;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/categorias")
public class CategoriaAdminController {

    private static final String VISTA = "admin/categorias/lista";

    private final CategoriaService servicio;

    public CategoriaAdminController(CategoriaService servicio) {
        this.servicio = servicio;
    }

    @InitBinder("categoria")
    void camposPermitidos(WebDataBinder binder) {
        binder.setAllowedFields("id", "nombre");
    }

    @GetMapping
    public String lista(Model modelo) {
        modelo.addAttribute("categorias", servicio.listar());
        modelo.addAttribute("categoria", new Categoria());
        return VISTA;
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("categorias", servicio.listar());
        modelo.addAttribute("categoria", servicio.buscarPorId(id));
        return VISTA;
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("categoria") Categoria categoria,
                          BindingResult resultado,
                          Model modelo,
                          RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                boolean nuevo = categoria.getId() == null;
                servicio.guardar(categoria);
                flash.addFlashAttribute("mensaje", nuevo ? "Categoría creada" : "Categoría actualizada");
                return "redirect:/admin/categorias";
            } catch (ReglaNegocioException e) {
                modelo.addAttribute("error", e.getMessage());
            }
        }
        modelo.addAttribute("categorias", servicio.listar());
        return VISTA;
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            servicio.eliminar(id);
            flash.addFlashAttribute("mensaje", "Categoría eliminada");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categorias";
    }
}
