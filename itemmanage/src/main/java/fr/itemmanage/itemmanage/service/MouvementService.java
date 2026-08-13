package fr.itemmanage.itemmanage.service;

import fr.itemmanage.itemmanage.dto.request.MouvementRequest;
import fr.itemmanage.itemmanage.dto.response.MouvementHistoriqueResponse;
import fr.itemmanage.itemmanage.dto.response.MouvementResponse;
import fr.itemmanage.itemmanage.enums.TypeMouvement;
import fr.itemmanage.itemmanage.exception.InvalidRequestException;
import fr.itemmanage.itemmanage.exception.ResourceNotFoundException;
import fr.itemmanage.itemmanage.model.Mouvement;
import fr.itemmanage.itemmanage.model.Produit;
import fr.itemmanage.itemmanage.repository.MouvementRepository;
import fr.itemmanage.itemmanage.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@RequiredArgsConstructor
public class MouvementService {

    private final MouvementRepository mouvementRepository;
    private final ProduitRepository produitRepository;
    private final MongoTemplate mongoTemplate;

    @Transactional
    public MouvementResponse enregistrerMouvement(MouvementRequest request) {

        if (request.quantite() == 0) {
            throw new InvalidRequestException("La quantité d'un mouvement ne peut pas être nulle");
        }

        produitRepository.findById(request.produitId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable : " + request.produitId()));

        Query query = new Query(where("id").is(request.produitId()));
        Update update = new Update().inc("quantiteActuelle", request.quantite());
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        Produit produitMisAJour = mongoTemplate.findAndModify(query, update, options, Produit.class);

        Mouvement mouvement = new Mouvement();
        mouvement.setProduitId(request.produitId());
        mouvement.setType(request.type());
        mouvement.setQuantite(request.quantite());
        mouvement.setStockApres(produitMisAJour.getQuantiteActuelle());
        mouvement.setDate(Instant.now());

        Mouvement mouvementCree = mouvementRepository.save(mouvement);
        return toResponse(mouvementCree);
    }

    public Page<MouvementHistoriqueResponse> rechercher(
            String produitId, String type,
            Instant dateDebut, Instant dateFin,
            int page, int taille) {

        Query query = new Query();

        if (produitId != null && !produitId.isBlank()) {
            query.addCriteria(Criteria.where("produitId").is(produitId));
        }

        if (type != null && !type.isBlank()) {
            try {
                query.addCriteria(Criteria.where("type").is(TypeMouvement.valueOf(type.toUpperCase())));
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException("Type de mouvement invalide : " + type);
            }
        }

        if (dateDebut != null || dateFin != null) {
            Criteria dateCriteria = Criteria.where("date");

            if (dateDebut != null) {
                dateCriteria.gte(dateDebut);
            }

            if (dateFin != null) {
                dateCriteria.lte(dateFin);
            }

            query.addCriteria(dateCriteria);
        }

        long total = mongoTemplate.count(query, Mouvement.class);

        query.with(Sort.by(Sort.Direction.DESC, "date"));
        PageRequest pageable = PageRequest.of(page, taille);
        query.with(pageable);

        List<Mouvement> mouvements = mongoTemplate.find(query, Mouvement.class);

        List<String> produitIds = mouvements.stream().map(Mouvement::getProduitId).distinct().toList();
        Map<String, String> nomsProduits = produitRepository.findAllById(produitIds).stream()
                .collect(Collectors.toMap(Produit::getId, Produit::getNom));

        List<MouvementHistoriqueResponse> responses = mouvements.stream()
                .map(m -> new MouvementHistoriqueResponse(
                        m.getId(), m.getProduitId(), nomsProduits.get(m.getProduitId()),
                        m.getType(), m.getQuantite(), m.getStockApres(), m.getDate()
                ))
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    private MouvementResponse toResponse(Mouvement mouvement) {
        return new MouvementResponse(
                mouvement.getId(), mouvement.getProduitId(), mouvement.getType(),
                mouvement.getQuantite(), mouvement.getStockApres(), mouvement.getDate()
        );
    }
}