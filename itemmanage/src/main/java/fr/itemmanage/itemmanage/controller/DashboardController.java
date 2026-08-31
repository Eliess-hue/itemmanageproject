package fr.itemmanage.itemmanage.controller;

import fr.itemmanage.itemmanage.dto.response.DashboardResponse;
import fr.itemmanage.itemmanage.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Dashboard",
        description = "Indicateurs synthétiques : produits, catégories, alertes de stock critique et derniers mouvements"
)
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "Récupérer les indicateurs du tableau de bord",
            description = "Agrège le nombre de produits, de catégories, de produits en stock critique, les mouvements du jour, les 5 dernières alertes critiques et les 9 derniers mouvements"
    )
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }
}