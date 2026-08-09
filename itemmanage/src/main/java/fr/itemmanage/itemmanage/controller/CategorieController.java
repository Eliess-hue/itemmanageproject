package fr.itemmanage.itemmanage.controller;

import fr.itemmanage.itemmanage.dto.request.CategorieRequest;
import fr.itemmanage.itemmanage.dto.response.CategorieResponse;
import fr.itemmanage.itemmanage.service.CategorieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorieController {

    private final CategorieService categorieService;

    // GET /api/categories
    @GetMapping
    public ResponseEntity<List<CategorieResponse>> getAll() {
        return ResponseEntity.ok(categorieService.getAll());
    }

    // POST /api/categories
    @PostMapping
    public ResponseEntity<CategorieResponse> create(
            @Valid @RequestBody CategorieRequest request) {

        CategorieResponse response = categorieService.create(request);

        URI location = URI.create("/api/categories/" + response.id());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    // PUT /api/categories/{id}
    @PutMapping("/{id}")
    public ResponseEntity<CategorieResponse> rename(
            @PathVariable String id,
            @Valid @RequestBody CategorieRequest request) {

        return ResponseEntity.ok(
                categorieService.rename(id, request)
        );
    }
}