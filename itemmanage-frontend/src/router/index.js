import { createRouter, createWebHistory } from 'vue-router'

import DashboardView from '@/views/DashboardView.vue'
import ProduitsView from '@/views/ProduitsView.vue'
import CategoriesView from '@/views/CategoriesView.vue'
import HistoriqueView from '@/views/HistoriqueView.vue'

const router = createRouter({
  history: createWebHistory(),

  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: DashboardView,
    },
    {
      path: '/produits',
      name: 'produits',
      component: ProduitsView,
    },
    {
      path: '/categories',
      name: 'categories',
      component: CategoriesView,
    },
    {
      path: '/historique',
      name: 'historique',
      component: HistoriqueView,
    },
  ],
})

export default router
