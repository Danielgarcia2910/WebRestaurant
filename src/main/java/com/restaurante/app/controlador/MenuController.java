package com.restaurante.app.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.restaurante.app.servicio.MenuService;

@Controller
public class MenuController {

    private final MenuService servicio;

    public MenuController(MenuService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/menu")
    public String menu(@RequestParam(defaultValue = "") String buscar,
                       @RequestParam(required = false) Long categoriaId,
                       Model modelo) {
        modelo.addAttribute("grupos", servicio.menu(buscar, categoriaId));
        modelo.addAttribute("categorias", servicio.categorias());
        modelo.addAttribute("buscar", buscar);
        modelo.addAttribute("categoriaId", categoriaId);
        return "menu/lista";
    }

    @GetMapping("/menu/{id}")
    public String detalle(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("plato", servicio.plato(id));
        return "menu/detalle";
    }
}
