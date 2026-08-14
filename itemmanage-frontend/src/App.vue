<script setup>
import mouvementService from './api/mouvementService'
import dashboardService from './api/dashboardService'
import { RouterView } from 'vue-router'
import {
  DialogRoot,
  DialogTrigger,
  DialogPortal,
  DialogOverlay,
  DialogContent,
  DialogTitle,
} from 'reka-ui'
import { onMounted } from 'vue'

onMounted(async () => {

  // Test dashboardService
  try {
    const dashboard = await dashboardService.getDashboard()
    console.log('✅ dashboard :', dashboard)
  } catch (error) {
    console.error('❌ Erreur dashboard :', error.messages)
  }

  // Test mouvementService.rechercher() sans filtre
  try {
    const mouvements = await mouvementService.rechercher({ page: 0, taille: 10 })
    console.log('✅ rechercher() sans filtre :', mouvements)
  } catch (error) {
    console.error('❌ Erreur rechercher() :', error.messages)
  }

  // Test mouvementService.enregistrer() — nécessite un vrai produitId existant en base
  try {
    const nouveauMouvement = await mouvementService.enregistrer({
      produitId: 'REMPLACE_PAR_UN_VRAI_ID',
      type: 'ENTREE',
      quantite: 5,
    })
    console.log('✅ enregistrer() :', nouveauMouvement)
  } catch (error) {
    console.error('❌ Erreur enregistrer() :', error.messages)
  }
})
</script>

<template>
  <div class="p-8">
    <DialogRoot>
      <DialogTrigger class="px-4 py-2 bg-green-700 text-white rounded">
        Ouvrir le dialog de test
      </DialogTrigger>
      <DialogPortal>
        <DialogOverlay class="fixed inset-0 bg-black/40" />
        <DialogContent
          class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-white rounded-lg p-6 w-96"
        >
          <DialogTitle class="font-semibold text-lg mb-2">Test Reka UI + Tailwind</DialogTitle>
          <p class="text-sm text-gray-600">
            Si tu vois cette modale centrée avec un fond assombri derrière, tout fonctionne.
          </p>
        </DialogContent>
      </DialogPortal>
    </DialogRoot>
    <RouterView />
  </div>
</template>
