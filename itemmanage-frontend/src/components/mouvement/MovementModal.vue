<script setup>
import { ref, watch } from 'vue'
import { DialogRoot, DialogPortal, DialogOverlay, DialogContent, DialogTitle } from 'reka-ui'

import produitService from '@/api/produitService.js'
import mouvementService from '@/api/mouvementService.js'

const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true,
  },
  produitId: {
    type: String,
    default: null,
  },
})

const emit = defineEmits(['close', 'success'])

const produits = ref([])
const produitIdSelectionne = ref('')
const produitSelectionne = ref(null)
const type = ref('ENTREE')
const quantite = ref(0)
const ajustementPositif = ref(true)

const errorMessages = ref([])

watch(
  () => props.isOpen,
  async (isOpen) => {
    if (!isOpen) {
      return
    }

    errorMessages.value = []
    type.value = 'ENTREE'
    quantite.value = 0
    ajustementPositif.value = true

    try {
      if (props.produitId) {
        produitIdSelectionne.value = props.produitId

        produitSelectionne.value = await produitService.getById(props.produitId)

        return
      }

      const response = await produitService.search({
        page: 0,
        taille: 100,
      })

      produits.value = response.content
      produitIdSelectionne.value = ''
      produitSelectionne.value = null
    } catch (error) {
      errorMessages.value = error.messages ?? ['Impossible de charger les produits.']
    }
  },
)

function fermer() {
  emit('close')
}

async function enregistrer() {
  errorMessages.value = []

  const quantiteAbsolue = Number(quantite.value)

  if (!produitIdSelectionne.value) {
    errorMessages.value = ['Veuillez sélectionner un produit.']
    return
  }

  if (!Number.isInteger(quantiteAbsolue) || quantiteAbsolue <= 0) {
    errorMessages.value = ['La quantité doit être un entier positif.']
    return
  }

  let quantiteAvecSigne

  if (type.value === 'ENTREE') {
    quantiteAvecSigne = quantiteAbsolue
  } else if (type.value === 'SORTIE') {
    quantiteAvecSigne = -quantiteAbsolue
  } else {
    quantiteAvecSigne = ajustementPositif.value ? quantiteAbsolue : -quantiteAbsolue
  }

  try {
    await mouvementService.enregistrer({
      produitId: produitIdSelectionne.value,
      type: type.value,
      quantite: quantiteAvecSigne,
    })

    emit('success')
  } catch (error) {
    errorMessages.value = error.messages ?? ['Une erreur est survenue lors de l’enregistrement.']
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
          <DialogTitle class="text-[16px] font-bold leading-5"> Nouveau mouvement </DialogTitle>

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

        <!-- Produit -->
        <section class="mt-6">
          <h2 class="mb-2 text-[14px] font-bold">1. Produit</h2>

          <!-- Produit imposé -->
          <template v-if="produitId">
            <div
              class="flex h-[29px] items-center rounded border border-contours bg-background px-2 text-[13px]"
            >
              <span class="mr-1">📦</span>

              <span>
                {{ produitSelectionne?.nom }}
              </span>
            </div>

            <p class="mt-2 text-[12px] leading-4">
              Le produit est pré-sélectionné et ne peut pas être modifié.
            </p>
          </template>

          <!-- Choix du produit -->
          <select
            v-else
            v-model="produitIdSelectionne"
            class="h-[36px] w-full rounded border border-contours px-3 text-[13px] outline-none focus:border-success"
          >
            <option value="" disabled>Sélectionner un produit</option>

            <option v-for="produit in produits" :key="produit.id" :value="produit.id">
              {{ produit.nom }}
            </option>
          </select>
        </section>

        <!-- Type de mouvement -->
        <section class="mt-7">
          <h2 class="mb-2 text-[14px] font-bold">2. Type de mouvement</h2>

          <div class="grid grid-cols-3">
            <!-- Entrée -->
            <button
              type="button"
              :class="[
                'h-[56px] rounded-l border border-contours px-2 text-left transition-colors',
                type === 'ENTREE' ? 'border-success bg-accent' : 'bg-white hover:bg-gray-50',
              ]"
              @click="type = 'ENTREE'"
            >
              <div class="flex items-center">
                <span class="mr-1 text-[14px]">📥</span>

                <strong class="text-[14px]"> Entrée </strong>
              </div>

              <span class="block pl-[21px] text-[12px] leading-4"> Réapprovisionnement </span>
            </button>

            <!-- Sortie -->
            <button
              type="button"
              :class="[
                'h-[56px] border border-l-0 border-contours px-2 text-left transition-colors',
                type === 'SORTIE' ? 'border-success bg-accent' : 'bg-white hover:bg-gray-50',
              ]"
              @click="type = 'SORTIE'"
            >
              <div class="flex items-center">
                <span class="mr-1 text-[14px]">📤</span>

                <strong class="text-[14px]"> Sortie </strong>
              </div>

              <span class="block pl-[21px] text-[12px] leading-4"> Vente/Consommation </span>
            </button>

            <!-- Ajustement -->
            <button
              type="button"
              :class="[
                'h-[56px] rounded-r border border-l-0 border-contours px-2 text-left transition-colors',
                type === 'AJUSTEMENT' ? 'border-success bg-accent' : 'bg-white hover:bg-gray-50',
              ]"
              @click="type = 'AJUSTEMENT'"
            >
              <div class="flex items-center">
                <span class="mr-1 text-[14px]">✏️</span>

                <strong class="text-[14px]"> Ajustement </strong>
              </div>

              <span class="block pl-[21px] text-[12px] leading-4"> Correction d'inventaire </span>
            </button>
          </div>
        </section>

        <!-- Quantité -->
        <section class="mt-7">
          <h2 class="mb-2 text-[14px] font-bold">3. Quantité</h2>

          <div class="flex h-[37px]">
            <input
              v-model.number="quantite"
              type="number"
              min="1"
              class="min-w-0 flex-1 rounded-l border border-contours px-3 text-[14px] outline-none focus:border-success"
            />

            <span
              class="flex items-center rounded-r border border-l-0 border-contours bg-background px-3 text-[13px]"
            >
              unités
            </span>
          </div>

          <p class="mt-2 text-[12px] leading-4">Saisissez une quantité positive.</p>

          <p v-if="type === 'AJUSTEMENT'" class="mt-1 text-[12px] leading-4">
            Le signe sera appliqué selon le sens choisi.
          </p>

          <p v-else class="mt-1 text-[12px] leading-4">
            Le signe sera appliqué automatiquement selon le type de mouvement.
          </p>
        </section>

        <!-- Sens de l'ajustement -->
        <section v-if="type === 'AJUSTEMENT'" class="mt-3">
          <h2 class="mb-2 text-[14px] font-bold">Sens de l'ajustement</h2>

          <div class="grid grid-cols-2">
            <!-- Augmenter -->
            <button
              type="button"
              :class="[
                'h-[56px] rounded-l border border-contours px-3 text-center transition-colors',
                ajustementPositif
                  ? 'bg-accent text-success'
                  : 'bg-white text-black hover:bg-gray-50',
              ]"
              @click="ajustementPositif = true"
            >
              <div class="flex items-center justify-center">
                <strong class="text-[16px]"> ＋ Augmenter le stock </strong>
              </div>

              <span class="block text-[12px] leading-4"> (quantité positive) </span>
            </button>

            <!-- Diminuer -->
            <button
              type="button"
              :class="[
                'h-[56px] rounded-r border border-l-0 border-contours px-3 text-center transition-colors',
                !ajustementPositif
                  ? 'bg-danger-light text-danger'
                  : 'bg-white text-black hover:bg-gray-50',
              ]"
              @click="ajustementPositif = false"
            >
              <div class="flex items-center justify-center">
                <strong class="text-[16px]"> − Diminuer le stock </strong>
              </div>

              <span class="block text-[12px] leading-4"> (quantité négative) </span>
            </button>
          </div>
        </section>

        <!-- Actions -->
        <div class="mt-6 flex justify-end gap-2">
          <button
            type="button"
            class="h-[36px] rounded border border-contours bg-white px-4 text-[14px] hover:bg-gray-50"
            @click="fermer"
          >
            Annuler
          </button>

          <button
            type="button"
            class="h-[36px] rounded bg-success px-4 text-[14px] text-white hover:opacity-90"
            @click="enregistrer"
          >
            Enregistrer le mouvement
          </button>
        </div>

        <!-- Rappel -->
        <div class="mt-6 rounded bg-info p-3 text-[12px] leading-5">
          <strong class="mb-1 block"> ℹ️ Rappel </strong>

          <p>
            <strong>Entrée :</strong>
            la quantité sera ajoutée au stock (+)
          </p>

          <p>
            <strong>Sortie :</strong>
            la quantité sera soustraite du stock (-)
          </p>

          <p>
            <strong>Ajustement :</strong>
            vous pourrez choisir le sens (+ ou -) à l'étape 3
          </p>
        </div>
      </DialogContent>
    </DialogPortal>
  </DialogRoot>
</template>
