package loader;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


import entidades.Musica;
import app.App;

public class MusicaLoader {
    /**
     * Metodo que carrega musicas para uma App
     * As musicas devem estar separadas por | e conter 10 campos separados por virgula
     * @param app Objeto App 
     * @param m String com as musicas 
     * @param artista String com o artista
     * @return Lista com o nome das musicas adicionadas
     */
    public static List<String> loadMusicas (App app, String m, String artista) {
        List<String> finais = new ArrayList<>();
        String[] musicas = m.split("\\|"); 
        for (String s : musicas) {
            String[] partes = s.split(",");
            if (partes.length < 10) continue;
    
            String nome = partes[0];
            String editora = partes[1];
            String letra = partes[2];
            String album = partes[3];
            List<String> partitura = Arrays.asList(partes[4].split("\\\\"));
            String genero = partes[5];
            int duracao = Integer.parseInt(partes[6]);
            int nReproducoes = Integer.parseInt(partes[7]);
            boolean explicita = Boolean.parseBoolean(partes[8]);
            boolean multimedia = Boolean.parseBoolean(partes[9]);
            Musica musica = new Musica(nome,artista,editora,letra,album,partitura,Musica.toGenero(genero),duracao,nReproducoes,explicita,multimedia);
            if(app.addMusica(musica)) finais.add(nome);
        }
        return finais;
    } 
}