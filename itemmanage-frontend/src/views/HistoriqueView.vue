<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

import MouvementsFiltres from '@/components/mouvement/MouvementsFiltres.vue'
import MouvementsTable from '@/components/mouvement/MouvementsTable.vue'
import PaginationGeneric from '@/components/PaginationGeneric.vue'

import { useMouvements } from '@/composables/useMouvements'
import { useProduitRecherche } from '@/composables/useProduitRecherche'

import { exporterMouvementsCsv } from '@/utils/csv'

const route = useRoute()

const {
  filtres,
  mouvements,
  pagination,
  loading,
  error,
  fetchMouvements,
  fetchTousLesMouvements,
  allerPage,
  changerTaille,
} = useMouvements()

const {
  rechercheProduit,
  produitsSuggestions,
  produitSelectionne,
  error: errorProduitRecherche,
  selectionnerProduit,
  viderSelection,
  chargerProduit,
} = useProduitRecherche()

// ----------------------------------------
// Synchronisation produitSelectionne → filtres.produitId
// ----------------------------------------

watch(produitSelectionne, (produit) => {
  filtres.produitId = produit ? produit.id : ''
})

// ----------------------------------------
// Filtres
// ----------------------------------------

const modifierRechercheProduit = (valeur) => {
  rechercheProduit.value = valeur
}

const modifierType = (valeur) => {
  filtres.type = valeur
}

const modifierDateDebut = (valeur) => {
  filtres.dateDebut = valeur
}

const modifierDateFin = (valeur) => {
  filtres.dateFin = valeur
}

// ----------------------------------------
// Export CSV
// ----------------------------------------

const exportEnCours = ref(false)
const erreurExport = ref(null)

const exporterCsv = async () => {
  erreurExport.value = null
  exportEnCours.value = true

  try {
    const tousLesMouvements = await fetchTousLesMouvements()
    exporterMouvementsCsv(tousLesMouvements)
  } catch (e) {
    erreurExport.value = "Impossible d'exporter l'historique."
  } finally {
    exportEnCours.value = false
  }
}

// ----------------------------------------
// Initialisation
// ----------------------------------------

onMounted(async () => {
  const produitId = route.query.produitId

  if (produitId) {
    // La chaîne réactive s'occupe du chargement :
    // chargerProduit → produitSelectionne → filtres.produitId → fetchMouvements
    await chargerProduit(produitId)
  } else {
    fetchMouvements()
  }
})
</script>

<template>
  <main class="p-6">
    <!-- En-tête -->
    <header class="flex items-center justify-between">
      <div>
        <h1 class="text-[24px] font-bold text-text-primary">Historique des mouvements</h1>
        <p class="mt-1 text-[13px]">Consultez l'ensemble des mouvements de stock.</p>
      </div>

      <button
        type="button"
        class="rounded-md border border-contours bg-white px-4 py-2 text-[14px] disabled:cursor-not-allowed disabled:opacity-50"
        :disabled="exportEnCours"
        @click="exporterCsv"
      >
        {{ exportEnCours ? 'Export en cours...' : 'Exporter CSV' }}
      </button>
    </header>

    <!-- Filtres -->
    <section class="mt-6">
      <MouvementsFiltres
        :recherche-produit="rechercheProduit"
        :produits-suggestions="produitsSuggestions"
        :produit-selectionne="produitSelectionne"
        :type="filtres.type"
        :date-debut="filtres.dateDebut"
        :date-fin="filtres.dateFin"
        @update:recherche-produit="modifierRechercheProduit"
        @selectionner-produit="selectionnerProduit"
        @vider-selection="viderSelection"
        @update:type="modifierType"
        @update:date-debut="modifierDateDebut"
        @update:date-fin="modifierDateFin"
      />
    </section>

    <!-- Erreur recherche produit -->
    <div v-if="errorProduitRecherche" class="mt-4 rounded border border-danger bg-danger-light p-3">
      <p class="text-[13px] text-danger">Impossible de rechercher les produits.</p>
    </div>

    <!-- Erreur export -->
    <div v-if="erreurExport" class="mt-4 rounded border border-danger bg-danger-light p-3">
      <p class="text-[13px] text-danger">{{ erreurExport }}</p>
      <button
        type="button"
        class="mt-2 text-[12px] text-danger underline"
        @click="erreurExport = null"
      >
        Fermer
      </button>
    </div>

    <!-- Erreur historique -->
    <div v-if="error" class="mt-4 rounded border border-danger bg-danger-light p-3">
      <p class="text-[13px] text-danger">Impossible de charger l'historique.</p>
      <button type="button" class="mt-2 text-[12px] text-danger underline" @click="fetchMouvements">
        Réessayer
      </button>
    </div>

    <!-- Tableau -->
    <section class="mt-6">
      <MouvementsTable :mouvements="mouvements" :loading="loading" />
    </section>

    <!-- Pagination -->
    <section v-if="pagination" class="mt-4">
      <PaginationGeneric
        :page="pagination.number"
        :total-pages="pagination.totalPages"
        :total-elements="pagination.totalElements"
        :taille="pagination.size"
        :tailles="[5, 10, 20, 50]"
        :loading="loading"
        @changer-page="allerPage"
        @changer-taille="changerTaille"
      />
    </section>
  </main>
</template>
