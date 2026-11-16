package tests;
import entidades.Playlist;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.List;

public class PlaylistTest {

    @Test
    public void testConstrutorVazio() {
        Playlist p = new Playlist();
        assertEquals("", p.getNome());
        assertTrue(p.getMusicas().isEmpty());
        assertFalse(p.isPublica());
    }

    @Test
    public void testConstrutorComParametros() {
        List<String> musicas = Arrays.asList("Musica1", "Musica2");
        Playlist p = new Playlist("Minha Playlist", musicas, true);

        assertEquals("Minha Playlist", p.getNome());
        assertEquals(musicas, p.getMusicas());
        assertTrue(p.isPublica());
    }

    @Test
    public void testAddERemMusica() {
        Playlist p = new Playlist();
        p.addMusica("Musica1");
        assertTrue(p.getMusicas().contains("Musica1"));

        boolean removida = p.remMusica("Musica1");
        assertTrue(removida);
        assertFalse(p.getMusicas().contains("Musica1"));
    }

    @Test
    public void testPrivacidade() {
        Playlist p = new Playlist();
        assertFalse(p.isPublica());

        p.setPrivacidade(true);
        assertTrue(p.isPublica());
    }

    @Test
    public void testClone() {
        Playlist original = new Playlist("Rock", Arrays.asList("A", "B"), true);
        Playlist clone = original.clone();

        assertEquals(original, clone);
        assertNotSame(original, clone); // objetos diferentes
    }

    @Test
    public void testEquals() {
        Playlist p1 = new Playlist("Igual", Arrays.asList("X", "Y"), true);
        Playlist p2 = new Playlist("Igual", Arrays.asList("X", "Y"), false); // `publica` não interfere em equals

        assertEquals(p1, p2);
    }
}

