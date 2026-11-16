<!-- Usado na página de FAQ -->
<template>
    <h3 class="feedback-title">Sugestões/Feedback</h3>
    <div class="feedback-form">

        <form @submit.prevent="submitFeedback">
            <div class="form-group">
                <textarea
                    v-model="feedbackText"
                    class="feedback-textarea"
                    placeholder="Escreva aqui a sua opinião."
                    rows="10"
                    required
                ></textarea>
            </div>

            <div class="rating-section">
                <label class="rating-label">Avaliação:</label>
                <div class="stars">
                    <span
                        v-for="star in 5"
                        :key="star"
                        class="star"
                        :class="{
                            active: star <= rating,
                            hovered: star <= hoverRating
                        }"
                        @click="setRating(star)"
                        @mouseenter="setHoverRating(star)"
                        @mouseleave="resetHoverRating"
                    >
                        ★
                    </span>
                </div>
                <div class="rating-text">{{ ratingText }}</div>
            </div>

            <button type="submit" class="submit-button" :disabled="!isFormValid">
                ENVIAR
            </button>
        </form>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const feedbackText = ref('')
const rating = ref(0)
const hoverRating = ref(0)

const ratingText = computed(() => {
    const texts = ['', 'Péssimo', 'Mau', 'Razoável', 'Bom', 'Excelente']
    return texts[rating.value]
})

const isFormValid = computed(() => {
    return feedbackText.value.trim() !== '' && rating.value > 0
})

const setRating = (stars) => {
    rating.value = stars
}

const setHoverRating = (stars) => {
    hoverRating.value = stars
}

const resetHoverRating = () => {
    hoverRating.value = 0
}

const submitFeedback = () => {
    if (!isFormValid.value) return
    feedbackText.value = ''
    rating.value = 0

    alert('Obrigado pelo seu feedback!')
}
</script>

<style scoped>
.feedback-form {
    background: white;
    padding: 2rem;
    border-radius: 13px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.feedback-title {
    font-size: 1.5rem;
    font-weight: 600;
    color: var(--color-staytistics-name);
    margin-bottom: 1.5rem;
    text-align: center;
}

.form-group {
    margin-bottom: 1.5rem;
}

.feedback-textarea {
    width: 100%;
    border: 1px solid #ddd;
    border-radius: 3px;
    font-size: 1rem;
    font-family: sans-serif;
    resize: none;
    transition: border-color 0.2s ease;
}

.feedback-textarea:focus {
    outline: none;
    border-color: var(--color-staytistics-name);
}

.rating-section {
    text-align: center;
    margin-bottom: 2rem;
}

.rating-label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 500;
    color: #333;
}

.stars {
    display: flex;
    justify-content: center;
    gap: 0.5rem;
    margin-bottom: 0.5rem;
}

.star {
    font-size: 2rem;
    color: #ddd;
    cursor: pointer;
    transition: color 0.2s ease;
    user-select: none;
}

.star.active {
    color: #ffc107;
}

.star.hovered {
    color: #ffc107;
}

.star:hover {
    color: #ffc107;
}

.rating-text {
    font-size: 0.9rem;
    color: #666;
    font-weight: 500;
}

.submit-button {
    width: 100%;
    padding: 1rem;
    background: var(--color-staytistics-name);
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 1.1rem;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.2s ease;
}

.submit-button:hover:not(:disabled) {
    background: var(--color-navbar-items);
}

.submit-button:disabled {
    background: #ccc;
    cursor: not-allowed;
}
</style>