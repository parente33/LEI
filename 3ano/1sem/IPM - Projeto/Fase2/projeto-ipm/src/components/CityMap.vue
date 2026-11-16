<!-- Usado na página de estatísticas para cada cidade -->
<template>
  <div class="map-column">
    <div class="map-container">
      <div id="map" ref="mapContainer"></div>
    </div>

    <!-- Informações do Mapa -->
    <div class="map-info">
      <p>Última atualização: {{ lastUpdate }}</p>
      <p class="map-attribution">Mapa por © <a href="https://www.openstreetmap.org/copyright" target="_blank">OpenStreetMap</a></p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'

// Leaflet para OpenStreetMap
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

// Props
const props = defineProps({
  cityData: {
    type: Object,
    required: true
  },
  listings: {
    type: Array,
    required: true
  },
  lastUpdate: {
    type: String,
    default: ''
  }
})

// Refs
const mapContainer = ref(null)
const map = ref(null)

// Coordenadas padrão para fallback
const defaultCoords = { lat: 0, lng: 0, zoom: 2 }

// Inicializar mapa
const initMap = () => {
  if (!mapContainer.value) return

  // Usar coordenadas da cidade ou fallback
  const coords = props.cityData?.coords || defaultCoords

  map.value = L.map(mapContainer.value).setView([coords.lat, coords.lng], coords.zoom)

  // Adicionar tile layer do OpenStreetMap
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    maxZoom: 18
  }).addTo(map.value)

  // Corrigir ícones do Leaflet
  delete L.Icon.Default.prototype._getIconUrl
  L.Icon.Default.mergeOptions({
    iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
    iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
  })

  // Adicionar markers para cada listing
  addMarkersToMap()
}

// Adicionar markers ao mapa
const addMarkersToMap = () => {
  if (!map.value || !props.listings.length) return

  // Limpar markers existentes
  map.value.eachLayer((layer) => {
    if (layer instanceof L.Marker) {
      map.value.removeLayer(layer)
    }
  })

  // Adicionar novos markers
  props.listings.forEach(listing => {
    const marker = L.marker([listing.latitude, listing.longitude]) // ??
      .addTo(map.value)
      .bindPopup(`
        <div class="popup-content">
          <h4>${listing.name}</h4>
          <p><strong>Preço:</strong> €${listing.price}/noite</p>
          <p><strong>Tipo:</strong> ${listing.room_type}</p>
          <p><strong>Bairro:</strong> ${listing.neighbourhood_group}</p>
          <p><strong>Anfitrião:</strong> ${listing.host_name}</p>
          ${listing.last_review ? `<p><strong>Última review:</strong> ${new Date(listing.last_review).toLocaleDateString('pt-PT')}</p>` : ''}
        </div>
      `)
  })
}

// Atualizar mapa quando os listings mudarem
watch(() => props.listings, () => {
  if (map.value) {
    addMarkersToMap()
  }
}, { deep: true })

// Atualizar vista quando a cidade mudar
watch(() => props.cityData, () => {
  if (map.value && props.cityData?.coords) {
    const coords = props.cityData.coords
    map.value.setView([coords.lat, coords.lng], coords.zoom)
    addMarkersToMap()
  }
})

// Inicializar quando o componente montar
onMounted(async () => {
  await nextTick()
  initMap()
})

// Limpar mapa quando o componente for destruído
onUnmounted(() => {
  if (map.value) {
    map.value.remove()
  }
})
</script>

<style scoped>
.map-column {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.map-container {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  height: 1000px;
}

#map {
  width: 100%;
  height: 100%;
}

.map-info {
  text-align: center;
  color: #666;
  font-size: 0.9rem;
}

.map-attribution {
  font-size: 0.8rem;
  margin-top: 0.5rem;
}

.map-attribution a {
  color: var(--color-staytistics-name);
  text-decoration: none;
}

.map-attribution a:hover {
  text-decoration: underline;
}

:deep(.leaflet-popup-content) {
  margin: 8px 12px;
}

:deep(.popup-content h4) {
  margin: 0 0 8px 0;
  color: var(--color-staytistics-name);
  font-size: 1.1rem;
}

:deep(.popup-content p) {
  margin: 4px 0;
  font-size: 0.9rem;
}

:deep(.leaflet-popup-content-wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}
</style>