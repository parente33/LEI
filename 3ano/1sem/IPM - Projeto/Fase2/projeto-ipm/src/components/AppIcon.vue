<!-- Usado na Navbar e no Footer -->
<template>
    <div class="application-icon" @click="goToHome">
        <img src="/bluebnb.png" class="application-icon-png" alt="Staytistics' icon" />
        <span class="application-icon-text">Staytistics</span>
    </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToHome = () => {
    router.push('/')
}
</script>

<style scoped>
.application-icon {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    text-decoration: none;
    cursor: pointer;
}

.application-icon-png {
    height: 55px;
    width: 55px;
}

.application-icon-text {
    color: var(--color-staytistics-name);
    font-size: 1.7rem;
    font-weight: bold;
}

.application-icon:hover {
    opacity: 0.8;
}
</style>