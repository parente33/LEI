package app;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.List;

import entidades.Musica;
import entidades.Playlist;
import entidades.Utilizador;
import entidades.Album;
import entidades.Artista;
import entidades.Historico;
import entidades.Utils;
import queries.QueryManager;
import entidades.Plano;

public class App implements Serializable{
    private Utilizador utilizador;  // utilizador com a sessao iniciada
    private  Map <String,Musica> musicas; // key -> nome da musica
    private  Map <String,Utilizador> utilizadores; // key -> nome do utilizador
    private  Map <String,Album> albuns;// key -> nome do album
    private  Map <String,Playlist> playlists_publicas; // key -> nome da playlist
    private  Map <String,Playlist> playlists_privadas; // key-> nome da playlist na mesma
    private  Map <String,Artista> artistas; // key -> nome do artista
    private  QueryManager queryManager;

    /**
     * Construtor por omissao da App
     */
    public App () {
        this.utilizador = new Utilizador();
        this.musicas = new HashMap <String,Musica>();
        this.utilizadores = new HashMap<String,Utilizador>();
        this.albuns = new HashMap<String,Album>();
        this.playlists_publicas = new HashMap<String,Playlist>();
        this.playlists_privadas = new HashMap<String,Playlist>();
        this.artistas = new HashMap<String,Artista>();
        this.queryManager = new QueryManager();
    }

    /**
     * Construtor parametrizado da App
     * @param utilizador Novo objeto Utilizador da app
     * @param musicas   Nova estrutura de músicas
     * @param utilizadores Nova estrutura de utilizadores
     * @param albuns    Nova estrutura de albuns
     * @param playlists_publicas    Nova estrutura de playlists públicas
     * @param playlists_privadas    Nova estrutura de playlists privadas
     * @param artistas  Nova estrutra de artistas
     * @param queryManager Novo objeto QueryManager
     *
     */
    public App (Utilizador utilizador,Map<String,Musica> musicas, Map<String,Utilizador> utilizadores,Map<String,Album> albuns, Map<String,Playlist> playlists_publicas,Map<String,Playlist> playlists_privadas,Map<String,Artista> artistas,QueryManager queryManager) {
        this.utilizador = utilizador != null ? utilizador.clone() : null;
        this.musicas = musicas != null ? Utils.copyMap(musicas) : new HashMap<String,Musica>();
        this.utilizadores = utilizadores != null ? Utils.copyMap(utilizadores) : new HashMap<String, Utilizador>();
        this.albuns = albuns != null ?Utils.copyMap(albuns) : new HashMap<String,Album>();
        this.playlists_publicas = playlists_publicas!=null ?Utils.copyMap(playlists_publicas) : new HashMap<String,Playlist>();
        this.playlists_privadas = playlists_privadas!=null ?Utils.copyMap(playlists_privadas) : new HashMap<String,Playlist>();
        this.artistas = artistas !=null ?Utils.copyMap(artistas) : new HashMap<String,Artista>();
        this.queryManager = queryManager != null ? queryManager.clone() : new QueryManager();
    }

    /**
     * Construtror de cópia
     * @param a Outra App
     */
    public App (App a) {
        this.utilizador = a.getUtilizador();
        this.musicas = a.getMapMusicas();
        this.utilizadores = a.getMapUtilizadores();
        this.albuns = a.getMapAlbuns();
        this.playlists_publicas = a.getMapPublicPlaylists();
        this.playlists_privadas = a.getMapPrivatePlaylists();
        this.artistas = a.getMapArtistas();
        this.queryManager = a.getQueryManager();
    }


    // Getters

    /**
     * Devolve uma cópia do utilizador da App
     * @return Cópia do utilizador da App
     */
    public Utilizador getUtilizador()                      {return this.utilizador.clone();          }
    /**
     * Devolve cópia profunda da estrutura de músicas da App
     * @return Cópia profunda da estrutura de músicas da App
     */
    public Map<String,Musica> getMapMusicas()              {return Utils.copyMap(this.musicas);      }
    /**
     * Devolve cópia profunda da estrutura de utilizadores da App
     * @return Cópia profunda da estrutura de utilizadoes da App
     */
    public Map<String,Utilizador> getMapUtilizadores()     {return Utils.copyMap (this.utilizadores);}
    /**
     * Devolve cópia profunda da estrutura de álbuns da App
     * @return Cópia profunda da estrutura de álbuns da App
     */
    public Map<String,Album> getMapAlbuns()                {return Utils.copyMap(this.albuns);       }
    /**
     * Devolve cópia profunda da estrutura de Playlists públicas da App
     * @return Cópia profunda da estrutura de Playlists públicas da App
     */
    public Map<String,Playlist> getMapPublicPlaylists()    {return Utils.copyMap(this.playlists_publicas);}
    /**
     * Devolve cópia profunda da estrutura de Playlists Privadas da App
     * @return Cópia profunda da estrutura de Playlists Privadas da App
     */
    public Map<String,Playlist> getMapPrivatePlaylists()   {return Utils.copyMap(this.playlists_privadas);}
    /**
     * Devolve cópia profunda da estrutura de Artistas da App
     * @return Cópia profunda da estrutura de Artistas da App
     */
    public Map<String,Artista> getMapArtistas()            {return Utils.copyMap(this.artistas);     }
    /**
     * Devolve cópia do QueryManager da App
     * @return Cópia do QueryManager da App
     */
    public QueryManager getQueryManager()                   {return this.queryManager.clone();}

    // Setters

    /**
     *Atualiza o utilizador da App
     * @param utilizador Novo utilizador da App
     */
    public void setUtilizador(Utilizador utilizador)                     {this.utilizador = utilizador.clone();          }

    /**
     *Atualiza a estrutra de músicas da App
     * @param musicas Nova estrutura de músicas da App
     */
    public void setMusicas(Map<String,Musica> musicas)                   {this.musicas =Utils.copyMap(musicas);          }

    /**
     *Atualiza a estrutra de utilizadores da App
     * @param utilizadores Nova estrutura de utilizadores da App
     */
    public void setUtilizadores(Map<String,Utilizador> utilizadores)     {this.utilizadores =Utils.copyMap(utilizadores);}

    /**
     *Atualiza a estrutra de álbuns da App
     * @param albuns Nova estrutura de álbuns da App
     */
    public void setAlbuns(Map<String,Album> albuns)                      {this.albuns =Utils.copyMap(albuns);            }

    /**
     * Atualiza a estruta de playists publicas da App
     * @param playlists Nova estrutura de playlists publicas da App
     */
    public void setPublicPlaylists(Map <String,Playlist> playlists)            {this.playlists_publicas = Utils.copyMap(playlists);     }

    /**
     * Atualiza a estrutura de playlists privadas da App
     * @param playlists Nova estrutura de playlists privadas da App
     */
    public void setPrivatePlaylists(Map <String,Playlist> playlists)            {this.playlists_privadas = Utils.copyMap(playlists);}
    /**
     * Atualiza a estrutura de artistas da App
     * @param artistas Nova estrutura de artistas da App
     */
    public void setArtistas(Map<String,Artista> artistas)                {this.artistas = Utils.copyMap(artistas);       }
    /**
     * Atualiza o QueryManager da App
     * @param q Nova estrutura do QueryManager da App
     */
    public void setQueryManager(QueryManager q)                          {this.queryManager = q.clone();}

    /**
     * Implementação do método de clonagem da App
     * @return Objeto do tipo App
     */
    @Override
    public App clone() {return new App(this);}

    /**
     * Implementaçãodo método de igualdade entre duas App
     * @param App App para ser comparada com o recetor
     * @return Booleano ture ou false
     */
    @Override
    public boolean equals(Object o) {
        if(o==this) return true;
        if (o==null || o.getClass() != this.getClass()) return false;
        App a = (App) o;
        return this.utilizador.equals(a.getUtilizador()) && this.musicas.equals(a.getMapMusicas()) && this.utilizadores.equals(a.getMapUtilizadores()) && this.albuns.equals(a.getMapAlbuns()) && this.artistas.equals(a.getMapArtistas()) && this.playlists_privadas.equals(a.getMapPrivatePlaylists()) && this.playlists_publicas.equals(a.getMapPublicPlaylists()) && this.queryManager.equals(a.getQueryManager());
    }

    /**
     * Método toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Utilizador : ").append(this.utilizador.clone()).append("\n");
        // Musicas
        sb.append("Músicas : {\n ");
        for (Musica m : this.musicas.values()) if(m != null) sb.append(m.toString());
        sb.append("}\n");
        // Utilizadores
        sb.append("Utilizadores :\n");
       for (Utilizador u: this.utilizadores.values()) if(u != null) sb.append(u.toString());
        sb.append("\n");
        // Albuns
        sb.append("Álbuns : {\n");
        for (Album a : this.albuns.values()) if(a != null) sb.append(a.toString());
        sb.append("}\n");
        // Playlists
        sb.append("Playlists Públicas : {\n");
        for (Playlist p : this.playlists_publicas.values()) if (p != null) sb.append(p.toString());
        sb.append("}\n");
        sb.append("Playlists Privadas : {\n");
        for (Playlist p : this.playlists_privadas.values()) if (p != null) sb.append(p.toString());
        sb.append("}\n");
        //Artistas
        sb.append("Artistas :{\n");
        for (Artista ar : this.artistas.values()) if (ar != null) sb.append(ar.toString());
        sb.append("}\n");

        //Query Manager
        sb.append("Query Manager :{\n").append(queryManager.toString());
        sb.append("}\n");
        return sb.toString();

    }



    // Adicionar

    /**
     * Adiciona uma nova música à App caso seja válida
     * Adiciona também a musica às estruturas das quries
     * @param m Objeto Musica a adicionar
     * @return true se foi adicionado ou false caso contrário
     */
    public boolean addMusica(Musica m) {
        if (m ==null ) return false;
        Musica novo = m.clone();
        if(this.musicaValida(novo)) {
            this.musicas.put(novo.getNome(), novo);
            this.queryManager.queryManageradd(novo.clone());
            return true;
        }
        return false;
    }
    /**
     * Adiciona um novo utilizador à App caso seja válido
     * @param u Objeto Utilizador a adicionar
     * @return true se foi adicionado ou false caso contrário
     */
    public boolean addUtilizador(Utilizador u) {
        if (u == null) return false;
        Utilizador novo = u.clone();
        if(this.utilizadorValido(novo)) {
            this.utilizadores.put(novo.getNome(), novo);
            this.queryManager.queryManageradd(novo.clone());
            return true;
        }
        return false;
    }

    /**
     * Adiciona um novo album à App caso seja válido
     * @param a Objeto Album a adicionar
     * @return true se foi adicionado ou false caso contrário
     */
    public boolean addAlbum(Album a) {
        if (a == null) return false;
        Album novo = a.clone();
        if(this.albumValido(novo)) {this.albuns.put(novo.getNome(), novo);return true;}
        return false;
    }

    /**
     * Adiciona uma playlist à App consoante a sua privacidade e caso seja válida
     * @param p Objeto Playlist a ser adicionado
     * @return true se foi adicionado ou false caso contrário
     */

    public boolean addPlaylist(Playlist p) {
        if (p == null) return false;
        Playlist novo = p.clone();
        if(this.playlistValida(novo)){
           if(p.isPublica()) this.playlists_publicas.put(novo.getNome(), novo);
           else this.playlists_privadas.put(novo.getNome(), novo);
           return true;
        }
        return false;
    }

    /**
     * Adiciona um artista à app caso seja válido
     * @param a Objeto Artista a ser adicionado
     * @return true se foi adicionado ou false caso contrário
     */
    public boolean addArtista(Artista a) {
        if (a == null) return false;
        Artista novo = a.clone();
        this.artistas.put(novo.getNome(),novo);
        this.queryManager.queryManageradd(novo.clone());
        return true;
    }

    //Remover
    /**
     * Remove uma musica da App
     * @param m Objeto Musica a ser removido
     */
    public void remMusica(String m) {
        if (m == null) return;
        this.musicas.remove(m);
    }

    /**
     * Remove utilizador da App
     * @param u Objeto utilizador a ser removido
     */
    public void remUtilizador(String u) {
        if (u == null) return;
        this.utilizadores.remove(u);
    }
    /**
     * Remove album da App
     * @param a Objeto Album a ser removido
     */
    public void remAlbum(String a) {
        if (a == null) return;
        this.albuns.remove(a);
    }

    /**
     * Remove playlist da App
     * @param p Objeto Playlist a ser removido
     */
    public void remPlaylist(String p) {
        if (p == null) return;
        this.playlists_privadas.remove(p);
        this.playlists_publicas.remove(p);
    }
    /**
     * Remove artista da App
     * @param a Objeto Artista a ser removido
     */
    public void remArtista(String a) {
        if (a == null) return;
        this.artistas.remove(a);
    }

    /**
     * Verifica a existência de uma musica
     * @param m Objeto Musica a ser verificada
     * @return true caso exista ou false caso contrário
     */
    public boolean existeMusica(Musica m) {
        if (m == null) return false;
        return this.musicas.containsKey(m.getNome());
    }
    /**
     * Verifica a existência de um utilizador
     * @param u Objeto Utilizador a ser verificado
     * @return true caso exista ou false caso contrário
     */
    public boolean existeUtilizador(Utilizador u) {
        if (u == null) return false;
        return this.utilizadores.containsKey(u.getNome());
    }
    /**
     * Verifica a existência de um album
     * @param a Objeto Album a ser verificado
     * @return true caso exista ou false caso contrário
     */
    public boolean existeAlbum(Album a) {
        if (a == null) return false;
        return this.albuns.containsKey(a.getNome());
    }
    /**
     * Verifica a existência de uma playlist
     * @param p Objeto Playlist a ser verificado
     * @return true caso exista ou false caso contrário
     */
    public boolean existePlaylist(Playlist p) {
        if (p == null) return false;
        return (this.playlists_publicas.containsKey(p.getNome()) || this.playlists_privadas.containsKey(p.getNome()));
    }
    /**
     * Verifica a existência de um artista
     * @param a Objeto Artista a ser verificado
     * @return true caso exista ou false caso contrário
     */
    public boolean existeArtista(Artista a) {
        if (a == null) return false;
        return this.artistas.containsKey(a.getNome());
    }

    // Procurar pela Key

    /**
     * Verifica a existência de uma musica pela key
     * @param m Nome da musica a ser verificada
     * @return true caso exista ou false caso contrário
     */
    public boolean existeMusica(String m) {
        if (m == null) return false;
        return this.musicas.containsKey(m);
    }
    /**
     * Verifica a existência de um utilizador pela key
     * @param u Nome do utilizador a ser verificado
     * @return true caso exista ou false caso contrário
     */
    public boolean existeUtilizador(String u) {
        if (u == null) return false;
        return this.utilizadores.containsKey(u);
    }
    /**
     * Verifica a existência de um album pela key
     * @param a Nome do album a ser verificado
     * @return true caso exista ou false caso contrário
     */
    public boolean existeAlbum(String a) {
        if (a == null) return false;
        return this.albuns.containsKey(a);
    }
    /**
     * Verifica a existência de uma Playlist pela key
     * @param p Nome da playlist a ser verificada
     * @return true caso exista ou false caso contrário
     */
    public boolean existePlaylist(String p) {
        if (p == null) return false;
        return (this.playlists_publicas.containsKey(p) || this.playlists_privadas.containsKey(p));
    }
    /**
     * Verifica a existência de uma Playlist publica pela key
     * @param m Nome da Playlist a ser verificada
     * @return true caso exista ou false caso contrário
     */
    public boolean existePublicPlaylist(String p) {
        return this.playlists_publicas.containsKey(p);
    }
    /**
     * Verifica a existência de uma Playlist privada pela key
     * @param m Nome da Playlist a ser verificada
     * @return true caso exista ou false caso contrário
     */
    public boolean existPrivatePlaylist(String p) {
        return this.playlists_privadas.containsKey(p);
    }
    /**
     * Verifica a existência de um artista pela key
     * @param m Nome do Artista a ser verificada
     * @return true caso exista ou false caso contrário
     */
    public boolean existeArtista(String a) {
        if (a == null) return false;
        return this.artistas.containsKey(a);
    }
    /**
     * Verifica a existência de uma musica pela key
     * @param m Nome da musica a ser verificada
     * @return true caso exista ou false caso contrário
     */
    public Musica getMusica(String key) {
        if (key == null) return null;
        return this.musicas.get(key).clone();
    }
    /**
     * Devolve uma cópia de um Utilizador caso esteja na App
     * @param key Nome do utilizador pretendido
     * @return Cópia do utilizador pretendido caso exista ou então null
     */
    public Utilizador getUtilizador(String key) {
        if (key == null) return null;
        return this.utilizadores.get(key).clone();
    }
     /**
     * Devolve uma cópia de um Album caso esteja na App
     * @param key Nome do Album pretendido
     * @return Cópia do album pretendido caso exista ou então null
     */
    public Album getAlbum(String key) {
        if (key == null) return null;
        return this.albuns.get(key).clone();
    }
     /**
     * Devolve uma cópia de uma Playlist caso esteja na App
     * @param key Nome da Playlist pretendida
     * @return Cópia da Playlist pretendida caso exista ou então null
     */
    public Playlist getPlaylist(String key) {
        if (key == null) return null;
        Playlist privada = this.playlists_privadas.get(key);
        if(privada!= null) return privada.clone();
        return this.playlists_publicas.get(key).clone();
    }
     /**
     * Devolve uma cópia de um Artista caso esteja na App
     * @param key Nome do Artista pretendido
     * @return Cópia do Artista pretendido caso exista ou então null
     */
    public Artista getArtista(String key) {
        if (key == null) return null;
        return this.artistas.get(key).clone();
    }
    /**
     * Adiciona uma musica caso exista na App a uma playlist caso exista
     * @param playlist Nome da paylist
     * @param musica Nome da playlist
     * @return true se foi adicionado ou falso caso contrário
     */
    public boolean addMusicaPlaylist(String playlist,String musica) {
        if(playlist ==null || !this.existePlaylist(playlist)) return false;
        if (musica == null || !this.existeMusica(musica)) return false;
        Playlist p = this.playlists_privadas.get(playlist) != null ? this.playlists_privadas.get(playlist) : this.playlists_publicas.get(playlist);
        p.addMusica(musica);
        return true;
    }
     /**
     * Remove uma musica caso exista na App a uma playlist caso exista
     * @param playlist Nome da paylist
     * @param musica Nome da playlist
     * @return true se foi removida ou falso caso contrário
     */
    public boolean remMusicaPlaylist(String playlist,String musica){
        if(playlist ==null || !this.existePlaylist(playlist)) return false;
        if (musica == null || !this.existeMusica(musica)) return false;
        Playlist p = this.playlists_privadas.get(playlist) != null ? this.playlists_privadas.get(playlist) : this.playlists_publicas.get(playlist);
        return p.remMusica(musica);
    }
     /**
     * Adiciona uma Playlist caso exista na App a um utilizador caso exista
     * @param utilizador Nome do utilizador
     * @param playlist Nome da playlist
     * @return true se foi adicionada ou falso caso contrário
     */
    public boolean addPlaylistUtilizador(String utilizador,String playlist) {
        if (utilizador == null || !this.existeUtilizador(utilizador)) return false;
        if (playlist == null || !this.existePlaylist(playlist)) return false;
        Utilizador u = this.utilizadores.get(utilizador);
        return u.addPlaylist(playlist);

    }
    /**
    * Remove a uma Playlist caso exista na App a um utilizador caso exista
    * @param utilizador Nome do utilizador
    * @param playlist Nome da playlist
    * @return true se foi removida ou falso caso contrário
    */
    public boolean remPlaylistUtilizador(String utilzador, String playlist) {
        if (utilizador == null || !this.existeUtilizador(utilizador)) return false;
        if (playlist == null || !this.existePlaylist(playlist)) return false;
        Utilizador u = this.utilizadores.get(utilzador);
        return u.remPlaylist(playlist);
    }
      /**
     * Adiciona um Album  caso exista na App a um Utilizador caso exista
     * @param utilizador Nome do utilizador
     * @param album Nome do Album
     * @return true se foi adicionado ou falso caso contrário
     */
    public boolean addAlbumUtilizador(String utilizador,String album) {
        if (utilizador == null || !this.existeUtilizador(utilizador)) return false;
        if (album == null || !this.existeAlbum(album)) return false;
        Utilizador u = this.utilizadores.get(utilizador);
        return u.addAlbum(album);

    }
    /**
    * Remove um Album  caso exista na App a um Utilizador caso exista
    * @param utilizador Nome do utilizador
    * @param album Nome do Album
    * @return true se foi removido ou falso caso contrário
    */
    public boolean remAlbumUtilizador(String utilzador, String album) {
        if (utilizador == null || !this.existeUtilizador(utilizador)) return false;
        if (album == null || !this.existePlaylist(album)) return false;
        Utilizador u = this.utilizadores.get(utilzador);
        return u.remAlbum(album);
    }

    /**
     * Devolve uma lista com o nome de todos os albuns na App
     * @return Lista com o nome de todos os albuns na App
     */
    public List<String> albunsDisponiveis() {
        List<String> nomesAlbuns = new ArrayList<>(this.albuns.keySet());
        return nomesAlbuns;
    }

     /**
     * Devolve uma lista com o nome de todas as playlists na App
     * @return Lista com o nome de todas as playlists na App
     */
    public List<String> playlistsDisponiveis() {
        List<String> nomesPlay = new ArrayList<>(this.playlists_publicas.keySet());
        return nomesPlay;
    }
     /**
     * Devolve uma lista com o nome de todas as musicas na App
     * @return Lista com o nome de todas as musicas na App
     */
    public List<String> musicas() {
        List<String> musicas = new ArrayList<>(this.musicas.keySet());
        return musicas;
    }

    // Validaçao Logica
    /**
     * Verifica se um Objeto Album é valido
     * É valido se o artista existe na App e as musicas tambem existem
     * @param a Album a ser avaliado
     * @return true caso seja válido ou false caso contrário
     */
    private boolean albumValido (Album a) {
        if (!this.existeArtista(a.getArtista())) return false;
        List <String> musicas = a.getMusicas();
        for (String m : musicas) if(!this.existeMusica(m)) return false;
        return true;
    }
     /**
     * Verifica se um Objeto Muscia é valido
     * É valido se o artista existe na App
     * @param m Musica a ser avaliada
     * @return true caso seja válido ou false caso contrário
     */
    private boolean musicaValida (Musica m) {
        if(!this.existeArtista(m.getInterprete())) return false;
        return true;
    }
     /**
     * Verifica se um Objeto Playlist é valido
     * É valido se o artista existe na App e as musicas tambem existem
     * @param p Playlist a ser avaliada
     * @return true caso seja válido ou false caso contrário
     */
    private boolean playlistValida (Playlist p) {
        if(this.existePlaylist(p)) return false;
        List <String> musicas = p.getMusicas();
        for (String m : musicas) if(!this.existeMusica(m))return false;
        return true;
    }
    /**
     * Verifica se um Objeto Utilizador é valido
     * É valido se o todos os historicos são validos, se todas as playlists e albuns existem na App
     * @param u Utilizador a ser avaliado
     * @return true caso seja válido ou false caso contrário
    */
    private boolean utilizadorValido (Utilizador u) {
        if(existeUtilizador(u)) return false;
        List<String> playlists = u.getPlaylists();
        for(String p : playlists) if(!this.existePlaylist(p)) return false;
        List <Historico> historico = u.getHistorico();
        for(Historico h : historico) if(!this.existeMusica(h.getMusica())) return false;
        List<String> albuns = u.getAlbuns();
        for(String a:albuns) if (!this.existeAlbum(a)) return false;
        return true;
    }
    /**
     * Realiza o parse de uma string com musicas e verifica se todas as musicas existem na App
     * @param musicas String com as musicas separads por virgula
     * @return String contendo a musica que nao existe, ou entao null
     */
    public String checkMusicas(String musicas) {
        String [] partes = musicas.split(",");
        for (String m : partes) if (!existeMusica(m)) return m;
        return null;
    }

    //// FUNCIONALIDADES

    /**
     * Inicia a sessão com um utilizador existente.
     * @param nome Nome do utilizador a iniciar sessão
     * @return true se o utilizador existir e a sessão for iniciada; false caso contrário
     */
    public boolean iniciaSessao(String nome) {
        if (!existeUtilizador(nome)) return false;
        this.utilizador = this.utilizadores.get(nome);
        return true;
    }

    /**
    * Adiciona um álbum à biblioteca do utilizador atual.
    * @param album Nome do álbum a adicionar
    * @return true se o álbum existir e for adicionado com sucesso; false caso contrário
    */
    public boolean adicionarAlbumBiblioteca (String album) {
        if(!this.existeAlbum(album)) return false;
        return this.utilizador.addAlbum(album);

    }
    /**
    * Adiciona uma playlist pública à biblioteca do utilizador atual.
    * @param playlist Nome da playlist pública
    * @return true se a playlist existir e for adicionada; false caso contrário
    */
    public boolean adicionarPlayBiblioteca(String playlist) {
        if (!this.existePublicPlaylist(playlist)) return false;
        return this.utilizador.addPlaylist(playlist);
    }

    /**
    * Remove um álbum da biblioteca do utilizador atual.
    * @param album Nome do álbum a remover
    * @return true se o álbum for removido; false caso contrário
    */
    public boolean removerAlbumBiblioteca(String album) {
        return this.utilizador.remAlbum(album);
    }
    /**
    * Remove uma playlist da biblioteca do utilizador atual.
    * Se for privada, remove também da aplicação.
    * @param playlist Nome da playlist a remover
    * @return true se for removida com sucesso; false caso contrário
    */
    public boolean removerPlayBiblioteca(String playlist) {
        if (this.existPrivatePlaylist(playlist)) this.remPlaylist(playlist); // remove da aplicacao a playlist
       return this.utilizador.remPlaylist(playlist);
    }
    /**
    * Reproduz uma música existente e atualiza estatísticas de utilizador, música e artista bem com as queries
    * @param musica Nome da música a reproduzir
    * @return Letra da música se existir; null caso contrário
    */
    public String reproduzirMusica(String musica) {
        if (!existeMusica(musica)) return null;
        Utilizador u = this.utilizador;
        double pontos = u.getPlano().getPercentagem() * u.getPontos() + u.getPlano().getPontos();
        u.addPontos(pontos);
        u.addHistorico(musica);
        this.queryManager.queryManageradd(u.clone());
        Musica m = this.musicas.get(musica);
        m.addReproducao();
        this.queryManager.queryManageradd(m.clone());
        Artista a = this.artistas.get(m.getInterprete());
        a.addReproducao();
        this.queryManager.queryManageradd(a.clone());
        //

        return m.getLetra();
    }

    // Reproduz sequencialmente uma playlist
    /**
    * Reproduz uma playlist na ordem original.
    * @param nomePlaylist Nome da playlist
    * @return Lista de músicas da playlist; null se não existir ou não for da biblioteca do utilizador
    */
    public List<String> reproduzirPlaylist(String nomePlaylist) {
        if (!existePlaylist(nomePlaylist)) return null;
        Playlist playlist = getPlaylist(nomePlaylist);
        Utilizador u = this.utilizador;
        if (u == null || !u.getPlaylists().contains(nomePlaylist)) return null;

        List<String> musicas = playlist.getMusicas();
        return musicas;
    }

    // Reproduz playlists de forma aleatória
    /**
    * Reproduz uma playlist de forma aleatória.
    * @param nomePlaylist Nome da playlist
    * @return Lista de músicas em ordem aleatória; null se não for válida
    */
    public List<String> reproduzPlaylistAleatoria(String nomePlaylist) {
        if (!existePlaylist(nomePlaylist)) return null;
        Playlist play = getPlaylist(nomePlaylist);
        Utilizador u = this.utilizador;
        if (u == null || !u.getPlaylists().contains(nomePlaylist)) return null;

        List<String> musicasBaralhadas = play.getMusicasEmOrdemAleatoria();
        return musicasBaralhadas;

    }

    // Reproduz sequencialmente um album

    /**
    * Reproduz um álbum na ordem original.
    * @param nomeAlbum Nome do álbum
    * @return Lista de músicas do álbum; null se o álbum não existir ou não estiver na biblioteca
    */
    public List<String> reproduzirAlbum(String nomeAlbum) {
        if (!existeAlbum(nomeAlbum)) return null;
        Album album = getAlbum(nomeAlbum);
        Utilizador u = this.utilizador;
        if (u == null || !u.getAlbuns().contains(nomeAlbum)) return null;

        List<String> musicas = album.getMusicas();
        return musicas;
    }

    /**
     * Cria uma nova playlist com músicas específicas.
     * @param nome Nome da nova playlist
     * @param musicas String com nomes das músicas separados por vírgula
     * @param isPublica Define se a playlist é pública
     * @return true se criada com sucesso; false caso contrário
     */
    public boolean criarPlaylist(String nome, String musicas, boolean isPublica){
        if(!this.checkPermissao()) return false;
        if(this.existePlaylist(nome))return false;
        String [] m = musicas.split(",");
        List <String> musicas_finais = new ArrayList<>();
        for (String s :m) musicas_finais.add(s);
        Playlist p = new Playlist(nome,musicas_finais,isPublica);
        if (this.addPlaylist(p))  return this.utilizador.addPlaylist(nome);
        return false;
    }


    /**
     * Gera uma playlist com as músicas mais ouvidas dentro de um limite de tempo.
     * @param nome Nome da playlist
     * @param tempoMaxSegundos Tempo máximo total das músicas (em segundos)
     * @return true se a playlist for criada; false caso contrário
     */
    public boolean gerarPlayMusicasPreferidasComTempo(String nome, int tempoMaxSegundos) {
        Map<String, Integer> contador = new HashMap<>();

        for (Historico h : this.utilizador.getHistorico()) {
            contador.merge(h.getMusica(), 1, Integer::sum);
        }

        List<Musica> ordenadas = contador.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
            .map(e -> this.musicas.get(e.getKey()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        List<String> nomesMusicas = new ArrayList<>();
        int tempoAtual = 0;

        for (Musica m : ordenadas) {
            if (tempoAtual + m.getDuracao() <= tempoMaxSegundos) {
                nomesMusicas.add(m.getNome());
                tempoAtual += m.getDuracao();
            } else {
                break;
            }
        }
        String musicasFinal = String.join(",", nomesMusicas);
        return criarPlaylist(nome, musicasFinal, false);
    }

     /**
     * Gera uma playlist com as músicas mais ouvidas do utilizador.
     * @param nome Nome da playlist
     * @return true se a playlist for criada; false caso contrário
     */
    public boolean gerarPlayMusicasPreferidas(String nome) {
        Map<String, Integer> contador = new HashMap<>();

        for (Historico h : this.utilizador.getHistorico()) {
            contador.merge(h.getMusica(), 1, Integer::sum);
        }

        String musicas = contador.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
            .map(e -> this.musicas.get(e.getKey()))
            .filter(Objects::nonNull)
            .map(Musica::getNome)
            .collect(Collectors.joining(","));
        return criarPlaylist(nome, musicas, false);
    }

    /**
     * Gera uma playlist com as músicas explícitas mais ouvidas do utilizador.
     * @param nome Nome da playlist
     * @return true se criada com sucesso; false caso contrário
     */
    public boolean gerarPlayMusicasPreferidasExplicitas(String nome) {
        Map<String, Integer> contador = new HashMap<>();

        for (Historico h : this.utilizador.getHistorico()) {
            contador.merge(h.getMusica(), 1, Integer::sum);
        }

        String musicas = contador.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
            .map(e -> this.musicas.get(e.getKey()))
            .filter(m -> m != null && m.isExplicita())
            .map(Musica::getNome)
            .collect(Collectors.joining(","));

        return criarPlaylist(nome, musicas, false);
    }


    /**
     * Escolhe aleatoriamente uma música da aplicação.
     * @return Nome da música escolhida
     */
    public String escolheRandomMusic() {
        List <String> keys = new ArrayList<>(this.musicas.keySet());
        Random rand = new Random();
        String musica = keys.get(rand.nextInt(keys.size()));
        return musica;
    }

      /**
     * Reproduz uma música aleatória, se o utilizador tiver permissões.
     * @param musica Nome da música a reproduzir
     * @return Letra da música ou null se sem permissão
     */
    public String reproduzirMusicaEscolhida(String musica) {
        if (!this.checkPermissao()) return null;
        return reproduzirMusica(musica);
    }
    /**
     * Verifica se o utilizador atual tem permissões premium.
     * @return true se for premium; false caso contrário
     */
    public boolean checkPermissao() {
        return this.utilizador.getPlano().isPremium();
    }
    /**
    * Resolve queries da aplicação com base em datas e contexto adicional.
    * @param query Numero da query
    * @param inicio Data de início caso seja preciso
    * @param fim Data de fim caso seja preciso
    * @return Resultado da query como String
    */
    public String solveQuery(int query,LocalDateTime inicio, LocalDateTime fim) {
        List <Object> info_extra = new ArrayList<>();
       if(query == 3 || query == 7) info_extra.add(this.getMapUtilizadores());
       if (query == 6) info_extra.add(this.getMapPublicPlaylists());
        info_extra.add(inicio);
        info_extra.add(fim);
        return this.queryManager.solveQuery(query,info_extra);
    }
     /**
     * Altera o plano atual do utilizador para o novo plano.
     * @param novoPlano Plano a ser atribuído ao utilizador
     * @return Mensagem de sucesso ou erro
     */
    public String upgradePlano(Plano novoPlano) {
        Plano planoAtual = this.utilizador.getPlano();

        if (planoAtual.getNome().equals(novoPlano.getNome())) {
            return "Já possui esse plano.";
        }
        this.utilizador.setPlano(novoPlano);
        if(novoPlano.getNome().equals("PremiumTop")) this.utilizador.addPontos(100);
        return "Plano alterado de " + planoAtual.getNome() + " para " + novoPlano.getNome();
    }

    // ESTADO DA APP
    public void atualizarEstado(AppState estado) {
        if (estado != null) {
            this.musicas = estado.getMusicas();
            this.utilizadores = estado.getUtilizadores();
            this.albuns = estado.getAlbuns();
            this.playlists_publicas = estado.getPlaylistsPublicas();
            this.playlists_privadas = estado.getPlaylistsPrivadas();
            this.artistas = estado.getArtistas();
            this.queryManager = estado.getQueryManager();

        }
    }

    /**
     * Guarda o estado atual da aplicação no ficheiro binário.
     */
    public void guardaEstado() {
        try {
            FileOutputStream f = new FileOutputStream("./../appState.bin");
            ObjectOutputStream o = new ObjectOutputStream(f);

            o.writeObject(this);
            o.flush();
            o.close();
        } catch (IOException e) {
            System.err.println("Erro ao guardar o estado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
    * Carrega o estado da aplicação a partir de um ficheiro binário.
    * @param nomeFicheiro Caminho para o ficheiro
    * @return Objeto App carregado ou null se falhar
    */
    public App carregaEstado(String nomeFicheiro) {
        try (ObjectInputStream o = new ObjectInputStream(new FileInputStream(nomeFicheiro))) {
            return (App) o.readObject();
        } catch (Exception e) {
            System.out.println("Erro ao carregar estado: " + e.getMessage());
            return null;
        }
    }
}