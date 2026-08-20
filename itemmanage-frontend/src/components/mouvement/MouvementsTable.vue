<script setup>
defineProps({
  mouvements: {
    type: Array,
    required: true,
  },
  loading: {
    type: Boolean,
    required: true,
  },
})

const iconesType = {
  ENTREE: '📥',
  SORTIE: '📤',
  AJUSTEMENT: '✏️',
}

const libellesType = {
  ENTREE: 'Entrée',
  SORTIE: 'Sortie',
  AJUSTEMENT: 'Ajustement',
}

const classeQuantite = (quantite) => {
  if (quantite > 0) {
    return 'text-success'
  }

  if (quantite < 0) {
    return 'text-danger'
  }

  return 'text-text-primary'
}

const pluraliserUnites = (valeur) => (Math.abs(valeur) === 1 ? 'unité' : 'unités')

const texteQuantite = (quantite) => {
  const signe = quantite > 0 ? '+' : ''
  return `${signe}${quantite} ${pluraliserUnites(quantite)}`
}

const formateurDate = new Intl.DateTimeFormat('fr-FR')

const formaterDate = (date) => formateurDate.format(new Date(date))
</script>

<template>
  <section class="rounded-md border border-contours bg-white">
    <!-- En-tête -->
    <div class="flex items-center justify-between border-b border-contours px-4 py-3">
      <div>
        <h2 class="text-[16px] font-bold text-text-primary">Historique</h2>
        <p class="mt-1 text-[12px] text-text-secondary">Liste des mouvements</p>
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
            <th class="px-4 py-3 text-[12px] font-bold text-text-primary">Date</th>
            <th class="px-4 py-3 text-[12px] font-bold text-text-primary">Produit</th>
            <th class="px-4 py-3 text-[12px] font-bold text-text-primary">Type</th>
            <th class="px-4 py-3 text-[12px] font-bold text-text-primary">Quantité</th>
            <th class="px-4 py-3 text-right text-[12px] font-bold text-text-primary">
              Stock après
            </th>
          </tr>
        </thead>

        <tbody>
          <tr v-if="!mouvements.length">
            <td colspan="5" class="px-4 py-10 text-center text-[13px] text-text-secondary">
              Aucun mouvement trouvé.
            </td>
          </tr>

          <tr
            v-for="mouvement in mouvements"
            :key="mouvement.id"
            class="border-b border-contours last:border-b-0"
          >
            <td class="px-4 py-3 text-[13px] text-text-primary">
              {{ formaterDate(mouvement.date) }}
            </td>

            <td class="px-4 py-3 text-[13px] font-bold text-text-primary">
              {{ mouvement.nomProduit }}
            </td>

            <td class="px-4 py-3 text-[13px] text-text-primary">
              {{ iconesType[mouvement.type] }} {{ libellesType[mouvement.type] ?? mouvement.type }}
            </td>

            <td :class="['px-4 py-3 text-[13px] font-bold', classeQuantite(mouvement.quantite)]">
              {{ texteQuantite(mouvement.quantite) }}
            </td>

            <td class="px-4 py-3 text-right text-[13px] text-text-primary">
              {{ mouvement.stockApres }} {{ pluraliserUnites(mouvement.stockApres) }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
