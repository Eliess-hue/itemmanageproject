package fr.itemmanage.itemmanage.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "produits")
@Getter
@Setter
public class Produit {

    @Id
    private String id;

    private String nom;
    private String description;

    @Indexed
    private String categorieId;

    private int quantiteActuelle;
    private int stockMinimum;

}