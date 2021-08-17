package uce.proyect.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uce.proyect.models.Facultad;

public interface FacultadRepository extends MongoRepository<Facultad, String> {

}
