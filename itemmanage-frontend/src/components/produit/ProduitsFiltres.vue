<script setup>
defineProps({
  nom: {
    type: String,
    required: true,
  },
  categorieId: {
    type: String,
    required: true,
  },
  etatStock: {
    type: String,
    required: true,
  },
  categories: {
    type: Array,
    required: true,
  },
})

const emit = defineEmits(['update:nom', 'update:categorieId', 'update:etatStock'])
</script>

<template>
  <section class="rounded-md border border-contours bg-white p-4">
    <div class="grid grid-cols-3 gap-4">
      <!-- Recherche -->
      <div>
        <label for="recherche-produit" class="mb-2 block text-[13px] font-bold text-text-primary">
          Rechercher
        </label>

        <input
          id="recherche-produit"
          type="search"
          :value="nom"
          placeholder="Rechercher un produit..."
          class="h-[36px] w-full rounded border border-contours px-3 text-[13px] outline-none focus:border-success"
          @input="emit('update:nom', $event.target.value)"
        />
      </div>

      <!-- Catégorie -->
      <div>
        <label for="filtre-categorie" class="mb-2 block text-[13px] font-bold text-text-primary">
          Catégorie
        </label>

        <select
          id="filtre-categorie"
          :value="categorieId"
          class="h-[36px] w-full rounded border border-contours bg-white px-3 text-[13px] outline-none focus:border-success"
          @change="emit('update:categorieId', $event.target.value)"
        >
          <option value="">Toutes les catégories</option>

          <option v-for="categorie in categories" :key="categorie.id" :value="categorie.id">
            {{ categorie.nom }}
          </option>
        </select>
      </div>

      <!-- État -->
      <div>
        <label for="filtre-etat-stock" class="mb-2 block text-[13px] font-bold text-text-primary">
          État du stock
        </label>

        <select
          id="filtre-etat-stock"
          :value="etatStock"
          class="h-[36px] w-full rounded border border-contours bg-white px-3 text-[13px] outline-none focus:border-success"
          @change="emit('update:etatStock', $event.target.value)"
        >
          <option value="">Tous les états</option>
          <option value="CRITIQUE">Critique</option>
          <option value="FAIBLE">Faible</option>
          <option value="OK">OK</option>
        </select>
      </div>
    </div>
  </section>
</template>
