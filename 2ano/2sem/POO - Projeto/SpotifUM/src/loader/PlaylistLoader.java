package loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;

import entidades.Playlist;
import app.App;

public class PlaylistLoader {

    /**
     * Metodo que vai ate ao ficheiro playlist.csv e separa as playlists
     * @param app objeto App
     * @param dir_path Caminho até à diretoria com o ficheiro
     * @return Lista com as playlists
     * @throws IOException 
     */
    private static List<Playlist> parserPlaylists(App app, String dir_path) throws IOException {
        List<Playlist> playlists = new ArrayList<>();
        Path fullPath = Paths.get(dir_path).resolve("playlists.csv");
        BufferedReader br = new BufferedReader(new FileReader(fullPath.toString()));
        String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length < 3) continue;
    
                String nomePlaylist = partes[0];
                String visibilidade = partes[partes.length - 1].trim().toLowerCase();
                boolean publica = visibilidade.equals("publica");
                List<String> nomes_musicas = Arrays.asList(Arrays.copyOfRange(partes, 1, partes.length - 1));
                Playlist p = new Playlist(nomePlaylist,nomes_musicas,publica);
                playlists.add(p);
            }
        br.close();
        return playlists;
    }
    


    /**
     * Metodo que insere as Playlists numa App
     * @param app Objeto App 
     * @param playlists Lista contendo as Playlists
     */
    private static void insertHash(App app ,List <Playlist> playlists) { // Carrega a lista na hash
        for (Playlist p : playlists) app.addPlaylist(p);
    }




    //Principais 

    /**
     * Método que orquestra o processo de parsing e loading das playlists
     * @param app App onde vao ser adicionadas as playlists
     * @param dir_path Caminho até à diretoria com o ficheiro
     */
    public static void loadPlaylist(App app,String dir_path) { // Orquestra tudo 
        try {
            List<Playlist> playlists = parserPlaylists(app,dir_path);
            insertHash(app,playlists);
        } catch (IOException e) {
            System.out.println("Erro ao ler o ficheiro de playlists ");
        }
    }
}