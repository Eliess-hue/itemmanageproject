package fr.itemmanage.itemmanage.service;

import fr.itemmanage.itemmanage.dto.request.CategorieRequest;
import fr.itemmanage.itemmanage.dto.response.CategorieResponse;
import fr.itemmanage.itemmanage.model.Categorie;
import fr.itemmanage.itemmanage.repository.CategorieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorieService {

    private final CategorieRepository categorieRepository;

    public List<CategorieResponse> getAll() {
        return categorieRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CategorieResponse create(CategorieRequest request) {
        Categorie categorie = new Categorie();
        categorie.setNom(request.nom());
        categorie.setDescription(request.description());
        Categorie categorieCreee = categorieRepository.save(categorie);
        return toResponse(categorieCreee);
    }

    public CategorieResponse rename(String id, CategorieRequest request) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable : " + id));

        categorie.setNom(request.nom());
        categorie.setDescription(request.description());
        Categorie categorieModifiee = categorieRepository.save(categorie);
        return toResponse(categorieModifiee);
    }

    private CategorieResponse toResponse(Categorie categorie) {
        return new CategorieResponse(categorie.getId(), categorie.getNom(), categorie.getDescription());
    }
}