import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TesteLampada {

    @Test
    public void testLampadaLigada() {
        Lampada lampada = new Lampada(0.01); // Consumo de 0.01 por ms
        lampada.lampON();

        assertEquals("LIGADA", lampada.getEstado());
    }

    @Test
    public void testLampadaDesligada() {
        Lampada lampada = new Lampada(0.01); // Consumo de 0.01 por ms
        lampada.lampOFF();

        assertEquals("DESLIGADA", lampada.getEstado());
    }

    @Test
    public void testLampadaModoEco() {
        Lampada lampada = new Lampada(0.01); // Consumo de 0.01 por ms
        lampada.lampECO();

        assertEquals("ECO", lampada.getEstado());
    }

    @Test
    public void testConsumoTotal() {
        Lampada lampada = new Lampada(0.01);
        lampada.lampON();
        lampada.passarTempo(1000); // 1 segundo (1000 ms)

        assertEquals(10, lampada.totalConsumo(), 0.001); // 0.01 * 1000 ms = 10 unidades de energia
    }

    @Test
    public void testConsumoPeriodo() {
        Lampada lampada = new Lampada(0.01);
        lampada.lampON();
        lampada.passarTempo(500); // 0.01 * 500 ms = 5 unidades de energia

        assertEquals(5, lampada.periodoConsumo(), 0.001);
    }

    @Test
    public void testModoEcoConsumo() {
        Lampada lampada = new Lampada(0.02); // Consumo de 0.02 por ms
        lampada.lampECO();
        lampada.passarTempo(1000); // 1 segundo (1000 ms)

        assertEquals(10, lampada.totalConsumo(), 0.001); // Modo ECO consome metade do valor, 0.02 / 2 * 1000 ms = 10
    }

    @Test
    public void testResetConsumo() {
        Lampada lampada = new Lampada(0.01);
        lampada.lampON();
        lampada.passarTempo(500); // 0.01 * 500 ms = 5 unidades de energia
        lampada.resetConsumo();

        assertEquals(0, lampada.periodoConsumo(), 0.001);
    }
}