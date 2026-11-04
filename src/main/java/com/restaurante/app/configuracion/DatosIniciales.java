package com.restaurante.app.configuracion;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.restaurante.app.modelo.Categoria;
import com.restaurante.app.modelo.Ingrediente;
import com.restaurante.app.modelo.Plato;
import com.restaurante.app.repositorio.CategoriaRepository;
import com.restaurante.app.repositorio.IngredienteRepository;
import com.restaurante.app.repositorio.PlatoRepository;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final PlatoRepository platos;
    private final CategoriaRepository categorias;
    private final IngredienteRepository ingredientes;

    public DatosIniciales(PlatoRepository platos, CategoriaRepository categorias,
                          IngredienteRepository ingredientes) {
        this.platos = platos;
        this.categorias = categorias;
        this.ingredientes = ingredientes;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (platos.count() > 0) {
            return;
        }

        Categoria entradas = categorias.save(new Categoria("Entradas"));
        Categoria fuertes = categorias.save(new Categoria("Platos fuertes"));
        Categoria bebidas = categorias.save(new Categoria("Bebidas"));
        Categoria postres = categorias.save(new Categoria("Postres"));

        plato("Patacones con hogao", "Plátano verde frito con hogao casero.", "12000",
                entradas, true, "Plátano verde", "Tomate", "Cebolla");
        plato("Ceviche de camarón", "Camarón fresco curado en limón con cilantro.", "28000",
                entradas, true, "Camarón", "Limón", "Cebolla", "Cilantro");
        plato("Deditos de queso", "Deditos de queso fritos.", "9000",
                entradas, true, "Maiz", "Huevo");

        plato("Cazuela de mariscos", "Mariscos en salsa cremosa de coco, con arroz.", "52000",
                fuertes, true, "Mariscos mixtos", "Leche de coco", "Arroz");
        plato("Arroz con coco y pescado frito", "Arroz titoté con pescado frito y patacón.", "38000",
                fuertes, true, "Arroz", "Coco", "Pescado", "Plátano verde");
        plato("Posta cartagenera", "Carne de res guisada en salsa de panela.", "34000",
                fuertes, true, "Carne de res", "Panela", "Cebolla");
        plato("Carne asada", "Carne de res a la parrilla.", "42000",
                fuertes, true, "Carne de res", "Chimichurri", "Papas a la francesa");
        plato("lasaña de carne", "Lasaña de carne con salsa bechamel y queso gratinado.", "36000",
                fuertes, true, "Carne molida", "Pasta para lasaña", "Queso", "Salsa bechamel");    
        plato("Pechuga de pollo gratinada", "Pechuga de pollo gratinada con salsa de queso.", "28000",
                fuertes, true, "Pechuga de pollo", "Queso","Salsa de queso");  
        plato("Chuleta de cerdo a la parrilla", "Chuleta de cerdo a la parrilla .", "22000",
                fuertes, true, "Chuleta de cerdo","Papas a la francesa");                  


        plato("Limonada de coco", "Limón y crema de coco, bien helada.", "9000",
                bebidas, true, "Limón", "Coco");
        plato("Jugo de lulo", "Lulo fresco, con agua o leche.", "8000",
                bebidas, true, "Lulo", "Azúcar");
        plato("Gaseosa", "Refresco de cola, naranja o limón.", "6000",
                bebidas, true, "Agua carbonatada", "Azúcar", "Saborizante");
        plato("limonada natural", "Limonada fresca, con hierbabuena.", "7000",
                bebidas, true, "Limón", "Hierbabuena", "Azúcar");
                                

        plato("tres leches", "Bizcocho empapado en tres tipos de leche.", "12000",
                postres, true, "bizcocho de vainilla", "leche condensada", "leche evaporada", "crema de leche");
        plato("Brownie con helado", "Postre de chocolate con bola de helado.", "1000",
                postres, false, "Brownie", "Helado de vainilla", "Chocolate");
        plato("Torta de chocolate", "Postre de chocolate.", "11000",
                postres, false, "Brownie", "fudge de chocolate", "Chocolate");
    }

    private Ingrediente ingrediente(String nombre) {
        return ingredientes.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> ingredientes.save(new Ingrediente(nombre)));
    }

    private Plato plato(String nombre, String descripcion, String precio, Categoria categoria,
                        boolean disponible, String... nombresIngredientes) {
        Plato plato = new Plato(nombre, descripcion, new BigDecimal(precio));
        plato.setCategoria(categoria);
        plato.setDisponible(disponible);
        for (String nombreIngrediente : nombresIngredientes) {
            plato.getIngredientes().add(ingrediente(nombreIngrediente));
        }
        return platos.save(plato);
    }
}
