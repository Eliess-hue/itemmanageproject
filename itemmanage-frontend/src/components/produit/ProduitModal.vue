<script setup>
import { ref, watch } from 'vue'
import { DialogRoot, DialogPortal, DialogOverlay, DialogContent, DialogTitle } from 'reka-ui'

import produitService from '@/api/produitService.js'

const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true,
  },
  produitId: {
    type: String,
    default: null,
  },
  categories: {
    type: Array,
    required: true,
  },
})

const emit = defineEmits(['close', 'success'])

const nom = ref('')
const description = ref('')
const categorieId = ref('')
const stockMinimum = ref(0)

const loading = ref(false)
const errorMessages = ref([])

const estModification = () => {
  return Boolean(props.produitId)
}

const reinitialiserFormulaire = () => {
  nom.value = ''
  description.value = ''
  categorieId.value = ''
  stockMinimum.value = 0
  errorMessages.value = []
}

const chargerProduit = async () => {
  reinitialiserFormulaire()

  if (!props.produitId) {
    return
  }

  loading.value = true

  try {
    const produit = await produitService.getById(props.produitId)

    nom.value = produit.nom
    description.value = produit.description ?? ''
    categorieId.value = produit.categorieId ?? ''
    stockMinimum.value = produit.stockMinimum
  } catch (error) {
    errorMessages.value = error.messages ?? ['Impossible de charger le produit.']
  } finally {
    loading.value = false
  }
}

watch(
  () => props.isOpen,
  (isOpen) => {
    if (!isOpen) {
      return
    }

    chargerProduit()
  },
)

const fermer = () => {
  emit('close')
}

const enregistrer = async () => {
  errorMessages.value = []

  if (!nom.value.trim()) {
    errorMessages.value = ['Le nom du produit est obligatoire.']
    return
  }

  if (!Number.isInteger(stockMinimum.value) || stockMinimum.value < 0) {
    errorMessages.value = ['Le stock minimum doit être un entier positif ou nul.']
    return
  }

  loading.value = true

  const produit = {
    nom: nom.value.trim(),
    description: description.value.trim(),
    categorieId: categorieId.value || null,
    stockMinimum: stockMinimum.value,
  }

  try {
    if (props.produitId) {
      await produitService.update(props.produitId, produit)
    } else {
      await produitService.create(produit)
    }

    emit('success')
  } catch (error) {
    errorMessages.value = error.messages ?? ['Une erreur est survenue lors de l’enregistrement.']
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <DialogRoot :open="isOpen" @update:open="(value) => !value && fermer()">
    <DialogPortal>
      <DialogOverlay class="fixed inset-0 bg-black/40" />

      <DialogContent
        class="fixed left-1/2 top-1/2 w-[540px] max-w-[calc(100vw-24px)] -translate-x-1/2 -translate-y-1/2 rounded-lg bg-white p-4 shadow-xl"
      >
        <!-- En-tête -->
        <div class="flex items-center justify-between">
          <DialogTitle class="text-[16px] font-bold leading-5">
            {{ estModification() ? 'Modifier le produit' : 'Nouveau produit' }}
          </DialogTitle>

          <button
            type="button"
            class="text-[13px] leading-none text-black hover:opacity-60"
            aria-label="Fermer"
            @click="fermer"
          >
            X
          </button>
        </div>

        <!-- Erreurs -->
        <div v-if="errorMessages.length" class="mt-3 rounded border border-danger bg-red-50 p-3">
          <ul class="list-disc pl-5 text-[12px] text-danger">
            <li v-for="message in errorMessages" :key="message">
              {{ message }}
            </li>
          </ul>
        </div>

        <!-- Chargement du produit à modifier -->
        <div
          v-if="loading && props.produitId && !nom"
          class="py-10 text-center text-[13px] text-text-secondary"
        >
          Chargement du produit...
        </div>

        <template v-else>
          <!-- Nom -->
          <section class="mt-6">
            <label for="produit-nom" class="mb-2 block text-[14px] font-bold"> Nom </label>

            <input
              id="produit-nom"
              v-model="nom"
              type="text"
              class="h-[36px] w-full rounded border border-contours px-3 text-[13px] outline-none focus:border-success"
              placeholder="Nom du produit"
            />
          </section>

          <!-- Description -->
          <section class="mt-5">
            <label for="produit-description" class="mb-2 block text-[14px] font-bold">
              Description
            </label>

            <textarea
              id="produit-description"
              v-model="description"
              rows="3"
              class="w-full resize-none rounded border border-contours px-3 py-2 text-[13px] outline-none focus:border-success"
              placeholder="Description du produit"
            />
          </section>

          <!-- Catégorie -->
          <section class="mt-5">
            <label for="produit-categorie" class="mb-2 block text-[14px] font-bold">
              Catégorie
            </label>

            <select
              id="produit-categorie"
              v-model="categorieId"
              class="h-[36px] w-full rounded border border-contours bg-white px-3 text-[13px] outline-none focus:border-success"
            >
              <option value="">Aucune catégorie</option>

              <option v-for="categorie in categories" :key="categorie.id" :value="categorie.id">
                {{ categorie.nom }}
              </option>
            </select>
          </section>

          <!-- Stock minimum -->
          <section class="mt-5">
            <label for="produit-stock-minimum" class="mb-2 block text-[14px] font-bold">
              Stock minimum
            </label>

            <div class="flex h-[36px]">
              <input
                id="produit-stock-minimum"
                v-model.number="stockMinimum"
                type="number"
                min="0"
                class="min-w-0 flex-1 rounded-l border border-contours px-3 text-[13px] outline-none focus:border-success"
              />

              <span
                class="flex items-center rounded-r border border-l-0 border-contours bg-background px-3 text-[12px]"
              >
                unités
              </span>
            </div>
          </section>

          <!-- Rappel -->
          <div class="mt-6 rounded bg-info p-3 text-[12px] leading-5">
            <strong class="mb-1 block"> ℹ️ Rappel </strong>

            <p>Le stock actuel ne peut pas être modifié ici.</p>

            <p class="mt-1">Pour modifier le stock, utilisez un mouvement.</p>
          </div>

          <!-- Actions -->
          <div class="mt-6 flex justify-end gap-2">
            <button
              type="button"
              class="h-[36px] rounded border border-contours bg-white px-4 text-[14px] hover:bg-gray-50"
              :disabled="loading"
              @click="fermer"
            >
              Annuler
            </button>

            <button
              type="button"
              class="h-[36px] rounded bg-success px-4 text-[14px] text-white hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
              :disabled="loading"
              @click="enregistrer"
            >
              {{ loading ? 'Enregistrement...' : 'Enregistrer' }}
            </button>
          </div>
        </template>
      </DialogContent>
    </DialogPortal>
  </DialogRoot>
</template>
