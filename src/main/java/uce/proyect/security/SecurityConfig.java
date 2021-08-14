package uce.proyect.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


//Este controlador maneja lo correspondiente a seguridad en para la API
@Controller
@EnableWebSecurity // Habilita seguridad para las API
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // Permite usar las anotaciones @secure o @preAuthorized en los endPoint, el primero no es tan necesarios
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint; // El componente que defino se lo envia para ser presentado como mensaje de no autorizado

    // Permite usar los servicios para autenticacion al iniciar sesion
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password(this.passwordEncoder().encode("!0410!")).authorities("ROLE_ADMIN", "ROLE_HC"); // Usuario en memoria siempre se podra acceder
        auth.userDetailsService(this.userDetailsService).passwordEncoder(this.passwordEncoder());
    }

    // Este bean define la instancia de la clase para la encriptación
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var corsConfiguration = new CorsConfiguration(); // Inicio definiendo los origenes y los metodos autorizados
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://localhost:3000"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));

        var configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**", corsConfiguration); // Cors definido
        return configurationSource;
    }

    // En este metodo se puede generar la mayoria de accesos y restricciones para los usaurios, se puede definir o quitar el login por defecto, rutas y demas
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests().antMatchers("/usuario").permitAll()
        http.authorizeRequests().anyRequest().authenticated() // Todas las rutas requieren autenticacion, usar los tokens para validar las session, se debe enviar un basic auth
                .and().csrf().disable().httpBasic().authenticationEntryPoint(this.authenticationEntryPoint) // csf no permite actualizar recursos
                // Spring Security al logearme guarda una sesion durante el tiempo que se ejecute la app
                // si ingreso cualquier contrasena toma la contrasena con la que ingrese sesion anteriormente
                // para deshabilitar esto hacer:
                .and().formLogin() // En lugar de la pagina vacia va a tener el login por defecto, mediante JWT no va a ser necesario registrarse a cada peticion, se define un tiempo
                .and().cors(); // defino el cors en el bean corsConfigurationSource
//                .and().sessionManagement().sessionCreationPolicy(STATELESS);  // desde el navegador no es posible acceder mediante el form porque nunca guarda el httpsession, dehabilitar para desarrollo y usar postman
    }
}
