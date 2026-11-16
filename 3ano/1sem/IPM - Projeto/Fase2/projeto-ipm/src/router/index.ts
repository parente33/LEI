import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/HomeView.vue'
import Export from '@/views/ExportView.vue'
import FAQ from '@/views/FAQView.vue'
import About from '@/views/AboutView.vue'
import City from '@/views/CityView.vue'
import CityDownloads from '@/views/CityDownloadView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    },
    {
      path: '/export',
      name: 'Exporter',
      component: Export
    },
    {
      path: '/faq',
      name: 'FAQ',
      component: FAQ
    },
    {
      path: '/about',
      name: 'About',
      component: About
    },
    {
      path: '/city/:slug',
      name: 'City',
      component: City,
      props: true
    },
    {
      path: '/downloads/:slug',
      name: 'CityDownloads',
      component: CityDownloads,
      props: true
    }
  ]
})

export default router