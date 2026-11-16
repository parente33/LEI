import { defineStore } from "pinia";

export const useSimulationStore = defineStore('simulation', {
  // guardar dados
  state: () => {
    return {
      // TODO: declarar startPlayer
      startPlayer: null,
      // TODO: declarar plays
      plays: []
    };
  },
  // mudar dados
  actions: {
    // TODO: implementar setStartPlayer(player)
    setStartPlayer(player) {
      this.startPlayer = player;
    },
    // TODO: implementar addPlay(play)
    addPlay(play) {
      this.plays.push(play);
    }
  },
  // ler dados/fazer operações -> sem nunca alterar o estado
  getters: {
    // TODO: containsSimulation -> true se startPlayer != null e plays não vazio
    containsSimulation: state => state.startPlayer !== null && state.plays.length !== 0
  }
});