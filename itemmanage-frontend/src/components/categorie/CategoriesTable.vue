<script setup>
defineProps({
  categories: {
    type: Array,
    required: true,
  },
  loading: {
    type: Boolean,
    required: true,
  },
})

const emit = defineEmits(['renommer'])
</script>

<template>
  <section class="rounded-md border border-contours bg-white">
    <!-- En-tête -->
    <div class="flex items-center justify-between border-b border-contours px-4 py-3">
      <div>
        <h2 class="text-[16px] font-bold text-text-primary">Catégories</h2>
        <p class="mt-1 text-[12px] text-text-secondary">Liste des catégories</p>
      </div>

      <div
        v-if="loading"
        class="flex items-center gap-2 text-[12px] text-text-secondary"
        aria-live="polite"
      >
        <span
          class="h-3 w-3 animate-spin rounded-full border-2 border-contours border-t-success"
          aria-hidden="true"
        />
        <span>Actualisation...</span>
      </div>
    </div>

    <!-- Tableau -->
    <div :class="['overflow-x-auto transition-opacity', loading ? 'opacity-60' : 'opacity-100']">
      <table class="w-full border-collapse">
        <thead>
          <tr class="border-b border-contours bg-background text-left">
            <th class="px-4 py-3 text-[12px] font-bold text-text-primary">Nom</th>
            <th class="px-4 py-3 text-[12px] font-bold text-text-primary">Produits associés</th>
            <th class="px-4 py-3 text-right text-[12px] font-bold text-text-primary">Actions</th>
          </tr>
        </thead>

        <tbody>
          <tr v-if="!categories.length">
            <td colspan="3" class="px-4 py-10 text-center text-[13px] text-text-secondary">
              Aucune catégorie trouvée.
            </td>
          </tr>

          <tr
            v-for="categorie in categories"
            :key="categorie.id"
            class="border-b border-contours last:border-b-0"
          >
            <td class="px-4 py-3 text-[13px] font-bold text-text-primary">
              {{ categorie.nom }}
            </td>

            <td class="px-4 py-3 text-[13px] text-text-primary">
              {{ categorie.nombreProduits }}
            </td>

            <td class="px-4 py-3">
              <div class="flex justify-end">
                <button
                  type="button"
                  class="text-[15px] hover:opacity-60"
                  aria-label="Renommer la catégorie"
                  title="Renommer"
                  @click="emit('renommer', categorie.id)"
                >
                  ✏️
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
