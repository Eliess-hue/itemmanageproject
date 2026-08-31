package fr.itemmanage.itemmanage.controller;

import fr.itemmanage.itemmanage.dto.request.CategorieRequest;
import fr.itemmanage.itemmanage.dto.response.CategorieResponse;
import fr.itemmanage.itemmanage.service.CategorieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(
        name = "Catégories",
        description = "Gestion des catégories de produits"
)
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorieController {

    private final CategorieService categorieService;

    @Operation(
            summary = "Lister toutes les catégories",
            description = "Retourne chaque catégorie avec le nombre de produits associés"
    )
    @GetMapping
    public ResponseEntity<List<CategorieResponse>> getAll() {
        return ResponseEntity.ok(categorieService.getAll());
    }

    @Operation(summary = "Créer une nouvelle catégorie")
    @PostMapping
    public ResponseEntity<CategorieResponse> create(
            @Valid @RequestBody CategorieRequest request) {

        CategorieResponse response = categorieService.create(request);

        URI location = URI.create("/api/categories/" + response.id());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @Operation(summary = "Renommer / mettre à jour une catégorie")
    @PutMapping("/{id}")
    public ResponseEntity<CategorieResponse> rename(
            @PathVariable String id,
            @Valid @RequestBody CategorieRequest request) {

        return ResponseEntity.ok(
                categorieService.rename(id, request)
        );
    }
}