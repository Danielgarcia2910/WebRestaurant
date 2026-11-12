package com.restaurante.app.servicio;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.restaurante.app.modelo.Plato;
import com.restaurante.app.repositorio.CategoriaRepository;
import com.restaurante.app.repositorio.IngredienteRepository;
import com.restaurante.app.repositorio.PlatoRepository;

@Service
public class PlatoServiceJpa implements PlatoService {

    private final PlatoRepository platos;
    private final CategoriaRepository categorias;
    private final IngredienteRepository ingredientes;
    private final AlmacenamientoService almacenamiento;

    public PlatoServiceJpa(PlatoRepository platos, CategoriaRepository categorias,
                           IngredienteRepository ingredientes, AlmacenamientoService almacenamiento) {
        this.platos = platos;
        this.categorias = categorias;
        this.ingredientes = ingredientes;
        this.almacenamiento = almacenamiento;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Plato> listar() {
        return platos.findAll(Sort.by("nombre"));
    }

    @Override
    @Transactional(readOnly = true)
    public Plato buscarPorId(Long id) {
        return platos.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el plato con id " + id));
    }

    @Override
    @Transactional
    public Plato guardar(Plato datos, Long categoriaId, List<Long> ingredienteIds, MultipartFile foto) {
        validarReglas(datos);

        Plato plato = datos.getId() == null ? new Plato() : buscarPorId(datos.getId());
        plato.setNombre(datos.getNombre().trim());
        plato.setDescripcion(datos.getDescripcion());
        plato.setPrecio(datos.getPrecio());
        plato.setDisponible(datos.isDisponible());

        if (categoriaId == null) {
            plato.setCategoria(null);
        } else {
            plato.setCategoria(categorias.findById(categoriaId)
                    .orElseThrow(() -> new ReglaNegocioException("La categoría no existe")));
        }

        if (ingredienteIds == null || ingredienteIds.isEmpty()) {
            plato.setIngredientes(new HashSet<>());
        } else {
            plato.setIngredientes(new HashSet<>(ingredientes.findAllById(ingredienteIds)));
        }

        if (foto != null && !foto.isEmpty()) {
            String nueva = almacenamiento.guardar(foto);
            almacenamiento.eliminar(plato.getFoto());
            plato.setFoto(nueva);
        }

        return platos.save(plato);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Plato plato = buscarPorId(id);
        almacenamiento.eliminar(plato.getFoto());
        platos.delete(plato);
    }

    @Override
    @Transactional
    public Plato cambiarDisponibilidad(Long id) {
        Plato plato = buscarPorId(id);
        plato.setDisponible(!plato.isDisponible());
        return platos.save(plato);
    }

    // ---------- Reglas de negocio ----------

    private void validarReglas(Plato datos) {
        // Regla: precio mayor que 0
        if (datos.getPrecio() == null || datos.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El precio debe ser mayor que 0");
        }
        // Regla: nombre único (sin importar mayúsculas)
        if (datos.getNombre() == null || datos.getNombre().isBlank()) {
            throw new ReglaNegocioException("El nombre es obligatorio");
        }
        String nombre = datos.getNombre().trim();
        boolean repetido = platos.findByNombreIgnoreCase(nombre)
                .filter(existente -> !existente.getId().equals(datos.getId()))
                .isPresent();
        if (repetido) {
            throw new ReglaNegocioException("Ya existe un plato con el nombre \"" + nombre + "\"");
        }
    }
}
