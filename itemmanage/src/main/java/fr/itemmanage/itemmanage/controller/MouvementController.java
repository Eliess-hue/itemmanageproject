package fr.itemmanage.itemmanage.controller;

import fr.itemmanage.itemmanage.dto.request.MouvementRequest;
import fr.itemmanage.itemmanage.dto.response.MouvementResponse;
import fr.itemmanage.itemmanage.service.MouvementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mouvements")
@RequiredArgsConstructor
public class MouvementController {

    private final MouvementService mouvementService;

    @PostMapping
    public MouvementResponse enregistrer(@RequestBody MouvementRequest request) {
        return mouvementService.enregistrerMouvement(request);
    }
}