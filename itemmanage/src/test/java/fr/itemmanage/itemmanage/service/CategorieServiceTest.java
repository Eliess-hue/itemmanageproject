package fr.itemmanage.itemmanage.service;

import fr.itemmanage.itemmanage.dto.request.CategorieRequest;
import fr.itemmanage.itemmanage.dto.response.CategorieResponse;
import fr.itemmanage.itemmanage.exception.ResourceNotFoundException;
import fr.itemmanage.itemmanage.model.Categorie;
import fr.itemmanage.itemmanage.repository.CategorieRepository;
import fr.itemmanage.itemmanage.repository.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategorieServiceTest {

    @Mock
    private CategorieRepository categorieRepository;

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private CategorieService categorieService;

    @Test
    void renameShouldUpdateCategorieAndReturnResponse() {

        // Given
        Categorie categorie = new Categorie();
        categorie.setId("abc");
        categorie.setNom("Ancien nom");
        categorie.setDescription("Ancienne description");

        CategorieRequest request = new CategorieRequest(
                "Nouveau nom",
                "Nouvelle description"
        );

        when(categorieRepository.findById("abc"))
                .thenReturn(Optional.of(categorie));

        when(categorieRepository.save(categorie))
                .thenReturn(categorie);

        when(produitRepository.countByCategorieId("abc"))
                .thenReturn(3L);

        // When
        CategorieResponse response =
                categorieService.rename("abc", request);

        // Then
        assertThat(response.id()).isEqualTo("abc");
        assertThat(response.nom()).isEqualTo("Nouveau nom");
        assertThat(response.description())
                .isEqualTo("Nouvelle description");
        assertThat(response.nombreProduits())
                .isEqualTo(3L);

        verify(categorieRepository).findById("abc");
        verify(categorieRepository).save(categorie);
        verify(produitRepository).countByCategorieId("abc");
    }

    @Test
    void renameShouldThrowWhenCategorieDoesNotExist() {

        // Given
        when(categorieRepository.findById("inexistant"))
                .thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() ->
                categorieService.rename(
                        "inexistant",
                        new CategorieRequest(
                                "Nouveau nom",
                                "Description"
                        )
                )
        )
                .isInstanceOf(ResourceNotFoundException.class);

        verify(categorieRepository).findById("inexistant");

        verify(produitRepository, never())
                .countByCategorieId(any());
    }

    @Test
    void createShouldCreateCategoryAndReturnResponse() {

        // Given
        CategorieRequest request = new CategorieRequest(
                "Informatique",
                "Matériel informatique"
        );

        Categorie categorieSauvegardee = new Categorie();
        categorieSauvegardee.setId("123");
        categorieSauvegardee.setNom("Informatique");
        categorieSauvegardee.setDescription("Matériel informatique");

        when(categorieRepository.save(any(Categorie.class)))
                .thenReturn(categorieSauvegardee);

        // When
        CategorieResponse response =
                categorieService.create(request);

        // Then : résultat
        assertThat(response.id())
                .isEqualTo("123");

        assertThat(response.nom())
                .isEqualTo("Informatique");

        assertThat(response.description())
                .isEqualTo("Matériel informatique");

        assertThat(response.nombreProduits())
                .isZero();

        // Then : vérifier ce qui a réellement été envoyé au repository
        ArgumentCaptor<Categorie> captor =
                ArgumentCaptor.forClass(Categorie.class);

        verify(categorieRepository).save(captor.capture());

        Categorie categorieEnvoyee = captor.getValue();

        assertThat(categorieEnvoyee.getNom())
                .isEqualTo(request.nom());

        assertThat(categorieEnvoyee.getDescription())
                .isEqualTo(request.description());

        // Une nouvelle catégorie ne possède aucun produit.
        // Aucun comptage MongoDB n'est donc nécessaire.
        verify(produitRepository, never())
                .countByCategorieId(any());
    }

}