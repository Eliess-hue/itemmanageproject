package fr.itemmanage.itemmanage.model;

import fr.itemmanage.itemmanage.enums.TypeMouvement;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "mouvements")
@CompoundIndexes({
        @CompoundIndex(name = "produit_date_idx", def = "{'produitId': 1, 'date': -1}")
})
@Getter
@Setter
public class Mouvement {

    @Id
    private String id;

    private String produitId;
    private TypeMouvement type;

    // déjà signée : +12 pour une entrée, -3 pour une sortie
    private int quantite;

    // capturé via findAndModify au moment de l'écriture
    private int stockApres;
    private Instant date;

}