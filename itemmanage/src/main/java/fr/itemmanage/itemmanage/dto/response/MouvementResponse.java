package fr.itemmanage.itemmanage.dto.response;


import fr.itemmanage.itemmanage.enums.TypeMouvement;
import java.time.Instant;

public record MouvementResponse(
        String id,
        String produitId,
        TypeMouvement type,
        int quantite,
        int stockApres,
        Instant date
) {
}
