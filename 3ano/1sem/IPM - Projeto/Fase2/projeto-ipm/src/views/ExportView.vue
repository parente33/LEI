<template>
    <main class="export-container">
        <div class="hero-text">
            <h1 class="main-title">Aceder à informação nunca foi tão fácil.</h1>
            <p class="subtitle">Os dados trimestrais dos últimos anos, para cada região, estão disponíveis para transferência gratuita nesta página.</p>
        </div>

        <div class="download-section">
            <h2 class="download-title">Zona de downloads:</h2>
            <div class="ttle-underline"></div>
        </div>

        <div class="catchy-text">
            <p>Cada cidade, o seu espaço dedicado, acesso imediato.</p>
        </div>

        <div class="section-divider"></div>

        <ContinentsGrid
            :continents-data="continentsData"
        />
    </main>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import ContinentsGrid from '@/components/ContinentsGrid.vue'
import staytisticsAPI from '@/api/staytisticsAPI'

const continentsData = ref([])

onMounted(async () => {
    try {
        const continents = await staytisticsAPI.getContinents()
        continentsData.value = continents
    } catch (error) {
        console.error('Erro ao carregar os dados:', error)
    }
})
</script>

<style scoped>
.export-container {
    max-width: 1950px;
    margin: 0 auto;
    padding: 2rem;
}

.hero-text {
    margin-bottom: 3rem;
}

.main-title {
    font-size: 2.5rem;
    font-weight: 600;
    color: var(--color-staytistics-name);
    line-height: 1.2;
    text-align: center;
}

.subtitle {
    font-size: 1.1rem;
    color: #333;
    line-height: 1.5;
    text-align: center;
}

.download-section {
    margin-bottom: -0.5rem;
}

.download-title {
    font-size: 1.8rem;
    font-weight: 600;
    color: var(--color-staytistics-name);
}

.title-underline {
    height: 2px;
    background-color: var(--color-staytistics-name);
    width: 100%;
}

.section-divider {
    height: 1px;
    background-color: var(--color-staytistics-name);
    margin: 2rem 0 3rem 0;
    width: 100%;
}

.catchy-text {
    font-size: 1.1rem;
    color: #333;
}
</style>