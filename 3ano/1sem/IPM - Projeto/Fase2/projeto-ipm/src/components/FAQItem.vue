<!-- Usado para representar cada pergunta (e resposta) na página de FAQ -->
<template>
    <div class="faq-item" :class="{ 'is-open': isOpen }">
        <div class="question-header" @click="$emit('toggle')">
            <h3 class="question-text">{{ question.question }}</h3>
            <div class="expand-icon">
                <span class="icon">{{ isOpen ? '−' : '+' }}</span>
            </div>
        </div>

        <div class="answer-content" v-if="isOpen">
            <p class="answer-text">{{ question.answer }}</p>
        </div>
    </div>
</template>

<script setup>
defineProps({
    question: {
        type: Object,
        required: true
    },
    isOpen: {
        type: Boolean,
        default: false
    }
})

defineEmits(['toggle'])
</script>

<style scoped>
.faq-item {
    border: 1px solid #e9ecef;
    border-radius: 8px;
    margin-bottom: 1rem;
    overflow: hidden;
    transition: all 0.3s ease;
}

.faq-item:not(:last-child) {
    margin-bottom: 1rem;
}

.faq-item.is-open {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.question-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1.5rem;
    background: #fff;
    cursor: pointer;
    transition: background-color 0.2s ease;
}

.question-header:hover {
    background: #f8f9fa;
}

.question-text {
    font-size: 1.1rem;
    font-weight: 600;
    color: #333;
    margin: 0;
    flex: 1;
}

.expand-icon {
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--color-staytistics-name);
    border-radius: 50%;
    color: white;
    font-weight: bold;
    font-size: 1.2rem;
}

.answer-content {
    padding: 1.5rem 1.5rem 1.5rem;
    background: #fff;
    border-top: 1px solid #e9ecef;
}

.answer-text {
    font-size: 1rem;
    line-height: 1.6;
    color: #555;
    margin: 0;
}
</style>