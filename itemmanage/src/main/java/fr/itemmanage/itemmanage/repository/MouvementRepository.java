package fr.itemmanage.itemmanage.repository;

import fr.itemmanage.itemmanage.model.Mouvement;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MouvementRepository extends MongoRepository<Mouvement, String> {
    List<Mouvement> findByProduitIdOrderByDateDesc(String produitId);
    boolean existsByProduitId(String produitId);
}