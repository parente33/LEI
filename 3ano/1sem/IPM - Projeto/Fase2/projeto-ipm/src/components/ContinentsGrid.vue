<!-- Usado na zona de extração de dados -->
<template>
    <div class="continents-grid">
        <div
            v-for="continent in continentsData"
            :key="continent.continent"
            class="continent-column"
        >
            <h3 class="continent-name">{{ continent.name }}</h3>
            <div class="cities-list">
                <CityItem
                    v-for="city in continent.cities"
                    :key="city.slug"
                    :city-name="city.name"
                    :city-slug="city.slug"
                />
            </div>
        </div>
    </div>
</template>

<script setup>
import CityItem from './CityItem.vue'

defineProps({
    continentsData: {
        type: Array,
        required: true
    }
})
</script>

<style scoped>
.continents-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
    gap: 1.5rem;
    margin-top: 2rem;
}

.continent-column {
    display: flex;
    flex-direction: column;
    min-width: 0;
}

.continent-name {
    font-size: 1.2rem;
    font-weight: 600;
    color: var(--color-staytistics-name);
    margin-bottom: 1rem;
    padding-bottom: 0.5rem;
    border-bottom: 1px solid #dee2e6;
    text-align: center;

    word-wrap: break-word;
    overflow-wrap: break-word;
}

.cities-list {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}
</style>