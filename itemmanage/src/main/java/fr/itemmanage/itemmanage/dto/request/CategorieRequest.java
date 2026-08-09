package fr.itemmanage.itemmanage.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategorieRequest(
        @NotBlank
        String nom,
        String description
) {
}
