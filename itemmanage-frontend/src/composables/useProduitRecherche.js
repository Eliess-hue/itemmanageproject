import { ref, watch } from 'vue'
import produitService from '@/api/produitService'

export function useProduitRecherche() {
  const rechercheProduit = ref('')
  const produitsSuggestions = ref([])
  const produitSelectionne = ref(null)

  const loading = ref(false)
  const error = ref(null)

  let timeoutRecherche = null

  const rechercherProduits = async () => {
    const terme = rechercheProduit.value.trim()

    if (!terme) {
      produitsSuggestions.value = []
      return
    }

    loading.value = true
    error.value = null

    try {
      const response = await produitService.search({ nom: terme, page: 0, taille: 10 })
      produitsSuggestions.value = response.content
    } catch (e) {
      error.value = e
    } finally {
      loading.value = false
    }
  }

  watch(rechercheProduit, () => {
    clearTimeout(timeoutRecherche)
    timeoutRecherche = setTimeout(() => {
      rechercherProduits()
    }, 300)
  })

  const selectionnerProduit = (produit) => {
    produitSelectionne.value = produit
  }

  const viderSelection = () => {
    produitSelectionne.value = null
    rechercheProduit.value = ''
    produitsSuggestions.value = []
  }

  // Utilisé pour préremplir le Combobox depuis ?produitId=X
  const chargerProduit = async (produitId) => {
    if (!produitId) {
      return
    }

    loading.value = true
    error.value = null

    try {
      const produit = await produitService.getById(produitId)
      produitSelectionne.value = produit
    } catch (e) {
      error.value = e
    } finally {
      loading.value = false
    }
  }

  return {
    rechercheProduit,
    produitsSuggestions,
    produitSelectionne,
    loading,
    error,
    selectionnerProduit,
    viderSelection,
    chargerProduit,
  }
}
