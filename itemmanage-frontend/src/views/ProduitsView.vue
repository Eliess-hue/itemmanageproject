<script setup>
import { onMounted, ref } from 'vue'

import ProduitsFiltres from '@/components/produit/ProduitsFiltres.vue'
import ProduitsTable from '@/components/produit/ProduitsTable.vue'
import ProduitModal from '@/components/produit/ProduitModal.vue'
import MouvementModal from '@/components/mouvement/MouvementModal.vue'
import PaginationGeneric from '@/components/PaginationGeneric.vue'

import { useProduits } from '@/composables/useProduits'
import { useCategories } from '@/composables/useCategories'

import produitService from '@/api/produitService'

const { filtres, produits, pagination, loading, error, fetchProduits, allerPage, changerTaille } =
  useProduits()

const {
  categories,
  error: errorCategories,
  fetchCategories,
} = useCategories()

// Modals
const produitModalOuvert = ref(false)
const produitIdAModifier = ref(null)

const mouvementModalOuvert = ref(false)
const produitIdPourMouvement = ref(null)

// Suppression
const erreurSuppression = ref(null)
const suppressionEnCours = ref(false)

// ----------------------------------------
// Filtres
// ----------------------------------------

const modifierNom = (nom) => {
  filtres.nom = nom
}

const modifierCategorie = (categorieId) => {
  filtres.categorieId = categorieId
}

const modifierEtatStock = (etatStock) => {
  filtres.etatStock = etatStock
}

// ----------------------------------------
// Tri
// ----------------------------------------

const trier = (champ) => {
  if (filtres.triChamp === champ) {
    filtres.triDirection = filtres.triDirection === 'ASC' ? 'DESC' : 'ASC'
  } else {
    filtres.triChamp = champ
    filtres.triDirection = 'ASC'
  }
}

// ----------------------------------------
// Produit
// ----------------------------------------

const ouvrirNouveauProduit = () => {
  produitIdAModifier.value = null
  produitModalOuvert.value = true
}

const ouvrirModification = (produitId) => {
  produitIdAModifier.value = produitId
  produitModalOuvert.value = true
}

const fermerProduitModal = () => {
  produitModalOuvert.value = false
  produitIdAModifier.value = null
}

const onProduitEnregistre = () => {
  fermerProduitModal()
  fetchProduits()
}

// ----------------------------------------
// Mouvement
// ----------------------------------------

const ouvrirMouvement = (produit) => {
  produitIdPourMouvement.value = produit.id
  mouvementModalOuvert.value = true
}

const fermerMouvementModal = () => {
  mouvementModalOuvert.value = false
  produitIdPourMouvement.value = null
}

const onMouvementEnregistre = () => {
  fermerMouvementModal()
  fetchProduits()
}

// ----------------------------------------
// Suppression
// ----------------------------------------

const supprimerProduit = async (produitId) => {
  erreurSuppression.value = null

  const confirmation = window.confirm('Voulez-vous vraiment supprimer ce produit ?')

  if (!confirmation) {
    return
  }

  suppressionEnCours.value = true

  try {
    await produitService.delete(produitId)

    await fetchProduits()
  } catch (error) {
    erreurSuppression.value = error.messages ?? ['Impossible de supprimer le produit.']
  } finally {
    suppressionEnCours.value = false
  }
}

// ----------------------------------------
// Initialisation
// ----------------------------------------

onMounted(() => {
  fetchProduits()
  fetchCategories()
})
</script>

<template>
  <main class="p-6">
    <!-- En-tête -->
    <header class="flex items-center justify-between">
      <div>
        <h1 class="text-[24px] font-bold text-text-primary">Produits</h1>

        <p class="mt-1 text-[13px]">Liste des produits</p>
      </div>

      <button
        type="button"
        class="rounded-md bg-success px-4 py-2 text-[14px] text-white"
        @click="ouvrirNouveauProduit"
      >
        + Nouveau produit
      </button>
    </header>

    <!-- Filtres -->
    <section class="mt-6">
      <ProduitsFiltres
        :nom="filtres.nom"
        :categorie-id="filtres.categorieId"
        :etat-stock="filtres.etatStock"
        :categories="categories"
        @update:nom="modifierNom"
        @update:categorie-id="modifierCategorie"
        @update:etat-stock="modifierEtatStock"
      />
    </section>

    <!-- Erreur suppression -->
    <div v-if="erreurSuppression" class="mt-4 rounded border border-danger bg-danger-light p-3">
      <p class="text-[13px] text-danger">
        {{ erreurSuppression[0] }}
      </p>

      <button
        type="button"
        class="mt-2 text-[12px] text-danger underline"
        @click="erreurSuppression = null"
      >
        Fermer
      </button>
    </div>

    <!-- Erreur catégories -->
    <div v-if="errorCategories" class="mt-4 rounded border border-danger bg-danger-light p-3">
      <p class="text-[13px] text-danger">Impossible de charger les catégories.</p>

      <button type="button" class="mt-2 text-[12px] text-danger underline" @click="fetchCategories">
        Réessayer
      </button>
    </div>

    <!-- Erreur produits -->
    <div v-if="error" class="mt-4 rounded border border-danger bg-danger-light p-3">
      <p class="text-[13px] text-danger">Impossible de charger les produits.</p>

      <button type="button" class="mt-2 text-[12px] text-danger underline" @click="fetchProduits">
        Réessayer
      </button>
    </div>

    <!-- Tableau -->
    <section class="mt-6">
      <ProduitsTable
        :produits="produits"
        :tri-champ="filtres.triChamp"
        :tri-direction="filtres.triDirection"
        :loading="loading"
        @trier="trier"
        @modifier="ouvrirModification"
        @supprimer="supprimerProduit"
        @mouvement="ouvrirMouvement"
      />
    </section>

    <!-- Pagination -->
    <section v-if="pagination" class="mt-4">
      <PaginationGeneric
        :page="pagination.number"
        :total-pages="pagination.totalPages"
        :total-elements="pagination.totalElements"
        :taille="pagination.size"
        :tailles="[10, 20, 50]"
        :loading="loading"
        @changer-page="allerPage"
        @changer-taille="changerTaille"
      />
    </section>

    <!-- Modal produit -->
    <ProduitModal
      :is-open="produitModalOuvert"
      :produit-id="produitIdAModifier"
      :categories="categories"
      @close="fermerProduitModal"
      @success="onProduitEnregistre"
    />

    <!-- Modal mouvement -->
    <MouvementModal
      :is-open="mouvementModalOuvert"
      :produit-id="produitIdPourMouvement"
      @close="fermerMouvementModal"
      @success="onMouvementEnregistre"
    />
  </main>
</template>
