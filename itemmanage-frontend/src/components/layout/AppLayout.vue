<script setup>
import TopBar from './TopBar.vue'
import SideBar from './SideBar.vue'
</script>

<template>
  <div class="min-h-screen">
    <TopBar />

    <div class="flex">
      <SideBar />

      <main class="flex-1 min-h-[calc(100vh-50px)] bg-background">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<style scoped></style>
