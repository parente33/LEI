import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TesteJogoFutebol {

    @Test
    public void testStartGame() {
        JogoFutebol jogo = new JogoFutebol();
        jogo.startGame();

        assertEquals("A_DECORRER", jogo.getEstado());
    }

    @Test
    public void testGoloVisitado() {
        JogoFutebol jogo = new JogoFutebol();
        jogo.startGame();
        jogo.goloVisitado();

        assertEquals("Visitado: 1 - Visitante: 0", jogo.resultadoActual());
    }

    @Test
    public void testGoloVisitante() {
        JogoFutebol jogo = new JogoFutebol();
        jogo.startGame();
        jogo.goloVisitante();

        assertEquals("Visitado: 0 - Visitante: 1", jogo.resultadoActual());
    }

    @Test
    public void testEndGame() {
        JogoFutebol jogo = new JogoFutebol();
        jogo.startGame();
        jogo.goloVisitado();
        jogo.endGame();

        assertEquals("TERMINADO", jogo.getEstado());
        assertEquals("Visitado: 1 - Visitante: 0", jogo.resultadoActual());
    }

    @Test
    public void testNaoPodeAdicionarGoloSeNaoEstiverADecorrer() {
        JogoFutebol jogo = new JogoFutebol();
        jogo.goloVisitado(); // Não está a decorrer, deve dar erro
        jogo.startGame();
        jogo.endGame();
        jogo.goloVisitante(); // Não pode adicionar golos após o fim do jogo

        assertEquals("Visitado: 0 - Visitante: 0", jogo.resultadoActual());
    }

    @Test
    public void testNaoPodeIniciarSeJaEstiverADecorrer() {
        JogoFutebol jogo = new JogoFutebol();
        jogo.startGame();
        jogo.startGame(); // Tentativa de iniciar o jogo novamente
        assertEquals("A_DECORRER", jogo.getEstado());
    }

    @Test
    public void testNaoPodeTerminarSeNaoEstiverADecorrer() {
        JogoFutebol jogo = new JogoFutebol();
        jogo.endGame(); // Tentativa de terminar o jogo sem ter iniciado
        assertEquals("POR_INICIAR", jogo.getEstado());
    }
}