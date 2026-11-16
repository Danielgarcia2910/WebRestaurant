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

import com.restaurante.app.modelo.Ingrediente;
import com.restaurante.app.servicio.IngredienteService;
import com.restaurante.app.servicio.ReglaNegocioException;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/ingredientes")
public class IngredienteAdminController {

    private static final String VISTA = "admin/ingredientes/lista";

    private final IngredienteService servicio;

    public IngredienteAdminController(IngredienteService servicio) {
        this.servicio = servicio;
    }

    @InitBinder("ingrediente")
    void camposPermitidos(WebDataBinder binder) {
        binder.setAllowedFields("id", "nombre");
    }

    @GetMapping
    public String lista(Model modelo) {
        modelo.addAttribute("ingredientes", servicio.listar());
        modelo.addAttribute("ingrediente", new Ingrediente());
        return VISTA;
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("ingredientes", servicio.listar());
        modelo.addAttribute("ingrediente", servicio.buscarPorId(id));
        return VISTA;
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("ingrediente") Ingrediente ingrediente,
                          BindingResult resultado,
                          Model modelo,
                          RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                boolean nuevo = ingrediente.getId() == null;
                servicio.guardar(ingrediente);
                flash.addFlashAttribute("mensaje", nuevo ? "Ingrediente creado" : "Ingrediente actualizado");
                return "redirect:/admin/ingredientes";
            } catch (ReglaNegocioException e) {
                modelo.addAttribute("error", e.getMessage());
            }
        }
        modelo.addAttribute("ingredientes", servicio.listar());
        return VISTA;
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            servicio.eliminar(id);
            flash.addFlashAttribute("mensaje", "Ingrediente eliminado");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/ingredientes";
    }
}
