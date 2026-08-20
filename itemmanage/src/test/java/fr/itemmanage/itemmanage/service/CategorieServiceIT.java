package fr.itemmanage.itemmanage.service;

import fr.itemmanage.itemmanage.dto.response.CategorieResponse;
import fr.itemmanage.itemmanage.model.Categorie;
import fr.itemmanage.itemmanage.model.Produit;
import fr.itemmanage.itemmanage.repository.CategorieRepository;
import fr.itemmanage.itemmanage.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class CategorieServiceIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer("mongo:8.0")
                    .withEnv("GLIBC_TUNABLES", "glibc.pthread.rseq=1");

    @Autowired
    private CategorieService categorieService;

    @Autowired
    private CategorieRepository categorieRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @BeforeEach
    void nettoyerBase() {
        produitRepository.deleteAll();
        categorieRepository.deleteAll();
    }

    @Test
    void getAllShouldReturnCategoriesWithProductCounts() {

        // Given
        Categorie informatique = new Categorie();
        informatique.setNom("Informatique");
        informatique.setDescription("Matériel informatique");

        Categorie bureau = new Categorie();
        bureau.setNom("Bureau");
        bureau.setDescription("Fournitures de bureau");

        Categorie categorieSansProduit = new Categorie();
        categorieSansProduit.setNom("Sans produit");
        categorieSansProduit.setDescription("Aucun produit");

        categorieRepository.saveAll(
                List.of(informatique, bureau, categorieSansProduit)
        );

        Produit produit1 = new Produit();
        produit1.setNom("Clavier");
        produit1.setCategorieId(informatique.getId());

        Produit produit2 = new Produit();
        produit2.setNom("Souris");
        produit2.setCategorieId(informatique.getId());

        Produit produit3 = new Produit();
        produit3.setNom("Chaise");
        produit3.setCategorieId(bureau.getId());

        produitRepository.saveAll(
                List.of(produit1, produit2, produit3)
        );

        // When
        List<CategorieResponse> responses = categorieService.getAll();

        // Then
        assertThat(responses).hasSize(3);

        CategorieResponse informatiqueResponse =
                responses.stream()
                        .filter(response ->
                                response.id().equals(informatique.getId()))
                        .findFirst()
                        .orElseThrow();

        CategorieResponse bureauResponse =
                responses.stream()
                        .filter(response ->
                                response.id().equals(bureau.getId()))
                        .findFirst()
                        .orElseThrow();

        CategorieResponse sansProduitResponse =
                responses.stream()
                        .filter(response ->
                                response.id().equals(categorieSansProduit.getId()))
                        .findFirst()
                        .orElseThrow();

        assertThat(informatiqueResponse.nombreProduits())
                .isEqualTo(2);

        assertThat(bureauResponse.nombreProduits())
                .isEqualTo(1);

        assertThat(sansProduitResponse.nombreProduits())
                .isZero();
    }

    @Test
    void getAllShouldReturnZeroForCategoryWithoutProducts() {

        // Given
        Categorie categorie = new Categorie();
        categorie.setNom("Informatique");
        categorie.setDescription("Matériel informatique");

        categorieRepository.save(categorie);

        // When
        List<CategorieResponse> responses = categorieService.getAll();

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).id())
                .isEqualTo(categorie.getId());
        assertThat(responses.get(0).nombreProduits())
                .isZero();
    }

    @Test
    void getAllShouldReturnEmptyListWhenNoCategoryExists() {

        // When
        List<CategorieResponse> responses = categorieService.getAll();

        // Then
        assertThat(responses).isEmpty();
    }

}
