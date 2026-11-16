package tests;

import entidades.Artista;
import org.junit.Test;
import static org.junit.Assert.*;


public class ArtistaTest {

    @Test
    public void testConstrutorVazio() {
        Artista a = new Artista();
        assertEquals("", a.getNome());
        assertFalse(a.getIsGroup());
        assertEquals(0, a.getnReproducoes());
    }

    @Test
    public void testConstrutorComParametros() {
        Artista a = new Artista("Nome", true, 100);
        assertEquals("Nome", a.getNome());
        assertTrue(a.getIsGroup());
        assertEquals(100, a.getnReproducoes());
    }

    @Test
    public void testConstrutorCopia() {
        Artista original = new Artista("X", false, 50);
        Artista copia = new Artista(original);

        assertEquals(original, copia);
        assertNotSame(original, copia);
    }

    @Test
    public void testSettersComValoresValidos() {
        Artista a = new Artista();
        a.setNome("Novo Nome");
        a.setIsGroup(true);
        a.setnReproducoes(42);

        assertEquals("Novo Nome", a.getNome());
        assertTrue(a.getIsGroup());
        assertEquals(42, a.getnReproducoes());
    }

    @Test
    public void testSetterNomeComNull() {
        Artista a = new Artista("Original", false, 0);
        a.setNome(null);
        assertEquals("Original", a.getNome()); // Nome não deve mudar
    }

    @Test
    public void testAddReproducao() {
        Artista a = new Artista("Teste", false, 2);
        a.addReproducao();
        assertEquals(3, a.getnReproducoes());
    }

    @Test
    public void testEquals() {
        Artista a1 = new Artista("Nome", true, 10);
        Artista a2 = new Artista("Nome", true, 10);
        Artista a3 = new Artista("Outro", false, 5);

        assertEquals(a1, a2);
        assertNotEquals(a1, a3);
    }

    @Test
    public void testClone() {
        Artista original = new Artista("Clone", false, 7);
        Artista copia = original.clone();

        assertEquals(original, copia);
        assertNotSame(original, copia);
    }

    @Test
    public void testToStringContemInfo() {
        Artista a = new Artista("Nome", true, 5);
        String s = a.toString();

        assertTrue(s.contains("Nome"));
        assertTrue(s.contains("true"));
        assertTrue(s.contains("5"));
    }
}
