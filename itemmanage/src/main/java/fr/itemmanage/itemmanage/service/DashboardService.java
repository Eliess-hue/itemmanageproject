package fr.itemmanage.itemmanage.service;

import fr.itemmanage.itemmanage.dto.response.*;
import fr.itemmanage.itemmanage.model.Mouvement;
import fr.itemmanage.itemmanage.model.Produit;
import fr.itemmanage.itemmanage.repository.CategorieRepository;
import fr.itemmanage.itemmanage.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MongoTemplate mongoTemplate;
    private final ProduitRepository produitRepository;
    private final CategorieRepository categorieRepository;
    private final ProduitService produitService; // réutilisation de criteriaPourEtat

    public DashboardResponse getDashboard() {

        long nombreProduits = produitRepository.count();
        long nombreCategories = categorieRepository.count();

        Query queryCritique = new Query(produitService.criteriaPourEtat("CRITIQUE"));
        long nombreProduitsCritiques = mongoTemplate.count(queryCritique, Produit.class);

        Instant debutJour = LocalDate.now(ZoneId.systemDefault())
                .atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant debutJourSuivant = LocalDate.now(ZoneId.systemDefault())
                .plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        Query queryMouvementsAujourdhui = new Query(
                Criteria.where("date").gte(debutJour).lt(debutJourSuivant)
        );
        long nombreMouvementsAujourdhui = mongoTemplate.count(queryMouvementsAujourdhui, Mouvement.class);

        Query queryAlertes = new Query(produitService.criteriaPourEtat("CRITIQUE"))
                .with(Sort.by(Sort.Direction.ASC, "quantiteActuelle"))
                .with(PageRequest.of(0, 5));
        List<Produit> produitsCritiques = mongoTemplate.find(queryAlertes, Produit.class);
        List<ProduitResponse> alertesCritiques = produitsCritiques.stream()
                .map(p -> new ProduitResponse(
                        p.getId(),
                        p.getNom(),
                        p.getDescription(),
                        null,
                        p.getQuantiteActuelle(),
                        p.getStockMinimum(),
                        "CRITIQUE"
                        ))
                .toList();

        Query queryDerniersMouvements = new Query()
                .with(Sort.by(Sort.Direction.DESC, "date"))
                .with(PageRequest.of(0, 9));
        List<Mouvement> derniersMouvements = mongoTemplate.find(queryDerniersMouvements, Mouvement.class);

        List<String> produitIds = derniersMouvements.stream().map(Mouvement::getProduitId).distinct().toList();
        Map<String, String> nomsProduits = produitRepository.findAllById(produitIds).stream()
                .collect(Collectors.toMap(Produit::getId, Produit::getNom));

        List<MouvementHistoriqueResponse> mouvementsResponse = derniersMouvements.stream()
                .map(m -> new MouvementHistoriqueResponse(m.getId(), m.getProduitId(),
                        nomsProduits.get(m.getProduitId()), m.getType(), m.getQuantite(), m.getStockApres(), m.getDate()))
                .toList();

        return new DashboardResponse(nombreProduits, nombreCategories, nombreProduitsCritiques,
                nombreMouvementsAujourdhui, alertesCritiques, mouvementsResponse);
    }
}