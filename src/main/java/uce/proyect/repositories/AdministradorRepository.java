package uce.proyect.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uce.proyect.models.Administrador;

import java.util.Optional;

public interface AdministradorRepository extends MongoRepository<Administrador, String> {
    Optional<Administrador> findByUsuario(String usuario);
}
