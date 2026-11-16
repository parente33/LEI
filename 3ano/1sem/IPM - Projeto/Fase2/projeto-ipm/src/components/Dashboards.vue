<template>
    <div class="dashboard-component">
        <!-- Modal -->
        <div v-if="showDashModal" class="modal-overlay" @click="closeDashModal">
            <div class="dashmodal-content" @click.stop>
                <div class="dashmodal-header">
                    <h3>{{ cityName }} - DataHub</h3>
                    <button class="dashmodal-close" @click="closeDashModal">×</button>
                </div>

                <div class="dashmodal-body">
                <!-- Primeira linha -->
                    <div class="dashboard-row">
                        <!-- Score de Qualidade dos Listings -->
                        <div class="dashboard-card quality-card">
                            <div class="card-icon"></div>
                            <div class="card-value">{{ dashboardStats.listingQualityScore }}%</div>
                            <div class="card-label">Score de Qualidade</div>
                        </div>

                        <!-- Receita Total Estimada -->
                        <div class="dashboard-card revenue-card">
                            <div class="card-icon"></div>
                            <div class="card-value">{{ formatCurrency(dashboardStats.estimatedRevenue) }}</div>
                            <div class="card-label">Receita Total Estimada</div>
                        </div>

                        <!-- Receita Média por Aluguer -->
                        <div class="dashboard-card avg-revenue-card">
                            <div class="card-icon"></div>
                            <div class="card-value">{{ formatCurrency(dashboardStats.avgRevenuePerBooking) }}</div>
                            <div class="card-label">Receita Média por Aluguer</div>
                        </div>
                    </div>

                    <!-- Segunda linha -->
                    <div class="dashboard-row">
                        <!-- Taxa de Cancelamento -->
                        <div class="dashboard-card cancellation-card">
                            <div class="card-icon"></div>
                            <div class="card-value">{{ dashboardStats.cancellationRate }}%</div>
                            <div class="card-label">Taxa de Cancelamento</div>
                        </div>

                        <!-- Total de dias alugados num ano -->
                        <div class="dashboard-card days-card">
                            <div class="card-icon"></div>
                            <div class="card-value">{{ dashboardStats.totalBookedDays }}</div>
                            <div class="card-label">Total Dias Alugados (Ano)</div>
                        </div>

                        <!-- Média de Hóspedes por Aluguer -->
                        <div class="dashboard-card avg-guests-card">
                            <div class="card-icon"></div>
                            <div class="card-value">{{ dashboardStats.avgGuestsPerBooking }}</div>
                            <div class="card-label">Média Hóspedes/Aluguer</div>
                        </div>
                    </div>

                    <!-- Terceira linha -->
                    <div class="dashboard-row">
                        <!-- Meses de Alta Procura -->
                        <div class="dashboard-card peak-card">
                            <div class="card-icon"></div>
                            <div class="card-value">{{ dashboardStats.peakMonths }}</div>
                            <div class="card-label">Meses de Alta Procura</div>
                        </div>

                        <!-- Preço Médio/Noite vs Média Continental -->
                        <div class="dashboard-card price-card">
                            <div class="card-icon"></div>
                            <div class="price-comparison">
                                <div class="price-item">
                                    <span class="price-label">Cidade:</span>
                                    <span class="price-value">{{ formatCurrency(dashboardStats.cityAvgPrice) }}</span>
                                </div>
                                <div class="price-item">
                                    <span class="price-label">Média Continental:</span>
                                    <span class="price-value">{{ formatCurrency(dashboardStats.continentalAvgPrice) }}</span>
                                </div>
                            </div>
                            <div class="card-label">Preço/Noite vs Média Continental</div>
                        </div>

                        <!-- Alugueres por Zona -->
                        <div class="dashboard-card zone-card">
                            <div class="card-icon"></div>
                            <div class="zone-chart">
                                <canvas ref="zoneChartRef"></canvas>
                            </div>
                            <div class="card-label">Distribuição por Zona</div>
                        </div>
                    </div>

                    <!-- Quarta linha -->
                    <div class="dashboard-row">
                        <!-- Anomalias -->
                        <div class="dashboard-card alerts-card">
                            <div class="card-icon"></div>
                            <div class="alerts-content">
                                <div class="alerts-count">{{ props.anomalyCount }}</div>
                                <div class="alerts-detail">anomalias detetadas</div>
                            </div>
                            <div class="card-label">Anomalias</div>
                        </div>

                        <!-- Top Hosts -->
                        <div class="dashboard-card top-hosts-card">
                            <div class="card-icon"></div>
                            <div class="hosts-list">
                                <div class="host-item" v-for="(host, index) in dashboardStats.topHosts" :key="index">
                                    <span class="host-name">{{ host.name }}</span>
                                    <span class="host-count">{{ host.count }} listings</span>
                                </div>
                            </div>
                            <div class="card-label">Top 3 Hosts</div>
                        </div>

                        <!-- Tipo de Propriedade Mais Popular -->
                        <div class="dashboard-card popular-type-card">
                            <div class="card-icon"></div>
                            <div class="popular-type">
                                <div class="type-name">{{ dashboardStats.mostPopularType.type }}</div>
                                <div class="type-percentage">{{ dashboardStats.mostPopularType.percentage }}% do total</div>
                            </div>
                            <div class="card-label">Tipo Mais Popular</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { Chart, registerables } from 'chart.js'
import staytisticsAPI from '@/api/staytisticsAPI'

// Registrar componentes do Chart.js
Chart.register(...registerables)

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
    showDashButton: {
        type: Boolean,
        default: true
    },
    modalVisible: {
        type: Boolean,
        default: false
    },
    anomalyCount: {
        type: Number,
        default: 0
    }
})

// Emit
const emit = defineEmits(['update:modalVisible'])

// Refs
const showDashModal = ref(props.modalVisible)
const zoneChartRef = ref(null)
const dashboardData = ref(null)
const loading = ref(false)
const error = ref(null)
let zoneChart = null

// Carregar dados da dashboard
const loadDashboardData = async () => {
    try {
        loading.value = true
        error.value = null

        // Tente primeiro com o slug direto da cidade
        const citySlug = props.citySlug || props.cityName.toLowerCase().replace(/\s+/g, '-')

        dashboardData.value = await staytisticsAPI.getDashboardData(citySlug)
    } catch (error) {
        error.value = error.message
    } finally {
        loading.value = false
    }
}

watch(() => props.modalVisible, async (newValue) => {
    showDashModal.value = newValue

    if (newValue) {
        // Pequeno delay para garantir que o DOM está pronto (não funciona de outra maneira)
        setTimeout(async () => {
            await loadDashboardData()
            setTimeout(createZoneChart, 100)
        }, 50)
    } else {
        dashboardData.value = null
        error.value = null
    }
}, { immediate: true }) // True para logar o valor inicial

const closeDashModal = () => {
    showDashModal.value = false
    emit('update:modalVisible', false)
}

// Formatação de moeda
const formatCurrency = (value) => {
    if (value >= 1000000) {
        return `€${(value / 1000000).toFixed(1)}M`
    } else if (value >= 1000) {
        return `€${(value / 1000).toFixed(1)}K`
    }
    return `€${value}`
}

// Calcular estatísticas do dashboard
const dashboardStats = computed(() => {
    if (!dashboardData.value || !dashboardData.value.dashboardStats) {
        return null
    }

    const stats = dashboardData.value.dashboardStats
    return {
        ...stats,
        totalBookedDays: stats.totalBookedNights || 0,
        cityAvgPrice: stats.avgPrice || 0,
        continentalAvgPrice: stats.continentAvgPrice || 0,
        listingQualityScore: stats.listingQualityScore || 0,
        estimatedRevenue: stats.estimatedRevenue || 0,
        avgRevenuePerBooking: stats.avgRevenuePerBooking || 0,
        cancellationRate: stats.cancellationRate || 0,
        avgGuestsPerBooking: stats.avgGuestsPerBooking || 0,
        peakMonths: stats.peakMonths || 'N/A',
        topHosts: stats.topHosts || [],
        mainRoomType: stats.mainRoomType || 'N/A',
        mostPopularType: stats.mostPopularType || { type: 'N/A', percentage: 0 }
    }
})

// Dados para o gráfico circular
const zoneDistribution = computed(() => {
    if (!props.listings || props.listings.length === 0) return []

    const zones = {}
    props.listings.forEach(listing => {
        const zone = listing.neighbourhood_group || 'Desconhecida'
        zones[zone] = (zones[zone] || 0) + 1
    })

    return Object.entries(zones).map(([zone, count]) => ({
        zone,
        count
    })).sort((a, b) => b.count - a.count).slice(0, 5)    // Top 5 zonas apenas
})

// Criar gráfico
const createZoneChart = () => {
    if (!zoneChartRef.value || !zoneDistribution.value.length) return

    const data = {
        labels: zoneDistribution.value.map(item => item.zone),
        datasets: [{
            data: zoneDistribution.value.map(item => item.count),
            backgroundColor: [
                '#4CAF50', '#2196F3', '#FF9800', '#9C27B0', '#FF5722'
            ],
            borderWidth: 1,
            borderColor: '#fff'
        }]
    }

    const config = {
        type: 'pie',
        data: data,
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: (context) => {
                            const label = context.label || ''
                            const value = context.parsed || 0
                            const total = context.dataset.data.reduce((a, b) => a + b, 0)
                            const percentage = Math.round((value / total) * 100)
                            return `${label}: ${value} (${percentage}%)`
                        }
                    }
                }
            }
        }
    }

    if (zoneChart) {
        zoneChart.destroy()
    }

    zoneChart = new Chart(zoneChartRef.value, config)
}

// Limpar gráfico
onUnmounted(() => {
    if (zoneChart) {
        zoneChart.destroy()
    }
})
</script>

<style scoped>
.dashboard-component {
    width: 100%;
}

.modal-overlay {
    position: fixed;
    top: 4em;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
}

.dashmodal-content {
    background: white;
    border-radius: 16px;
    max-width: 1400px;
    width: 95%;
    max-height: 85vh;
    overflow-y: auto;
}

.dashmodal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1.5rem 2rem;
    border-bottom: 1px solid #eee;
    background: var(--color-staytistics-name);
    color: white;
    border-radius: 12px 12px 0 0;
}

.loading-state, .no-data-state {
    text-align: center;
    padding: 3rem;
    color: #666;
}

.loading-state .loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid var(--color-staytistics-name);
    border-radius: 50%;
    margin: 0 auto 1rem;
}

.dashmodal-header h3 {
    margin: 0;
    font-size: 1.8rem;
    font-weight: 700;
}

.dashmodal-close {
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

.dashmodal-close:hover {
    background: rgba(255, 255, 255, 0.3);
}

.dashmodal-body {
    padding: 1rem;
}

.dashboard-row {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 1.5rem;
    margin-bottom: 1.5rem;
}

.dashboard-card {
    background: #f8f9fa;
    border-radius: 12px;
    padding: 1.5rem;
    text-align: center;
    transition: all 0.3s ease;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-between;
    min-height: 160px;
    border: 2px solid transparent;
}

.dashboard-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
    border-color: var(--color-staytistics-name);
}

.card-icon {
    font-size: 2rem;
    margin-bottom: 0.75rem;
}

.card-value {
    font-size: 2.2rem;
    font-weight: 700;
    color: var(--color-staytistics-name);
    margin-bottom: 0.5rem;
    line-height: 1;
}

.card-label {
    font-size: 1rem;
    color: #666;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.quality-card {
    background: linear-gradient(135deg, #e8f5e9, #c8e6c9);
}

.revenue-card {
    background: linear-gradient(135deg, #e0f7fa, #b2ebf2);
}

.avg-revenue-card {
    background: linear-gradient(135deg, #f3e5f5, #e1bee7);
}

.cancellation-card {
    background: linear-gradient(135deg, #ffebee, #ffcdd2);
}

.days-card {
    background: linear-gradient(135deg, #fff3e0, #ffe0b2);
}

.avg-guests-card {
    background: linear-gradient(135deg, #e3f2fd, #bbdefb);
}

.peak-card {
    background: linear-gradient(135deg, #f1f8e9, #dcedc8);
}

.price-card {
    background: linear-gradient(135deg, #e8eaf6, #c5cae9);
}

.zone-card {
    background: linear-gradient(135deg, #fce4ec, #f8bbd9);
    min-height: 200px;
}

.zone-chart {
    width: 120px;
    height: 120px;
    margin: 0.5rem 0;
}

.alerts-card {
    background: linear-gradient(135deg, #fff8e1, #ffecb3);
    cursor: pointer;
    transition: all 0.3s ease;
}

.alerts-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin: 0.5rem 0;
}

.alerts-count {
    font-size: 2.5rem;
    font-weight: 700;
    color: #ff0000;
    line-height: 1;
}

.alerts-detail {
    font-size: 0.9rem;
    color: #666;
    margin-top: 0.25rem;
}

.top-hosts-card {
    background: linear-gradient(135deg, #e1f5fe, #b3e5fc);
}

.hosts-list {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    margin: 0.5rem 0;
    width: 100%;
}

.host-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.25rem 0.5rem;
    background: rgba(255, 255, 255, 0.7);
    border-radius: 4px;
}

.host-name {
    font-size: 0.85rem;
    font-weight: 500;
    color: #333;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 60%;
}

.host-count {
    font-size: 0.8rem;
    font-weight: 600;
    color: var(--color-staytistics-name);
}

.popular-type-card {
    background: linear-gradient(135deg, #f9fbe7, #f0f4c3);
}

.popular-type {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin: 0.5rem 0;
}

.type-name {
    font-size: 1.1rem;
    font-weight: 600;
    color: #333;
    margin-bottom: 0.25rem;
}

.type-percentage {
    font-size: 0.9rem;
    color: #666;
}

.price-comparison {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    margin: 0.5rem 0;
}

.price-item {
    display: flex;
    width: 200px;
    justify-content: space-between;
    align-items: center;
    padding: 0.5rem 1rem;
    background: rgba(255, 255, 255, 0.7);
    border-radius: 4px;
}

.price-label {
    font-size: 0.95rem;
    color: #666;
}

.price-value {
    font-size: 0.9rem;
    font-weight: 600;
    color: var(--color-staytistics-name);
}
</style>