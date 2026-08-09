package fr.itemmanage.itemmanage.controller;

import fr.itemmanage.itemmanage.dto.request.ProduitFilterRequest;
import fr.itemmanage.itemmanage.dto.response.ProduitResponse;
import fr.itemmanage.itemmanage.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    @GetMapping("/search")
    public List<ProduitResponse> search(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String categorieId,
            @RequestParam(required = false) String etatStock,
            @RequestParam(required = false) String triChamp,
            @RequestParam(required = false) String triDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int taille
    ) {
        ProduitFilterRequest filtre = new ProduitFilterRequest(
                nom, categorieId, etatStock, triChamp, triDirection, page, taille
        );
        return produitService.search(filtre);
    }
}