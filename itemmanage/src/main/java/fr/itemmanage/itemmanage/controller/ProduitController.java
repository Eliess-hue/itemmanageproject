package fr.itemmanage.itemmanage.controller;

import fr.itemmanage.itemmanage.dto.request.ProduitFilterRequest;
import fr.itemmanage.itemmanage.dto.request.ProduitRequest;
import fr.itemmanage.itemmanage.dto.response.ProduitResponse;
import fr.itemmanage.itemmanage.service.ProduitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

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

    @GetMapping("/{id}")
    public ResponseEntity<ProduitResponse> getById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(produitService.getById(id));
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<ProduitResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ProduitRequest request
    ) {
        return ResponseEntity.ok(
                produitService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id
    ) {
        produitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}