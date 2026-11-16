package app;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


import entidades.Album;
import entidades.Artista;
import entidades.Musica;
import entidades.Playlist;
import entidades.Utilizador;
import entidades.Utils;
import queries.QueryManager;

public class AppState implements Serializable {
    private Map <String,Musica> musicas;
    private Map <String,Utilizador> utilizadores;
    private Map <String,Album> albuns;
    private Map <String,Playlist> playlists_publicas;
    private Map <String,Playlist> playlists_privadas;
    private Map <String,Artista> artistas;
    private QueryManager queryManager;


    /**
     * Construtor por omissao da AppState
     */
    public AppState() {
        this.musicas = new HashMap<String,Musica>();
        this.utilizadores = new HashMap<String,Utilizador>();
        this.albuns = new HashMap<String,Album>();
        this.playlists_publicas = new HashMap<String,Playlist>();
        this.playlists_privadas = new HashMap<String,Playlist>();
        this.artistas = new HashMap<String,Artista>();
        this.queryManager = new QueryManager();
    }

    /**
     * Construtor parametrizado da AppState
     * @param musicas   Nova estrutura de músicas
     * @param utilizadores Nova estrutura de utilizadores
     * @param albuns    Nova estrutura de albuns
     * @param playlists_publicas    Nova estrutura de playlists públicas
     * @param playlists_privadas    Nova estrutura de playlists privadas
     * @param artistas  Nova estrutra de artistas
     * @param queryManager Novo objeto QueryManager 
     * 
     */
    public AppState(Map<String,Musica> musicas, Map<String,Utilizador> utilizadores, Map<String,Album> albuns, Map<String,Playlist> playlists_pub, Map<String,Playlist> playlists_priv, Map<String,Artista> artistas,QueryManager query) {
        this.musicas = musicas != null ? Utils.copyMap(musicas) : new HashMap<String,Musica>();
        this.utilizadores = utilizadores != null ? Utils.copyMap(utilizadores) : new HashMap<String,Utilizador>();
        this.albuns = albuns != null ? Utils.copyMap(albuns) : new HashMap<String,Album>();
        this.playlists_publicas = playlists_pub != null ? Utils.copyMap(playlists_pub) : new HashMap<String,Playlist>();
        this.playlists_privadas = playlists_priv != null ? Utils.copyMap(playlists_priv) : new HashMap<String,Playlist>();
        this.artistas = artistas != null ? Utils.copyMap(artistas) : new HashMap<String,Artista>();
        this.queryManager = query != null ? query.clone() : new QueryManager();
    }

    /**
     * Construtror de cópia 
     * @param a Outra AppState
     */
    public AppState(AppState a) {
        this.musicas = a.getMusicas();
        this.utilizadores = a.getUtilizadores();
        this.albuns = a.getAlbuns();
        this.playlists_publicas = a.getPlaylistsPublicas();
        this.playlists_privadas = a.getPlaylistsPrivadas();
        this.artistas = a.getArtistas();
        this.queryManager = a.getQueryManager();
    }

    // Getters
    /**
     * Devolve cópia profunda da estrutura de músicas da AppState
     * @return Cópia profunda da estrutura de músicas da AppState
     */
    public Map<String,Musica> getMusicas() { return Utils.copyMap(this.musicas); }

    /**
    * Devolve cópia profunda da estrutura de utilizadores da AppState
    * @return Cópia profunda da estrutura de utilizadoes da AppState
    */
    public Map<String,Utilizador> getUtilizadores() { return Utils.copyMap(this.utilizadores); }

    /**
    * Devolve cópia profunda da estrutura de álbuns da AppState
    * @return Cópia profunda da estrutura de álbuns da AppState
    */
    public Map<String,Album> getAlbuns() { return Utils.copyMap(this.albuns); }

    /**
     * Devolve cópia profunda da estrutura de Playlists públicas da AppState
     * @return Cópia profunda da estrutura de Playlists públicas da AppState
     */
    public Map<String,Playlist> getPlaylistsPublicas() { return Utils.copyMap(this.playlists_publicas); }

    /**
     * Devolve cópia profunda da estrutura de Playlists Privadas da AppState
     * @return Cópia profunda da estrutura de Playlists Privadas da AppState
     */
    public Map<String,Playlist> getPlaylistsPrivadas() { return Utils.copyMap(this.playlists_privadas); }
    /**
     * Devolve cópia profunda da estrutura de Artistas da AppState
     * @return Cópia profunda da estrutura de Artistas da AppState
     */
    public Map<String,Artista> getArtistas() { return Utils.copyMap(this.artistas); }

     /**
     * Devolve cópia do QueryManager da AppState
     * @return Cópia do QueryManager da AppState
     */
    public QueryManager getQueryManager() {return this.queryManager.clone();}

    // Setters
    
     /**
     *Atualiza a estrutra de músicas da AppState
     * @param musicas Nova estrutura de músicas da AppState
     */
    public void setMusicas(Map<String,Musica> musicas) { this.musicas = Utils.copyMap(musicas); }

     /**
     *Atualiza a estrutra de utilizadores da AppState
     * @param utilizadores Nova estrutura de utilizadores da AppState
     */
    public void setUtilizadores(Map<String,Utilizador> utilizadores) { this.utilizadores = Utils.copyMap(utilizadores); }

    /**
     *Atualiza a estrutra de álbuns da AppState 
     * @param albuns Nova estrutura de álbuns da AppState
     */
    public void setAlbuns(Map<String,Album> albuns) { this.albuns = Utils.copyMap(albuns); }

     /**
     * Atualiza a estruta de playists publicas da AppState
     * @param playlists Nova estrutura de playlists publicas da AppState
     */
    public void setPlaylistsPublicas(Map<String,Playlist> playlists_pub) { this.playlists_publicas = Utils.copyMap(playlists_pub); }

     /**
     * Atualiza a estrutura de playlists privadas da AppState
     * @param playlists Nova estrutura de playlists privadas da AppState
     */
    public void setPlaylistsPrivadas(Map<String,Playlist> playlists_priv) { this.playlists_privadas = Utils.copyMap(playlists_priv); }

     /**
     * Atualiza o QueryManager da AppState 
     * @param q Nova estrutura do QueryManager da AppState
     */
    public void setQueryManager(QueryManager q) {this.queryManager = q.clone();}

    /**
     * Implementação do método de clonagem da AppState
     * @return Objeto do tipo AppState
     */
    @Override
    public AppState clone() { return new AppState(this); }

    /**
     * Implementaçãodo método de igualdade entre duas AppState
     * @param AppState AppState para ser comparada com o recetor
     * @return Booleano ture ou false
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        AppState a = (AppState) o;
        return this.musicas.equals(a.getMusicas()) && 
               this.utilizadores.equals(a.getUtilizadores()) && 
               this.albuns.equals(a.getAlbuns()) && 
               this.playlists_publicas.equals(a.getPlaylistsPublicas()) && 
               this.playlists_privadas.equals(a.getPlaylistsPrivadas()) && 
               this.artistas.equals(a.getArtistas()) &&
               this.queryManager.equals(a.getQueryManager());
    }
    
    /**
     * Método toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
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
}