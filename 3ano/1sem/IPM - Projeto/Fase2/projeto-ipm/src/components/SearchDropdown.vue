<!-- Dropdown que aparece após se clicar na barra de pesquisa -->
<template>
  <div class="search-dropdown" v-if="isOpen && filteredContinents.length > 0">
    <div class="dropdown-content">
      <div
        v-for="continent in filteredContinents"
        :key="continent.id || continent.name"
        class="continent-section"
      >
        <div class="continent-name">{{ continent.continent }}</div>
        <div
          v-for="city in continent.cities"
          :key="city.slug || city.id"
          class="city-item"
          @click="selectCity(city)"
        >
          {{ city.name }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true
  },
  searchQuery: {
    type: String,
    default: ''
  },
  continentsData: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['city-selected'])

// Processa os continentes para garantir a estrutura correta
const processedContinents = computed(() => {
  return props.continentsData.map(continent => {
    // Se a API retornar {id, name, cities}, converte para a estrutura esperada
    if (continent.name && continent.cities) {
      return {
        continent: continent.name,
        name: continent.name,
        id: continent.id,
        cities: continent.cities
      }
    }
    // Se já estiver na estrutura {continent, cities}, ignora e retorna
    return continent
  })
})

const filteredContinents = computed(() => {
  if (!props.searchQuery.trim()) {
    return processedContinents.value
  }

  const query = props.searchQuery.toLowerCase().trim()

  return processedContinents.value
    .map(continent => ({
      ...continent,
      cities: (continent.cities || []).filter(city =>
        city.name.toLowerCase().includes(query)
      )
    }))
    .filter(continent => continent.cities.length > 0)
})

const selectCity = (city) => {
  emit('city-selected', city.name)

  router.push(`/city/${city.slug}`)
}
</script>

<style scoped>
.search-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 899;
  margin-top: 4px;
  max-height: 300px;
  overflow-y: auto;
}

.dropdown-content {
  padding: 0.5rem 0;
}

.continent-section {
  margin-bottom: 0.5rem;
}

.continent-name {
  padding: 0.5rem 1rem;
  font-weight: bold;
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  color: #495057;
  font-size: 0.9rem;
  cursor: default;
}

.city-item {
  padding: 0.75rem 1rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
  color: #212529;
}

.city-item:hover {
  background-color: #e9ecef;
}

.search-dropdown::-webkit-scrollbar {
  width: 6px;
}

.search-dropdown::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 0 8px 8px 0;
}

.search-dropdown::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.search-dropdown::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>