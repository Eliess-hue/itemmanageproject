package fr.itemmanage.itemmanage.repository;

import fr.itemmanage.itemmanage.model.Categorie;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategorieRepository extends MongoRepository<Categorie, String> {
    boolean existsByNom(String nom);

    Optional<Categorie> findByNom(String nom);
}