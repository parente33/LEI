package tests;
import entidades.PremiumTop;
import org.junit.Test;
import static org.junit.Assert.*;

public class PremiumTopTest {

    @Test
    public void testGetters() {
        PremiumTop plano = new PremiumTop();
        assertEquals("PremiumTop", plano.getNome());
        assertEquals(0, plano.getPontos());
        assertTrue(plano.isPremium());
        assertEquals(0.025, plano.getPercentagem(), 0.0001);
    }

    @Test
    public void testClone() {
        PremiumTop original = new PremiumTop();
        PremiumTop copia = original.clone();
        assertEquals(original, copia);
        assertNotSame(original, copia); // objetos diferentes
    }

    @Test
    public void testEqualsMesmoObjeto() {
        PremiumTop plano = new PremiumTop();
        assertTrue(plano.equals(plano));
    }

    @Test
    public void testEqualsOutroObjetoIgual() {
        PremiumTop p1 = new PremiumTop();
        PremiumTop p2 = new PremiumTop();
        assertEquals(p1, p2);
    }

    @Test
    public void testEqualsComObjetoNulo() {
        PremiumTop plano = new PremiumTop();
        assertFalse(plano.equals(null));
    }

    @Test
    public void testEqualsComTipoDiferente() {
        PremiumTop plano = new PremiumTop();
        assertFalse(plano.equals("outra coisa"));
    }

    @Test
    public void testToStringContemCampos() {
        PremiumTop plano = new PremiumTop();
        String str = plano.toString();
        assertTrue(str.contains("Nome:"));
        assertTrue(str.contains("Pontos:"));
        assertTrue(str.contains("Premium:"));
    }
}
