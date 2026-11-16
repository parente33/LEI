<template>
  <div class="city-layout">
    <!-- Sidebar -->
    <CitySidebar
      v-if="cityData && listings.length > 0"
      :city-name="cityData.name"
      :country-name="cityData.country"
      :city-slug="cityData.slug"
      :last-update="lastUpdate"
      :total-listings="filteredListings.length"
      :listings="listings"
      :bookings="bookings"
    />

    <main class="city-container" v-if="cityData && listings">
      <div class="city-header">
        <h1 class="city-title">{{ cityData.name }}</h1>
        <p class="city-subtitle">Estatísticas detalhadas do mercado de alojamento</p>
      </div>

      <!-- Mensagem de aviso, quando os filtros excluem todos os dados -->
      <div v-if="filteredListings.length === 0 && Object.keys(activeFilters).length > 0" class="filter-warning">
        <svg class="warning-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
          <line x1="12" y1="9" x2="12" y2="13"></line>
          <line x1="12" y1="17" x2="12.01" y2="17"></line>
        </svg>
        <div class="warning-content">
          <p class="warning-title">Parece que filtrou os dados demasiado bem!</p>
          <p class="warning-text">Nenhum resultado corresponde aos filtros aplicados. A mostrar os dados completos da cidade como fallback.</p>
        </div>
      </div>

      <div class="city-content">
        <!-- Coluna da Esquerda - Mapa, Filtros e Estatísticas -->
        <div class="left-column">
          <CityMap
            :city-data="cityData"
            :listings="filteredListings"
            :last-update="lastUpdate"
          />
        </div>

        <!-- Coluna do Meio - Estatísticas Gerais e por Bairro -->
        <!-- Para não acontecerem situações onde o user filtra por filtros que não oferecem resultados
             mostram-se os dados todos para a cidade como fallback-->
        <div class="middle-column">
          <GeneralStats
            v-if="cityStats"
            :stats="cityStats"
            :listings="filteredListings.length > 0 ? filteredListings : listings"
          />
          <NeighbourhoodStats
            :listings="filteredListings.length > 0 ? filteredListings : listings"
          />
        </div>

        <!-- Coluna da Direita - Filtros -->
        <div class="right-column">
          <Filters
            v-if="listings.length > 0"
            :listings="listings"
            @filters-changed="handleFiltersChanged"
            class="filters-section"
          />
        </div>
      </div>
      <ShareButton />
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import GeneralStats from '@/components/GeneralStats.vue'
import NeighbourhoodStats from '@/components/NeighbourhoodStats.vue'
import Filters from '@/components/Filters.vue'
import CityMap from '@/components/CityMap.vue'
import CitySidebar from '@/components/CitySidebar.vue'
import ShareButton from '@/components/ShareButton.vue'
import staytisticsAPI from '@/api/staytisticsAPI'

const route = useRoute()
const cityData = ref(null)
const allListings = ref([])
const bookings = ref([])
const filteredListings = ref([])
const loading = ref(true)
const error = ref(null)
const activeFilters = ref({})
const cityStats = ref(null)

// Data da última atualização
const lastUpdate = computed(() => {
  return new Date().toLocaleDateString('pt-PT')
})

const listings = computed(() => {
  return allListings.value || []
})

// Função para buscar dados da cidade
const fetchCityData = async () => {
  try {
    loading.value = true
    error.value = null

    // Resetar dados
    cityData.value = null
    allListings.value = []
    bookings.value = []
    filteredListings.value = []

    // Usar a API para buscar dados completos da cidade
    const cityDisplayData = await staytisticsAPI.getCityDisplayData(route.params.slug)

    cityData.value = cityDisplayData.city
    allListings.value = cityDisplayData.listings || []
    bookings.value = cityDisplayData.bookings || []
    filteredListings.value = cityDisplayData.listings || []

    cityStats.value = cityDisplayData.stats // para as GeneralStats

  } catch (err) {
    console.error('Erro ao carregar dados da cidade:', err)
    error.value = err.message || 'Erro desconhecido ao carregar dados'
  } finally {
    loading.value = false
  }
}

// Aplicar filtros
const handleFiltersChanged = (filters) => {
  activeFilters.value = filters
  applyFilters()
}

const applyFilters = () => {
  let filtered = [...listings.value]

  // Filtro por tipo de propriedade
  if (activeFilters.value.roomType) {
    filtered = filtered.filter(listing => listing.room_type === activeFilters.value.roomType)
  }

  // Filtro por preço
  if (activeFilters.value.maxPrice) {
    filtered = filtered.filter(listing => listing.price <= activeFilters.value.maxPrice)
  }

  // Filtro por bairro
  if (activeFilters.value.neighbourhood) {
    filtered = filtered.filter(listing => listing.neighbourhood_group === activeFilters.value.neighbourhood)
  }

  // Filtro por licença
  if (activeFilters.value.license === 'Licensed') {
    // Tem licença (é uma string não vazia, que não seja "Pending" e "Exempt")
    filtered = filtered.filter(listing =>
      listing.license &&
      listing.license.trim() !== '' &&
      listing.license !== 'Pending' &&
      listing.license !== 'Exempt'
    )
  } else if (activeFilters.value.license === 'Not Licensed') {
    // Não tem licença (string vazia ou null/undefined)
    filtered = filtered.filter(listing =>
      !listing.license ||
      listing.license.trim() === ''
    )
  } else if (activeFilters.value.license === 'Pending') {
    // Licença pendente
    filtered = filtered.filter(listing =>
      listing.license &&
      listing.license.trim() === 'Pending'
    )
  } else if (activeFilters.value.license === 'Exempt') {
    // Isento de licença
    filtered = filtered.filter(listing =>
      listing.license &&
      listing.license.trim() === 'Exempt'
    )
  }

  filteredListings.value = filtered
}

onMounted(() => {
  fetchCityData()
})
</script>

<style scoped>
.city-layout {
  display: flex;
  min-height: calc(100vh - 4em); /* Altura total menos navbar */
}

.city-container {
  flex: 1;
  max-width: calc(100% - 200px); /* Ajusta para a sidebar */
  padding: 2rem;
  transition: margin-left 0.3s ease;
}

/* Ajusta para sidebar no estado colapsado */
.city-layout .city-sidebar.collapsed ~ .city-container {
  margin-left: 60px;
  max-width: calc(100% - 60px);
}

.city-header {
  text-align: center;
  margin-bottom: 2rem;
}

.city-title {
  font-size: 3rem;
  font-weight: 700;
  color: var(--color-staytistics-name);
  margin-bottom: 0.5rem;
}

.city-subtitle {
  font-size: 1.2rem;
  color: #666;
}

.filters-section {
  margin-bottom: 2rem;
}

.city-content {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 2rem;
  margin-bottom: 2rem;
}

.left-column {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.middle-column {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.right-column {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.bottom-section {
  justify-content: center;
  width: 100%;
}

.room-type-stats {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.room-type-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.room-type-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
}

.room-type-name {
  font-weight: 500;
  color: #333;
}

.room-type-count {
  color: #666;
  font-size: 0.85rem;
}

.room-type-bar {
  width: 100%;
  height: 8px;
  background-color: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
}

.room-type-fill {
  height: 100%;
  background-color: var(--color-staytistics-name);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.loading-container, .error-container {
  text-align: center;
  padding: 4rem 2rem;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid var(--color-staytistics-name);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

.back-button {
  display: inline-block;
  margin-top: 1rem;
  padding: 0.75rem 1.5rem;
  background: var(--color-staytistics-name);
  color: white;
  text-decoration: none;
  border-radius: 6px;
  transition: background-color 0.2s ease;
}

.back-button:hover {
  background: #0088cc;
}

.filter-warning {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  padding: 1rem 1.5rem;
  margin-bottom: 2rem;
  background: linear-gradient(135deg, #fff3cd 0%, #ffe5a0 100%);
  border-left: 4px solid #ffc107;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(255, 193, 7, 0.15);
}

.warning-icon {
  flex-shrink: 0;
  color: #f39c12;
  margin-top: 0.2rem;
}

.warning-content {
  flex: 1;
}

.warning-title {
  font-size: 1rem;
  font-weight: 600;
  color: #856404;
  margin: 0 0 0.25rem 0;
}

.warning-text {
  font-size: 0.9rem;
  color: #856404;
  margin: 0;
  opacity: 0.9;
}
</style>