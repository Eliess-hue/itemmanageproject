<script setup>
import { ref, watch } from 'vue'
import { DialogRoot, DialogPortal, DialogOverlay, DialogContent, DialogTitle } from 'reka-ui'

import categorieService from '@/api/categorieService.js'

const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true,
  },
  categorieId: {
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

const loading = ref(false)
const errorMessages = ref([])

const estModification = () => Boolean(props.categorieId)

const reinitialiserFormulaire = () => {
  nom.value = ''
  description.value = ''
  errorMessages.value = []
}

const chargerCategorie = () => {
  reinitialiserFormulaire()

  if (!props.categorieId) {
    return
  }

  // La catégorie est déjà en mémoire (liste chargée par useCategories),
  // pas besoin d'un appel réseau supplémentaire pour l'édition.
  const categorie = props.categories.find((c) => c.id === props.categorieId)

  if (!categorie) {
    errorMessages.value = ['Catégorie introuvable.']
    return
  }

  nom.value = categorie.nom
  description.value = categorie.description ?? ''
}

watch(
  () => props.isOpen,
  (isOpen) => {
    if (!isOpen) {
      return
    }
    chargerCategorie()
  },
)

const fermer = () => {
  emit('close')
}

const enregistrer = async () => {
  errorMessages.value = []

  if (!nom.value.trim()) {
    errorMessages.value = ['Le nom de la catégorie est obligatoire.']
    return
  }

  loading.value = true

  const categorie = {
    nom: nom.value.trim(),
    description: description.value.trim(),
  }

  try {
    if (props.categorieId) {
      await categorieService.rename(props.categorieId, categorie)
    } else {
      await categorieService.create(categorie)
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
        class="fixed left-1/2 top-1/2 w-[480px] max-w-[calc(100vw-24px)] -translate-x-1/2 -translate-y-1/2 rounded-lg bg-white p-4 shadow-xl"
      >
        <div class="flex items-center justify-between">
          <DialogTitle class="text-[16px] font-bold leading-5">
            {{ estModification() ? 'Renommer la catégorie' : 'Nouvelle catégorie' }}
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

        <div
          v-if="errorMessages.length"
          class="mt-3 rounded border border-danger bg-danger-light p-3"
        >
          <ul class="list-disc pl-5 text-[12px] text-danger">
            <li v-for="message in errorMessages" :key="message">
              {{ message }}
            </li>
          </ul>
        </div>

        <section class="mt-6">
          <label for="categorie-nom" class="mb-2 block text-[14px] font-bold">Nom</label>
          <input
            id="categorie-nom"
            v-model="nom"
            type="text"
            class="h-[36px] w-full rounded border border-contours px-3 text-[13px] outline-none focus:border-success"
            placeholder="Nom de la catégorie"
          />
        </section>

        <section class="mt-5">
          <label for="categorie-description" class="mb-2 block text-[14px] font-bold">
            Description
          </label>
          <textarea
            id="categorie-description"
            v-model="description"
            rows="3"
            class="w-full resize-none rounded border border-contours px-3 py-2 text-[13px] outline-none focus:border-success"
            placeholder="Description de la catégorie"
          />
        </section>

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
      </DialogContent>
    </DialogPortal>
  </DialogRoot>
</template>
