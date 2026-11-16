<template>
    <button
        class="nav-button"
        :class="buttonClass"
        @click="$emit('click')"
        :title="isCollapsed ? shortTitle : title"
    >
        <img :src="iconSrc" :alt="title" class="nav-icon" />
        <span class="nav-label" v-if="!isCollapsed">{{ label }}</span>
    </button>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
    type: {
        type: String,
        required: true,
        validator: (value) => ['graphs', 'anomalies', 'comparison', 'dashboard', 'downloads'].includes(value)
    },
    title: {
        type: String,
        required: true
    },
    shortTitle: {
        type: String,
        required: true
    },
    label: {
        type: String,
        required: true
    },
    isCollapsed: {
        type: Boolean,
        default: false
    }
})

const emit = defineEmits(['click'])

// Mapea os tipos para as classes CSS correspondentes
const buttonClass = computed(() => {
    return `${props.type}-button`
})

// Mapea os tipos para as imagens correspondentes
const iconSrc = computed(() => {
    const iconMap = {
        'graphs': '/graph.png',
        'anomalies': '/search.png',
        'comparison': '/compare.png',
        'dashboard': '/dashboard.png',
        'downloads': '/download-icon.png'
    }
    return iconMap[props.type] || ''
})
</script>

<style scoped>
.nav-button {
    width: 100%;
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 8px;
    color: white;
    padding: 1rem 1.5rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    transition: all 0.2s ease;
    font-size: 0.95rem;
    font-weight: 600;
    background: rgba(255, 255, 255, 0.08);
}

.nav-button:hover {
    background: rgba(0, 132, 255, 0.25);
    border-color: rgba(255, 255, 255, 0.4);
    transform: translateX(5px);
    box-shadow: 0 2px 8px rgba(0, 132, 255, 0.2);
}

.nav-icon {
    width: 38px;
    height: 38px;
    filter: invert(1);
}

.graphs-button,
.anomalies-button,
.comparison-button,
.dashboard-button,
.downloads-button {
    background: rgba(255, 255, 255, 0.08);
}

.graphs-button:hover,
.anomalies-button:hover,
.comparison-button:hover,
.dashboard-button:hover,
.dashboard-button:hover {
    background: rgba(0, 132, 255, 0.3);
}
</style>