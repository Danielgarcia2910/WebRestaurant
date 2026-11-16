package com.restaurante.app.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.restaurante.app.modelo.Plato;

public interface PlatoRepository extends JpaRepository<Plato, Long> {

    Optional<Plato> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<Plato> findTop5ByOrderByIdDesc();

    @Query("""
           select distinct p from Plato p
           left join fetch p.categoria
           left join fetch p.ingredientes
           where lower(p.nombre) like lower(concat('%', :texto, '%'))
              or lower(p.descripcion) like lower(concat('%', :texto, '%'))
           order by p.nombre
           """)
    List<Plato> buscarPorTexto(@Param("texto") String texto);

    @Query("""
           select distinct p from Plato p
           left join fetch p.categoria
           left join fetch p.ingredientes
           where (lower(p.nombre) like lower(concat('%', :texto, '%'))
              or lower(p.descripcion) like lower(concat('%', :texto, '%')))
             and p.categoria.id = :categoriaId
           order by p.nombre
           """)
    List<Plato> buscarPorTextoYCategoria(@Param("texto") String texto,
                                         @Param("categoriaId") Long categoriaId);
}
