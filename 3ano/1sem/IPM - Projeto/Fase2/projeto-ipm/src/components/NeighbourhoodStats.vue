<!-- Zona de estatísticas por bairro -->
<template>
  <div class="stats-card">
    <!-- Distribuição por Tipo de Alojamento -->
    <div class="room-type-section">
      <h3 class="stats-subtitle">Distribuição por Tipo de Alojamento</h3>
      <div class="room-type-stats">
        <div
          v-for="roomType in roomTypeDistribution"
          :key="roomType.type"
          class="room-type-item"
        >
          <div class="room-type-info">
            <span class="room-type-name">{{ roomType.type }}</span>
            <span class="room-type-count">{{ roomType.count }} ({{ roomType.percentage }}%)</span>
          </div>
          <div class="room-type-bar">
            <div
              class="room-type-fill"
              :style="{ width: roomType.percentage + '%' }"
            ></div>
          </div>
        </div>
      </div>
    </div>

    <div class="neighbourhood-header">
      <h3 class="stats-subtitle">Dados por Bairro</h3>
      <div class="neighbourhood-tabs">
        <button
          class="tab-button"
          :class="{ active: activeTab === 'prices' }"
          @click="activeTab = 'prices'"
        >
          Preço Médio/Noite
        </button>
        <button
          class="tab-button"
          :class="{ active: activeTab === 'counts' }"
          @click="activeTab = 'counts'"
        >
          Nº Alojamentos
        </button>
        <button
          class="tab-button"
          :class="{ active: activeTab === 'types' }"
          @click="activeTab = 'types'"
        >
          Tipo Alojamentos
        </button>
      </div>
    </div>

    <!-- Tab de Preços Médios -->
    <div v-if="activeTab === 'prices'" class="neighbourhood-stats">
      <div
        v-for="neighbourhood in neighbourhoodData"
        :key="neighbourhood.name + '-price'"
        class="neighbourhood-item"
      >
        <span class="neighbourhood-name">{{ neighbourhood.name }}</span>
        <span class="neighbourhood-price">€{{ neighbourhood.averagePrice }}</span>
      </div>
    </div>

    <!-- Tab de Número de Alojamentos -->
    <div v-if="activeTab === 'counts'" class="neighbourhood-stats">
      <div
        v-for="neighbourhood in neighbourhoodData"
        :key="neighbourhood.name + '-count'"
        class="neighbourhood-item"
      >
        <span class="neighbourhood-name">{{ neighbourhood.name }}</span>
        <span class="neighbourhood-count">{{ neighbourhood.listingCount }} alojamentos</span>
      </div>
    </div>

    <!-- Tab de Tipos de Alojamento -->
    <div v-if="activeTab === 'types'" class="neighbourhood-types">
      <div
        v-for="neighbourhood in neighbourhoodData"
        :key="neighbourhood.name + '-types'"
        class="neighbourhood-type-item"
      >
        <span class="neighbourhood-name">{{ neighbourhood.name }}</span>
        <div class="room-type-distribution">
          <div
            v-for="type in neighbourhood.roomTypes"
            :key="type.type"
            class="room-type-tag"
            :style="{ backgroundColor: getRoomTypeColor(type.type) }"
          >
            {{ type.type }} ({{ type.count }})
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// Props
const props = defineProps({
  listings: {
    type: Array,
    required: true
  }
})

// Estado para controlar a tab ativa
const activeTab = ref('prices') // 'prices', 'counts' ou 'types'

// Distribuição por tipo de alojamento
const roomTypeDistribution = computed(() => {
  if (!props.listings || props.listings.length === 0) {
    return []
  }

  const roomTypeCounts = {}
  const totalListings = props.listings.length

  // Contar ocorrências de cada tipo de alojamento
  props.listings.forEach(listing => {
    const roomType = listing.room_type
    roomTypeCounts[roomType] = (roomTypeCounts[roomType] || 0) + 1
  })

  // Converter para array e calcular percentagens
  return Object.entries(roomTypeCounts)
    .map(([type, count]) => ({
      type,
      count,
      percentage: Math.round((count / totalListings) * 100)
    }))
    .sort((a, b) => b.count - a.count) // Ordenar por ordem descendente
})

// Dados calculados
const neighbourhoodData = computed(() => calculateNeighbourhoodData(props.listings))

// Cores para tipos de alojamento
const getRoomTypeColor = (type) => {
  const colors = {
    'Entire home/apt': '#4CAF50',
    'Private room': '#2196F3',
    'Shared room': '#FF9800',
    'Hotel room': '#9C27B0'
  }
  return colors[type] || '#607D8B'
}

// Funções para calcular dados por cada bairro
const calculateNeighbourhoodData = (listings) => {
  if (!listings || listings.length === 0) return []

  const neighbourhoodData = {}

  listings.forEach(listing => {
    const neighbourhood = listing.neighbourhood_group
    if (!neighbourhoodData[neighbourhood]) {
      neighbourhoodData[neighbourhood] = {
        totalPrice: 0,
        listingCount: 0,
        roomTypes: {}
      }
    }

    neighbourhoodData[neighbourhood].totalPrice += listing.price
    neighbourhoodData[neighbourhood].listingCount += 1

    // Contar tipos de alojamento
    const roomType = listing.room_type
    neighbourhoodData[neighbourhood].roomTypes[roomType] =
      (neighbourhoodData[neighbourhood].roomTypes[roomType] || 0) + 1
  })

  const neighbourhoodStats = Object.entries(neighbourhoodData).map(([name, data]) => {
    // Converter roomTypes para array
    const roomTypesArray = Object.entries(data.roomTypes).map(([type, count]) => ({
      type,
      count,
      percentage: Math.round((count / data.listingCount) * 100)
    })).sort((a, b) => b.count - a.count)

    return {
      name,
      averagePrice: Math.round(data.totalPrice / data.listingCount),
      listingCount: data.listingCount,
      roomTypes: roomTypesArray
    }
  }).sort((a, b) => b.averagePrice - a.averagePrice)

  return neighbourhoodStats
}
</script>

<style scoped>
.stats-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 1.5rem;
}

.stats-subtitle {
  font-size: 1.2rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 1rem;
}

.room-type-section {
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #e9ecef;
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

.neighbourhood-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.neighbourhood-tabs {
  display: flex;
  gap: 0.25rem;
  background: #f8f9fa;
  padding: 0.25rem;
  border-radius: 8px;
}

.tab-button {
  padding: 0.5rem 0.75rem;
  border: none;
  background: transparent;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #666;
  white-space: nowrap;
}

.tab-button:hover {
  background: rgba(0, 162, 255, 0.1);
  color: var(--color-staytistics-name);
}

.tab-button.active {
  background: var(--color-staytistics-name);
  color: white;
}

.neighbourhood-stats {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  max-height: 300px;
  overflow-y: auto;
}

.neighbourhood-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.neighbourhood-name {
  font-weight: 500;
  flex: 1;
}

.neighbourhood-price {
  color: var(--color-staytistics-name);
  font-weight: 600;
  font-size: 0.9rem;
}

.neighbourhood-count {
  color: #28a745;
  font-weight: 600;
  font-size: 0.9rem;
}

.neighbourhood-types {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  max-height: 300px;
  overflow-y: auto;
}

.neighbourhood-type-item {
  padding: 0.75rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.room-type-distribution {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.room-type-tag {
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
  color: white;
  white-space: nowrap;
}
</style>