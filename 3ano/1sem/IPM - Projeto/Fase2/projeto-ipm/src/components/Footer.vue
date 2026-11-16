<!-- Rodapé -->
<template>
    <footer class="footer-container">
        <div class="footer-content">
            <!-- Logo & Description Section-->
            <div class="footer-section">
                <div class="logo-section">
                    <AppIcon />
                </div>
                <p class="footer-description">
                    Dados claros sobre alojamento mundial. Transformamos dados complexos em insights úteis.
                </p>
            </div>

            <!-- Navigation Section -->
            <div class="footer-section">
                <h4 class="footer-title">Navegação</h4>
                <ul class="footer-links">
                    <li><router-link to="/" class="footer-link">Home</router-link></li>
                    <li><router-link to="/export" class="footer-link">Exportar Dados</router-link></li>
                    <li><router-link to="/faq" class="footer-link">FAQ</router-link></li>
                    <li><router-link to="/about" class="footer-link">About Us</router-link></li>
                </ul>
            </div>

            <!-- Legal & Contacts Section -->
            <div class="footer-section">
                <h4 class="footer-title">Legal & Contactos</h4>
                <ul class="footer-links">
                    <li><a href="#" class="footer-link">Política de Privacidade</a></li>
                    <li><a href="#" class="footer-link">Termos de Utilização</a></li>
                    <li><a href="mailto:emailgenerico@dominiogenerico.tldgenerico" class="footer-link">Suporte</a></li>
                </ul>
            </div>
        </div>

        <div class="footer-divider"></div>

        <div class="footer-bottom">
            <div class="footer-bottom-content">
                <p class="copyright">
                    © 2025 Staytistics. Todos os direitos reservados.
                    <span class="data-source">Dados provenientes do InsideAirbnb</span>
                </p>
                <div class="footer-meta">
                    <span class="version">v1.0.0</span>
                </div>
            </div>
        </div>
    </footer>
</template>

<script setup>
import AppIcon from './AppIcon.vue'
</script>

<style scoped>
.footer-container {
    background-color: var(--footer-background-colour);
    margin-top: auto;
    width: 100%;
}

.footer-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 3rem 2rem 2rem;
    display: grid;
    grid-template-columns: 2fr 1fr 1fr;
    gap: 2rem;
}

.footer-section {
    display: flex;
    flex-direction: column;
}

.logo-section {
    margin-bottom: 1rem;
}

/* Ajustes para o AppIcon */
.logo-section :deep(.application-icon) {
    justify-content: flex-start;
}

.logo-section :deep(.application-icon-text) {
    color: white;
}

.logo-section :deep(.application-icon-png) {
    height: 45px;
    width: 45px;
}

.footer-description {
    font-size: 0.9rem;
    line-height: 1.5;
    color: #bdc3c7;
    margin: 0;
    max-width: 300px;
}

.footer-title {
    font-size: 1.1rem;
    font-weight: 600;
    color: white;
    margin-bottom: 1rem;
}

.footer-links {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.footer-link {
    color: var(--footer-link-colour);
    text-decoration: none;
    font-size: 0.9rem;
    transition: color 0.2s ease;
}

.footer-link:hover {
    color: var(--footer-link-hover-colour);
}

.footer-divider {
    height: 1px;
    margin: 0 2rem;
}

.footer-bottom {
    padding: 1.5rem 2rem;
    background-color: var(--footer-bottom-background-colour);
}

.footer-bottom-content {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 1rem;
}

.copyright {
    font-size: 0.85rem;
    color: var(--footer-bottom-text-colour);
    margin: 0;
}

.data-source {
    display: block;
    font-size: 0.8rem;
    margin-top: 0.25rem;
}

.footer-meta {
    display: flex;
    gap: 1.5rem;
    font-size: 0.8rem;
    color: var(--footer-bottom-text-colour);
}

.version {
    font-size: 0.8rem;
    color: var(--footer-bottom-text-colour);
}
</style>