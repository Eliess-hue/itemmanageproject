# ItemManage — Backend

[← Retour au README principal](../README.md)

API REST développée avec Spring Boot pour la gestion de stock ItemManage.

## Prérequis
* Java 21
* Maven (le wrapper `./mvnw` est fourni, pas besoin de l'installer globalement)
* Une instance MongoDB accessible (locale ou distante)

## Configuration

L'application utilise le profil **`dev`** par défaut (`spring.profiles.active=dev`).

Variable d'environnement requise :
```env
MONGODB_URI=mongodb://localhost:27017/itemmanage
```

Par défaut en profil `dev`, le frontend est attendu sur `http://localhost:5173` (port Vite par défaut) — modifiable via `item.frontend.url`.

## Lancer en local

```bash
export MONGODB_URI=mongodb://localhost:27017/itemmanage
./mvnw spring-boot:run
```

L'API est alors disponible sur `http://localhost:8080`.

## Tests

```bash
# Tests unitaires uniquement
./mvnw test

# Tests unitaires + tests d'intégration (Testcontainers)
./mvnw verify
```

Les tests d'intégration utilisent [Testcontainers](https://testcontainers.com/) pour démarrer une instance MongoDB éphémère — Docker doit donc être disponible sur la machine pour exécuter `./mvnw verify`.

## Documentation API

Une fois l'application lancée, la documentation Swagger UI est disponible sur :

http://localhost:8080/swagger-ui/index.html


Le contrat OpenAPI brut (JSON) est accessible sur `http://localhost:8080/v3/api-docs`.

## Supervision

Spring Boot Actuator expose des informations sur l'état de l'application :

http://localhost:8080/actuator/health