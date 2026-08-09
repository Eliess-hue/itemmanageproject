package fr.itemmanage.itemmanage.service;

import fr.itemmanage.itemmanage.dto.request.MouvementRequest;
import fr.itemmanage.itemmanage.dto.response.MouvementResponse;
import fr.itemmanage.itemmanage.model.Mouvement;
import fr.itemmanage.itemmanage.model.Produit;
import fr.itemmanage.itemmanage.repository.MouvementRepository;
import fr.itemmanage.itemmanage.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

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
            throw new RuntimeException("La quantité d'un mouvement ne peut pas être nulle");
        }

        produitRepository.findById(request.produitId())
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + request.produitId()));

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

    private MouvementResponse toResponse(Mouvement mouvement) {
        return new MouvementResponse(
                mouvement.getId(), mouvement.getProduitId(), mouvement.getType(),
                mouvement.getQuantite(), mouvement.getStockApres(), mouvement.getDate()
        );
    }
}