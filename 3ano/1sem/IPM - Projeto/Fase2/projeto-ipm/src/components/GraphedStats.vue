<!-- Usado para representar as estatísticas sobre gráficos -->
<template>
  <div class="graphs-component">
    <!-- Modal -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>Estatísticas Mensais - {{ cityName }}</h3>
          <button class="modal-close" @click="closeModal">×</button>
        </div>

        <div class="modal-body">
          <div class="graphed-stats-grid">
            <!-- Primeiro gráfico (de linhas): Evolução Preço Médio/Noite -->
            <div class="graphed-stat-item">
              <div class="chart-container">
                <h4 class="chart-title">Evolução Preço Médio/Noite</h4>
                <canvas ref="priceChartRef"></canvas>
                <div class="chart-legend">
                  <div v-for="roomType in roomTypes" :key="roomType" class="legend-item">
                    <span class="legend-color" :style="{ backgroundColor: getRoomTypeColor(roomType) }"></span>
                    <span class="legend-label">{{ roomType }}</span>
                  </div>
                </div>
              </div>
            </div>
            <!-- Segundo gráfico (de barras): Média de alojamentos ativos -->
            <div class="graphed-stat-item">
              <div class="chart-container">
                <h4 class="chart-title">Média de Alojamentos Ativos</h4>
                <canvas ref="activeListingsChartRef"></canvas>
              </div>
            </div>
            <!-- Terceiro gráfico (de barras): Nº Total de Noites reservadas -->
            <div class="graphed-stat-item">
              <div class="chart-container">
                <h4 class="chart-title">Nº Total de Noites Reservadas</h4>
                <canvas ref="nightsChartRef"></canvas>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import { Chart, registerables } from 'chart.js'

// Registrar componentes do Chart.js
Chart.register(...registerables)

// Props
const props = defineProps({
  cityName: {
    type: String,
    required: true
  },
  listings: {
    type: Array,
    required: true
  },
  bookings: {
    type: Array,
    default: () => []
  },
  showButton: {
    type: Boolean,
    default: true
  },
  modalVisible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modalVisible'])

// Refs para os canvases dos gráficos
const showModal = ref(props.modalVisible)
const priceChartRef = ref(null)
const activeListingsChartRef = ref(null)
const nightsChartRef = ref(null)

// Watch para sincronizar prop com estado interno
watch(() => props.modalVisible, (newValue) => {
  showModal.value = newValue
  if (newValue) {
    // Recriar gráficos quando o modal abrir
    setTimeout(() => {
      createPriceChart()
      createActiveListingsChart()
      createNightsChart()
    }, 100)
  }
})

const closeModal = () => {
  showModal.value = false
  emit('update:modalVisible', false)
}

// Instâncias dos gráficos
let priceChart = null
let activeListingsChart = null
let nightsChart = null

// Tipos de alojamento
const roomTypes = computed(() => {
  if (!props.listings || props.listings.length === 0) return []
  const types = [...new Set(props.listings.map(listing => listing.room_type))]
  return types.filter(type => type).sort()
})

// Cores para cada tipo de alojamento
const getRoomTypeColor = (type) => {
  const colors = {
    'Entire home/apt': '#4CAF50',
    'Private room': '#2196F3',
    'Shared room': '#FF9800',
    'Hotel room': '#9C27B0'
  }
  return colors[type] || '#607D8B'
}

const months = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez']

// IDs dos listings da cidade atual (para filtrar bookings)
const cityListingIds = computed(() => {
  if (!props.listings || props.listings.length === 0) return new Set()
  return new Set(props.listings.map(listing => listing.id))
})

// Bookings filtrados para a cidade atual
const cityBookings = computed(() => {
  if (!props.bookings || props.bookings.length === 0) return []

  // Filtrar apenas os bookings que pertencem aos listings da cidade atual
  return props.bookings.filter(booking => {
    return cityListingIds.value.has(booking.listing_id)
  })
})

// Calcular dados mensais
const monthlyStats = computed(() => {
  const stats = {
    priceByRoomType: {},
    activeListings: new Array(12).fill(0),
    bookedNights: new Array(12).fill(0)
  }

  // Inicializar estrutura para preços por tipo
  roomTypes.value.forEach(type => {
    stats.priceByRoomType[type] = new Array(12).fill(0)
  })

  if (!props.listings || props.listings.length === 0) return stats

  // Processar listings para calcular alojamentos ativos e preços médios
  props.listings.forEach(listing => {
    // Para questões de simplificação, consideramos que todos os listings estão disponíveis (ativos) o ano todo
    for (let month = 0; month < 12; month++) {
      stats.activeListings[month]++

      const roomType = listing.room_type
      if (roomType && stats.priceByRoomType[roomType]) {
        stats.priceByRoomType[roomType][month] += listing.price
      }
    }
  })

  // Calcular médias de preço
  roomTypes.value.forEach(type => {
    for (let month = 0; month < 12; month++) {
      const listingsOfType = props.listings.filter(l => l.room_type === type).length
      if (listingsOfType > 0) {
        stats.priceByRoomType[type][month] = Math.round(stats.priceByRoomType[type][month] / listingsOfType)
      }
    }
  })

  // Processar bookings filtrados para calcular noites reservadas
  if (cityBookings.value.length > 0)  {
    const executedBookings = cityBookings.value.filter(booking => booking.state === 'EXECUTED' || booking.state === 'RESERVED')

    executedBookings.forEach(booking => {
      const checkIn = new Date(booking.check_in)
      const checkOut = new Date(booking.check_out)

      // Simplificação: contar todas as noites do booking
      const nights = Math.ceil((checkOut - checkIn) / (1000 * 60 * 60 * 24))

      // Distribuir noites pelos meses
      const checkInMonth = checkIn.getMonth()
      stats.bookedNights[checkInMonth] += nights
    })
  }

  return stats
})

// Configuração do gráfico de preços (linhas)
const createPriceChart = () => {
  if (!priceChartRef.value || !monthlyStats.value) return

  const datasets = roomTypes.value.map(roomType => ({
    label: roomType,
    data: monthlyStats.value.priceByRoomType[roomType] || new Array(12).fill(0),
    borderColor: getRoomTypeColor(roomType),
    backgroundColor: getRoomTypeColor(roomType) + '20',
    borderWidth: 2,
    tension: 0.3,
    pointRadius: 6,
    pointHoverRadius: 8,
    fill: false
  }))

  const data = {
    labels: months,
    datasets: datasets
  }

  const config = {
    type: 'line',
    data: data,
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          mode: 'index',
          intersect: false,
          callbacks: {
            label: (context) => {
              return `${context.dataset.label}: €${context.parsed.y}`
            }
          }
        }
      },
      scales: {
        x: {
          title: {
            display: true,
            text: 'Meses'
          }
        },
        y: {
          title: {
            display: true,
            text: 'Preço Médio (€)'
          },
          beginAtZero: true,
          ticks: {
            callback: (value) => `€${value}`
          }
        }
      }
    }
  }

  if (priceChart) {
    priceChart.destroy()
  }

  priceChart = new Chart(priceChartRef.value, config)
}

// Configuração do gráfico de alojamentos ativos (barras)
const createActiveListingsChart = () => {
  if (!activeListingsChartRef.value || !monthlyStats.value) return

  const data = {
    labels: months,
    datasets: [{
      label: 'Alojamentos Ativos',
      data: monthlyStats.value.activeListings,
      backgroundColor: 'rgba(0, 132, 255, 0.7)',
      borderColor: 'rgba(0, 132, 255, 1)',
      borderWidth: 2,
      pointRadius: 6,
      pointHoverRadius: 8
    }]
  }

  const config = {
    type: 'bar',
    data: data,
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        }
      },
      scales: {
        x: {
          title: {
            display: true,
            text: 'Meses'
          }
        },
        y: {
          title: {
            display: true,
            text: 'Nº de Alojamentos'
          },
          beginAtZero: true
        }
      }
    }
  }

  if (activeListingsChart) {
    activeListingsChart.destroy()
  }

  activeListingsChart = new Chart(activeListingsChartRef.value, config)
}

// Configuração do gráfico de noites reservadas (barras)
const createNightsChart = () => {
  if (!nightsChartRef.value || !monthlyStats.value) return

  const data = {
    labels: months,
    datasets: [{
      label: 'Noites Reservadas',
      data: monthlyStats.value.bookedNights,
      backgroundColor: 'rgba(76, 175, 80, 0.7)',
      borderColor: 'rgba(76, 175, 80, 1)',
      borderWidth: 2,
      pointRadius: 6,
      pointHoverRadius: 8
    }]
  }

  const config = {
    type: 'bar',
    data: data,
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        }
      },
      scales: {
        x: {
          title: {
            display: true,
            text: 'Meses'
          }
        },
        y: {
          title: {
            display: true,
            text: 'Nº de Noites'
          },
          beginAtZero: true
        }
      }
    }
  }

  if (nightsChart) {
    nightsChart.destroy()
  }

  nightsChart = new Chart(nightsChartRef.value, config)
}

// Atualizar gráficos quando os dados mudarem
watch(monthlyStats, () => {
  if (showModal.value) {
    setTimeout(() => {
      createPriceChart()
      createActiveListingsChart()
      createNightsChart()
    }, 100)
  }
})

onMounted(() => {
  // Se o modal já estiver aberto quando o componente montar
  if (showModal.value) {
    setTimeout(() => {
      createPriceChart()
      createActiveListingsChart()
      createNightsChart()
    }, 100)
  }
})

// Limpar gráficos quando o componente for destruído
onUnmounted(() => {
  if (priceChart) priceChart.destroy()
  if (activeListingsChart) activeListingsChart.destroy()
  if (nightsChart) nightsChart.destroy()
})
</script>

<style scoped>
.graphs-component {
  width: 100%;
}

.modal-overlay {
  position: fixed;
  top: 4em;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background: white;
  border-radius: 16px;
  max-width: 1400px;
  width: 95%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 2rem;
  border-bottom: 1px solid #eee;
  background: var(--color-staytistics-name);
  color: white;
  border-radius: 12px 12px 0 0;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 700;
}

.modal-close {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: white;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s ease;
}

.modal-close:hover {
  background: rgba(255, 255, 255, 0.3);
}

.modal-body {
  padding: 2rem;
}

.graphed-stats-grid {
  display: flex;
  flex-direction: column;
  gap: 3rem;
  width: 100%;
}

.graphed-stat-item {
  text-align: center;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 12px;
  transition: transform 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.graphed-stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.chart-container {
  height: 500px;
  display: flex;
  flex-direction: column;
}

.chart-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--color-staytistics-name);
  margin-bottom: 1.5rem;
  text-align: center;
}

.chart-container canvas {
  flex: 1;
  width: 100% !important;
  height: 100% !important;
}

.chart-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  justify-content: center;
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e9ecef;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.25rem 0.5rem;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 6px;
  transition: all 0.2s ease;
}

.legend-item:hover {
  background: #e9ecef;
  transform: translateY(-2px);
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  display: inline-block;
}

.legend-label {
  font-size: 0.85rem;
  color: #666;
  font-weight: 500;
}
</style>