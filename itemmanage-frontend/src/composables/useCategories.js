import { ref } from 'vue'
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

  return {
    categories,
    loading,
    error,
    fetchCategories,
  }
}
