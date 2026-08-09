package fr.itemmanage.itemmanage.dto.response;

public record ProduitResponse(
        String id,
        String nom,
        String description,
        String nomCategorie,
        int quantiteActuelle,
        int stockMinimum
) {
}
