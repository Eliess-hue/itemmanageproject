<script setup>
import { RouterView } from 'vue-router'
import {
  DialogRoot,
  DialogTrigger,
  DialogPortal,
  DialogOverlay,
  DialogContent,
  DialogTitle,
} from 'reka-ui'
</script>

<template>
  <div class="p-8">
    <DialogRoot>
      <DialogTrigger class="px-4 py-2 bg-green-700 text-white rounded">
        Ouvrir le dialog de test
      </DialogTrigger>
      <DialogPortal>
        <DialogOverlay class="fixed inset-0 bg-black/40" />
        <DialogContent
          class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-white rounded-lg p-6 w-96"
        >
          <DialogTitle class="font-semibold text-lg mb-2">Test Reka UI + Tailwind</DialogTitle>
          <p class="text-sm text-gray-600">
            Si tu vois cette modale centrée avec un fond assombri derrière, tout fonctionne.
          </p>
        </DialogContent>
      </DialogPortal>
    </DialogRoot>
    <RouterView />
  </div>
</template>
