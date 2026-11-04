package com.restaurante.app.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurante.app.modelo.Ingrediente;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    Optional<Ingrediente> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
