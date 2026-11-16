package com.restaurante.app.controlador;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.restaurante.app.modelo.Reserva;
import com.restaurante.app.servicio.ReglaNegocioException;
import com.restaurante.app.servicio.ReservaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private static final String VISTA = "reservas/formulario";

    private final ReservaService servicio;

    public ReservaController(ReservaService servicio) {
        this.servicio = servicio;
    }

    // El cliente solo puede enviar estos campos (no puede poner el estado ni el id)
    @InitBinder("reserva")
    void camposPermitidos(WebDataBinder binder) {
        binder.setAllowedFields("nombre", "telefono", "fecha", "hora", "personas", "notas");
    }

    @GetMapping
    public String formulario(Model modelo) {
        modelo.addAttribute("reserva", new Reserva());
        datosDelFormulario(modelo);
        return VISTA;
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("reserva") Reserva reserva,
                        BindingResult resultado,
                        Model modelo,
                        RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                servicio.crear(reserva);
                flash.addFlashAttribute("mensaje",
                        "¡Reserva recibida! Te llamaremos al teléfono que dejaste para confirmarla.");
                return "redirect:/reservas";
            } catch (ReglaNegocioException e) {
                modelo.addAttribute("error", e.getMessage());
            }
        }
        datosDelFormulario(modelo);
        return VISTA;
    }

    private void datosDelFormulario(Model modelo) {
        modelo.addAttribute("horas", servicio.horasDisponibles());
        modelo.addAttribute("hoy", LocalDate.now().toString());
    }
}
