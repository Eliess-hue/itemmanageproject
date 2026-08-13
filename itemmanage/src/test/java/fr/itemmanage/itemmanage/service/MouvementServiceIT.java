package fr.itemmanage.itemmanage.service;

import fr.itemmanage.itemmanage.dto.request.MouvementRequest;
import fr.itemmanage.itemmanage.dto.response.MouvementHistoriqueResponse;
import fr.itemmanage.itemmanage.dto.response.MouvementResponse;
import fr.itemmanage.itemmanage.enums.TypeMouvement;
import fr.itemmanage.itemmanage.model.Mouvement;
import fr.itemmanage.itemmanage.model.Produit;
import fr.itemmanage.itemmanage.repository.CategorieRepository;
import fr.itemmanage.itemmanage.repository.MouvementRepository;
import fr.itemmanage.itemmanage.repository.ProduitRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static fr.itemmanage.itemmanage.enums.TypeMouvement.ENTREE;
import static fr.itemmanage.itemmanage.enums.TypeMouvement.SORTIE;
import static fr.itemmanage.itemmanage.enums.TypeMouvement.AJUSTEMENT;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class MouvementServiceIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer("mongo:8.0")
                    .withEnv("GLIBC_TUNABLES", "glibc.pthread.rseq=1");

    @Autowired
    private MouvementService mouvementService;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private MouvementRepository mouvementRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    @AfterEach
    void cleanup() {
        produitRepository.deleteAll();
        mouvementRepository.deleteAll();
        categorieRepository.deleteAll();
    }

    @Test
    void enregistrerMouvementDevraitIncrementerStockEtPersisterMouvement() {

        // Given
        Produit produit = new Produit();
        produit.setNom("Produit test");
        produit.setDescription("Description");
        produit.setCategorieId("categorie-1");
        produit.setQuantiteActuelle(10);
        produit.setStockMinimum(5);

        Produit produitSauvegarde = produitRepository.save(produit);

        MouvementRequest request = new MouvementRequest(
                produitSauvegarde.getId(),
                TypeMouvement.ENTREE,
                5
        );

        // When
        MouvementResponse response =
                mouvementService.enregistrerMouvement(request);

        // Then : réponse retournée
        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.produitId())
                .isEqualTo(produitSauvegarde.getId());
        assertThat(response.type())
                .isEqualTo(TypeMouvement.ENTREE);
        assertThat(response.quantite())
                .isEqualTo(5);
        assertThat(response.stockApres())
                .isEqualTo(15);
        assertThat(response.date())
                .isNotNull();

        // Then : produit réellement mis à jour en base
        Produit produitRelu = produitRepository
                .findById(produitSauvegarde.getId())
                .orElseThrow();

        assertThat(produitRelu.getQuantiteActuelle())
                .isEqualTo(15);

        // Then : mouvement réellement persisté en base
        Mouvement mouvementRelu = mouvementRepository
                .findById(response.id())
                .orElseThrow();

        assertThat(mouvementRelu.getId())
                .isEqualTo(response.id());
        assertThat(mouvementRelu.getProduitId())
                .isEqualTo(produitSauvegarde.getId());
        assertThat(mouvementRelu.getType())
                .isEqualTo(TypeMouvement.ENTREE);
        assertThat(mouvementRelu.getQuantite())
                .isEqualTo(5);
        assertThat(mouvementRelu.getStockApres())
                .isEqualTo(15);
        assertThat(mouvementRelu.getDate())
                .isNotNull();
    }

    @Test
    void enregistrerMouvementDevraitDecrementerStockAvecQuantiteNegative() {

        // Given
        Produit produit = new Produit();
        produit.setNom("Produit test");
        produit.setQuantiteActuelle(10);
        produit.setStockMinimum(5);

        Produit produitSauvegarde = produitRepository.save(produit);

        MouvementRequest request = new MouvementRequest(
                produitSauvegarde.getId(),
                TypeMouvement.SORTIE,
                -3
        );

        // When
        MouvementResponse response =
                mouvementService.enregistrerMouvement(request);

        // Then
        assertThat(response.quantite()).isEqualTo(-3);
        assertThat(response.stockApres()).isEqualTo(7);

        Produit produitRelu = produitRepository
                .findById(produitSauvegarde.getId())
                .orElseThrow();

        assertThat(produitRelu.getQuantiteActuelle())
                .isEqualTo(7);
    }

    @Test
    void rechercherDevraitFiltrerParProduitEtAssemblerNomProduit() {

        // Given
        Produit produitA = new Produit();
        produitA.setNom("Clavier");
        produitA.setQuantiteActuelle(10);
        produitA.setStockMinimum(5);

        Produit produitB = new Produit();
        produitB.setNom("Souris");
        produitB.setQuantiteActuelle(10);
        produitB.setStockMinimum(5);

        produitA = produitRepository.save(produitA);
        produitB = produitRepository.save(produitB);

        Mouvement mouvementA1 = new Mouvement();
        mouvementA1.setProduitId(produitA.getId());
        mouvementA1.setType(ENTREE);
        mouvementA1.setQuantite(5);
        mouvementA1.setStockApres(15);
        mouvementA1.setDate(Instant.now().minus(2, ChronoUnit.DAYS));

        Mouvement mouvementA2 = new Mouvement();
        mouvementA2.setProduitId(produitA.getId());
        mouvementA2.setType(SORTIE);
        mouvementA2.setQuantite(-2);
        mouvementA2.setStockApres(13);
        mouvementA2.setDate(Instant.now().minus(1, ChronoUnit.DAYS));

        Mouvement mouvementB = new Mouvement();
        mouvementB.setProduitId(produitB.getId());
        mouvementB.setType(ENTREE);
        mouvementB.setQuantite(10);
        mouvementB.setStockApres(20);
        mouvementB.setDate(Instant.now());

        mouvementRepository.saveAll(
                List.of(mouvementA1, mouvementA2, mouvementB)
        );

        // When
        Page<MouvementHistoriqueResponse> result =
                mouvementService.rechercher(
                        produitA.getId(),
                        null,
                        null,
                        null,
                        0,
                        10
                );

        // Then
        assertThat(result.getContent())
                .extracting(MouvementHistoriqueResponse::produitId)
                .containsExactly(
                        produitA.getId(),
                        produitA.getId()
                );

        assertThat(result.getContent())
                .extracting(MouvementHistoriqueResponse::nomProduit)
                .containsExactly(
                        "Clavier",
                        "Clavier"
                );

        assertThat(result.getTotalElements())
                .isEqualTo(2);
    }

    @Test
    void rechercherDevraitFiltrerParType() {

        // Given
        Produit produit = new Produit();
        produit.setNom("Produit test");
        produit.setQuantiteActuelle(10);
        produit.setStockMinimum(5);

        produit = produitRepository.save(produit);

        Mouvement entree = new Mouvement();
        entree.setProduitId(produit.getId());
        entree.setType(ENTREE);
        entree.setQuantite(10);
        entree.setStockApres(20);
        entree.setDate(Instant.now().minus(2, ChronoUnit.DAYS));

        Mouvement sortie = new Mouvement();
        sortie.setProduitId(produit.getId());
        sortie.setType(SORTIE);
        sortie.setQuantite(-3);
        sortie.setStockApres(17);
        sortie.setDate(Instant.now().minus(1, ChronoUnit.DAYS));

        Mouvement ajustement = new Mouvement();
        ajustement.setProduitId(produit.getId());
        ajustement.setType(AJUSTEMENT);
        ajustement.setQuantite(2);
        ajustement.setStockApres(19);
        ajustement.setDate(Instant.now());

        mouvementRepository.saveAll(
                List.of(entree, sortie, ajustement)
        );

        // When
        Page<MouvementHistoriqueResponse> result =
                mouvementService.rechercher(
                        null,
                        "SORTIE",
                        null,
                        null,
                        0,
                        10
                );

        // Then
        assertThat(result.getContent())
                .extracting(MouvementHistoriqueResponse::type)
                .containsExactly(SORTIE);

        assertThat(result.getTotalElements())
                .isEqualTo(1);
    }

    @Test
    void rechercherDevraitTrierParDateDecroissante() {

        // Given
        Produit produit = new Produit();
        produit.setNom("Produit test");
        produit.setQuantiteActuelle(10);
        produit.setStockMinimum(5);

        produit = produitRepository.save(produit);

        Instant maintenant = Instant.now();

        Instant ancien = maintenant.minus(1, ChronoUnit.DAYS);
        Instant recent = maintenant.plus(1, ChronoUnit.DAYS);
        Instant milieu = maintenant;

        Mouvement mouvementAncien = creerMouvement(
                produit.getId(), ENTREE, 1, ancien
        );

        Mouvement mouvementRecent = creerMouvement(
                produit.getId(), ENTREE, 3, recent
        );

        Mouvement mouvementMilieu = creerMouvement(
                produit.getId(), ENTREE, 2, milieu
        );

        // Ordre volontairement différent de l'ordre attendu
        mouvementRepository.saveAll(
                List.of(
                        mouvementAncien,
                        mouvementRecent,
                        mouvementMilieu
                )
        );

        // When
        Page<MouvementHistoriqueResponse> result =
                mouvementService.rechercher(
                        null,
                        null,
                        null,
                        null,
                        0,
                        10
                );

        // Then
        assertThat(result.getContent())
                .extracting(MouvementHistoriqueResponse::quantite)
                .containsExactly(
                        3, // J+1
                        2, // J
                        1  // J-1
                );
    }

    @Test
    void rechercherDevraitFiltrerCorrectementSelonLesDates() {

        // Given
        Produit produit = new Produit();
        produit.setNom("Produit test");
        produit.setQuantiteActuelle(10);
        produit.setStockMinimum(5);

        produit = produitRepository.save(produit);

        Instant maintenant = Instant.now();

        Instant jMoins3 = maintenant.minus(3, ChronoUnit.DAYS);
        Instant jMoins2 = maintenant.minus(2, ChronoUnit.DAYS);
        Instant jMoins1 = maintenant.minus(1, ChronoUnit.DAYS);
        Instant j = maintenant;
        Instant jPlus1 = maintenant.plus(1, ChronoUnit.DAYS);

        Mouvement m1 = creerMouvement(
                produit.getId(), ENTREE, 1, jMoins3
        );

        Mouvement m2 = creerMouvement(
                produit.getId(), ENTREE, 2, jMoins2
        );

        Mouvement m3 = creerMouvement(
                produit.getId(), ENTREE, 3, jMoins1
        );

        Mouvement m4 = creerMouvement(
                produit.getId(), ENTREE, 4, j
        );

        Mouvement m5 = creerMouvement(
                produit.getId(), ENTREE, 5, jPlus1
        );

        mouvementRepository.saveAll(
                List.of(m1, m2, m3, m4, m5)
        );

        // 1. dateDebut seule
        Page<MouvementHistoriqueResponse> avecDateDebut =
                mouvementService.rechercher(
                        null,
                        null,
                        jMoins1,
                        null,
                        0,
                        10
                );

        assertThat(avecDateDebut.getContent())
                .extracting(MouvementHistoriqueResponse::quantite)
                .containsExactly(5, 4, 3);

        // 2. dateFin seule
        Page<MouvementHistoriqueResponse> avecDateFin =
                mouvementService.rechercher(
                        null,
                        null,
                        null,
                        jMoins1,
                        0,
                        10
                );

        assertThat(avecDateFin.getContent())
                .extracting(MouvementHistoriqueResponse::quantite)
                .containsExactly(3, 2, 1);

        // 3. dateDebut + dateFin
        Page<MouvementHistoriqueResponse> intervalle =
                mouvementService.rechercher(
                        null,
                        null,
                        jMoins1,
                        j,
                        0,
                        10
                );

        assertThat(intervalle.getContent())
                .extracting(MouvementHistoriqueResponse::quantite)
                .containsExactly(4, 3);
    }

    private Mouvement creerMouvement(
            String produitId,
            fr.itemmanage.itemmanage.enums.TypeMouvement type,
            int quantite,
            Instant date
    ) {
        Mouvement mouvement = new Mouvement();
        mouvement.setProduitId(produitId);
        mouvement.setType(type);
        mouvement.setQuantite(quantite);
        mouvement.setStockApres(10 + quantite);
        mouvement.setDate(date);
        return mouvement;
    }

}
