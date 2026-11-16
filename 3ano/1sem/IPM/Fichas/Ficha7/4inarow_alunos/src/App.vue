<!-- Este documento apresenta uma solução em construção. É fornecido como 
     material de estudo. Recomenda-se que o revejam e melhorem conforme   
     forem adquirindo novos conhecimentos. -->

<script>
import OJogo from './components/OJogo.vue';
import Navbar from './components/Navbar.vue';
import { Jogo } from './models/jogo';
import { ResultadoJogo } from './models/resultadoJogo';

export default {
  components: {
    Navbar,
    OJogo      // equivalente a: 'OJogo': OJogo, permite também usar <o-jogo>
  },
  data() {
    return {
      jogadorAJogar: false, // false = amarelo, true = vermelho
      vencedor: null,       // null = jogo em curso
      jogo: new Jogo()
    }
  },
  mounted() {
    // Inicializa o estado com o jogador atual do jogo
    this.jogadorAJogar = this.jogo.jogadorAJogar;
  },
  methods: { 
    onJogadorAlterado(jogador) {
      console.log('Jogador alterado:', jogador);
      this.jogadorAJogar = jogador;
    },
    onJogoTerminou(vencedor) {
      console.log('Jogo terminou. Vencedor:', vencedor);
      this.vencedor = vencedor;
    },
    onReset(jogadorInicial) {
      console.log('Jogo reiniciado. Jogador inicial:', jogadorInicial);
      this.jogadorAJogar = jogadorInicial;
      this.vencedor = null;
    }
  }
}
</script>

<template>
  <Navbar 
    :jogadorAJogar="jogadorAJogar" 
    :vencedor="vencedor" 
  />
  <OJogo 
    :jogo="jogo"
    @jogador-alterado="onJogadorAlterado"
    @jogo-terminou="onJogoTerminou"
    @reset="onReset"
  />
</template>