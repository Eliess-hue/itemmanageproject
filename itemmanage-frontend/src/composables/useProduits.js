import { reactive, ref, watch } from 'vue'
import produitService from '@/api/produitService'

export function useProduits() {
  const filtres = reactive({
    nom: '',
    categorieId: '',
    etatStock: '',
    triChamp: 'nom',
    triDirection: 'ASC',
    page: 0,
    taille: 10,
  })

  const produits = ref([])
  const pagination = ref(null)
  const loading = ref(false)
  const error = ref(null)

  let timeoutRecherche = null

  const fetchProduits = async () => {
    loading.value = true
    error.value = null

    try {
      const response = await produitService.search(filtres)

      produits.value = response.content
      pagination.value = response.page
    } catch (e) {
      error.value = e
    } finally {
      loading.value = false
    }
  }

  watch(
    () => filtres.nom,
    () => {
      clearTimeout(timeoutRecherche)

      timeoutRecherche = setTimeout(() => {
        filtres.page = 0
        fetchProduits()
      }, 300)
    },
  )

  watch(
    [
      () => filtres.categorieId,
      () => filtres.etatStock,
      () => filtres.triChamp,
      () => filtres.triDirection,
    ],
    () => {
      filtres.page = 0
      fetchProduits()
    },
  )

  const allerPage = (numero) => {
    if (numero < 0) {
      return
    }

    if (
      pagination.value &&
      pagination.value.totalPages > 0 &&
      numero >= pagination.value.totalPages
    ) {
      return
    }

    filtres.page = numero
    fetchProduits()
  }

  const changerTaille = (taille) => {
    filtres.taille = taille
    filtres.page = 0
    fetchProduits()
  }

  const initialiserFiltre = (etatStock) => {
    filtres.etatStock = etatStock
  }

  return {
    filtres,
    produits,
    pagination,
    loading,
    error,
    fetchProduits,
    allerPage,
    changerTaille,
    initialiserFiltre,
  }
}
