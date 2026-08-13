package fr.itemmanage.itemmanage.service;

import fr.itemmanage.itemmanage.dto.request.CategorieRequest;
import fr.itemmanage.itemmanage.dto.response.CategorieResponse;
import fr.itemmanage.itemmanage.exception.ResourceNotFoundException;
import fr.itemmanage.itemmanage.model.Categorie;
import fr.itemmanage.itemmanage.repository.CategorieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CategorieServiceTest {

    @Mock
    private CategorieRepository categorieRepository;

    @InjectMocks
    private CategorieService categorieService;

    @Test
    void renameShouldUpdateCategorieAndReturnResponse() {

        //Given
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

        //When
        CategorieResponse response =
                categorieService.rename("abc", request);

        //Then
        assertThat(response.id()).isEqualTo("abc");
        assertThat(response.nom()).isEqualTo("Nouveau nom");
        assertThat(response.description()).isEqualTo("Nouvelle description");

        verify(categorieRepository).findById("abc");
        verify(categorieRepository).save(categorie);

    }

    @Test
    void renameShouldThrowWhenCategorieDoesNotExist() {

        //Given
        when(categorieRepository.findById("inexistant"))
                .thenReturn(Optional.empty());

        //Given & Then
        assertThatThrownBy(() ->
                categorieService.rename(
                        "inexistant",
                        new CategorieRequest("Nouveau nom", "Description")
                )
        )
                .isInstanceOf(ResourceNotFoundException.class);

        verify(categorieRepository).findById("inexistant");

    }

    @Test
    void getAllShouldReturnAllCategories() {

        // Given
        Categorie categorie1 = new Categorie();
        categorie1.setId("1");
        categorie1.setNom("Informatique");
        categorie1.setDescription("Matériel informatique");

        Categorie categorie2 = new Categorie();
        categorie2.setId("2");
        categorie2.setNom("Bureau");
        categorie2.setDescription("Fournitures de bureau");

        when(categorieRepository.findAll())
                .thenReturn(List.of(categorie1, categorie2));

        // When
        List<CategorieResponse> responses = categorieService.getAll();

        // Then
        assertThat(responses).hasSize(2);

        assertThat(responses.get(0).id()).isEqualTo("1");
        assertThat(responses.get(0).nom()).isEqualTo("Informatique");
        assertThat(responses.get(0).description())
                .isEqualTo("Matériel informatique");

        assertThat(responses.get(1).id()).isEqualTo("2");
        assertThat(responses.get(1).nom()).isEqualTo("Bureau");
        assertThat(responses.get(1).description())
                .isEqualTo("Fournitures de bureau");

        verify(categorieRepository).findAll();
    }

    @Test
    void getAllShouldReturnEmptyListWhenNoCategoryExists() {

        // Given
        when(categorieRepository.findAll())
                .thenReturn(List.of());

        // When
        List<CategorieResponse> responses = categorieService.getAll();

        // Then
        assertThat(responses).isEmpty();

        verify(categorieRepository).findAll();
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
        assertThat(response.id()).isEqualTo("123");
        assertThat(response.nom()).isEqualTo("Informatique");
        assertThat(response.description())
                .isEqualTo("Matériel informatique");

        // Then : vérifier ce qui a réellement été envoyé au repository
        ArgumentCaptor<Categorie> captor =
                ArgumentCaptor.forClass(Categorie.class);

        verify(categorieRepository).save(captor.capture());

        Categorie categorieEnvoyee = captor.getValue();

        assertThat(categorieEnvoyee.getNom())
                .isEqualTo(request.nom());

        assertThat(categorieEnvoyee.getDescription())
                .isEqualTo(request.description());
    }

}