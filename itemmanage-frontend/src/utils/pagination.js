export function genererPages(page, totalPages) {
  if (totalPages === 0) {
    return []
  }

  const derniere = totalPages - 1

  const candidats = new Set([0, derniere, page - 1, page, page + 1])

  const pagesAffichees = [...candidats].filter((p) => p >= 0 && p <= derniere).sort((a, b) => a - b)

  const resultat = [pagesAffichees[0]]

  for (let i = 0; i < pagesAffichees.length - 1; i++) {
    const courante = pagesAffichees[i]
    const suivante = pagesAffichees[i + 1]
    const ecart = suivante - courante

    if (ecart === 2) {
      resultat.push(courante + 1)
    } else if (ecart >= 3) {
      resultat.push('ellipsis')
    }

    resultat.push(suivante)
  }

  return resultat
}
