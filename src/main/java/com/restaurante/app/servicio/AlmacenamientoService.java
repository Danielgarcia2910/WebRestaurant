package com.restaurante.app.servicio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AlmacenamientoService {

    // Tipos de imagen permitidos y la extensión con la que se guardan
    private static final Map<String, String> TIPOS = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp");

    private final Path carpeta;

    public AlmacenamientoService(@Value("${restaurante.fotos}") String carpeta) {
        this.carpeta = Path.of(carpeta).toAbsolutePath().normalize();
    }

    /** Guarda la foto y devuelve el nombre del archivo generado. */
    public String guardar(MultipartFile archivo) {
        String extension = TIPOS.get(archivo.getContentType());
        if (extension == null) {
            throw new ReglaNegocioException("La foto debe ser JPG, PNG o WEBP");
        }
        String nombre = UUID.randomUUID() + extension;
        try {
            Files.createDirectories(carpeta);
            try (InputStream entrada = archivo.getInputStream()) {
                Files.copy(entrada, carpeta.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new ReglaNegocioException("No se pudo guardar la foto");
        }
        return nombre;
    }

    /** Borra una foto si existe. */
    public void eliminar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return;
        }
        try {
            Path archivo = carpeta.resolve(nombre).normalize();
            if (archivo.startsWith(carpeta)) {
                Files.deleteIfExists(archivo);
            }
        } catch (IOException e) {
            // Si no se puede borrar, no interrumpimos la operación
        }
    }
}
