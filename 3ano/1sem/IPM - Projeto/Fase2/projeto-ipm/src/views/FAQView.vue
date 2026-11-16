<template>
    <main class="faq-container">
        <div class="faq-header">
            <h1 class="main-title">Tem alguma questão?</h1>
            <h2 class="subtitle">Nós temos a resposta!</h2>
        </div>

        <div class="faq-content">
            <div class="questions-column">
                <div
                    v-for="(category, categoryIndex) in faqCategories"
                    :key="categoryIndex"
                    class="category-section"
                >
                    <h2 class="category-title">{{ category.category }}</h2>
                    <div class="questions-list">
                        <FAQItem
                            v-for="(question, questionIndex) in category.questions"
                            :key="questionIndex"
                            :question="question"
                            :is-open="isQuestionOpen(categoryIndex, questionIndex)"
                            @toggle="toggleQuestion(categoryIndex, questionIndex)"
                        />
                    </div>
                </div>
            </div>

            <div class="feedback-column">
                <FeedbackForm />
            </div>
        </div>
    </main>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import FAQItem from '@/components/FAQItem.vue'
import FeedbackForm from '@/components/FeedbackForm.vue'
import staytisticsAPI from '@/api/staytisticsAPI'

const openQuestions = ref([])
const faqCategories = ref([])

onMounted(async () => {
    try {
        const response = await staytisticsAPI.getFaqs()
        faqCategories.value = response
    } catch (error) {
        console.error('Erro ao carregar os dados das FAQ:', error)
    }
})

const isQuestionOpen = (categoryIndex, questionIndex) => {
    return openQuestions.value.some(
        ([catIndex, questIndex]) => catIndex === categoryIndex && questIndex === questionIndex
    )
}

const toggleQuestion = (categoryIndex, questionIndex) => {
    const questionKey = [categoryIndex, questionIndex]
    const existingIndex = openQuestions.value.findIndex(
        ([catIndex, questIndex]) => catIndex === categoryIndex && questIndex === questionIndex
    )

    if (existingIndex > -1) {
        openQuestions.value.splice(existingIndex, 1)
    } else {
        openQuestions.value.push(questionKey)
    }
}
</script>

<style scoped>
.faq-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem;
}

.faq-header {
    text-align: center;
    margin-bottom: 3rem;
}

.main-title {
    font-size: 2.5rem;
    font-weight: 600;
    color: var(--color-staytistics-name);
    line-height: 1.2;
}

.subtitle {
    font-size: 1.5rem;
    color: #666;
    font-weight: 400;
}

.faq-content {
    display: grid;
    grid-template-columns: 1fr 400px;
    gap: 3rem;
    align-items: start;
}

.questions-column {
    background: #fff;
    border-radius: 12px;
    padding: 0;
}

.category-section {
    margin-bottom: 3rem;
}

.category-section:last-child {
    margin-bottom: 0;
}

.category-title {
    font-size: 1.8rem;
    font-weight: 600;
    color: var(--color-staytistics-name);
    margin-bottom: 1.5rem;
    padding-bottom: 0.5rem;
    border-bottom: 2px solid var(--color-staytistics-name);
}

.feedback-column {
    background: #f8f9fa;
    border-radius: 12px;
    padding: 2rem;
    top: 2rem;
}
</style>