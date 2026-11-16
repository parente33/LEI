<!-- Usado na HomePage, nas cidades em destaque -->
<template>
  <router-link :to="`/city/${city.slug}`" class="city-card">
    <div class="image-container">
      <img :src="city.image" :alt="`Mapa de ${city.name}`" class="city-image" />
    </div>
    <div class="city-info">
      <h3>{{ city.name }}</h3>
    </div>
  </router-link>
</template>

<script>
export default {
  props: {
    city: Object
  }
}
</script>

<style scoped>
.city-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  border-radius: 1rem;
  overflow: hidden;
  text-decoration: none;
  cursor: pointer;
  position: relative;
}

.image-container {
  width: 100%;
  height: 100%;
  overflow: hidden;
  position: relative;
}

.city-card:hover {
  transform: scale(1.05);
}

.city-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
  filter: blur(2px);
}

.city-card:hover .city-image {
  transform: scale(1.8);
  filter: blur(0);
}

.city-info {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: 0.75rem 1rem;
  text-align: center;
}

.city-info h3 {
  margin: 0;
  color: var(--color-staytistics-name);
  font-size: 1.8rem;
  -webkit-text-stroke: 0.6px var(--color-staytistics-name);
}
</style>