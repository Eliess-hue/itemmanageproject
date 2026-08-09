package fr.itemmanage.itemmanage.controller;

import fr.itemmanage.itemmanage.dto.response.MouvementHistoriqueResponse;
import fr.itemmanage.itemmanage.dto.response.MouvementResponse;
import fr.itemmanage.itemmanage.dto.request.MouvementRequest;
import fr.itemmanage.itemmanage.service.MouvementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;

@RestController
@RequestMapping("/api/mouvements")
@RequiredArgsConstructor
public class MouvementController {

    private final MouvementService mouvementService;

    @PostMapping
    public ResponseEntity<MouvementResponse> enregistrer(
            @Valid @RequestBody MouvementRequest request
    ) {
        MouvementResponse response =
                mouvementService.enregistrerMouvement(request);

        URI location = URI.create("/api/mouvements/" + response.id());

        return ResponseEntity.created(location).body(response);
    }

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