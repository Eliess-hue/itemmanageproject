package fr.itemmanage.itemmanage.controller;

import fr.itemmanage.itemmanage.dto.response.MouvementHistoriqueResponse;
import fr.itemmanage.itemmanage.dto.response.MouvementResponse;
import fr.itemmanage.itemmanage.dto.request.MouvementRequest;
import fr.itemmanage.itemmanage.service.MouvementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;

@Tag(
        name = "Mouvements",
        description = "Enregistrement et historique des entrées/sorties de stock"
)
@RestController
@RequestMapping("/api/mouvements")
@RequiredArgsConstructor
public class MouvementController {

    private final MouvementService mouvementService;

    @Operation(
            summary = "Enregistrer un mouvement de stock",
            description = "Crée un mouvement (entrée ou sortie) et met à jour la quantité actuelle du produit concerné"
    )
    @PostMapping
    public ResponseEntity<MouvementResponse> enregistrer(
            @Valid @RequestBody MouvementRequest request
    ) {
        MouvementResponse response =
                mouvementService.enregistrerMouvement(request);

        URI location = URI.create("/api/mouvements/" + response.id());

        return ResponseEntity.created(location).body(response);
    }

    @Operation(
            summary = "Rechercher l'historique des mouvements",
            description = "Recherche paginée avec filtres optionnels par produit, type de mouvement et plage de dates"
    )
    @GetMapping
    public ResponseEntity<Page<MouvementHistoriqueResponse>> rechercher(
            @RequestParam(required = false) String produitId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Instant dateDebut,
            @RequestParam(required = false) Instant dateFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int taille
    ) {
        Page<MouvementHistoriqueResponse> response =
                mouvementService.rechercher(
                        produitId,
                        type,
                        dateDebut,
                        dateFin,
                        page,
                        taille
                );

        return ResponseEntity.ok(response);
    }
}