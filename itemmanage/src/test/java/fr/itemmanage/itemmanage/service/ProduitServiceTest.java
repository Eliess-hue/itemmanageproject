package fr.itemmanage.itemmanage.service;

import fr.itemmanage.itemmanage.dto.request.ProduitRequest;
import fr.itemmanage.itemmanage.dto.response.ProduitResponse;
import fr.itemmanage.itemmanage.exception.ResourceNotFoundException;
import fr.itemmanage.itemmanage.exception.ConflictException;
import fr.itemmanage.itemmanage.model.Categorie;
import fr.itemmanage.itemmanage.model.Produit;
import fr.itemmanage.itemmanage.repository.CategorieRepository;
import fr.itemmanage.itemmanage.repository.MouvementRepository;
import fr.itemmanage.itemmanage.repository.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private CategorieRepository categorieRepository;

    @Mock
    private MouvementRepository mouvementRepository;

    @InjectMocks
    private ProduitService produitService;

    @Test
    void getByIdShouldReturnProductWithCategoryName() {

        // Given
        Produit produit = new Produit();
        produit.setId("prod-1");
        produit.setNom("Ordinateur");
        produit.setDescription("PC portable");
        produit.setCategorieId("cat-1");
        produit.setQuantiteActuelle(10);
        produit.setStockMinimum(5);

        Categorie categorie = new Categorie();
        categorie.setId("cat-1");
        categorie.setNom("Informatique");

        when(produitRepository.findById("prod-1"))
                .thenReturn(Optional.of(produit));

        when(categorieRepository.findById("cat-1"))
                .thenReturn(Optional.of(categorie));

        // When
        ProduitResponse response = produitService.getById("prod-1");

        // Then
        assertThat(response.id()).isEqualTo("prod-1");
        assertThat(response.nom()).isEqualTo("Ordinateur");
        assertThat(response.description()).isEqualTo("PC portable");
        assertThat(response.nomCategorie()).isEqualTo("Informatique");
        assertThat(response.quantiteActuelle()).isEqualTo(10);
        assertThat(response.stockMinimum()).isEqualTo(5);
        assertThat(response.etatStock()).isEqualTo("OK");

        verify(produitRepository).findById("prod-1");
        verify(categorieRepository).findById("cat-1");
    }

    @Test
    void getByIdShouldReturnNullCategoryNameWhenCategoryDoesNotExist() {

        // Given
        Produit produit = new Produit();
        produit.setId("prod-1");
        produit.setNom("Ordinateur");
        produit.setDescription("PC portable");
        produit.setCategorieId("cat-inexistante");
        produit.setQuantiteActuelle(10);
        produit.setStockMinimum(5);

        when(produitRepository.findById("prod-1"))
                .thenReturn(Optional.of(produit));

        when(categorieRepository.findById("cat-inexistante"))
                .thenReturn(Optional.empty());

        // When
        ProduitResponse response = produitService.getById("prod-1");

        // Then
        assertThat(response.id()).isEqualTo("prod-1");
        assertThat(response.nom()).isEqualTo("Ordinateur");
        assertThat(response.nomCategorie()).isNull();
        assertThat(response.etatStock()).isEqualTo("OK");

        verify(produitRepository).findById("prod-1");
        verify(categorieRepository).findById("cat-inexistante");
    }

    @Test
    void getByIdShouldThrowWhenProductDoesNotExist() {

        // Given
        when(produitRepository.findById("prod-inexistant"))
                .thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() ->
                produitService.getById("prod-inexistant")
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Produit introuvable : prod-inexistant");

        verify(produitRepository).findById("prod-inexistant");

        verify(categorieRepository, never())
                .findById(any());
    }

    @Test
    void createShouldCreateProductWithCategoryName() {

        // Given
        ProduitRequest request = new ProduitRequest(
                "Ordinateur",
                "PC portable",
                "cat-1",
                5
        );

        Categorie categorie = new Categorie();
        categorie.setId("cat-1");
        categorie.setNom("Informatique");

        Produit produitSauvegarde = new Produit();
        produitSauvegarde.setId("prod-1");
        produitSauvegarde.setNom("Ordinateur");
        produitSauvegarde.setDescription("PC portable");
        produitSauvegarde.setCategorieId("cat-1");
        produitSauvegarde.setQuantiteActuelle(0);
        produitSauvegarde.setStockMinimum(5);

        when(categorieRepository.existsById("cat-1"))
                .thenReturn(true);

        when(produitRepository.save(any(Produit.class)))
                .thenReturn(produitSauvegarde);

        when(categorieRepository.findById("cat-1"))
                .thenReturn(Optional.of(categorie));

        // When
        ProduitResponse response = produitService.create(request);

        // Then
        ArgumentCaptor<Produit> captor =
                ArgumentCaptor.forClass(Produit.class);

        verify(produitRepository).save(captor.capture());

        Produit produitEnvoye = captor.getValue();

        assertThat(produitEnvoye.getNom())
                .isEqualTo(request.nom());

        assertThat(produitEnvoye.getDescription())
                .isEqualTo(request.description());

        assertThat(produitEnvoye.getCategorieId())
                .isEqualTo(request.categorieId());

        assertThat(produitEnvoye.getStockMinimum())
                .isEqualTo(request.stockMinimum());

        assertThat(produitEnvoye.getQuantiteActuelle())
                .isZero();

        assertThat(response.id())
                .isEqualTo("prod-1");

        assertThat(response.nom())
                .isEqualTo("Ordinateur");

        assertThat(response.description())
                .isEqualTo("PC portable");

        assertThat(response.nomCategorie())
                .isEqualTo("Informatique");

        assertThat(response.quantiteActuelle())
                .isZero();

        assertThat(response.stockMinimum())
                .isEqualTo(5);

        assertThat(response.etatStock())
                .isEqualTo("CRITIQUE");

        verify(produitRepository).save(any(Produit.class));
        verify(categorieRepository).findById("cat-1");
    }

    @Test
    void createShouldThrowWhenCategoryDoesNotExist() {

        // Given
        ProduitRequest request = new ProduitRequest(
                "Ordinateur",
                "PC portable",
                "cat-inexistante",
                5
        );

        when(categorieRepository.existsById("cat-inexistante"))
                .thenReturn(false);

        // When / Then
        assertThatThrownBy(() ->
                produitService.create(request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Catégorie introuvable : cat-inexistante");

        // Le produit ne doit surtout pas être créé.
        verify(produitRepository, never())
                .save(any(Produit.class));

        // On ne doit pas non plus chercher le nom de la catégorie
        // après un échec de validation.
        verify(categorieRepository, never())
                .findById("cat-inexistante");
    }

    @Test
    void updateShouldModifyProductWithoutChangingCurrentQuantity() {

        Produit produit = new Produit();
        produit.setId("prod-1");
        produit.setNom("Ancien nom");
        produit.setDescription("Ancienne description");
        produit.setCategorieId("cat-1");
        produit.setStockMinimum(10);
        produit.setQuantiteActuelle(42);

        ProduitRequest request = new ProduitRequest(
                "Nouveau nom",
                "Nouvelle description",
                "cat-2",
                20
        );

        Categorie categorie = new Categorie();
        categorie.setId("cat-2");
        categorie.setNom("Nouvelle catégorie");

        when(produitRepository.findById("prod-1"))
                .thenReturn(Optional.of(produit));

        when(categorieRepository.existsById("cat-2"))
                .thenReturn(true);

        when(categorieRepository.findById("cat-2"))
                .thenReturn(Optional.of(categorie));

        when(produitRepository.save(any(Produit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ProduitResponse response =
                produitService.update("prod-1", request);

        // Then
        ArgumentCaptor<Produit> captor =
                ArgumentCaptor.forClass(Produit.class);

        verify(produitRepository).save(captor.capture());

        Produit produitSauvegarde = captor.getValue();

        assertThat(produitSauvegarde.getNom())
                .isEqualTo("Nouveau nom");

        assertThat(produitSauvegarde.getDescription())
                .isEqualTo("Nouvelle description");

        assertThat(produitSauvegarde.getCategorieId())
                .isEqualTo("cat-2");

        assertThat(produitSauvegarde.getStockMinimum())
                .isEqualTo(20);

        // Règle métier importante :
        // update() ne doit jamais modifier le stock actuel.
        assertThat(produitSauvegarde.getQuantiteActuelle())
                .isEqualTo(42);

        assertThat(response.nom())
                .isEqualTo("Nouveau nom");

        assertThat(response.nomCategorie())
                .isEqualTo("Nouvelle catégorie");

        assertThat(response.etatStock())
                .isEqualTo("OK");
    }

    @Test
    void updateShouldThrowWhenProductDoesNotExist() {

        when(produitRepository.findById("prod-1"))
                .thenReturn(Optional.empty());

        ProduitRequest request = new ProduitRequest(
                "Nouveau nom",
                "Description",
                "cat-1",
                20
        );

        assertThatThrownBy(() ->
                produitService.update("prod-1", request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Produit introuvable : prod-1");

        verify(produitRepository, never())
                .save(any(Produit.class));
    }

    @Test
    void deleteShouldThrowWhenProductDoesNotExist() {

        when(produitRepository.existsById("prod-1"))
                .thenReturn(false);

        assertThatThrownBy(() ->
                produitService.delete("prod-1")
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Produit introuvable : prod-1");

        verify(produitRepository, never())
                .deleteById(any());
    }

    @Test
    void deleteShouldThrowConflictWhenProductHasMovements() {

        when(produitRepository.existsById("prod-1"))
                .thenReturn(true);

        when(mouvementRepository.existsByProduitId("prod-1"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                produitService.delete("prod-1")
        )
                .isInstanceOf(ConflictException.class)
                .hasMessage(
                        "Impossible de supprimer un produit ayant des mouvements enregistrés"
                );

        verify(produitRepository, never())
                .deleteById(any());
    }

    @Test
    void deleteShouldDeleteProductWhenItHasNoMovements() {

        when(produitRepository.existsById("prod-1"))
                .thenReturn(true);

        when(mouvementRepository.existsByProduitId("prod-1"))
                .thenReturn(false);

        produitService.delete("prod-1");

        verify(produitRepository)
                .deleteById("prod-1");
    }

    @Test
    void getByIdShouldReturnCritiqueStockState() {

        Produit produit = new Produit();
        produit.setId("prod-1");
        produit.setNom("Produit critique");
        produit.setCategorieId("cat-1");
        produit.setQuantiteActuelle(4);
        produit.setStockMinimum(5);

        when(produitRepository.findById("prod-1"))
                .thenReturn(Optional.of(produit));

        when(categorieRepository.findById("cat-1"))
                .thenReturn(Optional.empty());

        ProduitResponse response = produitService.getById("prod-1");

        assertThat(response.etatStock())
                .isEqualTo("CRITIQUE");
    }

    @Test
    void getByIdShouldReturnFaibleStockState() {

        Produit produit = new Produit();
        produit.setId("prod-1");
        produit.setNom("Produit faible");
        produit.setCategorieId("cat-1");
        produit.setQuantiteActuelle(7);
        produit.setStockMinimum(5);

        when(produitRepository.findById("prod-1"))
                .thenReturn(Optional.of(produit));

        when(categorieRepository.findById("cat-1"))
                .thenReturn(Optional.empty());

        ProduitResponse response = produitService.getById("prod-1");

        assertThat(response.etatStock())
                .isEqualTo("FAIBLE");
    }

    @Test
    void getByIdShouldReturnOkStockState() {

        Produit produit = new Produit();
        produit.setId("prod-1");
        produit.setNom("Produit OK");
        produit.setCategorieId("cat-1");
        produit.setQuantiteActuelle(10);
        produit.setStockMinimum(5);

        when(produitRepository.findById("prod-1"))
                .thenReturn(Optional.of(produit));

        when(categorieRepository.findById("cat-1"))
                .thenReturn(Optional.empty());

        ProduitResponse response = produitService.getById("prod-1");

        assertThat(response.etatStock())
                .isEqualTo("OK");
    }

}
