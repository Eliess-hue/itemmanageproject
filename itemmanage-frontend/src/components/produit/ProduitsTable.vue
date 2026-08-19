<script setup>
import { RouterLink } from 'vue-router'

const props = defineProps({
  produits: {
    type: Array,
    required: true,
  },
  triChamp: {
    type: String,
    required: true,
  },
  triDirection: {
    type: String,
    required: true,
  },
  loading: {
    type: Boolean,
    required: true,
  },
})

const emit = defineEmits(['trier', 'modifier', 'supprimer', 'mouvement'])

const etatsStock = {
  CRITIQUE: {
    label: 'Critique',
    classe: 'bg-danger-light text-danger',
  },
  FAIBLE: {
    label: 'Faible',
    classe: 'bg-warning-light text-warning',
  },
  OK: {
    label: 'OK',
    classe: 'bg-accent text-success',
  },
}

const afficherEtat = (etat) => {
  return (
    etatsStock[etat] ?? {
      label: etat,
      classe: 'bg-background text-text-primary',
    }
  )
}

const estTrie = (champ) => {
  return props.triChamp === champ
}
</script>

<template>
  <section class="rounded-md border border-contours bg-white">
    <!-- En-tête du tableau -->
    <div class="flex items-center justify-between border-b border-contours px-4 py-3">
      <div>
        <h2 class="text-[16px] font-bold text-text-primary">Produits</h2>

        <p class="mt-1 text-[12px] text-text-secondary">Liste des produits</p>
      </div>

      <!-- Indicateur léger pendant le rechargement -->
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
            <!-- Produit -->
            <th class="px-4 py-3">
              <button
                type="button"
                class="flex items-center gap-1 text-[12px] font-bold text-text-primary"
                @click="emit('trier', 'nom')"
              >
                Produit

                <span
                  :class="['text-[12px]', estTrie('nom') ? 'text-success' : 'text-text-secondary']"
                >
                  {{ estTrie('nom') && triDirection === 'DESC' ? '⌃' : '⌄' }}
                </span>
              </button>
            </th>

            <!-- Catégorie -->
            <th class="px-4 py-3 text-[12px] font-bold text-text-primary">Catégorie</th>

            <!-- Stock actuel -->
            <th class="px-4 py-3">
              <button
                type="button"
                class="flex items-center gap-1 text-[12px] font-bold text-text-primary"
                @click="emit('trier', 'quantiteActuelle')"
              >
                Stock actuel

                <span
                  :class="[
                    'text-[12px]',
                    estTrie('quantiteActuelle') ? 'text-success' : 'text-text-secondary',
                  ]"
                >
                  {{ estTrie('quantiteActuelle') && triDirection === 'DESC' ? '⌃' : '⌄' }}
                </span>
              </button>
            </th>

            <!-- Stock minimum -->
            <th class="px-4 py-3">
              <button
                type="button"
                class="flex items-center gap-1 text-[12px] font-bold text-text-primary"
                @click="emit('trier', 'stockMinimum')"
              >
                Stock minimum

                <span
                  :class="[
                    'text-[12px]',
                    estTrie('stockMinimum') ? 'text-success' : 'text-text-secondary',
                  ]"
                >
                  {{ estTrie('stockMinimum') && triDirection === 'DESC' ? '⌃' : '⌄' }}
                </span>
              </button>
            </th>

            <!-- État -->
            <th class="px-4 py-3 text-[12px] font-bold text-text-primary">État</th>

            <!-- Actions -->
            <th class="px-4 py-3 text-right text-[12px] font-bold text-text-primary">Actions</th>
          </tr>
        </thead>

        <tbody>
          <!-- État vide -->
          <tr v-if="!produits.length">
            <td colspan="6" class="px-4 py-10 text-center text-[13px] text-text-secondary">
              Aucun produit trouvé.
            </td>
          </tr>

          <!-- Produits -->
          <tr
            v-for="produit in produits"
            :key="produit.id"
            class="border-b border-contours last:border-b-0"
          >
            <!-- Produit -->
            <td class="px-4 py-3">
              <div>
                <p class="text-[13px] font-bold text-text-primary">
                  {{ produit.nom }}
                </p>

                <p
                  v-if="produit.description"
                  class="mt-1 max-w-[280px] truncate text-[12px] text-text-secondary"
                >
                  {{ produit.description }}
                </p>
              </div>
            </td>

            <!-- Catégorie -->
            <td class="px-4 py-3 text-[13px] text-text-primary">
              {{ produit.nomCategorie || '—' }}
            </td>

            <!-- Stock actuel -->
            <td
              :class="[
                'px-4 py-3 text-[13px] font-bold',
                produit.etatStock === 'CRITIQUE'
                  ? 'text-danger'
                  : produit.etatStock === 'FAIBLE'
                    ? 'text-warning'
                    : 'text-success',
              ]"
            >
              {{ produit.quantiteActuelle }}
            </td>

            <!-- Stock minimum -->
            <td class="px-4 py-3 text-[13px] text-text-primary">
              {{ produit.stockMinimum }}
            </td>

            <!-- État -->
            <td class="px-4 py-3">
              <span
                :class="[
                  'inline-flex rounded-full px-2 py-1 text-[11px] font-bold',
                  afficherEtat(produit.etatStock).classe,
                ]"
              >
                {{ afficherEtat(produit.etatStock).label }}
              </span>
            </td>

            <!-- Actions -->
            <td class="px-4 py-3">
              <div class="flex justify-end gap-2">
                <!-- Modifier -->
                <button
                  type="button"
                  class="text-[15px] hover:opacity-60"
                  aria-label="Modifier le produit"
                  title="Modifier"
                  @click="emit('modifier', produit.id)"
                >
                  ✏️
                </button>

                <!-- Supprimer -->
                <button
                  type="button"
                  class="text-[15px] hover:opacity-60"
                  aria-label="Supprimer le produit"
                  title="Supprimer"
                  @click="emit('supprimer', produit.id)"
                >
                  🗑️
                </button>

                <!-- Mouvement -->
                <button
                  type="button"
                  class="text-[15px] hover:opacity-60"
                  aria-label="Ajouter un mouvement"
                  title="Nouveau mouvement"
                  @click="emit('mouvement', produit)"
                >
                  ➕
                </button>

                <!-- Historique -->
                <RouterLink
                  :to="`/historique?produitId=${produit.id}`"
                  class="text-[15px] hover:opacity-60"
                  aria-label="Voir l'historique du produit"
                  title="Historique"
                >
                  📜
                </RouterLink>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
