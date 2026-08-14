import { request } from './apiClient'

const categorieService = {
  getAll() {
    return request('/categories')
  },

  create(categorie) {
    return request('/categories', {
      method: 'POST',
      body: JSON.stringify(categorie),
    })
  },

  rename(id, categorie) {
    return request(`/categories/${id}`, {
      method: 'PUT',
      body: JSON.stringify(categorie),
    })
  },
}

export default categorieService
