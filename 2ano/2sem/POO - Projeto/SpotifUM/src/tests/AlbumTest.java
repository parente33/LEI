package tests;

import entidades.Album;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

public class AlbumTest {

    @Test
    public void testConstrutorVazio() {
        Album a = new Album();
        assertEquals("", a.getNome());
        assertEquals("", a.getArtista());
        assertTrue(a.getMusicas().isEmpty());
    }

    @Test
    public void testConstrutorComParametros() {
        List<String> musicas = Arrays.asList("M1", "M2");
        Album a = new Album("Album X", "Artista Y", musicas);

        assertEquals("Album X", a.getNome());
        assertEquals("Artista Y", a.getArtista());
        assertEquals(musicas, a.getMusicas());
        assertNotSame(musicas, a.getMusicas()); // Garantir deep copy
    }

    @Test
    public void testConstrutorCopia() {
        Album original = new Album("A", "B", Arrays.asList("M1", "M2"));
        Album copia = new Album(original);

        assertEquals(original, copia);
        assertNotSame(original.getMusicas(), copia.getMusicas());
    }

    @Test
    public void testSettersComValoresValidos() {
        Album a = new Album();
        a.setNome("Nome A");
        a.setArtista("Artista B");
        a.setMusicas(Arrays.asList("X", "Y"));

        assertEquals("Nome A", a.getNome());
        assertEquals("Artista B", a.getArtista());
        assertEquals(Arrays.asList("X", "Y"), a.getMusicas());
    }

    @Test
    public void testSettersComNull() {
        Album a = new Album("Nome", "Artista", Arrays.asList("M1"));
        a.setNome(null);
        a.setArtista(null);
        a.setMusicas(null);

        // Os valores não devem mudar
        assertEquals("Nome", a.getNome());
        assertEquals("Artista", a.getArtista());
        assertEquals(Arrays.asList("M1"), a.getMusicas());
    }

    @Test
    public void testClone() {
        Album a = new Album("Clone", "Artista", Arrays.asList("M1", "M2"));
        Album clone = a.clone();

        assertEquals(a, clone);
        assertNotSame(a.getMusicas(), clone.getMusicas());
    }

    @Test
    public void testEquals() {
        Album a1 = new Album("X", "Y", Arrays.asList("A", "B"));
        Album a2 = new Album("X", "Y", Arrays.asList("A", "B"));
        Album a3 = new Album("X", "Z", Arrays.asList("A", "B"));

        assertEquals(a1, a2);
        assertNotEquals(a1, a3);
    }

    @Test
    public void testToStringContemInfo() {
        Album a = new Album("Nome", "Artista", Arrays.asList("M1", "M2"));
        String s = a.toString();

        assertTrue(s.contains("Nome"));
        assertTrue(s.contains("Artista"));
        assertTrue(s.contains("M1"));
        assertTrue(s.contains("M2"));
    }
}
