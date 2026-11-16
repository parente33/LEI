<template>
    <main class="about-container">
        <!-- Secção About Us -->
        <section class="section section-light">
            <div class="section-content">
                <h1 class="section-title">About Us</h1>
                <p class="section-text">
                    Staytistics: dados do InsideAirbnb, insights claros. Visualize, compreenda e decida com confiança.
                </p>
            </div>
        </section>

        <!-- Secção Fundadores -->
        <section class="section section-dark">
            <div class="section-content">
                <h2 class="section-title">Conheça os nossos fundadores!</h2>
                <div class="founders-grid">
                    <div class="founder-card">
                        <div class="founder-initials">D</div>
                        <h3 class="founder-name">Daniel</h3>
                        <div class="founder-expertise">
                            <span class="expertise-tag">Backend</span>
                            <span class="expertise-tag">Design</span>
                            <span class="expertise-tag">Data</span>
                        </div>
                        <p class="founder-bio">Combina clean designs com arquiteturas robustas para criar experiências fluídas.</p>
                    </div>
                    <div class="founder-card">
                        <div class="founder-initials">P</div>
                        <h3 class="founder-name">Pedro</h3>
                        <div class="founder-expertise">
                            <span class="expertise-tag">Frontend</span>
                            <span class="expertise-tag">DataViz</span>
                            <span class="expertise-tag">UX</span>
                        </div>
                        <p class="founder-bio">Transforma dados complexos em visualizações intuitivas.</p>
                    </div>
                </div>
            </div>
        </section>

        <!-- Secção História -->
         <section class="section section-light">
            <div class="section-content">
                <h2 class="section-title">A nossa história</h2>
                <p class="section-text">
                    A Staytistics nasceu em outubro de 2025 com um objetivo simples: transformar os dados complexos da InsideAirbnb em informações claras e úteis.
                    Desde então, temos ajudado viajantes e curiosos a descobrir padrões de preços, tendências de ocupação e oportunidades únicas,
                    tornando a escolha do alojamento no estrangeiro mais fácil e informada.</p>
            </div>
        </section>

        <!-- Secção Estatísticas -->
        <section class="section section-dark">
            <div class="section-content">
                <h2 class="section-title">Números que contam</h2>
                <div class="stats-grid">
                    <div class="stat-item">
                        <div class="stat-number">+30</div>
                        <div class="stat-label">cidades em 6 continentes</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number">+1.5M</div>
                        <div class="stat-label">estadias registadas</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number">+5</div>
                        <div class="stat-label">anos de dados históricos</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number">100%</div>
                        <div class="stat-label">dados gratuitos e abertos</div>
                    </div>
                </div>
            </div>
        </section>

         <!-- Secção Contactos -->
          <section class="section section-light">
            <div class="section-content">
                <h2 class="section-title">Como nos pode contactar</h2>
                <div class="contacts-list">
                    <div class="contact-item">
                        <h3 class="contact-type">Questões gerais</h3>
                        <p class="contact-detail">emailgenerico@dominiogenerico.tldgenerico</p>
                    </div>
                    <div class="contact-item">
                        <h3 class="contact-type">Assuntos de media</h3>
                        <p class="contact-detail">businessgenerico@dominiogenerico.tldgenerico</p>
                    </div>
                </div>
            </div>
          </section>
    </main>
</template>

<script setup>

</script>

<style scoped>
.about-container {
    min-height: 100vh;
}

.section {
    padding: 3rem 4rem;
    display: flex;
    justify-content: center;
}

.section-light {
    background-color: var(--color-about-us-light);
    color: #333333;
}

.section-dark {
    background-color: var(--color-about-us-dark);
    color: #333333;
}

.section-content {
    max-width: 1200px;
    width: 100%;
}

.section-title {
    margin-bottom: 3.5rem;
    text-align: center;
    font-size: 2.5rem;
    font-weight: 600;
    color: var(--color-staytistics-name);
    line-height: 1.2;
}

.section-text {
    font-size: 1.2rem;
    line-height: 1.8;
    text-align: center;
    margin: 0 auto;
}

.founders-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 2rem;
}

.founder-card {
    text-align: center;
    padding: 2rem;
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease;
}

.founder-card:hover {
    transform: translateY(-7px);
}

.founder-name {
    font-size: 1.5rem;
    font-weight: 600;
    color: var(--color-staytistics-name);
    margin-bottom: 1rem;
}

.founder-bio {
    font-size: 1rem;
    line-height: 1.6;
    color: #666;
}

.founder-initials {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    background: linear-gradient(135deg, var(--color-staytistics-name), #2196F3);
    color: white;
    font-size: 2.5rem;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 1.5rem;
}

.founder-expertise {
    display: flex;
    gap: 0.5rem;
    justify-content: center;
    margin-bottom: 1rem;
    flex-wrap: wrap;
}

.expertise-tag {
    background: rgba(81, 173, 84, 0.349);
    color: var(--color-staytistics-name);
    padding: 0.25rem 0.75rem;
    border-radius: 20px;
    font-size: 0.85rem;
    font-weight: 500;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 3rem;
    margin-top: 2rem;
}

.stat-item {
    text-align: center;
    padding: 2rem 1rem;
    position: relative;
}

.stat-number {
    font-size: 3.5rem;
    font-weight: 800;
    color: var(--color-staytistics-name);
    line-height: 1;
    margin-bottom: 0.5rem;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stat-label {
    font-size: 1.2rem;
    color: #555;
    font-weight: 500;
    line-height: 1.4;
    max-width: 200px;
    margin: 0 auto;
}

.stat-item::before {
    content: '';
    position: absolute;
    top: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 60px;
    height: 3px;
    background: var(--color-staytistics-name);
    border-radius: 2px;
}

.stat-item:hover .stat-number {
    transform: scale(1.05);
    transition: transform 0.3s ease;
}

.contacts-list {
    display: flex;
    flex-direction: column;
    gap: 2rem;
    max-width: 600px;
    margin: 0 auto;
}

.contact-item {
    text-align: center;
    padding: 1.5rem;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.contact-type {
    font-size: 1.2rem;
    font-weight: 600;
    color: var(--color-staytistics-name);
    margin-bottom: 1.5rem;
    margin-top: 1rem;
}

.contact-detail {
    font-size: 1.1rem;
    color: #333;
    margin: 0;
}
</style>