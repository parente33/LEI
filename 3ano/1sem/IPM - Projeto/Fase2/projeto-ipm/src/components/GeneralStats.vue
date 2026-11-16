<!-- Estátisticas Gerais, para a página de estatísticas -->
<template>
  <div class="stats-card">
    <h2 class="stats-title">Estatísticas Gerais</h2>

    <div class="stats-grid">
      <div class="stat-item">
        <div class="stat-value">{{ stats.totalListings || 0 }}</div>
        <div class="stat-label">Total de Anúncios</div>
      </div>

      <div class="stat-item">
        <div class="stat-value">€{{ stats.avgPrice || 0}}</div>
        <div class="stat-label">Preço Médio/Noite</div>
      </div>

      <div class="stat-item">
        <div class="stat-value">{{ stats.occupancyRate || 0 }}%</div>
        <div class="stat-label">Taxa de Ocupação</div>
      </div>

      <div class="stat-item">
        <div class="stat-value">{{ activeHosts }}</div>
        <div class="stat-label">Anfitriões Ativos</div>
      </div>

      <div class="stat-item">
        <div class="stat-value">{{ stats.avgStayDuration || 0 }}</div>
        <div class="stat-label">Média Dias Alugados</div>
      </div>

      <div class="stat-item">
        <div class="stat-value">{{ stats.totalGuests || 0 }}</div>
        <div class="stat-label">Nº de Hóspedes Totais</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

// Props
const props = defineProps({
  stats: {
    type: Object,
    required: true,
    default: () => ({
      totalListings: 0,
      avgPrice: 0,
      occupancyRate: 0,
      avgStayDuration: 0,
      totalGuests: 0
    })
  },
  listings: {  // Ainda são precisos os listings para calcular os hosts únicos
    type: Array,
    required: true
  }
})

const activeHosts = computed(() => {
  if (!props.listings || props.listings.length === 0) return 0
  return new Set(props.listings.map(listing => listing.host_name)).size
})
</script>

<style scoped>
.stats-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 1.5rem;
  width: 500px;
}

.stats-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--color-staytistics-name);
  margin-bottom: 1.5rem;
  text-align: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
}

.stat-item {
  text-align: center;
  padding: 1.5rem 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  transition: transform 0.2s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--color-staytistics-name);
  margin-bottom: 0.5rem;
  line-height: 1;
}

.stat-label {
  font-size: 0.85rem;
  color: #666;
  font-weight: 500;
  line-height: 1.3;
}
</style>