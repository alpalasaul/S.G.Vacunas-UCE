package uce.proyect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uce.proyect.models.User;
import uce.proyect.service.agreement.UserService;

@SpringBootApplication
@Slf4j
public class SgVacunasSiApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SgVacunasSiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
