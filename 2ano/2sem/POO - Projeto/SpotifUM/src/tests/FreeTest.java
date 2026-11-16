package tests;

import entidades.Free;
import org.junit.Test;
import static org.junit.Assert.*;


public class FreeTest {

    @Test
    public void testValoresPadrao() {
        Free plano = new Free();

        assertEquals("Free", plano.getNome());
        assertEquals(5, plano.getPontos());
        assertFalse(plano.isPremium());
        assertEquals(0.0, plano.getPercentagem(), 0.0001);
    }

    @Test
    public void testClone() {
        Free original = new Free();
        Free copia = original.clone();

        assertEquals(original, copia);
        assertNotSame(original, copia);
    }

    @Test
    public void testEqualsMesmaInstancia() {
        Free plano = new Free();
        assertEquals(plano, plano); // Mesma instância
    }

    @Test
    public void testEqualsObjetosIguais() {
        Free p1 = new Free();
        Free p2 = new Free();

        assertEquals(p1, p2); // Mesmos valores
    }

    @Test
    public void testEqualsComNull() {
        Free plano = new Free();
        assertNotEquals(null, plano);
    }

    @Test
    public void testEqualsComOutroTipo() {
        Free plano = new Free();
        assertNotEquals("Free", plano); // Comparação com String
    }

    @Test
    public void testToStringContemInfo() {
        Free plano = new Free();
        String s = plano.toString();

        assertTrue(s.contains("Nome: Free"));
        assertTrue(s.contains("Pontos :5"));
        assertTrue(s.contains("Premium :false"));
    }
}
