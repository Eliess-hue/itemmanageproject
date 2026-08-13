package fr.itemmanage.itemmanage.service;

import fr.itemmanage.itemmanage.dto.request.MouvementRequest;
import fr.itemmanage.itemmanage.enums.TypeMouvement;
import fr.itemmanage.itemmanage.exception.InvalidRequestException;
import fr.itemmanage.itemmanage.exception.ResourceNotFoundException;
import fr.itemmanage.itemmanage.repository.MouvementRepository;
import fr.itemmanage.itemmanage.repository.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MouvementServiceTest {

    @Mock
    private MouvementRepository mouvementRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private MouvementService mouvementService;

    @Test
    void enregistrerMouvementQuantiteNulleLeveUneInvalidRequestException() {
        MouvementRequest request = new MouvementRequest(
                "produit-1",
                TypeMouvement.ENTREE,
                0
        );

        assertThatThrownBy(() ->
                mouvementService.enregistrerMouvement(request)
        )
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("La quantité d'un mouvement ne peut pas être nulle");

        verify(produitRepository, never())
                .findById(any());
    }

    @Test
    void enregistrerMouvementProduitInexistantLeveUneResourceNotFoundException() {
        MouvementRequest request = new MouvementRequest(
                "produit-1",
                TypeMouvement.ENTREE,
                10
        );

        when(produitRepository.findById("produit-1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                mouvementService.enregistrerMouvement(request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Produit introuvable : produit-1");

        verify(produitRepository)
                .findById("produit-1");
    }

    @Test
    void rechercherTypeInvalideLeveUneInvalidRequestException() {
        assertThatThrownBy(() ->
                mouvementService.rechercher(
                        null,
                        "LIVRAISON",
                        null,
                        null,
                        0,
                        10
                )
        )
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Type de mouvement invalide : LIVRAISON");
    }

}
