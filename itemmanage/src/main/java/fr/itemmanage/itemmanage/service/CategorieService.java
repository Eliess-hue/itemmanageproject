package fr.itemmanage.itemmanage.service;

import fr.itemmanage.itemmanage.dto.request.CategorieRequest;
import fr.itemmanage.itemmanage.dto.response.CategorieResponse;
import fr.itemmanage.itemmanage.exception.ResourceNotFoundException;
import fr.itemmanage.itemmanage.model.Categorie;
import fr.itemmanage.itemmanage.model.Produit;
import fr.itemmanage.itemmanage.repository.CategorieRepository;
import fr.itemmanage.itemmanage.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategorieService {

    private final CategorieRepository categorieRepository;
    private final ProduitRepository produitRepository;
    private final MongoTemplate mongoTemplate;

    public List<CategorieResponse> getAll() {

        Map<String, Long> nombresProduits = compterProduitsParCategorie();

        return categorieRepository.findAll().stream()
                .map(categorie -> toResponse(
                        categorie,
                        nombresProduits.getOrDefault(categorie.getId(), 0L)
                ))
                .toList();
    }

    public CategorieResponse create(CategorieRequest request) {
        Categorie categorie = new Categorie();

        categorie.setNom(request.nom());
        categorie.setDescription(request.description());

        Categorie categorieCreee = categorieRepository.save(categorie);

        return toResponse(categorieCreee, 0L);
    }

    public CategorieResponse rename(String id, CategorieRequest request) {

        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Catégorie introuvable : " + id
                        )
                );

        categorie.setNom(request.nom());
        categorie.setDescription(request.description());

        Categorie categorieModifiee = categorieRepository.save(categorie);

        long nombresProduits = produitRepository.countByCategorieId(
                categorieModifiee.getId()
        );

        return toResponse(
                categorieModifiee,
                nombresProduits
        );
    }

    private Map<String, Long> compterProduitsParCategorie() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("categorieId")
                        .count()
                        .as("nombre"),
                Aggregation.project()
                        .and("_id").as("categorieId")
                        .and("nombre").as("nombre")
        );

        AggregationResults<ComptageParCategorie> resultats =
                mongoTemplate.aggregate(aggregation, Produit.class, ComptageParCategorie.class);

        return resultats.getMappedResults().stream()
                .collect(Collectors.toMap(
                        ComptageParCategorie::categorieId,
                        ComptageParCategorie::nombre
                ));
    }

    private record ComptageParCategorie(
            String categorieId,
            long nombre
    ) {
    }

    private CategorieResponse toResponse(Categorie categorie, long nombreProduits) {
        return new CategorieResponse(
                categorie.getId(),
                categorie.getNom(),
                categorie.getDescription(),
                nombreProduits
        );
    }
}