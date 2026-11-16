<template>
    <main class="city-downloads-container" v-if="cityData">
        <!-- Cabeçalho -->
        <div class="downloads-header">
            <h1 class="city-title">{{ cityData.name }}, {{ cityData.country }}</h1>
            <p class="update-info">Última atualização: {{ lastUpdate }}</p>
        </div>

        <!-- Tabela -->
        <DownloadsTable
            :city-name="cityData.name"
            :country-name="cityData.country"
            :cityId="cityData.id"
        />
    </main>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import DownloadsTable from '@/components/DownloadsTable.vue'
import staytisticsAPI from '@/api/staytisticsAPI'

const route = useRoute()
const cityData = ref(null)
const isLoading = ref(true)

// Data da última atualização
const lastUpdate = computed(() => {
    return new Date().toLocaleDateString('pt-PT')
})

// Buscar dados da cidade
const fetchData = async () => {
    try {
        isLoading.value = true
        const city = await staytisticsAPI.getCityBySlug(route.params.slug)
        cityData.value = city

    } catch (error) {
        console.error('Erro ao carregar dados:', error)
        isLoading.value = false
    }
}

onMounted(() => {
    fetchData()
})
</script>

<style scoped>
.city-downloads-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 2rem;
}

.downloads-header {
    text-align: center;
    margin-bottom: 3rem;
}

.city-title {
    font-size: 3rem;
    font-weight: 700;
    color: var(--color-staytistics-name);
    margin-bottom: 0.5rem;
    line-height: 1.2;
}

.update-info {
    font-size: 1.1rem;
    color: #666;
    font-style: italic;
}
</style>