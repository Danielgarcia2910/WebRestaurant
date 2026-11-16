package com.restaurante.app.servicio;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurante.app.modelo.EstadoReserva;
import com.restaurante.app.modelo.Reserva;
import com.restaurante.app.repositorio.ReservaRepository;

@Service
@Transactional(readOnly = true)
public class ReservaService {

    private final ReservaRepository reservas;
    private final LocalTime apertura;
    private final LocalTime cierre;

    public ReservaService(ReservaRepository reservas,
                          @Value("${restaurante.reservas.apertura:12:00}") String apertura,
                          @Value("${restaurante.reservas.cierre:22:00}") String cierre) {
        this.reservas = reservas;
        this.apertura = LocalTime.parse(apertura);
        this.cierre = LocalTime.parse(cierre);
    }

    /** Horas que se pueden elegir: cada 30 minutos, desde la apertura hasta una hora antes del cierre. */
    public List<String> horasDisponibles() {
        List<String> horas = new ArrayList<>();
        LocalTime ultima = cierre.minusHours(1);
        for (LocalTime h = apertura; !h.isAfter(ultima); h = h.plusMinutes(30)) {
            horas.add(h.toString());
        }
        return horas;
    }

    public List<Reserva> listar() {
        return reservas.findAllByOrderByFechaAscHoraAsc();
    }

    @Transactional
    public Reserva crear(Reserva datos) {
        if (!horasDisponibles().contains(datos.getHora().toString())) {
            throw new ReglaNegocioException("Elige una hora de la lista");
        }
        if (LocalDateTime.of(datos.getFecha(), datos.getHora()).isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("Esa fecha y hora ya pasaron. Elige una posterior");
        }
        Reserva reserva = new Reserva();
        reserva.setNombre(datos.getNombre().trim());
        reserva.setTelefono(datos.getTelefono().trim());
        reserva.setFecha(datos.getFecha());
        reserva.setHora(datos.getHora());
        reserva.setPersonas(datos.getPersonas());
        String notas = datos.getNotas() == null ? "" : datos.getNotas().trim();
        reserva.setNotas(notas.isEmpty() ? null : notas);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        return reservas.save(reserva);
    }

    @Transactional
    public void cambiarEstado(Long id, EstadoReserva estado) {
        buscarPorId(id).setEstado(estado);
    }

    @Transactional
    public void eliminar(Long id) {
        reservas.delete(buscarPorId(id));
    }

    private Reserva buscarPorId(Long id) {
        return reservas.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe la reserva con id " + id));
    }
}
