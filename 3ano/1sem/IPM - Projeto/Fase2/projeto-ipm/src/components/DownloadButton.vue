<!-- Usado na página de extração de dados -->
<template>
    <button
        class="download-button"
        :class="{ disabled: disabled }"
        @click="$emit('click')"
        :disabled="disabled"
    >
        <img src="/download-icon.png" alt="Download" class="download-icon" />
        <span class="download-text">Download</span>
    </button>
</template>

<script setup>
defineProps({
    disabled: {
        type: Boolean,
        default: false
  }
})

defineEmits(['click'])
</script>

<style scoped>
.download-button {
    background: var(--color-staytistics-name);
    color: white;
    border: none;
    padding: 0.75rem 1.25rem;
    border-radius: 6px;
    font-weight: 600;
    font-size: 0.9rem;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    transition: all 0.2s ease;
    min-width: 120px;
    justify-content: center;
}

.download-button:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 132, 255, 0.3);
    background: #005079;
}

.download-button:disabled {
    background: #6c757d;
    cursor: not-allowed;
    opacity: 0.6;
}

.download-icon {
    width: 27px;
    height: 27px;
    object-fit: contain;
    filter: invert(1); /* Transforma o preto da imagem do ícone em branco, por questões de visibilidade */
}

.download-text {
    font-weight: 600;
}
</style>