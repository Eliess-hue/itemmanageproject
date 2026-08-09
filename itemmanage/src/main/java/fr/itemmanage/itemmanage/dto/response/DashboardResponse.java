package fr.itemmanage.itemmanage.dto.response;

import java.util.List;

public record DashboardResponse(
        long nombreProduits,
        long nombreCategories,
        long nombreProduitsCritiques,
        long nombreMouvementsAujourdhui,
        List<ProduitResponse> alertesCritiques,
        List<MouvementHistoriqueResponse> derniersMouvements
) {}