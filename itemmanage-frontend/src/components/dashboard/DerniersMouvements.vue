<script setup>
defineProps({
  derniersMouvements: {
    type: Array,
    required: true,
  },
})

const icones = {
  ENTREE: '📥',
  SORTIE: '📤',
  AJUSTEMENT: '✏️',
}

const couleurQuantite = (quantite) => {
  if (quantite > 0) {
    return 'text-success'
  }

  if (quantite < 0) {
    return 'text-danger'
  }

  return 'text-contours'
}

const formaterDate = (date) => {
  return new Intl.DateTimeFormat('fr-FR').format(new Date(date))
}

const formaterQuantite = (quantite) => {
  return quantite > 0 ? `+${quantite}` : quantite
}
</script>

<template>
  <section class="bg-white border border-contours rounded-md p-6">
    <h2 class="text-[16px] text-text-primary">Derniers mouvements</h2>

    <p class="mt-1 text-[14px] text-text-primary">Les 9 mouvements les plus récents</p>

    <div class="mt-6 overflow-hidden border border-contours rounded-md">
      <table class="w-full border-collapse text-[14px]">
        <thead class="bg-contours">
          <tr>
            <th class="px-3 py-2 text-left font-normal">Date</th>

            <th class="px-3 py-2 text-left font-normal">Produit</th>

            <th class="px-3 py-2 text-center font-normal">Type</th>

            <th class="px-3 py-2 text-right font-normal">Qte</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="mouvement in derniersMouvements"
            :key="mouvement.id"
            class="border-t border-contours"
          >
            <td class="px-3 py-2">
              {{ formaterDate(mouvement.date) }}
            </td>

            <td class="px-3 py-2">
              {{ mouvement.nomProduit }}
            </td>

            <td class="px-3 py-2 text-center">
              {{ icones[mouvement.type] }}
            </td>

            <td
              class="px-3 py-2 text-right font-medium"
              :class="couleurQuantite(mouvement.quantite)"
            >
              {{ formaterQuantite(mouvement.quantite) }}
            </td>
          </tr>

          <tr v-if="derniersMouvements.length === 0">
            <td colspan="4" class="px-3 py-6 text-center text-[14px] text-text-primary">
              Aucun mouvement récent.
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <RouterLink to="/historique" class="mt-6 inline-block text-[12px] text-info">
      Voir tout l'historique &gt;
    </RouterLink>
  </section>
</template>
