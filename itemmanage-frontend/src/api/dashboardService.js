import { request } from './apiClient'

const dashboardService = {
  getDashboard() {
    return request('/dashboard')
  },
}

export default dashboardService
