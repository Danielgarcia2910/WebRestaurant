package com.restaurante.app.modelo;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "ingredientes")
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del ingrediente es obligatorio")
    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @ManyToMany(mappedBy = "ingredientes", fetch = FetchType.LAZY)
    private Set<Plato> platos = new HashSet<>();

    public Ingrediente() {
    }

    public Ingrediente(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Set<Plato> getPlatos() { return platos; }
    public void setPlatos(Set<Plato> platos) { this.platos = platos; }
}
