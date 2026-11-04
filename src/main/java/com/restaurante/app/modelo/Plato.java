package com.restaurante.app.modelo;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "platos")
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, unique = true, length = 120)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede pasar de 500 caracteres")
    @Column(length = 500)
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    @Digits(integer = 8, fraction = 2, message = "Precio con formato no válido")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private boolean disponible = true;

    private String foto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "platos_ingredientes",
               joinColumns = @JoinColumn(name = "plato_id"),
               inverseJoinColumns = @JoinColumn(name = "ingrediente_id"))
    private Set<Ingrediente> ingredientes = new HashSet<>();

    public Plato() {
    }

    public Plato(String nombre, String descripcion, BigDecimal precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public String getEstado() {
        return disponible ? "Disponible" : "Agotado";
    }

    public String getNombreCategoria() {
        return categoria == null ? "" : categoria.getNombre();
    }

    public String getNombresIngredientes() {
        return String.join(", ", ingredientes.stream().map(Ingrediente::getNombre).sorted().toList());
    }

    public String getIngredientesComoTexto() {
        return ingredientes.isEmpty() ? "Sin ingredientes" : getNombresIngredientes();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Set<Ingrediente> getIngredientes() { return ingredientes; }
    public void setIngredientes(Set<Ingrediente> ingredientes) { this.ingredientes = ingredientes; }
}
