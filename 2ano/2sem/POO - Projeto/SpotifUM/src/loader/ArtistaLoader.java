package loader;

import entidades.Artista;
import app.App;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ArtistaLoader {
    
    /**
     * Lê o ficheiro 'artistas.csv' e constrói objetos Artista com base nas linhas do ficheiro.
     * @param dir_path Diretório onde se encontra o ficheiro
     * @return Lista de objetos Artista lidos do ficheiro
     * @throws IOException Se ocorrer erro ao ler o ficheiro
     */
    private static List<Artista> parserArtistas (String dir_path) throws IOException { 
        List<Artista> artistas = new ArrayList<>(); 
        Path fullPath = Paths.get(dir_path).resolve("artistas.csv");
        BufferedReader br = new BufferedReader(new FileReader(fullPath.toString()));
        String linha;
        while((linha = br.readLine()) != null){
            String partes[] = linha.split(";");
            if (partes.length < 3) continue;
            String nome = partes[0]; 
            boolean isgroup = partes[1].equals("grupo");
            int reproducoes = Integer.parseInt(partes[2]);
            Artista a = new Artista(nome,isgroup,reproducoes); 
            artistas.add(a);
        }
        br.close();
        return artistas;
    }

    /**
     * Insere os artistas na hash da App
     * @param app Instância da App
     * @param artistas Lista de artistas a inserir
     */
    private static void insertHash (App app, List<Artista> artistas) {
        for (Artista a :artistas) app.addArtista(a);
    }

    /**
     * Carrega os artistas a partir do ficheiro e insere-os na App
     * @param app Instância da App
     * @param dir_path Caminho do diretório onde se encontra o ficheiro 'artistas.csv'
     */
    public static void loadArtistas(App app, String dir_path) {
        try {
            List<Artista> artistas = parserArtistas(dir_path);
            insertHash(app, artistas);
        }
        catch(IOException e) {
            System.out.println("Erro ao ler ficheiro de artistas\n");
        }
    }
    


}