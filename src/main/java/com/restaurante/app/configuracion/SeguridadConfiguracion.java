package com.restaurante.app.configuracion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SeguridadConfiguracion {

    @Bean
    SecurityFilterChain filtros(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(reglas -> reglas
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                .requestMatchers("/api/**").hasRole("ADMIN")
                .anyRequest().permitAll())
            .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/admin/platos", true)
                .permitAll())
            .logout(salir -> salir.logoutSuccessUrl("/"))
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/h2-console/**"))
            .headers(cabeceras -> cabeceras.frameOptions(marcos -> marcos.sameOrigin()));
        return http.build();
    }

    @Bean
    PasswordEncoder codificador() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService usuarios(PasswordEncoder codificador,
                                @Value("${restaurante.admin.usuario}") String usuario,
                                @Value("${restaurante.admin.clave}") String clave) {
        UserDetails admin = User.withUsername(usuario)
                .password(codificador.encode(clave))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }
}
