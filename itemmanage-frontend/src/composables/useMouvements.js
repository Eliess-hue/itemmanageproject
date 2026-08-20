import { reactive, ref, watch } from 'vue'
import mouvementService from '@/api/mouvementService'

const convertirDateDebut = (date) => {
  if (!date) {
    return undefined
  }
  return new Date(`${date}T00:00:00`).toISOString()
}

const convertirDateFin = (date) => {
  if (!date) {
    return undefined
  }
  return new Date(`${date}T23:59:59.999`).toISOString()
}

export function useMouvements() {
  const filtres = reactive({
    produitId: '',
    type: '',
    dateDebut: '',
    dateFin: '',
    page: 0,
    taille: 10,
  })

  const mouvements = ref([])
  const pagination = ref(null)
  const loading = ref(false)
  const error = ref(null)

  const fetchMouvements = async () => {
    loading.value = true
    error.value = null

    try {
      const response = await mouvementService.rechercher({
        produitId: filtres.produitId,
        type: filtres.type,
        dateDebut: convertirDateDebut(filtres.dateDebut),
        dateFin: convertirDateFin(filtres.dateFin),
        page: filtres.page,
        taille: filtres.taille,
      })

      mouvements.value = response.content
      pagination.value = response.page
    } catch (e) {
      error.value = e
    } finally {
      loading.value = false
    }
  }

  // Chaque changement de filtre (produit, type, dates) reset la page
  // et redéclenche immédiatement la recherche. Pas de debounce ici :
  // contrairement à une recherche texte, ce sont des sélections discrètes
  // (Combobox, select, input date), pas une frappe continue.
  watch(
    [() => filtres.produitId, () => filtres.type, () => filtres.dateDebut, () => filtres.dateFin],
    () => {
      filtres.page = 0
      fetchMouvements()
    },
  )

  const TAILLE_EXPORT = 10000

  // Récupère tous les mouvements correspondant aux filtres actifs,
  // sans toucher à `mouvements`/`pagination` (l'état affiché à l'écran
  // reste intact). Utilisé uniquement pour l'export CSV.
  const fetchTousLesMouvements = async () => {
    const response = await mouvementService.rechercher({
      produitId: filtres.produitId,
      type: filtres.type,
      dateDebut: convertirDateDebut(filtres.dateDebut),
      dateFin: convertirDateFin(filtres.dateFin),
      page: 0,
      taille: TAILLE_EXPORT,
    })

    return response.content
  }

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
    fetchMouvements()
  }

  const changerTaille = (taille) => {
    filtres.taille = taille
    filtres.page = 0
    fetchMouvements()
  }

  return {
    filtres,
    mouvements,
    pagination,
    loading,
    error,
    fetchMouvements,
    fetchTousLesMouvements,
    allerPage,
    changerTaille,
  }
}
