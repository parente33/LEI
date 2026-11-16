<!-- Barra de pesquisa de cidades, na página inicial -->
<template>
  <div class="search-container">
    <div class="search-bar" @click="openDropdown">
      <input
        type="text"
        :placeholder="placeholder"
        v-model="searchQuery"
        @input="handleInput"
        @focus="openDropdown"
        @blur="handleBlur"
        class="search-input"
        ref="inputRef"
      />
      <img
        :src="searchIcon"
        alt="Search"
        class="search-icon"
      />
    </div>

    <SearchDropdown
      :is-open="isDropdownOpen"
      :search-query="searchQuery"
      :continents-data="continentsData"
      @city-selected="handleCitySelected"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import SearchDropdown from './SearchDropdown.vue'
import staytisticsAPI from '@/api/staytisticsAPI'

const props = defineProps({
  placeholder: {
    type: String,
    default: 'Pesquise uma cidade...'
  },
  icon: {
    type: String,
    default: '/search.png'
  }
})

const searchQuery = ref('')
const searchIcon = props.icon
const isDropdownOpen = ref(false)
const inputRef = ref(null)
const continentsData = ref([])

const emit = defineEmits(['search', 'city-selected'])

onMounted(async () => {
  try {
    const continents = await staytisticsAPI.getContinents()
    continentsData.value = continents
  } catch (error) {
    console.error('Erro ao carregar dados das cidades:', error)
  }
})

const handleInput = () => {
  emit('search', searchQuery.value)
  isDropdownOpen.value = true
}

const openDropdown = () => {
  isDropdownOpen.value = true
}

const handleBlur = () => {
  setTimeout(() => {
    isDropdownOpen.value = false
  }, 1000)
}

const handleCitySelected = (cityName) => {
  searchQuery.value = cityName
  isDropdownOpen.value = false
  emit('search', cityName)
  emit('city-selected', cityName)

  if (inputRef.value) {
    inputRef.value.focus()
  }
}
</script>

<style scoped>
.search-container {
  position: relative;
  width: 100%;
  max-width: 400px;
  z-index: 900;
}

.search-bar {
  display: flex;
  align-items: center;
  background-color: #f0f0f0;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  width: 100%;
  max-width: 400px;
  transition: background-color 0.3s ease;
  cursor: text;
  position: relative;
  z-index: 901;
}

.search-bar:focus-within {
  background-color: #e0e0e0;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 1rem;
  padding: 0.5rem 0;
  cursor: text;
}

.search-input::placeholder {
  color: #666;
}

.search-icon {
  width: 20px;
  height: 20px;
  margin-left: 0.5rem;
  cursor: pointer;
}
</style>