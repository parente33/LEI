import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TesteCarro {

    @Test
    public void testLigarEDesligarCarro() {
        Carro carro = new Carro("Marca A", "Modelo X", 2020, 5.0, 0.1);
        carro.ligaCarro();
        assertTrue(carro.isLigado());
        
        carro.desligaCarro();
        assertFalse(carro.isLigado());
    }

    @Test
    public void testAvancarCarro() {
        Carro carro = new Carro("Marca A", "Modelo X", 2020, 5.0, 0.1);
        carro.ligaCarro();
        carro.avancaCarro(1000, 100); // Avança 1 km a 100 km/h

        assertEquals(1.0, carro.getKmsTotais());
        assertEquals(1.0, carro.getKmsUltimaViagem());
    }

    @Test
    public void testTravarCarro() {
        Carro carro = new Carro("Marca A", "Modelo X", 2020, 5.0, 0.1);
        carro.ligaCarro();
        carro.travaCarro(1000); // Trava 1 km

        assertEquals(1.0, carro.getKmsTotais());
        assertEquals(1.0, carro.getKmsUltimaViagem());
    }

    @Test
    public void testResetUltimaViagem() {
        Carro carro = new Carro("Marca A", "Modelo X", 2020, 5.0, 0.1);
        carro.ligaCarro();
        carro.avancaCarro(1000, 100);
        carro.resetUltimaViagem();
        
        assertEquals(0.0, carro.getKmsUltimaViagem());
        assertEquals(0.0, carro.getMediaConsumoUltimaViagem());
    }

    @Test
    public void testNaoPodeAvancarSeDesligado() {
        Carro carro = new Carro("Marca A", "Modelo X", 2020, 5.0, 0.1);
        carro.avancaCarro(1000, 100); // Não pode avançar se estiver desligado
        
        assertEquals(0.0, carro.getKmsTotais());
    }

    @Test
    public void testNaoPodeTravarSeDesligado() {
        Carro carro = new Carro("Marca A", "Modelo X", 2020, 5.0, 0.1);
        carro.travaCarro(1000); // Não pode travar se estiver desligado
        
        assertEquals(0.0, carro.getKmsTotais());
    }
}