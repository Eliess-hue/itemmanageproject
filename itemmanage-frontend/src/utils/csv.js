const SEPARATEUR = ';'

/**
 * Échappe une valeur pour le format CSV.
 * Entoure de guillemets si la valeur contient le séparateur, un guillemet,
 * ou un retour à la ligne, et double les guillemets internes.
 */
function echapperValeur(valeur) {
  const texte = String(valeur ?? '')

  const doitEtreEntouree = texte.includes(SEPARATEUR) || texte.includes('"') || texte.includes('\n')

  if (!doitEtreEntouree) {
    return texte
  }

  return `"${texte.replace(/"/g, '""')}"`
}

/**
 * Construit le contenu CSV (BOM + en-têtes + lignes) à partir
 * d'un tableau d'en-têtes et d'un tableau de lignes (tableaux de valeurs).
 */
function construireCsv(entetes, lignes) {
  const BOM = '\uFEFF'

  const lignesTexte = [entetes, ...lignes].map((ligne) =>
    ligne.map(echapperValeur).join(SEPARATEUR),
  )

  return BOM + lignesTexte.join('\n')
}

/**
 * Déclenche le téléchargement d'un contenu texte comme fichier.
 */
function telechargerFichier(contenu, nomFichier) {
  const blob = new Blob([contenu], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)

  const lien = document.createElement('a')
  lien.href = url
  lien.download = nomFichier
  document.body.appendChild(lien)
  lien.click()
  document.body.removeChild(lien)

  URL.revokeObjectURL(url)
}

const LIBELLES_TYPE = {
  ENTREE: 'Entrée',
  SORTIE: 'Sortie',
  AJUSTEMENT: 'Ajustement',
}

const formateurDate = new Intl.DateTimeFormat('fr-FR')

/**
 * Exporte une liste de mouvements (MouvementHistoriqueResponse) en CSV
 * et déclenche le téléchargement du fichier.
 */
export function exporterMouvementsCsv(mouvements, nomFichier = 'historique-mouvements.csv') {
  const entetes = ['Date', 'Produit', 'Type', 'Quantité', 'Stock après']

  const lignes = mouvements.map((mouvement) => [
    formateurDate.format(new Date(mouvement.date)),
    mouvement.nomProduit,
    LIBELLES_TYPE[mouvement.type] ?? mouvement.type,
    mouvement.quantite,
    mouvement.stockApres,
  ])

  const contenu = construireCsv(entetes, lignes)
  telechargerFichier(contenu, nomFichier)
}
