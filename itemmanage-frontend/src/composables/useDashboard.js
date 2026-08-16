import { ref } from 'vue'
import dashboardService from '@/api/dashboardService'

export function useDashboard() {
  const dashboard = ref(null)
  const loading = ref(false)
  const error = ref(null)

  const fetchDashboard = async () => {
    loading.value = true
    error.value = null

    try {
      dashboard.value = await dashboardService.getDashboard()
    } catch (e) {
      error.value = e
    } finally {
      loading.value = false
    }
  }

  return {
    dashboard,
    loading,
    error,
    fetchDashboard,
  }
}
