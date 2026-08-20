import { ref, computed, watch } from 'vue'
import categorieService from '@/api/categorieService'

export function useCategories() {
  const categories = ref([])
  const loading = ref(false)
  const error = ref(null)

  const fetchCategories = async () => {
    loading.value = true
    error.value = null
    try {
      categories.value = await categorieService.getAll()
    } catch (e) {
      error.value = e
    } finally {
      loading.value = false
    }
  }

  // ----------------------------------------
  // Recherche + pagination (100% côté client)
  // ----------------------------------------

  const recherche = ref('')
  const page = ref(0)
  const taille = ref(10)

  const categoriesFiltrees = computed(() => {
    const terme = recherche.value.trim().toLowerCase()

    if (!terme) {
      return categories.value
    }

    return categories.value.filter((categorie) => categorie.nom.toLowerCase().includes(terme))
  })

  const totalPages = computed(() => {
    return Math.ceil(categoriesFiltrees.value.length / taille.value)
  })

  const categoriesPage = computed(() => {
    const debut = page.value * taille.value
    return categoriesFiltrees.value.slice(debut, debut + taille.value)
  })

  const pagination = computed(() => ({
    number: page.value,
    totalPages: totalPages.value,
    totalElements: categoriesFiltrees.value.length,
    size: taille.value,
  }))

  // Reset de la page quand la recherche change
  watch(recherche, () => {
    page.value = 0
  })

  const allerPage = (numero) => {
    if (numero < 0) {
      return
    }

    if (totalPages.value > 0 && numero >= totalPages.value) {
      return
    }

    page.value = numero
  }

  const changerTaille = (nouvelleTaille) => {
    taille.value = nouvelleTaille
    page.value = 0
  }

  return {
    categories,
    loading,
    error,
    fetchCategories,

    recherche,
    categoriesPage,
    pagination,
    allerPage,
    changerTaille,
  }
}
