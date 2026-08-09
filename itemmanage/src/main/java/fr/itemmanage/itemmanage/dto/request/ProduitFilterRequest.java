package fr.itemmanage.itemmanage.dto.request;

public record ProduitFilterRequest(
        String nom,
        String categorieId,
        String etatStock, // "OK", "FAIBLE", "CRITIQUE", ou null (tous)
        String triChamp,   // "nom" ou "quantiteActuelle"
        String triDirection, // "ASC" ou "DESC"
        int page,
        int taille
) {
}
