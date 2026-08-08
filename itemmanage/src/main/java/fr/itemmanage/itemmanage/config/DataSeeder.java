package fr.itemmanage.itemmanage.config;

import fr.itemmanage.itemmanage.model.Categorie;
import fr.itemmanage.itemmanage.repository.CategorieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final String NOM_CATEGORIE_PAR_DEFAUT = "Non catégorisé";

    private final CategorieRepository categorieRepository;

    public DataSeeder(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    @Override
    public void run(String... args) {
        if (!categorieRepository.existsByNom(NOM_CATEGORIE_PAR_DEFAUT)) {
            Categorie categorieParDefaut = new Categorie();
            categorieParDefaut.setNom(NOM_CATEGORIE_PAR_DEFAUT);
            categorieParDefaut.setDescription(
                    "Catégorie assignée automatiquement aux produits sans catégorie spécifique."
            );

            categorieRepository.save(categorieParDefaut);
        }
    }
}