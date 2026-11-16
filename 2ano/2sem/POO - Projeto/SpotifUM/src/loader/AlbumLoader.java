package loader;


import entidades.Album;
import app.App;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class AlbumLoader {

    /**
     * Lê o ficheiro 'albuns.csv' e constrói objetos Album com base nas linhas do ficheiro.
     * @param app Instância da aplicação para carregar as músicas
     * @param dir_path Diretório onde se encontra o ficheiro
     * @return Lista de objetos Album lidos do ficheiro
     * @throws IOException Se ocorrer erro ao ler o ficheiro
     */
    private static List<Album> parserAlbuns(App app ,String dir_path) throws IOException{
        List <Album> albuns = new ArrayList<>();
        Path fullPath = Paths.get(dir_path).resolve("albuns.csv");
        BufferedReader br = new BufferedReader(new FileReader(fullPath.toString()));
        String linha;
        while ((linha = br.readLine())!=null) {
            String partes[] = linha.split(";"); 
            if(partes.length<3) continue;
            String nome = partes[0];
            String artista = partes[1];
            List<String> musicas = MusicaLoader.loadMusicas(app,partes[2],partes[1]);
            Album album = new Album(nome,artista,musicas);
            albuns.add(album);
        }
        br.close();
        return albuns;
    }

    /**
     * Insere os álbuns na hash da App.
     * @param app Instância da aplicação
     * @param albuns Lista de álbuns a inserir
     */
    private static void insertHash(App app, List<Album> albuns) {
        for (Album a :albuns) app.addAlbum(a);
    } 

    /**
     * Carrega os álbuns a partir do ficheiro e insere-os na App
     * @param app Instância da aplicação
     * @param dir_path Caminho do diretório onde se encontra o ficheiro 'albuns.csv'
     */
    public static void loadAlbuns(App app, String dir_path) {
        try {
            List<Album> albuns = parserAlbuns(app, dir_path);
            insertHash(app, albuns);
        }
        catch(IOException e){
            System.out.println("Erro ao ler ficheiro de albuns\n");
        }
    }   

    
                                                        
}