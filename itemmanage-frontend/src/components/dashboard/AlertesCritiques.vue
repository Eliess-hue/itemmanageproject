<script setup>
defineProps({
  alertesCritiques: {
    type: Array,
    required: true,
  },
})
</script>

<template>
  <section class="bg-white border border-contours rounded-md p-6">
    <div class="flex items-center gap-3">
      <span class="w-6 h-6 rounded-full bg-danger"></span>

      <h2 class="text-[16px] text-text-primary">Alerte critique</h2>
    </div>

    <p class="mt-4 text-[14px] text-text-primary">Produits en rupture ou en stock critique.</p>

    <div v-if="alertesCritiques.length > 0" class="mt-6 space-y-4">
      <article
        v-for="alerte in alertesCritiques"
        :key="alerte.id"
        class="flex items-center gap-4 border border-danger bg-danger-light/30 rounded-md px-4 py-3"
      >
        <span class="w-6 h-6 shrink-0 rounded-full bg-danger"></span>

        <div class="w-10 h-10 shrink-0 rounded-md bg-contours"></div>

        <div class="min-w-0 flex-1">
          <p class="text-[12px] font-bold text-text-primary">
            {{ alerte.nom }}
          </p>

          <span class="inline-block mt-1 rounded-md bg-danger px-2 py-1 text-[12px] text-white">
            Critique
          </span>
        </div>

        <p class="text-[12px] text-text-primary whitespace-nowrap">
          Stock :
          <span class="text-danger">
            {{ alerte.quantiteActuelle }}
          </span>
          / Mini: {{ alerte.stockMinimum }}
        </p>
      </article>
    </div>

    <p v-else class="mt-6 py-6 text-center text-[14px] text-text-primary">
      Aucun produit critique.
    </p>

    <RouterLink
      :to="{
        path: '/produits',
        query: {
          filtre: 'critique',
        },
      }"
      class="mt-6 flex items-center justify-between text-[12px] text-info"
    >
      <span>Voir toutes les alertes</span>
      <span>&gt;</span>
    </RouterLink>
  </section>
</template>
