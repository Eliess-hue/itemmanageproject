<script setup>
import {
  ComboboxRoot,
  ComboboxAnchor,
  ComboboxInput,
  ComboboxPortal,
  ComboboxContent,
  ComboboxViewport,
  ComboboxItem,
  ComboboxEmpty,
} from 'reka-ui'

defineProps({
  rechercheProduit: {
    type: String,
    required: true,
  },
  produitsSuggestions: {
    type: Array,
    required: true,
  },
  produitSelectionne: {
    type: Object,
    default: null,
  },
  type: {
    type: String,
    required: true,
  },
  dateDebut: {
    type: String,
    required: true,
  },
  dateFin: {
    type: String,
    required: true,
  },
})

const emit = defineEmits([
  'update:recherche-produit',
  'selectionner-produit',
  'vider-selection',
  'update:type',
  'update:date-debut',
  'update:date-fin',
])
</script>

<template>
  <section class="rounded-md border border-contours bg-white p-4">
    <div class="grid grid-cols-4 gap-4">
      <!-- Combobox Produit -->
      <div>
        <label class="mb-2 block text-[13px] font-bold text-text-primary"> Produit </label>

        <ComboboxRoot
          :model-value="produitSelectionne"
          :ignore-filter="true"
          @update:model-value="(produit) => emit('selectionner-produit', produit)"
        >
          <ComboboxAnchor
            class="flex h-[36px] items-center rounded border border-contours px-3 focus-within:border-success"
          >
            <ComboboxInput
              class="w-full text-[13px] outline-none"
              placeholder="Rechercher un produit..."
              :display-value="(produit) => produit?.nom ?? ''"
              :model-value="rechercheProduit"
              @update:model-value="(valeur) => emit('update:recherche-produit', valeur)"
            />

            <button
              v-if="produitSelectionne"
              type="button"
              class="ml-2 text-[13px] text-text-secondary hover:text-danger"
              aria-label="Retirer le filtre produit"
              @click.stop="emit('vider-selection')"
            >
              ✕
            </button>
          </ComboboxAnchor>

          <ComboboxPortal>
            <ComboboxContent
              position="popper"
              :side-offset="4"
              class="z-50 max-h-[240px] w-[--reka-combobox-trigger-width] overflow-y-auto rounded border border-contours bg-white shadow-lg"
            >
              <ComboboxViewport>
                <ComboboxEmpty class="px-3 py-2 text-[13px] text-text-secondary">
                  Aucun produit trouvé.
                </ComboboxEmpty>

                <ComboboxItem
                  v-for="produit in produitsSuggestions"
                  :key="produit.id"
                  :value="produit"
                  class="cursor-pointer px-3 py-2 text-[13px] text-text-primary hover:bg-background data-[highlighted]:bg-background"
                >
                  {{ produit.nom }}
                </ComboboxItem>
              </ComboboxViewport>
            </ComboboxContent>
          </ComboboxPortal>
        </ComboboxRoot>
      </div>

      <!-- Type -->
      <div>
        <label for="filtre-type" class="mb-2 block text-[13px] font-bold text-text-primary">
          Type
        </label>

        <select
          id="filtre-type"
          :value="type"
          class="h-[36px] w-full rounded border border-contours bg-white px-3 text-[13px] outline-none focus:border-success"
          @change="emit('update:type', $event.target.value)"
        >
          <option value="">Tous les types</option>
          <option value="ENTREE">Entrée</option>
          <option value="SORTIE">Sortie</option>
          <option value="AJUSTEMENT">Ajustement</option>
        </select>
      </div>

      <!-- Date début -->
      <div>
        <label for="filtre-date-debut" class="mb-2 block text-[13px] font-bold text-text-primary">
          Date début
        </label>

        <input
          id="filtre-date-debut"
          type="date"
          :value="dateDebut"
          class="h-[36px] w-full rounded border border-contours px-3 text-[13px] outline-none focus:border-success"
          @change="emit('update:date-debut', $event.target.value)"
        />
      </div>

      <!-- Date fin -->
      <div>
        <label for="filtre-date-fin" class="mb-2 block text-[13px] font-bold text-text-primary">
          Date fin
        </label>

        <input
          id="filtre-date-fin"
          type="date"
          :value="dateFin"
          class="h-[36px] w-full rounded border border-contours px-3 text-[13px] outline-none focus:border-success"
          @change="emit('update:date-fin', $event.target.value)"
        />
      </div>
    </div>
  </section>
</template>
