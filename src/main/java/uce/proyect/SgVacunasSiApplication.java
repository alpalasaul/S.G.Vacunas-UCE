package uce.proyect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class SgVacunasSiApplication implements CommandLineRunner {

    @Value("${spring.profiles.active}")
    private String perfilActivo;

    public static void main(String[] args) {
        SpringApplication.run(SgVacunasSiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Perfil activo: ".concat(this.perfilActivo));
    }
}
