<script setup>
import { onMounted, ref } from 'vue'

import CategoriesTable from '@/components/categorie/CategoriesTable.vue'
import CategorieModal from '@/components/categorie/CategorieModal.vue'
import PaginationGeneric from '@/components/PaginationGeneric.vue'

import { useCategories } from '@/composables/useCategories'

const {
  categories,
  loading,
  error,
  fetchCategories,
  recherche,
  categoriesPage,
  pagination,
  allerPage,
  changerTaille,
} = useCategories()

// Modal
const categorieModalOuverte = ref(false)
const categorieIdARenommer = ref(null)

const ouvrirNouvelleCategorie = () => {
  categorieIdARenommer.value = null
  categorieModalOuverte.value = true
}

const ouvrirRenommer = (categorieId) => {
  categorieIdARenommer.value = categorieId
  categorieModalOuverte.value = true
}

const fermerCategorieModal = () => {
  categorieModalOuverte.value = false
  categorieIdARenommer.value = null
}

const onCategorieEnregistree = () => {
  fermerCategorieModal()
  fetchCategories()
}

onMounted(() => {
  fetchCategories()
})
</script>

<template>
  <main class="p-6">
    <!-- En-tête -->
    <header class="flex items-center justify-between">
      <div>
        <h1 class="text-[24px] font-bold text-text-primary">Catégories</h1>
        <p class="mt-1 text-[13px]">Consultez et gérez l'ensemble de vos catégories.</p>
      </div>

      <button
        type="button"
        class="rounded-md bg-success px-4 py-2 text-[14px] text-white"
        @click="ouvrirNouvelleCategorie"
      >
        + Nouvelle catégorie
      </button>
    </header>

    <!-- Recherche -->
    <section class="mt-6 rounded-md border border-contours bg-white p-4">
      <label for="recherche-categorie" class="mb-2 block text-[13px] font-bold text-text-primary">
        Rechercher
      </label>

      <input
        id="recherche-categorie"
        v-model="recherche"
        type="search"
        placeholder="Rechercher une catégorie..."
        class="h-[36px] w-full max-w-[320px] rounded border border-contours px-3 text-[13px] outline-none focus:border-success"
      />
    </section>

    <!-- Erreur -->
    <div v-if="error" class="mt-4 rounded border border-danger bg-danger-light p-3">
      <p class="text-[13px] text-danger">Impossible de charger les catégories.</p>
      <button type="button" class="mt-2 text-[12px] text-danger underline" @click="fetchCategories">
        Réessayer
      </button>
    </div>

    <!-- Tableau -->
    <section class="mt-6">
      <CategoriesTable :categories="categoriesPage" :loading="loading" @renommer="ouvrirRenommer" />
    </section>

    <!-- Pagination -->
    <section v-if="categories.length" class="mt-4">
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

    <!-- Modal -->
    <CategorieModal
      :is-open="categorieModalOuverte"
      :categorie-id="categorieIdARenommer"
      :categories="categories"
      @close="fermerCategorieModal"
      @success="onCategorieEnregistree"
    />
  </main>
</template>
