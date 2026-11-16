<!-- Para ser usado nas páginas de estatísticas -->
<template>
    <div class="anomalies-component">
        <!-- Modal de anomalias -->
        <div v-if="showModal" class="modal-overlay" @click="closeModal">
          <div class="modal-content" @click.stop>
            <div class="modal-header">
              <h3>Anomalias Detetadas</h3>
              <button class="modal-close" @click="closeModal">×</button>
            </div>
            <div class="modal-body">
              <div v-if="anomalies.length > 0">
                <div class="anomaly-item" v-for="anomaly in anomalies" :key="anomaly.id">
                  <h4>{{ anomaly.title }}</h4>
                  <p>{{ anomaly.description }}</p>
                  <span class="anomaly-severity" :class="anomaly.severity">
                    {{ anomaly.severity }}
                  </span>
                </div>
              </div>
              <div v-else class="no-anomalies">
                <p>✅ Nenhuma anomalia foi detetada nos dados atuais.</p>
                <p class="subtext">Todos os parâmetros estão dentro dos limites normais.</p>
              </div>
            </div>
          </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { anomaliesStore } from '@/stores/anomaliesStore'

// Props
const props = defineProps({
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

const showModal = ref(props.modalVisible)

// Watch para sincronizar prop com estado interno
watch(() => props.modalVisible, (newValue) => {
  showModal.value = newValue
})

// Detetar anomalias (a partir das informações dos bookings)
const detectAnomalies = (listings, bookings) => {
  const anomalies = []

  // Preços anormalmente altos
  const highPriceListings = listings.filter(l => l.price >= 400)
    anomalies.push({
      id: 1,
      title: 'Preços Excessivamente Altos',
      description: `${highPriceListings.length} propriedades com preços superiores a €400/noite`,
      severity: 'urgent'
    })

  // Listings sem licença
  const noLicenseListings = listings.filter(l => !l.license)
  if (noLicenseListings.length > 0) {
    anomalies.push({
      id: 2,
      title: 'Listings Sem Licença',
      description: `${noLicenseListings.length} propriedades sem licença registada`,
      severity: 'high'
    })
  }

  // Alta taxa de cancelamento
  if (bookings && bookings.length > 0) {
    const totalBookings = bookings.length
    const canceledBookings = bookings.filter(b => b.state === 'CANCELLED').length
    const cancellationRate = (canceledBookings / totalBookings) * 100

    if (cancellationRate >= 20) {
      anomalies.push({
        id: 3,
        title: 'Alta Taxa de Cancelamento',
        description: `${Math.round(cancellationRate)}% das reservas foram canceladas`,
        severity: 'medium'
      })
    }
  }

  // Concentração de muitos listings em poucos hosts
  const hostCounts = {}
  listings.forEach(l => {
    hostCounts[l.host_name] = (hostCounts[l.host_name] || 0) + 1
  })
  const topHost = Object.entries(hostCounts).sort((a, b) => b[1] - a[1])[0]
  if (topHost && topHost[1] >= 5) {
    anomalies.push({
      id: 4,
      title: 'Host com Múltiplas Propriedades',
      description: `${topHost[0]} gere ${topHost[1]} propriedades`,
      severity: 'low'
    })
  }

  return anomalies
}

const anomalies = computed(() => detectAnomalies(props.listings, props.bookings))

// Atualizar store
watch(anomalies, (newAnomalies) => {
  anomaliesStore.count = newAnomalies.length
  anomaliesStore.anomalies = newAnomalies
}, { immediate: true })

const closeModal = () => {
  showModal.value = false
  emit('update:modalVisible', false)
}
</script>

<style scoped>
.anomalies-component {
  width: 100%;
}

.anomalies-section {
  text-align: center;
  margin: 1rem 0;
}

.anomalies-button {
  background-color: rgb(202, 21, 21);
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
  font-size: 1.1rem;
}

.anomalies-button:hover {
  transform: translateY(-2px);
  background-color: rgb(128, 13, 13);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
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
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  padding: 0;
  max-width: 1000px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #eee;
  background: var(--color-staytistics-name);
  color: white;
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
  padding: 1.5rem;
}

.anomaly-item {
  padding: 1rem;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 1rem;
}

.anomaly-item:hover {
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.anomaly-item h4 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.anomaly-item p {
  margin: 0 0 0.5rem 0;
  color: #666;
}

.anomaly-severity {
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
  color: white;
}

.anomaly-severity.urgent {
  background: #5f0c14;
}

.anomaly-severity.high {
  background: #dc3545;
}

.anomaly-severity.medium {
  background: #ffc107;
  color: #000;
}

.anomaly-severity.low {
  background: #28a745;
}

.no-anomalies {
  text-align: center;
  padding: 2rem 1rem;
}

.no-anomalies p {
  font-size: 1.1rem;
  color: #28a745;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.no-anomalies .subtext {
  font-size: 0.9rem;
  color: #666;
  font-weight: normal;
}
</style>