<template>
    <div class="comparison-component">
        <!-- Modal -->
        <div v-if="showModal" class="modal-overlay" @click="closeModal">
            <div class="modal-content" @click.stop>
                <div class="modal-header">
                    <h3>Comparação de Dados - {{ cityName }}</h3>
                    <button class="modal-close" @click="closeModal">×</button>
                </div>

                <div class="modal-body">
                    <!-- Tabs -->
                    <!-- Só uma para já, depois mete-se mais com tempo -->
                    <div class="tabs">
                        <button
                            class="tab-button"
                            :class="{ active: activeTab === 'insights' }"
                            @click="activeTab = 'insights'"
                        >
                             Insights Automáticos
                        </button>
                    </div>

                    <!-- Conteúdo da Tabs -->
                    <div class="tabs-content">
                        <div v-if="activeTab === 'insights'" class="tab-pane">
                            <h4>Comparações com resto do Continente</h4>
                            <div class="insights-list">
                                <div
                                    v-for="insight in insights"
                                    :key="insight.id"
                                    class="insight-item"
                                    :class="insight.type"
                                >
                                    <div class="insight-text">
                                        <strong>{{ insight.title }}</strong>
                                        <p>{{ insight.description }}</p>
                                    </div>
                                    <span class="insight-tag" :class="insight.type">
                                        {{ insight.tag }}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import staytisticsAPI from '@/api/staytisticsAPI'

// Props
const props = defineProps({
    cityName: {
        type: String,
        required: true
    },
    citySlug: {
        type: String,
        required: true
    },
    listings: {
        type: Array,
        required: true
    },
    bookings: {
        type: Array,
        default: () => []
    },
    showButton: {
        type: Boolean,
        default: true
    },
    modalVisible: {
        type: Boolean,
        default: false
    }
})

const emit = defineEmits(['update:modalVisible'])

// Estados
const showModal = ref(props.modalVisible)
const activeTab = ref('insights')
const comparisonData = ref(null)
const loading = ref(false)

const loadComparisonData = async () => {
    try {
        loading.value = true
        comparisonData.value = await staytisticsAPI.getComparisonData(props.citySlug)
    } catch (error) {
        console.error('Erro ao carregar dados de comparação:', error)
    } finally {
        loading.value = false
    }
}

// Watch para sincronizar props com estado interno
watch(() => props.modalVisible, async (newValue) => {
    showModal.value = newValue
    if (newValue) {
        setTimeout(() => {
            loadComparisonData()
        }, 0)
    }
}, { immediate: true })

onMounted(() => {

    // Se o modal já estiver aberto quando o componente montar, carregue os dados
    if (props.modalVisible) {
        loadComparisonData()
    }
})

const closeModal = () => {
    showModal.value = false
    emit('update:modalVisible', false)
}

const insights = computed(() => {

    if (!comparisonData.value || !comparisonData.value.comparisons) {
        return []
    }
    return comparisonData.value.comparisons
})
</script>

<style scoped>
.comparison-component {
    width: 100%;
}

.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.modal-content {
    background: white;
    border-radius: 12px;
    padding: 0;
    max-width: 800px;
    width: 95%;
    max-height: 85vh;
    overflow-y: auto;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1.5rem;
    border-bottom: 1px solid #eee;
    background: var(--color-staytistics-name);
    color: white;
    border-radius: 12px 12px 0 0;
}

.modal-header h3 {
    margin: 0;
    font-size: 1.5rem;
    font-weight: 700;
}

.modal-close {
    background: rgba(255, 255, 255, 0.2);
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    color: white;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: background-color 0.2s ease;
}

.modal-close:hover {
    background: rgba(255, 255, 255, 0.3);
}

.modal-body {
    padding: 1.5rem;
}

.tabs {
    display: flex;
    background: #f8f9fa;
    border-radius: 8px;
    padding: 0.25rem;
    margin-bottom: 1.5rem;
}

.tab-button {
    flex: 1;
    padding: 0.75rem 1rem;
    border: none;
    background: transparent;
    border-radius: 6px;
    font-size: 0.9rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
    color: #666;
    text-align: center;
}

.tab-button:hover {
    background: rgba(0, 0, 0, 0.05);
    color: #333;
}

.tab-button.active {
    background: white;
    color: var(--color-staytistics-name);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.tabs-content {
    min-height: 400px;
}

.tab-pane h4 {
    margin: 0 0 1.5rem 0;
    color: #333;
    font-size: 1.2rem;
    font-weight: 600;
}

.insights-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.insight-item {
    display: flex;
    align-items: center;
    padding: 1rem;
    background: #f8f9fa;
    border-radius: 8px;
    gap: 1rem;
    transition: all 0.2s ease;
}

.insight-item:hover {
    transform: translateX(4px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.insight-item.higher {
    border-left: 4px solid #28a745;
}

.insight-item.lower {
    border-left: 4px solid #dc3545;
}

.insight-item.similar {
    border-left: 4px solid #ffc107;
}

.insight-item.info {
    border-left: 4px solid var(--color-staytistics-name);
}

.insight-text {
    flex: 1;
}

.insight-text strong {
    display: block;
    margin-bottom: 0.25rem;
    color: #333;
}

.insight-text p {
    margin: 0;
    color: #666;
    font-size: 0.9rem;
}

.insight-tag {
    padding: 0.25rem 0.75rem;
    border-radius: 20px;
    font-size: 0.8rem;
    font-weight: 600;
    white-space: nowrap;
}

.insight-tag.higher {
    background: rgba(40, 167, 69, 0.1);
    color: #28a745;
}

.insight-tag.lower {
    background: rgba(220, 53, 69, 0.1);
    color: #dc3545;
}

.insight-tag.similar {
    background: rgba(255, 193, 7, 0.1);
    color: #856404;
}

.insight-tag.info {
    background: rgba(4, 60, 92, 0.1);
    color: var(--color-staytistics-name);
}
</style>