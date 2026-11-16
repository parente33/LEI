package tests;

import entidades.Historico;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;


public class HistoricoTest {

    @Test
    public void testConstrutores() {
        // Default
        Historico h1 = new Historico();
        assertNotNull(h1.getMusica());
        assertNotNull(h1.getDataHora());

        // Com nome
        Historico h2 = new Historico("Música A");
        assertEquals("Música A", h2.getMusica());

        // Com nome e data
        LocalDateTime now = LocalDateTime.of(2024, 5, 15, 14, 30);
        Historico h3 = new Historico("Música B", now);
        assertEquals("Música B", h3.getMusica());
        assertEquals(now, h3.getDataHora());

        // Cópia
        Historico h4 = new Historico(h3);
        assertEquals(h3, h4);
        assertNotSame(h3, h4);
    }

    @Test
    public void testSettersAndGetters() {
        Historico h = new Historico();
        h.setMusica("Nova Música");
        assertEquals("Nova Música", h.getMusica());

        LocalDateTime dt = LocalDateTime.of(2023, 12, 25, 20, 0);
        h.setDataHora(dt);
        assertEquals(dt, h.getDataHora());
    }

    @Test
    public void testCloneAndEquals() {
        Historico h1 = new Historico("Clone Test", LocalDateTime.now());
        Historico h2 = h1.clone();

        assertEquals(h1, h2);
        assertNotSame(h1, h2);
    }

    @Test
    public void testToString() {
        LocalDateTime dt = LocalDateTime.of(2023, 1, 1, 10, 0);
        Historico h = new Historico("Teste", dt);
        String str = h.toString();
        assertTrue(str.contains("Teste"));
        assertTrue(str.contains("01/01/2023"));
    }
}
