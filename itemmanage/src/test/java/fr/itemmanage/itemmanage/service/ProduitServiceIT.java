package fr.itemmanage.itemmanage.service;

import fr.itemmanage.itemmanage.dto.request.ProduitFilterRequest;
import fr.itemmanage.itemmanage.dto.response.ProduitResponse;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class ProduitServiceIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer("mongo:8.0")
                    .withEnv("GLIBC_TUNABLES", "glibc.pthread.rseq=1");

    @Autowired
    private ProduitService produitService;

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
    void searchShouldReturnAllProductsWithoutFilters() {

        Produit produitA = new Produit();
        produitA.setNom("Produit A");
        produitA.setQuantiteActuelle(10);
        produitA.setStockMinimum(5);

        Produit produitB = new Produit();
        produitB.setNom("Produit B");
        produitB.setQuantiteActuelle(20);
        produitB.setStockMinimum(10);

        Produit produitC = new Produit();
        produitC.setNom("Produit C");
        produitC.setQuantiteActuelle(5);
        produitC.setStockMinimum(3);

        produitRepository.save(produitA);
        produitRepository.save(produitB);
        produitRepository.save(produitC);

        ProduitFilterRequest filtre = new ProduitFilterRequest(
                null,
                null,
                null,
                null,
                null,
                0,
                10
        );

        Page<ProduitResponse> result = produitService.search(filtre);

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(3);

        assertThat(result.getContent())
                .extracting(ProduitResponse::nom)
                .containsExactlyInAnyOrder(
                        "Produit A",
                        "Produit B",
                        "Produit C"
                );
    }

    @Test
    void searchShouldFilterProductsByNamePartiallyAndCaseInsensitively() {

        Produit clavier = new Produit();
        clavier.setNom("Clavier mécanique");
        clavier.setQuantiteActuelle(10);
        clavier.setStockMinimum(5);

        Produit clavierSansFil = new Produit();
        clavierSansFil.setNom("Clavier sans fil");
        clavierSansFil.setQuantiteActuelle(8);
        clavierSansFil.setStockMinimum(5);

        Produit souris = new Produit();
        souris.setNom("Souris ergonomique");
        souris.setQuantiteActuelle(15);
        souris.setStockMinimum(5);

        produitRepository.save(clavier);
        produitRepository.save(clavierSansFil);
        produitRepository.save(souris);

        ProduitFilterRequest filtre = new ProduitFilterRequest(
                "CLAV",
                null,
                null,
                null,
                null,
                0,
                10
        );

        Page<ProduitResponse> result = produitService.search(filtre);

        assertThat(result.getTotalElements()).isEqualTo(2);

        assertThat(result.getContent())
                .extracting(ProduitResponse::nom)
                .containsExactlyInAnyOrder(
                        "Clavier mécanique",
                        "Clavier sans fil"
                )
                .doesNotContain("Souris ergonomique");
    }

    @Test
    void searchShouldFilterProductsByCategoryId() {

        Produit produitA = new Produit();
        produitA.setNom("Clavier");
        produitA.setCategorieId("cat-123");
        produitA.setQuantiteActuelle(10);
        produitA.setStockMinimum(5);

        Produit produitB = new Produit();
        produitB.setNom("Souris");
        produitB.setCategorieId("cat-123");
        produitB.setQuantiteActuelle(10);
        produitB.setStockMinimum(5);

        Produit produitC = new Produit();
        produitC.setNom("Écran");
        produitC.setCategorieId("cat-456");
        produitC.setQuantiteActuelle(10);
        produitC.setStockMinimum(5);

        produitRepository.save(produitA);
        produitRepository.save(produitB);
        produitRepository.save(produitC);

        ProduitFilterRequest filtre = new ProduitFilterRequest(
                null,
                "cat-123",
                null,
                null,
                null,
                0,
                10
        );

        Page<ProduitResponse> result = produitService.search(filtre);

        assertThat(result.getTotalElements()).isEqualTo(2);

        assertThat(result.getContent())
                .extracting(ProduitResponse::nom)
                .containsExactlyInAnyOrder(
                        "Clavier",
                        "Souris"
                );
    }

    @Test
    void searchFiltreEtatStockCritique() {
        Produit produitCritique = new Produit();
        produitCritique.setNom("Produit critique");
        produitCritique.setQuantiteActuelle(9);
        produitCritique.setStockMinimum(10);

        Produit produitLimite = new Produit();
        produitLimite.setNom("Produit limite");
        produitLimite.setQuantiteActuelle(10);
        produitLimite.setStockMinimum(10);

        produitRepository.saveAll(List.of(
                produitCritique,
                produitLimite
        ));

        ProduitFilterRequest filtre = new ProduitFilterRequest(
                null,
                null,
                "CRITIQUE",
                null,
                null,
                0,
                10
        );

        Page<ProduitResponse> result = produitService.search(filtre);

        assertThat(result.getContent())
                .extracting(ProduitResponse::nom)
                .containsExactly("Produit critique");
    }

    @Test
    void searchFiltreEtatStockFaible() {
        Produit produitCritique = new Produit();
        produitCritique.setNom("Produit critique");
        produitCritique.setQuantiteActuelle(9);
        produitCritique.setStockMinimum(10);

        Produit produitFaibleMinimum = new Produit();
        produitFaibleMinimum.setNom("Produit faible minimum");
        produitFaibleMinimum.setQuantiteActuelle(10);
        produitFaibleMinimum.setStockMinimum(10);

        Produit produitFaibleMaximum = new Produit();
        produitFaibleMaximum.setNom("Produit faible maximum");
        produitFaibleMaximum.setQuantiteActuelle(19);
        produitFaibleMaximum.setStockMinimum(10);

        Produit produitOk = new Produit();
        produitOk.setNom("Produit OK");
        produitOk.setQuantiteActuelle(20);
        produitOk.setStockMinimum(10);

        produitRepository.saveAll(List.of(
                produitCritique,
                produitFaibleMinimum,
                produitFaibleMaximum,
                produitOk
        ));

        ProduitFilterRequest filtre = new ProduitFilterRequest(
                null,
                null,
                "FAIBLE",
                null,
                null,
                0,
                10
        );

        Page<ProduitResponse> result = produitService.search(filtre);

        assertThat(result.getContent())
                .extracting(ProduitResponse::nom)
                .containsExactlyInAnyOrder(
                        "Produit faible minimum",
                        "Produit faible maximum"
                );
    }

    @Test
    void searchFiltreEtatStockOk() {
        Produit produitFaible = new Produit();
        produitFaible.setNom("Produit faible");
        produitFaible.setQuantiteActuelle(19);
        produitFaible.setStockMinimum(10);

        Produit produitOk = new Produit();
        produitOk.setNom("Produit OK");
        produitOk.setQuantiteActuelle(20);
        produitOk.setStockMinimum(10);

        produitRepository.saveAll(List.of(
                produitFaible,
                produitOk
        ));

        ProduitFilterRequest filtre = new ProduitFilterRequest(
                null,
                null,
                "OK",
                null,
                null,
                0,
                10
        );

        Page<ProduitResponse> result = produitService.search(filtre);

        assertThat(result.getContent())
                .extracting(ProduitResponse::nom)
                .containsExactly("Produit OK");
    }

    @Test
    void searchTrieParQuantiteActuelle() {
        Produit produitA = new Produit();
        produitA.setNom("Produit A");
        produitA.setQuantiteActuelle(30);
        produitA.setStockMinimum(10);

        Produit produitB = new Produit();
        produitB.setNom("Produit B");
        produitB.setQuantiteActuelle(10);
        produitB.setStockMinimum(10);

        Produit produitC = new Produit();
        produitC.setNom("Produit C");
        produitC.setQuantiteActuelle(20);
        produitC.setStockMinimum(10);

        produitRepository.saveAll(List.of(
                produitA,
                produitB,
                produitC
        ));

        // Tri croissant
        ProduitFilterRequest filtreAsc = new ProduitFilterRequest(
                null,
                null,
                null,
                "quantiteActuelle",
                "ASC",
                0,
                10
        );

        Page<ProduitResponse> resultAsc =
                produitService.search(filtreAsc);

        assertThat(resultAsc.getContent())
                .extracting(ProduitResponse::quantiteActuelle)
                .containsExactly(
                        10,
                        20,
                        30
                );

        // Tri décroissant
        ProduitFilterRequest filtreDesc = new ProduitFilterRequest(
                null,
                null,
                null,
                "quantiteActuelle",
                "DESC",
                0,
                10
        );

        Page<ProduitResponse> resultDesc =
                produitService.search(filtreDesc);

        assertThat(resultDesc.getContent())
                .extracting(ProduitResponse::quantiteActuelle)
                .containsExactly(
                        30,
                        20,
                        10
                );
    }

    @Test
    void searchPagineLesResultats() {
        Produit produitA = new Produit();
        produitA.setNom("Produit A");
        produitA.setQuantiteActuelle(10);
        produitA.setStockMinimum(10);

        Produit produitB = new Produit();
        produitB.setNom("Produit B");
        produitB.setQuantiteActuelle(10);
        produitB.setStockMinimum(10);

        Produit produitC = new Produit();
        produitC.setNom("Produit C");
        produitC.setQuantiteActuelle(10);
        produitC.setStockMinimum(10);

        Produit produitD = new Produit();
        produitD.setNom("Produit D");
        produitD.setQuantiteActuelle(10);
        produitD.setStockMinimum(10);

        Produit produitE = new Produit();
        produitE.setNom("Produit E");
        produitE.setQuantiteActuelle(10);
        produitE.setStockMinimum(10);

        produitRepository.saveAll(List.of(
                produitA,
                produitB,
                produitC,
                produitD,
                produitE
        ));

        ProduitFilterRequest filtre = new ProduitFilterRequest(
                null,               // nom
                null,               // categorieId
                null,               // etatStock
                "nom",              // triChamp
                "ASC",              // triDirection
                1,                  // page
                2                   // taille
        );

        Page<ProduitResponse> result =
                produitService.search(filtre);

        assertThat(result.getContent())
                .extracting(ProduitResponse::nom)
                .containsExactly(
                        "Produit C",
                        "Produit D"
                );

        assertThat(result.getTotalElements())
                .isEqualTo(5);
    }

}
