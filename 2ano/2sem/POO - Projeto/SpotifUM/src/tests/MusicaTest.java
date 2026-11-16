package tests;

import entidades.Musica;
import entidades.Musica.Genero;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;


public class MusicaTest {

    @Test
    public void testConstrutorDefault() {
        Musica m = new Musica();
        assertEquals("", m.getNome());
        assertEquals("", m.getInterprete());
        assertEquals("", m.getEditora());
        assertEquals("", m.getLetra());
        assertEquals("", m.getAlbum());
        assertNotNull(m.getPartitura());
        assertEquals(0, m.getDuracao());
        assertEquals(0, m.getReproducoes());
        assertFalse(m.isExplicita());
        assertFalse(m.isMultimedia());
    }

    @Test
    public void testConstrutorParametrizado() {
        List<String> partitura = Arrays.asList("A", "B", "C");
        Musica m = new Musica("Nome", "Intérprete", "Editora", "Letra", "Álbum", partitura, Genero.ROCK, 180, 10, true, true);

        assertEquals("Nome", m.getNome());
        assertEquals("Intérprete", m.getInterprete());
        assertEquals("Editora", m.getEditora());
        assertEquals("Letra", m.getLetra());
        assertEquals("Álbum", m.getAlbum());
        assertEquals(partitura, m.getPartitura());
        assertEquals(Genero.ROCK, m.getGenero());
        assertEquals(180, m.getDuracao());
        assertEquals(10, m.getReproducoes());
        assertTrue(m.isExplicita());
        assertTrue(m.isMultimedia());
    }

    @Test
    public void testSettersAndGetters() {
        Musica m = new Musica();
        m.setNome("Nova Música");
        assertEquals("Nova Música", m.getNome());

        m.setInterprete("Novo Artista");
        assertEquals("Novo Artista", m.getInterprete());

        m.setEditora("Nova Editora");
        assertEquals("Nova Editora", m.getEditora());

        m.setLetra("La la la");
        assertEquals("La la la", m.getLetra());

        m.setAlbum("Álbum 2024");
        assertEquals("Álbum 2024", m.getAlbum());

        List<String> novaPartitura = Arrays.asList("D", "E", "F");
        m.setPartitura(novaPartitura);
        assertEquals(novaPartitura, m.getPartitura());

        m.setGenero(Genero.JAZZ);
        assertEquals(Genero.JAZZ, m.getGenero());

        m.setDuracao(200);
        assertEquals(200, m.getDuracao());

        m.setExplicita(true);
        assertTrue(m.isExplicita());

        m.setMultimedia(true);
        assertTrue(m.isMultimedia());
    }

    @Test
    public void testCloneAndEquals() {
        List<String> partitura = Arrays.asList("X", "Y", "Z");
        Musica m1 = new Musica("Nome", "Intérprete", "Editora", "Letra", "Álbum", partitura, Genero.POP, 150, 5, false, true);
        Musica m2 = m1.clone();

        assertEquals(m1, m2);
        assertNotSame(m1, m2);
    }

    @Test
    public void testAddReproducao() {
        Musica m = new Musica();
        assertEquals(0, m.getReproducoes());
        m.addReproducao();
        assertEquals(1, m.getReproducoes());
    }

    @Test
    public void testToGenero() {
        assertEquals(Genero.FUNK, Musica.toGenero("funk"));
        assertEquals(Genero.RAP, Musica.toGenero("RAP"));
        assertNull(Musica.toGenero("clássica")); // não existe
        assertNull(Musica.toGenero(null));
    }

    @Test
    public void testToString() {
        Musica m = new Musica("X", "Y", "Z", "letra", "alb", List.of(), Genero.ELETRONIC, 123, 3, false, false);
        String s = m.toString();
        assertTrue(s.contains("Nome: X"));
        assertTrue(s.contains("Genero: ELETRONIC"));
    }
}
