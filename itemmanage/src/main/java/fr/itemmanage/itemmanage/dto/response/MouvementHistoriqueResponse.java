package fr.itemmanage.itemmanage.dto.response;

import fr.itemmanage.itemmanage.enums.TypeMouvement;

import java.time.Instant;

public record MouvementHistoriqueResponse(
        String id,
        String produitId,
        String nomProduit,
        TypeMouvement type,
        int quantite,
        int stockApres,
        Instant date
) {}