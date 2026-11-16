public class JogoFutebol {
    // Enum para representar o estado do jogo
    private enum Estado {
        POR_INICIAR, A_DECORRER, TERMINADO
    }

    // Variáveis de instância
    private Estado estado;
    private int golosVisitado;
    private int golosVisitante;

    /** Construtor da classe */
    public JogoFutebol() {
        this.estado = Estado.POR_INICIAR; // O jogo começa no estado "Por Iniciar"
        this.golosVisitado = 0;
        this.golosVisitante = 0;
    }

    /** Método que dá início ao jogo */
    public void startGame() {
        if (estado == Estado.POR_INICIAR) {
            estado = Estado.A_DECORRER; // Muda o estado para "A Decorrer"
        } else {
            System.out.println("Não é possível iniciar o jogo. O jogo já está a decorrer ou já terminou.");
        }
    }

    /** Método que termina o jogo */
    public void endGame() {
        if (estado == Estado.A_DECORRER) {
            estado = Estado.TERMINADO; // Muda o estado para "Terminado"
        } else {
            System.out.println("Não é possível terminar o jogo. O jogo não está a decorrer.");
        }
    }

    /** Método que adiciona um golo à equipa visitada */
    public void goloVisitado() {
        if (estado == Estado.A_DECORRER) {
            golosVisitado++;
        } else {
            System.out.println("Não é possível adicionar um golo. O jogo não está a decorrer.");
        }
    }

    /** Método que adiciona um golo à equipa visitante */
    public void goloVisitante() {
        if (estado == Estado.A_DECORRER) {
            golosVisitante++;
        } else {
            System.out.println("Não é possível adicionar um golo. O jogo não está a decorrer.");
        }
    }

    /** Método que devolve o resultado actual do jogo */
    public String resultadoActual() {
        return "Visitado: " + golosVisitado + " - Visitante: " + golosVisitante;
    }

    /** Método que devolve o estado actual do jogo */
    public String getEstado() {
        return estado.name();
    }
}