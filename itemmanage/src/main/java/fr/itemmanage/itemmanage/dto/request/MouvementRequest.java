package fr.itemmanage.itemmanage.dto.request;

import fr.itemmanage.itemmanage.enums.TypeMouvement;
import jakarta.validation.constraints.NotNull;

public record MouvementRequest(
        String produitId,

        @NotNull
        TypeMouvement type,
        int quantite
) {
}
