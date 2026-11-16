package com.restaurante.app.controlador.api;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.restaurante.app.controlador.dto.PlatoDTO;
import com.restaurante.app.controlador.dto.PlatoEntrada;
import com.restaurante.app.modelo.Plato;
import com.restaurante.app.servicio.MenuService;
import com.restaurante.app.servicio.NoEncontradoException;
import com.restaurante.app.servicio.PlatoService;
import com.restaurante.app.servicio.ReglaNegocioException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/platos")
public class PlatoRestController {

    private final PlatoService platoService;
    private final MenuService menuService;

    public PlatoRestController(PlatoService platoService, MenuService menuService) {
        this.platoService = platoService;
        this.menuService = menuService;
    }

    @GetMapping
    public List<PlatoDTO> listar(@RequestParam(defaultValue = "") String buscar,
                                 @RequestParam(required = false) Long categoriaId) {
        return menuService.buscar(buscar, categoriaId).stream().map(PlatoDTO::de).toList();
    }

    @GetMapping("/{id}")
    public PlatoDTO uno(@PathVariable Long id) {
        return PlatoDTO.de(menuService.plato(id));
    }

    @PostMapping
    public ResponseEntity<PlatoDTO> crear(@Valid @RequestBody PlatoEntrada entrada) {
        Plato creado = platoService.guardar(aPlato(entrada, null),
                entrada.categoriaId(), entrada.ingredienteIds(), null);
        return ResponseEntity
                .created(URI.create("/api/platos/" + creado.getId()))
                .body(PlatoDTO.de(creado));
    }

    @PutMapping("/{id}")
    public PlatoDTO editar(@PathVariable Long id, @Valid @RequestBody PlatoEntrada entrada) {
        platoService.buscarPorId(id);
        Plato actualizado = platoService.guardar(aPlato(entrada, id),
                entrada.categoriaId(), entrada.ingredienteIds(), null);
        return PlatoDTO.de(actualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        platoService.eliminar(id);
    }

    @ExceptionHandler(NoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> noEncontrado(NoEncontradoException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> reglaRota(ReglaNegocioException e) {
        return Map.of("error", e.getMessage());
    }

    private Plato aPlato(PlatoEntrada entrada, Long id) {
        Plato plato = new Plato(entrada.nombre(), entrada.descripcion(), entrada.precio());
        plato.setId(id);
        plato.setDisponible(entrada.disponible() == null || entrada.disponible());
        return plato;
    }
}
