<template>
  <main class="home-container">
    <div class="hero-text">
      <h1>Bem-vindo à <span class="highlight">Staytistics</span>!</h1>
      <p>O melhor site para visualizar informações sobre alojamentos nas suas cidades favoritas.</p>
    </div>

    <SearchBar
      class="search-bar-container"
    />

    <p class="or-text">Ou, se preferir,</p>

    <h2 class="hot-topics">Cidades em Destaque:</h2>

    <section class="cities-grid">
      <CityCard
        v-for="city in featuredCities"
        :key="city.slug"
        :city="city"
      />
    </section>
  </main>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import CityCard from '@/components/CityCard.vue'
import SearchBar from '@/components/SearchBar.vue'
import staytisticsAPI from '@/api/staytisticsAPI'

const continentsData = ref([])

const featuredCities = computed(() => {
  if (!continentsData.value.length) return []

  const allCities = continentsData.value.flatMap(continent =>
    continent.cities.map(city => ({
      ...city,
      continentName: continent.name
    }))
  )

  const citiesWithImages = allCities.filter(city => city.image)

  return citiesWithImages
})

onMounted(async () => {
  try {
    const continents = await staytisticsAPI.getContinents()
    continentsData.value = continents
  } catch (error) {
    console.error('Erro ao carregar dados:', error)
  }
})
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  min-height: 80vh;
  text-align: center;
  padding: 2rem;
}

.hero-text h1 {
  font-size: 2.5rem;
  font-weight: 600;
  color: var(--color-staytistics-name);
  line-height: 1.2;
}

.search-bar-container {
  margin: 2rem 0;
  z-index: 1000;
}

.highlight {
  color: var(--color-staytistics-name);
}

.hero-text p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  color: #444;
}

.or-text {
  margin-top: 2rem;
  font-size: 1.1rem;
  font-style: italic;
}

.cities-container {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f9f9f9;
}

.hot-topics {
  margin-top: 2rem;
  font-size: 1.8rem;
  font-weight: bold;
  color: var(--color-staytistics-name);
}

.cities-grid {
  display: grid;
  grid-template-columns: repeat(3, 400px);
  grid-template-rows: repeat(2, auto);
  gap: 1.5rem;
  justify-content: center;
  align-content: center;
}
</style>