import { request } from './apiClient'

const produitService = {
  search(filters = {}) {
    const params = new URLSearchParams()

    if (filters.nom) {
      params.set('nom', filters.nom)
    }

    if (filters.categorieId) {
      params.set('categorieId', filters.categorieId)
    }

    if (filters.etatStock) {
      params.set('etatStock', filters.etatStock)
    }

    if (filters.triChamp) {
      params.set('triChamp', filters.triChamp)
    }

    if (filters.triDirection) {
      params.set('triDirection', filters.triDirection)
    }

    if (filters.page !== undefined && filters.page !== null) {
      params.set('page', filters.page)
    }

    if (filters.taille !== undefined && filters.taille !== null) {
      params.set('taille', filters.taille)
    }

    const queryString = params.toString()

    return request(queryString ? `/produits/search?${queryString}` : `/produits/search`)
  },

  getById(id) {
    return request(`/produits/${id}`)
  },

  create(produit) {
    return request('/produits', {
      method: 'POST',
      body: JSON.stringify(produit),
    })
  },

  update(id, produit) {
    return request(`/produits/${id}`, {
      method: 'PUT',
      body: JSON.stringify(produit),
    })
  },

  delete(id) {
    return request(`/produits/${id}`, {
      method: 'DELETE',
    })
  },
}

export default produitService
