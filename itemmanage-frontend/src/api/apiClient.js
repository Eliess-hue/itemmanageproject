const API_BASE_URL = import.meta.env.VITE_API_URL

async function request(url, options = {}) {

  let response

  try {

    response = await fetch(`${API_BASE_URL}${url}`, {

      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
    })
  } catch (error) {

    // Erreur réseau : backend inaccessible, problème DNS, etc.
    const networkError = new Error('Impossible de contacter le serveur.')

    networkError.messages = [networkError.message]

    throw networkError
  }

  // Cas 204 No Content : rien à parser
  if (response.status === 204) {
    return null
  }

  // On essaie de lire le corps de la réponse
  let data = null

  try {

    data = await response.json()

  } catch {

    // Le serveur a répondu, mais avec un corps vide
    // ou qui n'est pas du JSON.
    data = null

  }

  // fetch ne considère PAS les 4xx/5xx comme des exceptions.
  // On doit donc les gérer nous-mêmes.
  if (!response.ok) {

    const error = new Error(

      data?.messages?.join(', ') || data?.message || `Erreur HTTP ${response.status}`,

    )

    // On conserve le tableau fourni par le backend
    // pour permettre au frontend d'afficher plusieurs erreurs.
    error.messages = Array.isArray(data?.messages) ? data.messages : [error.message]

    error.status = response.status

    throw error
  }

  return data
}

export { request }
