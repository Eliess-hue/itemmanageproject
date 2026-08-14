import { request } from './apiClient'

const mouvementService = {
  rechercher(filters = {}) {
    const params = new URLSearchParams()

    if (filters.produitId) {
      params.set('produitId', filters.produitId)
    }

    if (filters.type) {
      params.set('type', filters.type)
    }

    if (filters.dateDebut) {
      params.set('dateDebut', filters.dateDebut)
    }

    if (filters.dateFin) {
      params.set('dateFin', filters.dateFin)
    }

    if (filters.page !== undefined && filters.page !== null) {
      params.set('page', filters.page)
    }

    if (filters.taille !== undefined && filters.taille !== null) {
      params.set('taille', filters.taille)
    }

    const queryString = params.toString()

    return request(queryString ? `/mouvements?${queryString}` : '/mouvements')
  },

  enregistrer(mouvement) {
    return request('/mouvements', {
      method: 'POST',
      body: JSON.stringify(mouvement),
    })
  },
}

export default mouvementService
