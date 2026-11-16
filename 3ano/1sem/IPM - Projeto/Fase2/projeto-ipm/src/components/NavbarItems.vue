<!-- Butões para a Navbar -->
<template>
  <ul class="navbar-items">
    <RouterLink
      class="navbar-items-pill"
      v-for="link in props.links"
      :key="link.url"
      :to="link.url">
      <li>
        {{ link.name }}
      </li>
    </RouterLink>
  </ul>
</template>

<script setup lang="ts">
const props = defineProps<{
  links: {
    name: string;
    url: string;
  }[];
}>();
</script>

<style scoped>
.navbar-items {
  display: flex;
  gap: 0.5em;
}

.navbar-items-pill {
  list-style-type: none;
  text-decoration: none;

  position: relative;
  padding: 0.5em 1em;
  border-radius: 0.5em;

  color: var(--color-navbar-items);
  font-size: 1.2rem;

  transition:
      background-color 0.1s,
      color 0.1s;

  display: flex;
  align-items: center;
  justify-content: center;
}

.navbar-items-pill.router-link-active {
  color: var(--color-staytistics-name);
  background-color: #fff;
  text-shadow: 0px 0px 1px #000;
}

.navbar-items-pill:hover:not(.router-link.active) {
  color: #fff;
  background-color: var(--color-navbar-items);
}
</style>