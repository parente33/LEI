<!-- Para ser incorporado no ShareButton -->
<template>
    <button
        class="social-option"
        @click="$emit('click')"
        :title="title"
    >
        <div class="social-icon">
            <img :src="iconSrc" :alt="name" class="social-img" />
        </div>
        <span class="social-name">{{ name }}</span>
    </button>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  name: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['click'])

const title = computed(() => {
  return props.name === 'Copiar Link'
    ? 'Copiar link da página'
    : `Partilhar no ${props.name}`
})

const iconSrc = computed(() => {
  const iconMap = {
    'WhatsApp': '/whatsapp-icon.png',
    'Twitter': '/twitter-icon.jpg',
    'Facebook': '/facebook-icon.png',
    'Copiar Link': '/copylink-icon.jpg'
  }

  return iconMap[props.name] || ''
})
</script>

<style scoped>
.social-option {
  background: #f8f9fa;
  border: 2px solid transparent;
  border-radius: 12px;
  padding: 1rem 0.5rem;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
  min-width: 80px;
}

.social-option:hover {
  background: white;
  border-color: var(--color-staytistics-name);
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.social-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.social-img {
  width: 28px;
  height: 28px;
  object-fit: contain;
}

.social-name {
  font-size: 0.85rem;
  font-weight: 500;
  color: #333;
  text-align: center;
  line-height: 1.2;
}
</style>