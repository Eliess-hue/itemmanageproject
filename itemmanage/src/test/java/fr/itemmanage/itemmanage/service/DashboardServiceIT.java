package fr.itemmanage.itemmanage.service;


import fr.itemmanage.itemmanage.dto.response.DashboardResponse;
import fr.itemmanage.itemmanage.dto.response.MouvementHistoriqueResponse;
import fr.itemmanage.itemmanage.dto.response.ProduitResponse;
import fr.itemmanage.itemmanage.enums.TypeMouvement;
import fr.itemmanage.itemmanage.model.Categorie;
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
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class DashboardServiceIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer("mongo:8.0")
                    .withEnv("GLIBC_TUNABLES", "glibc.pthread.rseq=1");

    @Autowired
    private  DashboardService dashboardService;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    @Autowired
    private MouvementRepository mouvementRepository;

    @AfterEach
    void cleanup() {
        produitRepository.deleteAll();
        categorieRepository.deleteAll();
        mouvementRepository.deleteAll();
    }

    @Test
    void getDashboardDevraitRetournerLesCompteursEtAlertesCritiques() {

        // Given
        Categorie categorie1 = new Categorie();
        categorie1.setNom("Catégorie 1");

        Categorie categorie2 = new Categorie();
        categorie2.setNom("Catégorie 2");

        categorieRepository.saveAll(List.of(categorie1, categorie2));

        Produit critique1 = new Produit();
        critique1.setNom("Produit critique 1");
        critique1.setQuantiteActuelle(5);
        critique1.setStockMinimum(10);

        Produit critique2 = new Produit();
        critique2.setNom("Produit critique 2");
        critique2.setQuantiteActuelle(8);
        critique2.setStockMinimum(10);

        Produit ok = new Produit();
        ok.setNom("Produit OK");
        ok.setQuantiteActuelle(20);
        ok.setStockMinimum(10);

        produitRepository.saveAll(List.of(critique1, critique2, ok));

        // When
        DashboardResponse result = dashboardService.getDashboard();

        // Then
        assertThat(result.nombreProduits()).isEqualTo(3);
        assertThat(result.nombreCategories()).isEqualTo(2);

        assertThat(result.nombreProduitsCritiques()).isEqualTo(2);

        assertThat(result.alertesCritiques())
                .extracting(ProduitResponse::nom)
                .containsExactly("Produit critique 1", "Produit critique 2");
    }

    @Test
    void getDashboardDevraitCompterUniquementLesMouvementsDuJour() {

        // Given
        Produit produit = new Produit();
        produit.setNom("Produit test");
        produit.setQuantiteActuelle(10);
        produit.setStockMinimum(5);
        produit = produitRepository.save(produit);

        Instant debutJour = LocalDate.now(ZoneId.systemDefault())
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        Instant debutJourSuivant = LocalDate.now(ZoneId.systemDefault())
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        Mouvement hier = creerMouvement(
                produit.getId(),
                TypeMouvement.ENTREE,
                1,
                debutJour.minusSeconds(1)
        );

        Mouvement debutDuJour = creerMouvement(
                produit.getId(),
                TypeMouvement.ENTREE,
                2,
                debutJour
        );

        Mouvement aujourdHui = creerMouvement(
                produit.getId(),
                TypeMouvement.ENTREE,
                3,
                debutJour.plusSeconds(3600)
        );

        Mouvement debutDemain = creerMouvement(
                produit.getId(),
                TypeMouvement.ENTREE,
                4,
                debutJourSuivant
        );

        mouvementRepository.saveAll(
                List.of(hier, debutDuJour, aujourdHui, debutDemain)
        );

        // When
        DashboardResponse result = dashboardService.getDashboard();

        // Then
        assertThat(result.nombreMouvementsAujourdhui())
                .isEqualTo(2);
    }

    @Test
    void getDashboardDevraitRetournerLesDerniersMouvementsDansLeBonOrdreAvecLeNomProduit() {

        // Given
        Produit produit1 = new Produit();
        produit1.setNom("Produit A");
        produit1.setQuantiteActuelle(10);
        produit1.setStockMinimum(5);

        Produit produit2 = new Produit();
        produit2.setNom("Produit B");
        produit2.setQuantiteActuelle(20);
        produit2.setStockMinimum(10);

        produitRepository.saveAll(List.of(produit1, produit2));

        Instant maintenant = Instant.now();

        Mouvement ancien = creerMouvement(
                produit1.getId(),
                TypeMouvement.ENTREE,
                1,
                maintenant.minusSeconds(300)
        );

        Mouvement recent = creerMouvement(
                produit2.getId(),
                TypeMouvement.SORTIE,
                -2,
                maintenant.minusSeconds(100)
        );

        Mouvement milieu = creerMouvement(
                produit1.getId(),
                TypeMouvement.ENTREE,
                3,
                maintenant.minusSeconds(200)
        );

        mouvementRepository.saveAll(
                List.of(ancien, recent, milieu)
        );

        // When
        DashboardResponse result = dashboardService.getDashboard();

        // Then
        assertThat(result.derniersMouvements())
                .extracting(
                        MouvementHistoriqueResponse::nomProduit,
                        MouvementHistoriqueResponse::quantite
                )
                .containsExactly(
                        tuple("Produit B", -2),
                        tuple("Produit A", 3),
                        tuple("Produit A", 1)
                );
    }

    @Test
    void getDashboardDevraitLimiterLesAlertesCritiquesA5() {

        // Given
        List<Produit> produits = List.of(
                creerProduit("Produit 1", 1, 10),
                creerProduit("Produit 2", 2, 10),
                creerProduit("Produit 3", 3, 10),
                creerProduit("Produit 4", 4, 10),
                creerProduit("Produit 5", 5, 10),
                creerProduit("Produit 6", 6, 10)
        );

        produitRepository.saveAll(produits);

        // When
        DashboardResponse result = dashboardService.getDashboard();

        // Then
        assertThat(result.alertesCritiques())
                .hasSize(5);

        assertThat(result.alertesCritiques())
                .extracting(ProduitResponse::nom)
                .containsExactly(
                        "Produit 1",
                        "Produit 2",
                        "Produit 3",
                        "Produit 4",
                        "Produit 5"
                );
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

    private Produit creerProduit(String nom, int quantiteActuelle, int stockMinimum) {
        Produit produit = new Produit();
        produit.setNom(nom);
        produit.setQuantiteActuelle(quantiteActuelle);
        produit.setStockMinimum(stockMinimum);
        return produit;
    }

}
