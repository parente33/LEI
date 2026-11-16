<script>
import { ResultadoJogo } from '../models/resultadoJogo';

export default {
  props: ['jogadorAJogar', 'vencedor'],
  computed: {
    mensagemEstado() {
      if (this.vencedor === null) {
        // Jogo em andamento
        const jogador = this.jogadorAJogar ? 'Vermelho' : 'Amarelo';
        return `Vez do jogador: ${jogador}`;
      } else {
        // Jogo terminado
        switch (this.vencedor) {
          case ResultadoJogo.VERMELHO:
            return 'Jogador Vermelho venceu!';
          case ResultadoJogo.AMARELO:
            return 'Jogador Amarelo venceu!';
          case ResultadoJogo.EMPATE:
            return 'Jogo empatado!';
          default:
            return 'Jogo em andamento';
        }
      }
    },
    corTexto() {
      if (this.vencedor !== null) {
        return 'black'; // Cor fixa quando o jogo termina
      }
      return this.jogadorAJogar ? 'var(--color-red)' : 'var(--color-yellow)';
    }
  }
}
</script>

<template>
  <nav class="navbar">
    <div class="navbar-content">
      <div class="estado-jogo" :style="{ color: corTexto }">
        {{ mensagemEstado }}
      </div>
      <div class="titulo-jogo">
        4 em Linha
      </div>
    </div>
  </nav>
</template>

<style scoped>
.navbar {
  background-color: #f8f9fa;
  padding: 1rem 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid #dee2e6;
}

.navbar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.estado-jogo {
  font-weight: 600;
  font-size: 1.1rem;
  transition: color 0.3s ease;
}

.titulo-jogo {
  font-weight: 700;
  font-size: 1.5rem;
  color: #4357A0;
}

.button {
  border: 1px solid black;
  border-radius: 5px;
  text-align: center;
  padding: 10px;
  font-weight: 600;
  cursor: pointer;
  background-color: white;
  transition: all 0.3s ease;
}

.button:hover {
  background-color: #f0f0f0;
}
</style>