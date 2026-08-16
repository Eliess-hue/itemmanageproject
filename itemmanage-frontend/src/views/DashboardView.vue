<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'

import KpiCard from '@/components/dashboard/KpiCard.vue'
import AlertesCritiques from '@/components/dashboard/AlertesCritiques.vue'
import DerniersMouvements from '@/components/dashboard/DerniersMouvements.vue'
import { useDashboard } from '@/composables/useDashboard'
import MouvementModal from '@/components/mouvement/MouvementModal.vue'

const { dashboard, loading, error, fetchDashboard } = useDashboard()

const modalOuvert = ref(false)

const onMouvementEnregistre = () => {
  modalOuvert.value = false
  fetchDashboard()
}

onMounted(fetchDashboard)
</script>

<template>
  <main class="p-6">
    <!-- Chargement -->
    <div v-if="loading">Chargement...</div>

    <!-- Erreur -->
    <div v-else-if="error">
      <p class="text-danger">Impossible de charger le tableau de bord.</p>

      <button type="button" class="mt-2 text-info" @click="fetchDashboard">Réessayer</button>
    </div>

    <!-- Dashboard -->
    <div v-else-if="dashboard">
      <!-- En-tête -->
      <header class="flex items-center justify-between">
        <h1 class="text-[24px] font-bold text-text-primary">Tableau de bord</h1>

        <div class="flex gap-3">
          <RouterLink to="/produits" class="bg-success px-4 py-2 rounded-md text-[14px] text-white">
            + Nouveau produit
          </RouterLink>

          <button
            type="button"
            class="bg-success px-4 py-2 rounded-md text-[14px] text-white"
            @click="modalOuvert = true"
          >
            + Nouveau mouvement
          </button>
        </div>
      </header>

      <!-- KPI -->
      <section class="mt-6 grid grid-cols-4 gap-4">
        <KpiCard titre="Nombre de produits" :valeur="dashboard.nombreProduits" />

        <KpiCard titre="Nombre de catégories" :valeur="dashboard.nombreCategories" />

        <KpiCard
          titre="Stock critique"
          :valeur="dashboard.nombreProduitsCritiques"
          :type="dashboard.nombreProduitsCritiques > 0 ? 'danger' : 'neutral'"
        />

        <KpiCard titre="Mouvements aujourd'hui" :valeur="dashboard.nombreMouvementsAujourdhui" />
      </section>

      <!-- Alertes + mouvements -->
      <section class="mt-6 grid grid-cols-2 gap-6">
        <AlertesCritiques :alertes-critiques="dashboard.alertesCritiques" />

        <DerniersMouvements :derniers-mouvements="dashboard.derniersMouvements" />
      </section>
    </div>

    <MouvementModal
      :is-open="modalOuvert"
      @close="modalOuvert = false"
      @success="onMouvementEnregistre"
    />
  </main>
</template>
