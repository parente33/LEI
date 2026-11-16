<!-- Barra de Navegação -->
<template>
  <nav class="navbar">
    <AppIcon />
    <NavbarItems :links="items" />
  </nav>
</template>

<script setup>
import AppIcon from "./AppIcon.vue";
import NavbarItems from "./NavbarItems.vue";

const items = [
      {
        name: "Exportar Dados",
        url: "/export"
      },
      {
        name: "FAQ",
        url: "/faq"
      },
      {
        name: "About Us",
        url: "/about"
      }
];
</script>

<style scoped>
.navbar {
  height: 4em;
  width: 100%;
  display: flex;
  justify-content: space-between;

  background-color: #fff;
  padding: 1rem 2rem;
  box-shadow: 0 2px 4px var(--color-staytistics-name);

  position: sticky;
  top: 0;
  z-index: 1001;
}

.navbar-items {
  flex-grow: 1;
}
</style>