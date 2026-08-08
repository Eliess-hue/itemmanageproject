package fr.itemmanage.itemmanage.repository;

import fr.itemmanage.itemmanage.model.Produit;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProduitRepository extends MongoRepository<Produit, String> {
}