<!-- Usado na zona de exportação de dados (na grelha das cidades disponíveis) -->
<template>
    <router-link
        :to="`/downloads/${citySlug}`"
        class="city-item"
    >
        {{ cityName }}
    </router-link>
</template>

<script setup>
const props = defineProps({
    cityName: {
        type: String,
        required: true
    },
    citySlug: {
        type: String,
        required: true
    }
})
</script>

<style scoped>
.city-item {
    padding: 0.5rem 0.75rem;
    background-color: white;
    border-radius: 4px;
    border: 1px solid #e9ecef;
    font-size: 0.9rem;
    color: #495057;
    transition: all 0.2s ease;
    cursor: pointer;
    text-align: center;
    word-wrap: break-word;
    text-decoration: none;
}

.city-item:hover {
    background-color: var(--color-staytistics-name);
    color: white;
    transform: translateY(-1px);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    text-decoration: none;
}
</style>