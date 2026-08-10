package fr.itemmanage.itemmanage.service;

import fr.itemmanage.itemmanage.dto.request.ProduitFilterRequest;
import fr.itemmanage.itemmanage.dto.request.ProduitRequest;
import fr.itemmanage.itemmanage.dto.response.ProduitResponse;
import fr.itemmanage.itemmanage.exception.ConflictException;
import fr.itemmanage.itemmanage.exception.ResourceNotFoundException;
import fr.itemmanage.itemmanage.model.Categorie;
import fr.itemmanage.itemmanage.model.Produit;
import fr.itemmanage.itemmanage.repository.CategorieRepository;
import fr.itemmanage.itemmanage.repository.MouvementRepository;
import fr.itemmanage.itemmanage.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    public Page<ProduitResponse> search(ProduitFilterRequest filtre) {

        Query query = new Query();

        // Filtre par nom
        if (filtre.nom() != null && !filtre.nom().isBlank()) {
            query.addCriteria(
                    Criteria.where("nom").regex(filtre.nom(), "i")
            );
        }

        // Filtre par catégorie
        if (filtre.categorieId() != null && !filtre.categorieId().isBlank()) {
            query.addCriteria(
                    Criteria.where("categorieId").is(filtre.categorieId())
            );
        }

        // Filtre par état du stock
        if (filtre.etatStock() != null && !filtre.etatStock().isBlank()) {
            query.addCriteria(criteriaPourEtat(filtre.etatStock()));
        }

        /*
         * On compte AVANT d'appliquer la pagination.
         *
         * Exemple :
         * 145 produits correspondent aux filtres.
         * La page demandée contient seulement 10 produits.
         *
         * totalElements doit donc être 145 et non 10.
         */
        long total = mongoTemplate.count(query, Produit.class);

        // Tri
        if (filtre.triChamp() != null && !filtre.triChamp().isBlank()) {

            Sort.Direction direction =
                    "DESC".equalsIgnoreCase(filtre.triDirection())
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC;

            query.with(
                    Sort.by(direction, filtre.triChamp())
            );
        }

        // Pagination
        PageRequest pageable = PageRequest.of(
                filtre.page(),
                filtre.taille()
        );

        query.with(pageable);

        // Récupération de la page
        List<Produit> produits = mongoTemplate.find(query, Produit.class);

        // Assemblage des noms de catégorie
        List<String> categorieIds = produits.stream()
                .map(Produit::getCategorieId)
                .distinct()
                .toList();

        Map<String, String> nomsCategories =
                categorieRepository.findAllById(categorieIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Categorie::getId,
                                Categorie::getNom
                        ));

        List<ProduitResponse> responses = produits.stream()
                .map(p -> toResponse(
                        p,
                        nomsCategories.get(p.getCategorieId())
                ))
                .toList();

        // Construction de la Page retournée
        return new PageImpl<>(
                responses,
                pageable,
                total
        );
    }

    public ProduitResponse getById(String id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Produit introuvable : " + id)
                );

        String nomCategorie = categorieRepository
                .findById(produit.getCategorieId())
                .map(Categorie::getNom)
                .orElse(null);

        return toResponse(produit, nomCategorie);
    }

    public ProduitResponse create(ProduitRequest request) {
        Produit produit = new Produit();

        produit.setNom(request.nom());
        produit.setDescription(request.description());
        produit.setCategorieId(request.categorieId());
        produit.setStockMinimum(request.stockMinimum());

        // Un produit commence toujours avec un stock à 0.
        produit.setQuantiteActuelle(0);

        Produit produitCree = produitRepository.save(produit);

        String nomCategorie = categorieRepository
                .findById(produitCree.getCategorieId())
                .map(Categorie::getNom)
                .orElse(null);

        return toResponse(produitCree, nomCategorie);
    }

    public ProduitResponse update(String id, ProduitRequest request) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Produit introuvable : " + id)
                );

        produit.setNom(request.nom());
        produit.setDescription(request.description());
        produit.setCategorieId(request.categorieId());
        produit.setStockMinimum(request.stockMinimum());

        // quantiteActuelle n'est volontairement jamais modifiée ici.
        // Elle ne peut évoluer que via MouvementService.

        Produit produitModifie = produitRepository.save(produit);

        String nomCategorie = categorieRepository
                .findById(produitModifie.getCategorieId())
                .map(Categorie::getNom)
                .orElse(null);

        return toResponse(produitModifie, nomCategorie);
    }

    public void delete(String id) {
        if (!produitRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Produit introuvable : " + id
            );
        }

        if (mouvementRepository.existsByProduitId(id)) {
            throw new ConflictException(
                    "Impossible de supprimer un produit ayant des mouvements enregistrés"
            );
        }

        produitRepository.deleteById(id);
    }

    Criteria criteriaPourEtat(String etat) {

        Document exprCritique = new Document(
                "$lt",
                List.of("$quantiteActuelle", "$stockMinimum")
        );

        Document exprOk = new Document(
                "$gte",
                List.of(
                        "$quantiteActuelle",
                        new Document(
                                "$multiply",
                                List.of("$stockMinimum", 2)
                        )
                )
        );

        return switch (etat.toUpperCase()) {

            case "CRITIQUE" -> new Criteria()
                    .andOperator(
                            Criteria.where("$expr")
                                    .is(exprCritique)
                    );

            case "OK" -> new Criteria()
                    .andOperator(
                            Criteria.where("$expr")
                                    .is(exprOk)
                    );

            case "FAIBLE" -> new Criteria()
                    .andOperator(
                            Criteria.where("$expr")
                                    .is(
                                            new Document(
                                                    "$and",
                                                    List.of(
                                                            new Document(
                                                                    "$gte",
                                                                    List.of(
                                                                            "$quantiteActuelle",
                                                                            "$stockMinimum"
                                                                    )
                                                            ),
                                                            new Document(
                                                                    "$lt",
                                                                    List.of(
                                                                            "$quantiteActuelle",
                                                                            new Document(
                                                                                    "$multiply",
                                                                                    List.of(
                                                                                            "$stockMinimum",
                                                                                            2
                                                                                    )
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                    );

            default -> new Criteria();
        };
    }

    private ProduitResponse toResponse(
            Produit produit,
            String nomCategorie
    ) {
        return new ProduitResponse(
                produit.getId(),
                produit.getNom(),
                produit.getDescription(),
                nomCategorie,
                produit.getQuantiteActuelle(),
                produit.getStockMinimum()
        );
    }
}