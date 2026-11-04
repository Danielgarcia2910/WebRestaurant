package com.restaurante.app.configuracion;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguracion implements WebMvcConfigurer {

    private final String carpeta;

    public WebConfiguracion(@Value("${restaurante.fotos}") String carpeta) {
        this.carpeta = carpeta;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registro) {
        String ruta = Path.of(carpeta).toAbsolutePath().normalize().toUri().toString();
        if (!ruta.endsWith("/")) {
            ruta += "/";
        }
        registro.addResourceHandler("/fotos/**").addResourceLocations(ruta);
    }
}
