package tests;
import entidades.Utilizador;
import entidades.Free;
import entidades.Historico;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class UtilizadorTest {

    @Test
    public void testConstrutorDefault() {
        Utilizador u = new Utilizador();
        assertEquals("", u.getNome());
        assertEquals("", u.getEmail());
        assertEquals("", u.getMorada());
        assertTrue(u.getPlano() instanceof Free);
        assertEquals(0.0, u.getPontos(), 0.0001);
        assertTrue(u.getPlaylists().isEmpty());
        assertTrue(u.getAlbuns().isEmpty());
        assertTrue(u.getHistorico().isEmpty());
    }

    @Test
    public void testAddRemPlaylist() {
        Utilizador u = new Utilizador();
        assertTrue(u.addPlaylist("Rock"));
        assertFalse(u.addPlaylist("Rock")); // já existe
        assertTrue(u.remPlaylist("Rock"));
        assertFalse(u.remPlaylist("Rock")); // já foi removido
    }

    @Test
    public void testAddRemAlbum() {
        Utilizador u = new Utilizador();
        assertTrue(u.addAlbum("Album1"));
        assertFalse(u.addAlbum("Album1"));
        assertTrue(u.remAlbum("Album1"));
        assertFalse(u.remAlbum("Album1"));
    }

    @Test
    public void testAddPontos() {
        Utilizador u = new Utilizador();
        u.addPontos(5.5);
        assertEquals(5.5, u.getPontos(), 0.0001);
    }

    @Test
    public void testHistorico() {
        Utilizador u = new Utilizador();
        Historico h = new Historico("Musica1");
        u.addHistorico(h);
        List<Historico> hist = u.getHistorico();
        assertEquals(1, hist.size());
        assertEquals(h, hist.get(0));
    }

    @Test
    public void testCloneEquals() {
        Utilizador u1 = new Utilizador();
        u1.setNome("Ana");
        u1.setEmail("ana@mail.com");
        u1.setMorada("Rua 1");
        u1.addPlaylist("Pop");
        u1.addAlbum("AlbumX");
        u1.addPontos(10);
        u1.addHistorico("Música1");

        Utilizador u2 = u1.clone();

        assertEquals(u1, u2);
        assertNotSame(u1, u2); // objetos diferentes
        assertEquals(u1.toString(), u2.toString());
    }

    @Test
    public void testSettersEGetters() {
        Utilizador u = new Utilizador();
        u.setNome("Carlos");
        u.setEmail("carlos@mail.com");
        u.setMorada("Rua 2");
        u.setPontos(7.0);
        u.setPlano(new Free());
        u.setPlaylists(Arrays.asList("P1", "P2"));
        u.setAlbuns(Arrays.asList("A1", "A2"));
        u.setHistorico(Arrays.asList(new Historico("X")));

        assertEquals("Carlos", u.getNome());
        assertEquals("carlos@mail.com", u.getEmail());
        assertEquals("Rua 2", u.getMorada());
        assertEquals(7.0, u.getPontos(), 0.0001);
        assertEquals(Arrays.asList("P1", "P2"), u.getPlaylists());
        assertEquals(Arrays.asList("A1", "A2"), u.getAlbuns());
    }
}
