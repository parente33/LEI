package loader;

import app.App;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import entidades.Historico;
import entidades.Utilizador;
import entidades.Plano;
import entidades.Free; 
import entidades.PremiumBase;
import entidades.PremiumTop;

public class UserLoader {
    /**
     * Lê o ficheiro utilizadores.csv, cria objetos Utilizador e devolve-os numa lista.
     * @param app Instância da App
     * @param dir_path Caminho para o diretório com o ficheiro utilizadores.csv
     * @return Lista de utilizadores lidos do ficheiro
     * @throws IOException Se ocorrer um erro ao ler o ficheiro
     */
    private static List <Utilizador> parserUtilizadores(App app, String dir_path)throws IOException {
        List <Utilizador> utilizadores = new ArrayList<Utilizador>();
          Path fullPath = Paths.get(dir_path).resolve("utilizadores.csv");
        BufferedReader br = new BufferedReader(new FileReader(fullPath.toString()));
        String linha;
        while((linha = br.readLine())!= null) {
            String [] partes = linha.split(";");
            if (partes.length<8) continue;
            String nome = partes[0];
            String email = partes[1];
            String morada = partes[2];
            Plano plano = qualPlano(partes[3]);
            int pontos = Integer.parseInt(partes[4]); 
            List <String> playlists = parsePlaylist(partes[5]);
            List <String>albuns = parseAlbuns(partes[6]);
            List<Historico> historicos = parseHistoricos(partes[7]);
            Utilizador u = new Utilizador(nome,email,morada,plano,pontos,playlists,albuns,historicos);
            utilizadores.add(u);
        }
        br.close();
        return utilizadores;
    }

    /**
     * Insere os utilizadores na App
     * @param app Instância da App
     * @param utilizadores Lista de utilizadores a adicionar
     */
    private static void insertHash (App app, List<Utilizador> utilizadores) {
        for (Utilizador u : utilizadores) app.addUtilizador(u);

    } 

    /**
     * Carrega os utilizadores a partir do ficheiro e insere na App
     * @param app Instância da App
     * @param dir_path Caminho para o diretório com o ficheiro utilizadores.csv
     */
    public static void loadUtilizadores(App app, String dir_path) {
        try {
            List<Utilizador> utilizadores = parserUtilizadores(app,dir_path);
            insertHash(app,utilizadores);
        } catch (IOException e) {
            System.out.println("Erro ao ler o ficheiro de utilizadores ");
        }
    }


     /**
     * Converte uma string de nomes de playlists separados por vírgula numa lista.
     * @param playlists String com os nomes das playlists
     * @return Lista de nomes de playlists
     */
    private static List <String> parsePlaylist (String playlists) {
        List <String> novo = new ArrayList<>();
        String [] partes = playlists.split(",");
        for (String s:partes) novo.add(s);
        return novo; 
    }

    /**
     * Converte uma string de nomes de álbuns separados por vírgula numa lista.
     * @param albuns String com os nomes dos álbuns
     * @return Lista de nomes de álbuns
     */
    private static List<String> parseAlbuns (String albuns) {
        List<String> novo = new ArrayList<>();
        String [] partes = albuns.split(",");
        for (String s:partes) novo.add(s);
        return novo;
    }

    /**
     * Converte uma string com históricos de reprodução numa lista de objetos Historico.
     * @param historicos String com pares musica,data|hora separados por |
     * @return Lista de objetos Historico
     */
    private static List <Historico> parseHistoricos(String historicos) {
        List<Historico> novo = new ArrayList<>();
        String [] partes = historicos.split("\\|");
        for (String p : partes) {
            String [] campos = p.split(",");
            if (campos.length<2) continue;
            String musica = campos[0];
            LocalDateTime DataHora = LocalDateTime.parse(campos[1].trim());
            Historico hist = new Historico(musica,DataHora);
            novo.add(hist);
        }
        return novo;
    }

    /**
     * Devolve uma instância do plano correspondente a partir do nome.
     * @param plano String com o nome do plano
     * @return Instância de Plano (Free, PremiumBase ou PremiumTop)
     */
    private static Plano qualPlano (String plano) {
        if(plano.toUpperCase().equals("FREE")) return new Free();
        if (plano.toUpperCase().equals("PREMIUMTOP"))return new PremiumTop();
        if (plano.toUpperCase().equals("PREMIUMBASE"))return new PremiumBase();
        return null;
    }

} 