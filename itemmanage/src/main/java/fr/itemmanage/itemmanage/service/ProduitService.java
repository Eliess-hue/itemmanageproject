package fr.itemmanage.itemmanage.service;

import fr.itemmanage.itemmanage.dto.request.ProduitFilterRequest;
import fr.itemmanage.itemmanage.dto.request.ProduitRequest;
import fr.itemmanage.itemmanage.dto.response.ProduitResponse;
import fr.itemmanage.itemmanage.model.Categorie;
import fr.itemmanage.itemmanage.model.Produit;
import fr.itemmanage.itemmanage.repository.CategorieRepository;
import fr.itemmanage.itemmanage.repository.MouvementRepository;
import fr.itemmanage.itemmanage.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProduitService {

    private final MongoTemplate mongoTemplate;
    private final CategorieRepository categorieRepository;
    private final MouvementRepository mouvementRepository;
    private final ProduitRepository produitRepository;

    public List<ProduitResponse> search(ProduitFilterRequest filtre) {
        Query query = new Query();

        if (filtre.nom() != null && !filtre.nom().isBlank()) {
            query.addCriteria(Criteria.where("nom").regex(filtre.nom(), "i"));
        }

        if (filtre.categorieId() != null && !filtre.categorieId().isBlank()) {
            query.addCriteria(Criteria.where("categorieId").is(filtre.categorieId()));
        }

        if (filtre.etatStock() != null) {
            query.addCriteria(criteriaPourEtat(filtre.etatStock()));
        }

        if (filtre.triChamp() != null) {
            Sort.Direction direction = "DESC".equalsIgnoreCase(filtre.triDirection())
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            query.with(Sort.by(direction, filtre.triChamp()));
        }

        query.with(PageRequest.of(filtre.page(), filtre.taille()));

        List<Produit> produits = mongoTemplate.find(query, Produit.class);

        // Assemblage des noms de catégorie
        List<String> categorieIds = produits.stream().map(Produit::getCategorieId).distinct().toList();
        Map<String, String> nomsCategories = categorieRepository.findAllById(categorieIds).stream()
                .collect(Collectors.toMap(Categorie::getId, Categorie::getNom));

        return produits.stream()
                .map(p -> toResponse(p, nomsCategories.get(p.getCategorieId())))
                .toList();
    }

    public ProduitResponse getById(String id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + id));
        String nomCategorie = categorieRepository.findById(produit.getCategorieId())
                .map(Categorie::getNom).orElse(null);
        return toResponse(produit, nomCategorie);
    }

    public ProduitResponse create(ProduitRequest request) {
        Produit produit = new Produit();
        produit.setNom(request.nom());
        produit.setDescription(request.description());
        produit.setCategorieId(request.categorieId());
        produit.setStockMinimum(request.stockMinimum());
        produit.setQuantiteActuelle(0);
        Produit produitCree = produitRepository.save(produit);
        String nomCategorie = categorieRepository.findById(produitCree.getCategorieId())
                .map(Categorie::getNom).orElse(null);
        return toResponse(produitCree, nomCategorie);
    }

    public ProduitResponse update(String id, ProduitRequest request) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + id));
        produit.setNom(request.nom());
        produit.setDescription(request.description());
        produit.setCategorieId(request.categorieId());
        produit.setStockMinimum(request.stockMinimum());

        // quantiteActuelle jamais touché ici, structurellement impossible via ce DTO
        Produit produitModifie = produitRepository.save(produit);
        String nomCategorie = categorieRepository.findById(produitModifie.getCategorieId())
                .map(Categorie::getNom).orElse(null);
        return toResponse(produitModifie, nomCategorie);
    }

    public void delete(String id) {
        if (!produitRepository.existsById(id)) {
            throw new RuntimeException("Produit introuvable : " + id);
        }
        if (mouvementRepository.existsByProduitId(id)) {
            throw new RuntimeException("Impossible de supprimer un produit ayant des mouvements enregistrés");
        }
        produitRepository.deleteById(id);
    }

    private Criteria criteriaPourEtat(String etat) {
        Document exprCritique = new Document("$lt", List.of("$quantiteActuelle", "$stockMinimum"));
        Document exprOk = new Document("$gte", List.of("$quantiteActuelle",
                new Document("$multiply", List.of("$stockMinimum", 2))));

        return switch (etat.toUpperCase()) {
            case "CRITIQUE" -> new Criteria().andOperator(
                    Criteria.where("$expr").is(exprCritique)
            );
            case "OK" -> new Criteria().andOperator(
                    Criteria.where("$expr").is(exprOk)
            );
            case "FAIBLE" -> new Criteria().andOperator(
                    Criteria.where("$expr").is(new Document("$and", List.of(
                            new Document("$gte", List.of("$quantiteActuelle", "$stockMinimum")),
                            new Document("$lt", List.of("$quantiteActuelle",
                                    new Document("$multiply", List.of("$stockMinimum", 2))))
                    )))
            );
            default -> new Criteria();
        };
    }

    private ProduitResponse toResponse(Produit produit, String nomCategorie) {
        return new ProduitResponse(
                produit.getId(), produit.getNom(), produit.getDescription(),
                nomCategorie, produit.getQuantiteActuelle(), produit.getStockMinimum()
        );
    }
}