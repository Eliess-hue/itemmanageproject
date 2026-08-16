<script setup>
defineProps({
  titre: {
    type: String,
    required: true,
  },
  valeur: {
    type: [Number, String],
    required: true,
  },
  type: {
    type: String,
    default: 'neutral',
    validator: (value) => ['neutral', 'danger'].includes(value),
  },
})
</script>

<template>
  <article
    class="bg-white border-l-4 rounded-md px-4 py-3"
    :class="{
      'border-l-transparent': type === 'neutral',
      'border-l-danger': type === 'danger',
    }"
  >
    <h2 class="text-[14px] text-text-primary">
      {{ titre }}
    </h2>

    <p class="text-[24px] font-bold text-text-primary">
      {{ valeur }}
    </p>
  </article>
</template>
