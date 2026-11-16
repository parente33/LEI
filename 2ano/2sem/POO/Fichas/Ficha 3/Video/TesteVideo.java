import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class TesteVideo {
    
    @Test
    public void testConstrutorParametro() {
        byte[] conteudo = new byte[]{1, 2, 3, 4};
        Date dataCarregamento = new Date();
        Video video = new Video("Meu Video", conteudo, dataCarregamento, "1920x1080", 5, 30);

        assertEquals("Meu Video", video.getNome());
        assertArrayEquals(conteudo, video.getConteudo());
        assertEquals(dataCarregamento, video.getDataCarregamento());
        assertEquals("1920x1080", video.getResolucao());
        assertEquals(5, video.getDuracaoMinutos());
        assertEquals(30, video.getDuracaoSegundos());
    }

    @Test
    public void testConstrutorCopia() {
        byte[] conteudo = new byte[]{1, 2, 3, 4};
        Date dataCarregamento = new Date();
        Video videoOriginal = new Video("Meu Video", conteudo, dataCarregamento, "1920x1080", 5, 30);

        Video videoCopia = videoOriginal.clone();

        assertEquals(videoOriginal.getNome(), videoCopia.getNome());
        assertArrayEquals(videoOriginal.getConteudo(), videoCopia.getConteudo());
        assertEquals(videoOriginal.getDataCarregamento(), videoCopia.getDataCarregamento());
    }

    @Test
    public void testInsereComentario() {
        Video video = new Video();
        video.insereComentario("Ótimo vídeo!");

        assertTrue(video.getComentarios().contains("Ótimo vídeo!"));
    }

    @Test
    public void testQtsDiasDepois() {
        Date dataCarregamento = new Date(System.currentTimeMillis() - 86400000L);  // 1 dia atrás
        Video video = new Video("Meu Video", new byte[]{1, 2, 3}, dataCarregamento, "1920x1080", 5, 30);

        assertEquals(1, video.qtsDiasDepois());
    }

    @Test
    public void testThumbsUp() {
        Video video = new Video();
        video.thumbsUp();
        video.thumbsUp();

        assertEquals(2, video.getLikes());
    }

    @Test
    public void testProcessa() {
        byte[] conteudo = new byte[]{1, 2, 3, 4};
        Date dataCarregamento = new Date();
        Video video = new Video("Meu Video", conteudo, dataCarregamento, "1920x1080", 5, 30);

        String resultado = video.processa();
        assertTrue(resultado.contains("Meu Video"));
        assertTrue(resultado.contains("1920x1080"));
        assertTrue(resultado.contains("5min"));
        assertTrue(resultado.contains("30s"));
    }

    @Test
    public void testToString() {
        byte[] conteudo = new byte[]{1, 2, 3, 4};
        Date dataCarregamento = new Date();
        Video video = new Video("Meu Video", conteudo, dataCarregamento, "1920x1080", 5, 30);

        String resultado = video.toString();
        assertTrue(resultado.contains("Meu Video"));
        assertTrue(resultado.contains("1920x1080"));
        assertTrue(resultado.contains("5m"));
        assertTrue(resultado.contains("30s"));
        assertTrue(resultado.contains("Likes: 0"));
    }

    @Test
    public void testEquals() {
        byte[] conteudo = new byte[]{1, 2, 3, 4};
        Date dataCarregamento = new Date();
        Video video1 = new Video("Meu Video", conteudo, dataCarregamento, "1920x1080", 5, 30);
        Video video2 = new Video("Meu Video", conteudo, dataCarregamento, "1920x1080", 5, 30);

        assertTrue(video1.equals(video2));
    }
}