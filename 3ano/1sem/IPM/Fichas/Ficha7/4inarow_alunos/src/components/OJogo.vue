<script>
import { Jogo } from "../models/jogo";
import { ResultadoJogada } from "../models/resultadoJogada";
import { ResultadoJogo } from "../models/resultadoJogo";
import Button from "./Button.vue";

export default {
  components: {
    Button
  },
  props: {
    jogo: {
      type: Jogo,
      required: true
    }
  },
  data() {
    return {
      
    }
  },
  computed: {
    numColunas() { return this.jogo ? Jogo.NUMERO_COLUNAS : 7; },
    numLinhas() { return this.jogo ? Jogo.NUMERO_LINHAS : 6; }
  },
  methods: {
    jogar(coluna) {
      if (this.jogo.terminado) {
        console.log('Jogo já terminou');
        return;
      }

      const jogadorAnterior = !this.jogo.jogadorAJogar;

      // Executa a jogada
      const resultado = this.jogo.jogar(coluna);
      
      // Processa o resultado da jogada
      switch (resultado) {
        case ResultadoJogada.SUCESSO:
          console.log('Jogada realizada com sucesso');
          this.$emit('jogadorAlterado', this.jogo.jogadorAJogar);
          break;
        case ResultadoJogada.TERMINOU:
          console.log('Jogada terminou o jogo');
          this.$emit('jogoTerminou', this.jogo.vencedor);
          break;
        case ResultadoJogada.ERRO_COLUNACHEIA:
          console.log('Coluna cheia');
          break;
        case ResultadoJogada.ERRO_COLUNAINVALIDA:
          console.log('Coluna inválida');
          break;
        case ResultadoJogada.ERRO_JOGOTERMINADO:
          console.log('Jogo já terminado');
          break;
      }

      // Força a atualização da view
      this.$forceUpdate();
    },
    reset() {
      const jogadorInicial = this.jogo.jogadorAJogar;

      this.jogo.reset();

      this.$emit('reset', this.jogo.jogadorAJogar);
      this.$emit('jogadorAlterado', this.jogo.jogadorAJogar);

      this.$forceUpdate();
    },
    aplicarVermelho(coluna, linha) {
      const celula = this.jogo.getCelula(coluna, linha);
      return celula === true; // true representa o jogador vermelho
    },
    aplicarAmarelo(coluna, linha) {
      const celula = this.jogo.getCelula(coluna, linha);
      return celula === false; // false representa o jogador amarelo
    },
    obterClasseCelula(coluna, linha) {
      // Retorna as classes CSS apropriadas para a célula
      const classes = ['celula'];
      
      if (this.aplicarVermelho(coluna, linha)) {
        classes.push('red');
      } else if (this.aplicarAmarelo(coluna, linha)) {
        classes.push('yellow');
      }
      
      return classes;
    }
  }
}
</script>

<template>
  <div class="tabuleiro">
    <div 
      v-for="c in numColunas" 
      :key="c" 
      class="coluna" 
      @click="jogar(c-1)"
    >
      <div 
        v-for="l in numLinhas" 
        :key="l"
        :class="obterClasseCelula(c-1, l-1)"
      >
      </div>
    </div>
  </div>
  <div class="button-container">
    <Button @click="reset()">Reset</Button>
  </div>
</template>

<style scoped>
.tabuleiro {
  display: flex;
  width: 40%;
  margin: 20px auto auto;
  background-color: #4357A0;
  padding: 10px;
  border-radius: 10px;
}

.coluna {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  cursor: pointer;
}

.coluna:hover {
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 5px;
}

.celula {
  border-radius: 100%;
  background-color: #001773;
  max-height: 100%;
  max-width: 100%;
  aspect-ratio: 1;
  margin: 4px;
}

.yellow {
  background-color: var(--color-yellow);
}

.red {
  background-color: var(--color-red);
}

.button-container {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}
</style>