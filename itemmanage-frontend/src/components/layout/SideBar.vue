<script setup>
const links = [
  { label: 'Dashboard', to: '/dashboard' },
  { label: 'Produits', to: '/produits' },
  { label: 'Catégories', to: '/categories' },
  { label: 'Mouvements', to: '/mouvements' },
  { label: 'Historique', to: '/historique' },
]
</script>

<template>
  <aside class="w-[200px] min-h-[calc(100vh-50px)] bg-sidebar">
    <nav class="flex flex-col">
      <RouterLink
        v-for="link in links"
        :key="link.to"
        :to="link.to"
        class="h-[50px] flex items-center px-5 text-[14px] text-text-primary"
      >
        {{ link.label }}
      </RouterLink>
    </nav>
  </aside>
</template>

<style scoped>
.router-link-active {
  background-color: var(--color-accent);
}
</style>
