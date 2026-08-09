package fr.itemmanage.itemmanage.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ProduitRequest(
        @NotBlank
        String nom,
        String description,
        String categorieId,

        @Min(0)
        int stockMinimum
) {
}
