package fr.itemmanage.itemmanage.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
@Getter
@Setter
public class Categorie {

    @Id
    private String id;

    private String nom;
    private String description;

}