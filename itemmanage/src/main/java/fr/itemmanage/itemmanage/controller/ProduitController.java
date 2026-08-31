package fr.itemmanage.itemmanage.controller;

import fr.itemmanage.itemmanage.dto.request.ProduitFilterRequest;
import fr.itemmanage.itemmanage.dto.request.ProduitRequest;
import fr.itemmanage.itemmanage.dto.response.ProduitResponse;
import fr.itemmanage.itemmanage.service.ProduitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(
        name = "Produits",
        description = "Gestion des produits en stock : recherche, création, mise à jour et suppression"
)
@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    @Operation(
            summary = "Rechercher des produits",
            description = "Recherche paginée des produits avec filtres optionnels par nom, catégorie et état de stock (CRITIQUE, FAIBLE, OK)"
    )
    @GetMapping("/search")
    public ResponseEntity<Page<ProduitResponse>> search(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String categorieId,
            @RequestParam(required = false) String etatStock,
            @RequestParam(required = false) String triChamp,
            @RequestParam(required = false) String triDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int taille
    ) {
        ProduitFilterRequest filtre = new ProduitFilterRequest(
                nom,
                categorieId,
                etatStock,
                triChamp,
                triDirection,
                page,
                taille
        );

        return ResponseEntity.ok(produitService.search(filtre));
    }

    @Operation(summary = "Récupérer un produit par son identifiant")
    @GetMapping("/{id}")
    public ResponseEntity<ProduitResponse> getById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(produitService.getById(id));
    }

    @Operation(
            summary = "Créer un nouveau produit",
            description = "Le produit est créé avec une quantité actuelle initialisée à 0"
    )
    @PostMapping
    public ResponseEntity<ProduitResponse> create(
            @Valid @RequestBody ProduitRequest request
    ) {
        ProduitResponse response = produitService.create(request);

        URI location = URI.create("/api/produits/" + response.id());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @Operation(
            summary = "Mettre à jour un produit",
            description = "Met à jour les informations du produit (nom, description, catégorie, seuil minimum). La quantité actuelle n'est jamais modifiée par cet endpoint."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ProduitResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ProduitRequest request
    ) {
        return ResponseEntity.ok(
                produitService.update(id, request)
        );
    }

    @Operation(
            summary = "Supprimer un produit",
            description = "Échoue si le produit possède des mouvements de stock enregistrés"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id
    ) {
        produitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}