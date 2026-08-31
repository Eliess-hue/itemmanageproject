# ItemManage — Frontend

[← Retour au README principal](../README.md)

Interface utilisateur développée avec Vue 3 pour l'application de gestion de stock ItemManage.

## Prérequis
* Node.js `^22.18.0` ou `>=24.12.0`
* npm

## Configuration

L'URL de l'API backend est configurée via la variable d'environnement `VITE_API_URL`.

Créer un fichier `.env` à la racine de `itemmanage-frontend/` :
```env
VITE_API_URL=http://localhost:8080
```

## Lancer en local

```bash
npm install
npm run dev
```

L'application est alors disponible sur `http://localhost:5173`.

## Build de production

```bash
npm run build
npm run preview
```

## Qualité de code

```bash
# Linting (ESLint + oxlint)
npm run lint

# Formatage (Prettier)
npm run format
```