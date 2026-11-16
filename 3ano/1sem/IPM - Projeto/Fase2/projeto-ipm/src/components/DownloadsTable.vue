<template>
    <div class="downloads-table-container">
        <table class="downloads-table">
            <thead>
                <tr>
                    <th class="city-col">Cidade, País</th>
                    <th class="file-type-col">Tipo de Dados</th>
                    <th class="format-col">CSV</th>
                    <th class="format-col">JSON</th>
                    <th class="file-desc-col">Descrição</th>
                    <th class="action-col">Download</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(fileType, index) in fileTypes" :key="index">
                    <td class="city-col">
                        <div class="city-info">
                            <span class="city-name">{{ cityName }}, {{ countryName }}</span>
                        </div>
                    </td>
                    <td class="file-type-col">{{ fileType.name }}</td>
                    <td class="format-col">
                        <button
                            class="format-link"
                            @click="downloadFile(fileType.type, 'csv')"
                            :title="`Download ${fileType.name} em CSV`"
                        >
                            ✓
                        </button>
                    </td>
                    <td class="format-col">
                        <button
                            class="format-link"
                            @click="downloadFile(fileType.type, 'json')"
                            :title="`Download ${fileType.name} em JSON`"
                        >
                            ✓
                        </button>
                    </td>
                    <td class="file-desc-col">{{ fileType.description }}</td>
                    <td class="action-col">
                        <DownloadButton
                            @click="downloadFile(fileType.type, 'json')"
                            :title="`Download ${fileType.name}`"
                        />
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script setup>
import DownloadButton from './DownloadButton.vue';
import { computed } from 'vue';

const props = defineProps({
    cityName: {
        type: String,
        required: true
    },
    countryName: {
        type: String,
        required: true
    },
    cityId: {
        type: Number,
        required: true
    }
})

const fileTypes = computed(() => [
    {
        type: 'general',
        name: 'Informações Gerais',
        description: 'Dados gerais sobre a cidade (nome, coordenadas, etc.)'
    },
    {
        type: 'listings',
        name: 'Listings',
        description: 'Todos os alojamentos disponíveis nesta cidade'
    },
    {
        type: 'bookings',
        name: 'Reservas',
        description: 'Reservas associadas aos listings desta cidade'
    }
])

const downloadFile = (fileType, format) => {
    const citySlug = props.cityName.toLowerCase().replace(/\s+/g, '_');
    const countrySlug = props.countryName.toLowerCase().replace(/\s+/g, '_');

    const filename = `${citySlug}_${countrySlug}_${fileType}.${format}`;
    const filepath = `/downloads/${filename}`;
    // Cria link temporário para download
    const link = document.createElement('a');
    link.href = filepath;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}
</script>

<style scoped>
.downloads-table-container {
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 16px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    margin-top: 2rem;
}

.downloads-table {
    width: 100%;
    border-collapse: separate;
    border-spacing: 0;
}

.downloads-table thead {
    background: white;
}

.downloads-table th {
    color: var(--color-staytistics-name);
    font-weight: 600;
    padding: 1.5rem 1rem;
    text-align: left;
    font-size: 1rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    border-bottom: 2px solid var(--color-staytistics-name);
}

.downloads-table tbody tr {
    background: white;
    transition: background-color 0.2s ease;
}

.downloads-table tbody tr:hover {
    background-color: #f8f9fa;
}

.downloads-table tbody tr:last-child {
    border-bottom: none;
}

.downloads-table td {
    padding: 1.25rem 1rem;
    color: #333;
    vertical-align: middle;
    background: white;
}

.city-col {
    width: 20%;
}

.file-type-col {
    width: 25%;
    font-weight: 500;
}

.format-col {
    width: 8%;
    text-align: center;
    font-size: 1.2rem;
}

.file-desc-col {
    width: 30%;
    color: #666;
    line-height: 1.4;
}

.action-col {
    width: 15%;
    text-align: center;
}

.city-info {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
}

.city-name {
    font-weight: 600;
    color: #333;
}

.country-name {
    font-size: 0.9rem;
    color: #666;
    font-style: italic;
}

.format-link {
    color: #28a745;
    font-weight: bold;
    font-size: 1.3rem;
    background: none;
    border: none;
    cursor: pointer;
    padding: 0;
    text-decoration: underline;
}

.format-link:hover {
    color: #1e7e34;
}
</style>