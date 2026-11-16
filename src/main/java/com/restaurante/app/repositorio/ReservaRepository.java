package com.restaurante.app.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurante.app.modelo.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findAllByOrderByFechaAscHoraAsc();
}
