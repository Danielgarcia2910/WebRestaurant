package com.restaurante.app.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.restaurante.app.servicio.NoEncontradoException;

@ControllerAdvice
public class ManejadorDeErrores {

    @ExceptionHandler(NoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noEncontrado(NoEncontradoException e, Model modelo) {
        modelo.addAttribute("mensaje", e.getMessage());
        return "error/404";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String fotoGrande(RedirectAttributes flash) {
        flash.addFlashAttribute("error", "La foto no puede pasar de 1 MB");
        return "redirect:/admin/platos";
    }
}
