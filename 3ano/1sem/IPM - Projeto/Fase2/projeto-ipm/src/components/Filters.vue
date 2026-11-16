<!-- Template dos filtros, usado na página de estatísticas -->
<template>
    <div class="filters-card">
        <h3 class="filters-title">Filtros</h3>

        <!-- Filtro por Tipo de Propriedade -->
        <div class="filter-group">
            <label class="filter-label">Tipo de Propriedade</label>
            <select v-model="filters.roomType" class="filter-select">
                <option value="">Todos os tipos</option>
                <option value="Entire home/apt">Casa/Apartamento Inteiro</option>
                <option value="Private room">Quarto Privado</option>
                <option value="Shared room">Quarto Partilhado</option>
                <option value="Hotel room">Quarto de Hotel</option>
            </select>
        </div>

        <!-- Filtro por Preço -->
        <div class="filter-group">
            <label class="filter-label">Preço Máximo</label>
            <input
                type="range"
                v-model="filters.maxPrice"
                :min="0"
                :max="700"
                class="filter-slider"
            >
            <div class="price-display">{{ filters.maxPrice }}</div>
        </div>

        <!-- Filtro por Localização -->
        <div class="filter-group">
            <label class="filter-label">Bairro</label>
            <select v-model="filters.neighbourhood" class="filter-select">
                <option value="">Todos os bairros</option>
                <option
                    v-for="neighbourhood in uniqueNeighbourhoods"
                    :key="neighbourhood"
                    :value="neighbourhood"
                >
                    {{ neighbourhood }}
                </option>
            </select>
        </div>

        <!-- Filtro por Licença -->
        <div class="filter-group">
            <label class="filter-label">Licença</label>
            <select v-model="filters.license" class="filter-select">
                <option value="">Todos os tipos</option>
                <option value="Licensed">Com licença</option>
                <option value="Not Licensed">Sem Licença</option>
                <option value="Exempt">Isento</option>
                <option value="Pending">Pendente</option>
            </select>
        </div>

        <!-- Botões de Ação -->
        <div class="filter-actions">
          <button class="apply-filters-button" @click="applyFilters">
            Aplicar Filtros
          </button>
          <button class="reset-filters-button" @click="resetFilters">
            Limpar Filtros
          </button>
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

// Emits
const emit = defineEmits(['filters-changed'])

// Estados padrão dos filtros (default state)
const filters = ref({
  roomType: '',
  maxPrice: 700,
  neighbourhood: '',
  license: ''
})

// Extrair bairros para o filtro das localidades
const uniqueNeighbourhoods = computed(() => {
  const neighbourhoods = [...new Set(props.listings.map(listing => listing.neighbourhood_group))]
  return neighbourhoods.filter(n => n).sort()
})

// Aplicar filtros
const applyFilters = () => {
  // Emite os filtros aplicados
  emit('filters-changed', { ...filters.value })
}

// Resetar filtros
const resetFilters = () => {
  filters.value = {
    roomType: '',
    maxPrice: 700,
    neighbourhood: '',
    license: ''
  }
  // Emite os filtros resetados
  emit('filters-changed', { ...filters.value })
}
</script>

<style scoped>
.filters-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filters-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 1.5rem;
  text-align: center;
}

.filter-group {
  margin-bottom: 1.5rem;
}

.filter-label {
  display: block;
  font-weight: 500;
  margin-bottom: 0.5rem;
  color: #555;
  font-size: 0.9rem;
}

.filter-select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  font-size: 0.9rem;
  transition: border-color 0.2s ease;
}

.filter-select:focus {
  outline: none;
  border-color: var(--color-staytistics-name);
}

.filter-slider {
  width: 100%;
  margin: 0.5rem 0;
  height: 6px;
  border-radius: 3px;
  background: #ddd;
  outline: none;
}

.filter-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: var(--color-staytistics-name);
  cursor: pointer;
}

.filter-slider::-moz-range-thumb {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: var(--color-staytistics-name);
  cursor: pointer;
  border: none;
}

.price-display {
  text-align: center;
  font-weight: 600;
  color: var(--color-staytistics-name);
  font-size: 1.1rem;
  margin-top: 0.5rem;
}

.filter-actions {
  display: flex;
  gap: 0.75rem;
  margin-top: 1rem;
}

.apply-filters-button {
  flex: 2;
  padding: 0.75rem;
  background: var(--color-staytistics-name);
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease;
  font-size: 0.9rem;
}

.apply-filters-button:hover {
  background: #0088cc;
}

.reset-filters-button {
  flex: 1;
  padding: 0.75rem;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease;
  font-size: 0.9rem;
}

.reset-filters-button:hover {
  background: #545b62;
}
</style>