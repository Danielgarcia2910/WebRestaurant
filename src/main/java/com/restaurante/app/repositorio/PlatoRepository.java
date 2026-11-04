package com.restaurante.app.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurante.app.modelo.Plato;

public interface PlatoRepository extends JpaRepository<Plato, Long> {

    Optional<Plato> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
