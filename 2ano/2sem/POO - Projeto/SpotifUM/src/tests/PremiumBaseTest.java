package tests;
import entidades.PremiumBase;
import org.junit.Test;
import static org.junit.Assert.*;

public class PremiumBaseTest {

    @Test
    public void testGetters() {
        PremiumBase plano = new PremiumBase();
        assertEquals("PremiumBase", plano.getNome());
        assertEquals(10, plano.getPontos());
        assertTrue(plano.isPremium());
        assertEquals(0.0, plano.getPercentagem(), 0.0001);
    }

    @Test
    public void testClone() {
        PremiumBase original = new PremiumBase();
        PremiumBase copia = original.clone();
        assertEquals(original, copia);
        assertNotSame(original, copia);
    }

    @Test
    public void testEqualsMesmoObjeto() {
        PremiumBase plano = new PremiumBase();
        assertTrue(plano.equals(plano));
    }

    @Test
    public void testEqualsOutroObjetoIgual() {
        PremiumBase p1 = new PremiumBase();
        PremiumBase p2 = new PremiumBase();
        assertEquals(p1, p2);
    }

    @Test
    public void testEqualsComObjetoNulo() {
        PremiumBase plano = new PremiumBase();
        assertFalse(plano.equals(null));
    }

    @Test
    public void testEqualsComTipoDiferente() {
        PremiumBase plano = new PremiumBase();
        assertFalse(plano.equals("string"));
    }

    @Test
    public void testToStringContemCampos() {
        PremiumBase plano = new PremiumBase();
        String str = plano.toString();
        assertTrue(str.contains("Nome:"));
        assertTrue(str.contains("Pontos:"));
        assertTrue(str.contains("Premium:"));
    }

    
}
