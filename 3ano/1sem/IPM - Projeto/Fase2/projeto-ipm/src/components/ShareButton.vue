<!-- Para partilhar os gráficos na CityView -->
<template>
  <div class="share-component">
    <!-- Botão de Partilha -->
    <button
      class="share-button"
      @click="toggleShareModal"
      @mouseenter="showTooltip = true"
      @mouseleave="showTooltip = false"
    >
      <span class="share-icon">↗</span>
      <span class="share-text">Partilhar</span>
    </button>

    <!-- Tooltip quando se dá hover no botão -->
    <div v-if="showTooltip && !isModalOpen" class="share-tooltip">
      Partilhar esta página
    </div>

    <!-- Modal de Partilha -->
    <div v-if="isModalOpen" class="share-modal-overlay" @click="closeModal">
      <div class="share-modal" @click.stop>
        <div class="modal-header">
          <h3>Para onde quer partilhar a informação?</h3>
          <button class="modal-close" @click="closeModal">×</button>
        </div>

        <div class="social-options">
          <!-- WhatsApp -->
          <SocialOption
            name="WhatsApp"
            @click="shareTo('WhatsApp')"
          />

          <!-- Twitter -->
          <SocialOption
            name="Twitter"
            @click="shareTo('Twitter')"
          />

          <!-- Facebook -->
          <SocialOption
            name="Facebook"
            @click="shareTo('Facebook')"
          />

          <!-- Copiar Link -->
          <SocialOption
            name="Copiar Link"
            @click="copyLink"
          />
        </div>
      </div>
    </div>

    <!-- Toast de confirmação -->
    <div v-if="showToast" class="toast">
      {{ toastMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import SocialOption from './SocialOption.vue'

// Estados do componente
const isModalOpen = ref(false)
const showTooltip = ref(false)
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success')

// URL atual da página
const currentUrl = ref(window.location.href)

// Alterna modal
const toggleShareModal = () => {
  isModalOpen.value = !isModalOpen.value
  showTooltip.value = false
}

const closeModal = () => {
  isModalOpen.value = false
}

// Partilhar para uma rede social
const shareTo = (platform) => {
  const pageTitle = document.title || 'Staytistics'
  const shareText = `Confere estas estatísticas na ${pageTitle}!`

  switch (platform) {
    case 'WhatsApp':
      // WhatsApp Web/App URL scheme
      const whatsappUrl = `https://wa.me/?text=${encodeURIComponent(shareText + ' ' + currentUrl.value)}`
      window.open(whatsappUrl, '_blank')
      showToastMessage('A abrir WhatsApp...', 'success')
      break

    case 'Twitter':
      // Twitter/X share URL
      const twitterUrl = `https://twitter.com/intent/tweet?text=${encodeURIComponent(shareText)}&url=${encodeURIComponent(currentUrl.value)}`
      window.open(twitterUrl, '_blank', 'width=550,height=420')
      showToastMessage('A abrir Twitter...', 'success')
      break

    case 'Facebook':
      // Facebook Sharer URL
      const facebookUrl = `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(currentUrl.value)}`
      window.open(facebookUrl, '_blank', 'width=550,height=420')
      showToastMessage('A abrir Facebook...', 'success')
      break

    default:
      showToastMessage('Plataforma não suportada', 'error')
      return
  }

  closeModal()
}

// Copiar link para a área de transferência
const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(currentUrl.value)
    showToastMessage('Link copiado para a área de transferência!', 'success')
  } catch (err) {
    showToastMessage('Erro ao copiar o link.', 'error')
    console.error('Erro ao copiar:', err)
  }
  closeModal()
}

// Mostra toast de confirmação
const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true

  // Esconde toast após 3 segundos (4 segundos para mensagens de info)
  setTimeout(() => {
    showToast.value = false
  }, type === 'info' ? 4000 : 3000)
}
</script>

<style scoped>
.share-component {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  z-index: 999;
}

.share-button {
  background: var(--color-staytistics-name);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 50px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.share-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
  background: linear-gradient(135deg, #0088cc, var(--color-staytistics-name));
}

.share-icon {
  font-size: 1.2rem;
  font-weight: bold;
}

.share-text {
  font-weight: 500;
}

.share-tooltip {
  position: absolute;
  bottom: 100%;
  right: 0;
  background: #333;
  color: white;
  padding: 0.5rem 0.75rem;
  border-radius: 4px;
  font-size: 0.8rem;
  white-space: nowrap;
  margin-bottom: 0.5rem;
}

.share-tooltip::after {
  content: '';
  position: absolute;
  top: 100%;
  right: 1rem;
  border-width: 5px;
  border-style: solid;
  border-color: #333 transparent transparent transparent;
}

.share-modal-overlay {
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

.share-modal {
  background: white;
  border-radius: 16px;
  padding: 2rem;
  max-width: 500px;
  width: 90%;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2rem;
}

.modal-header h3 {
  margin: 0;
  color: #333;
  font-size: 1.3rem;
  font-weight: 600;
  line-height: 1.4;
  flex: 1;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.8rem;
  cursor: pointer;
  color: #666;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.modal-close:hover {
  background: #f0f0f0;
  color: #333;
}

.social-options {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
}

.toast {
  position: fixed;
  bottom: 6rem;
  right: 2rem;
  background: #28a745;
  color: white;
  padding: 1rem 1.5rem;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  font-weight: 500;
  z-index: 1001;
}
</style>