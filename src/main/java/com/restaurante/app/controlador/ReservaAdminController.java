package com.restaurante.app.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.restaurante.app.modelo.EstadoReserva;
import com.restaurante.app.servicio.ReservaService;

@Controller
@RequestMapping("/admin/reservas")
public class ReservaAdminController {

    private final ReservaService servicio;

    public ReservaAdminController(ReservaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public String lista(Model modelo) {
        modelo.addAttribute("reservas", servicio.listar());
        return "admin/reservas/lista";
    }

    @PostMapping("/{id}/confirmar")
    public String confirmar(@PathVariable Long id, RedirectAttributes flash) {
        servicio.cambiarEstado(id, EstadoReserva.CONFIRMADA);
        flash.addFlashAttribute("mensaje", "Reserva confirmada");
        return "redirect:/admin/reservas";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes flash) {
        servicio.cambiarEstado(id, EstadoReserva.CANCELADA);
        flash.addFlashAttribute("mensaje", "Reserva cancelada");
        return "redirect:/admin/reservas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        servicio.eliminar(id);
        flash.addFlashAttribute("mensaje", "Reserva eliminada");
        return "redirect:/admin/reservas";
    }
}
