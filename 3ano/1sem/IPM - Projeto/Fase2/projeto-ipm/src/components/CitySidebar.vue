<template>
    <aside class="city-sidebar" :class="{ collapsed: isCollapsed}">
        <!-- Botão para recolher/expandir sidebar -->
        <button class="sidebar-toggle-button" @click="toggleSidebar">
            <img :src="toggleIconSrc" class="toggle-icon" />
        </button>

        <!-- Conteúdo da sidebar -->
        <div class="sidebar-content">
            <!-- Navegação -->
            <nav class="sidebar-nav">
                <ul class="nav-list">
                    <!-- Estatísticas avançadas (com gráficos) -->
                    <li class="nav-item">
                        <CitySidebarItem
                            type="graphs"
                            title="Ver Estatísticas Avançadas"
                            short-title="Gráficos"
                            label="Estatísticas Avançadas"
                            :is-collapsed="isCollapsed"
                            @click="showGraphsModal = true"
                        />
                    </li>

                    <!-- Anomalias -->
                    <li class="nav-item">
                        <CitySidebarItem
                            type="anomalies"
                            title="Detetar Anomalias"
                            short-title="Anomalias"
                            label="Detetar Anomalias"
                            :is-collapsed="isCollapsed"
                            @click="showAnomaliesModal = true"
                        />
                    </li>

                    <!-- Modo de comparações -->
                    <li class="nav-item">
                        <CitySidebarItem
                            type="comparison"
                            title="Comparar dados com outra cidade"
                            short-title="Comparações"
                            label="Comparar Dados"
                            :is-collapsed="isCollapsed"
                            @click="showComparisonModal = true"
                        />
                    </li>

                    <!-- Modal de dashboards -->
                    <li class="nav-item">
                        <CitySidebarItem
                            type="dashboard"
                            title="Ver dashboards executivas"
                            short-title="Dashboards"
                            label="Dashboards"
                            :is-collapsed="isCollapsed"
                            @click="showDashboardModal = true"
                        />
                    </li>

                    <!-- Downloads -->
                    <li class="nav-item">
                        <CitySidebarItem
                            type="downloads"
                            title="Baixar Dados"
                            short-title="Downloads"
                            label="Baixar Dados"
                            :is-collapsed="isCollapsed"
                            @click="navigateToDownloads"
                        />
                    </li>
                </ul>
            </nav>
        </div>

        <!-- Modal de gráficos -->
        <GraphedStats
            v-if="showGraphsModal"
            :city-name="cityName"
            :listings="listings"
            :bookings="bookings"
            :modal-visible="showGraphsModal"
            @update:modalVisible="showGraphsModal = $event"
        />

        <!-- Modal de anomalias -->
        <Anomalies
            v-if="showAnomaliesModal"
            :listings="listings"
            :bookings="bookings"
            :show-button="false"
            :modal-visible="showAnomaliesModal"
            @update:modalVisible="showAnomaliesModal = $event"
        />

        <Comparisons
            v-if="showComparisonModal"
            :city-name="cityName"
            :city-slug="citySlug"
            :listings="listings"
            :bookings="bookings"
            :modal-visible="showComparisonModal"
            @update:modalVisible="showComparisonModal = $event"
        />

        <!-- Modal de dashboards -->
        <Dashboards
            v-if="showDashboardModal"
            :city-name="cityName"
            :city-slug="citySlug"
            :listings="listings"
            :bookings="bookings"
            :anomaly-count="anomalyCount"
            :show-dash-button="false"
            :modal-visible="showDashboardModal"
            @update:modalVisible="showDashboardModal = $event"
            @close="showDashboardModal = false"
        />
    </aside>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import Anomalies from './Anomalies.vue'
import CitySidebarItem from './CitySidebarItem.vue'
import Dashboards from './Dashboards.vue'
import GraphedStats from './GraphedStats.vue'
import Comparisons from './Comparisons.vue'
import { anomaliesStore } from '@/stores/anomaliesStore'

const router = useRouter()

const props = defineProps({
    cityName: {
        type: String,
        required: true
    },
    citySlug: {
        type: String,
        required: true
    },
    totalListings: {
        type: Number,
        default: 0
    },
    listings: {
        type: Array,
        required: true,
        default: () => []
    },
    bookings: {
        type: Array,
        default: () => []
    }
})

// Estados
const isCollapsed = ref(false)
const showGraphsModal = ref(false)
const showAnomaliesModal = ref(false)
const showDashboardModal = ref(false)
const showComparisonModal = ref(false)

const toggleIconSrc = computed(() => {
    return isCollapsed.value ? '/right-arrow.png' : '/left-arrow.png'
})

const anomalyCount = computed(() => anomaliesStore.count)

// Navegação no botão de downloads
const navigateToDownloads = () => {
    router.push(`/downloads/${props.citySlug}`)
}

// Alterna entre sidebar recolhida/expandida
const toggleSidebar = () => {
    isCollapsed.value = !isCollapsed.value
}
</script>

<style scoped>
.city-sidebar {
    position: sticky;
    left: 0;
    top: 6.1em; /* Altura da sidebar (fica desde o fim da navbar até ao início do footer */
    bottom: 0;
    width: 320px;
    background: linear-gradient(180deg, #1a3a52 0%, #2a4a62 50%, #1f405a 100%);
    color: white;
    transition: all 0.3s ease;
    z-index: 900;
    box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
}

.city-sidebar.collapsed {
    width: 130px;
}

.sidebar-toggle-button {
    position: absolute;
    right: -12px;
    top: 35rem;
    background: white;
    border-radius: 50%;
    width: 35px;
    height: 35px;
    cursor: pointer;
}

.toggle-icon {
    width: 22px;
    height: 22px;
    object-fit: contain;
}

.sidebar-content {
    flex: 1;
    padding: 1.5rem 1rem;
    overflow-y: auto;
}

.sidebar-nav {
    margin-bottom: 2rem;
}

.nav-list {
    list-style: none;
    padding: 0;
    margin: 0;
}

.nav-item {
    margin-bottom: 0.5rem;
}
</style>