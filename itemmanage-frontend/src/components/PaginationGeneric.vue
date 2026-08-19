<script setup>
import { computed } from 'vue'
import { genererPages } from '@/utils/pagination'

const props = defineProps({
  page: {
    type: Number,
    required: true,
  },
  totalPages: {
    type: Number,
    required: true,
  },
  totalElements: {
    type: Number,
    required: true,
  },
  taille: {
    type: Number,
    required: true,
  },
  tailles: {
    type: Array,
    required: true,
  },
  loading: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['changer-page', 'changer-taille'])

const pages = computed(() => {
  return genererPages(props.page, props.totalPages)
})

const estPremierePage = computed(() => {
  return props.page === 0
})

const estDernierePage = computed(() => {
  return props.totalPages === 0 || props.page === props.totalPages - 1
})

const premierElement = computed(() => {
  if (props.totalElements === 0) {
    return 0
  }

  return props.page * props.taille + 1
})

const dernierElement = computed(() => {
  if (props.totalElements === 0) {
    return 0
  }

  return Math.min(premierElement.value + props.taille - 1, props.totalElements)
})

const allerPage = (numero) => {
  emit('changer-page', numero)
}

const allerPremierePage = () => {
  allerPage(0)
}

const allerPagePrecedente = () => {
  allerPage(props.page - 1)
}

const allerPageSuivante = () => {
  allerPage(props.page + 1)
}

const allerDernierePage = () => {
  allerPage(props.totalPages - 1)
}

const changerTaille = (event) => {
  emit('changer-taille', Number(event.target.value))
}
</script>

<template>
  <section
    class="flex flex-col gap-4 border-t border-contours pt-4 sm:flex-row sm:items-center sm:justify-between"
  >
    <!-- Taille -->
    <div class="flex items-center gap-2">
      <label for="pagination-taille" class="text-[12px] text-text-primary"> Afficher </label>

      <select
        id="pagination-taille"
        :value="taille"
        class="h-[32px] rounded border border-contours bg-white px-2 text-[12px] outline-none focus:border-success disabled:opacity-50"
        :disabled="loading"
        @change="changerTaille"
      >
        <option v-for="option in tailles" :key="option" :value="option">
          {{ option }}
        </option>
      </select>

      <span class="text-[12px] text-text-primary"> produits par page </span>
    </div>

    <!-- Informations -->
    <p class="text-[12px] text-text-primary">
      <template v-if="totalElements > 0">
        Affichage de {{ premierElement }} à {{ dernierElement }} sur {{ totalElements }} produits
      </template>

      <template v-else> Aucun produit </template>
    </p>

    <!-- Navigation -->
    <div class="flex items-center gap-1">
      <!-- Première page -->
      <button
        type="button"
        class="flex h-[32px] min-w-[32px] items-center justify-center rounded border border-contours bg-white px-2 text-[13px] text-text-primary disabled:cursor-not-allowed disabled:opacity-40"
        :disabled="estPremierePage || loading"
        aria-label="Première page"
        title="Première page"
        @click="allerPremierePage"
      >
        <<
      </button>

      <!-- Page précédente -->
      <button
        type="button"
        class="flex h-[32px] min-w-[32px] items-center justify-center rounded border border-contours bg-white px-2 text-[13px] text-text-primary disabled:cursor-not-allowed disabled:opacity-40"
        :disabled="estPremierePage || loading"
        aria-label="Page précédente"
        title="Page précédente"
        @click="allerPagePrecedente"
      >
        <
      </button>

      <!-- Numéros de pages -->
      <template v-for="(item, index) in pages" :key="index">
        <!-- Ellipse -->
        <span
          v-if="item === 'ellipsis'"
          class="flex h-[32px] min-w-[32px] items-center justify-center px-1 text-[13px] text-text-secondary"
          aria-hidden="true"
        >
          ...
        </span>

        <!-- Numéro de page -->
        <button
          v-else
          type="button"
          :class="[
            'flex h-[32px] min-w-[32px] items-center justify-center rounded border px-2 text-[13px] disabled:cursor-not-allowed disabled:opacity-40',
            item === page
              ? 'border-success bg-success text-white'
              : 'border-contours bg-white text-text-primary hover:bg-background',
          ]"
          :disabled="loading"
          :aria-current="item === page ? 'page' : undefined"
          :aria-label="`Page ${item + 1}`"
          @click="allerPage(item)"
        >
          {{ item + 1 }}
        </button>
      </template>

      <!-- Page suivante -->
      <button
        type="button"
        class="flex h-[32px] min-w-[32px] items-center justify-center rounded border border-contours bg-white px-2 text-[13px] text-text-primary disabled:cursor-not-allowed disabled:opacity-40"
        :disabled="estDernierePage || loading"
        aria-label="Page suivante"
        title="Page suivante"
        @click="allerPageSuivante"
      >
        >
      </button>

      <!-- Dernière page -->
      <button
        type="button"
        class="flex h-[32px] min-w-[32px] items-center justify-center rounded border border-contours bg-white px-2 text-[13px] text-text-primary disabled:cursor-not-allowed disabled:opacity-40"
        :disabled="estDernierePage || loading"
        aria-label="Dernière page"
        title="Dernière page"
        @click="allerDernierePage"
      >
        >>
      </button>
    </div>
  </section>
</template>
