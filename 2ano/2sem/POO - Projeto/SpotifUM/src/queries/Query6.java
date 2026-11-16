package queries;

import java.util.List;
import java.util.Map;
import java.io.Serializable;

import entidades.Playlist;

public class Query6 implements QuerySolver, Serializable {

    private int playlist_publicas;


    /**
     * Construtor por omissão.
     */
    public Query6() {
        this.playlist_publicas = -1;
    }

    /**
     * Construtor parametrizado.
     * @param playlist_publicas Número de playlists públicas
     */
    public Query6(int playlist_publicas) {
        this.playlist_publicas = playlist_publicas;
    }

    /**
     * Construtor de cópia.
     * @param q6 Objeto Query6 a copiar
     */
    public Query6(Query6 q6) {
        this.playlist_publicas = q6.getPlaylistsPublicas();
    }


    /**
     * Devolve o número de playlists públicas.
     * @return Número de playlists públicas
     */
    public int getPlaylistsPublicas() {
        return this.playlist_publicas;
    }

    /**
     * Define o número de playlists públicas.
     * @param playlist_publicas Número de playlists públicas
     */
    public void setPlaylistsPublicas(int playlist_publicas) {
        this.playlist_publicas = playlist_publicas;
    }


    /**
     * Regista o listener no gestor de queries.
     * (Não utilizado nesta query)
     * @param q Gestor de queries
     */
    public void call(QueryManager q) {
        // Não utilizado
    }

    /**
     * Recebe objetos da aplicação.
     * (Não utilizado nesta query)
     * @param o Objeto recebido
     */
    public void listener(Object o) {
        // Não utilizado
    }

    /**
     * Responde à query com base nas playlists públicas fornecidas.
     * @param extra_info Lista contendo um Map com as playlists públicas
     * @return String com a resposta da query
     */
    public String respondeQuery(List<Object> extra_info) {
        if (extra_info == null || extra_info.size() < 1) {
            return "Informação extra insuficiente para executar a query.";
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Playlist> play_publicas = (Map<String, Playlist>) extra_info.get(0); 
            return "Existem " + play_publicas.size() + " playlist públicas!";
        } catch (Exception e) {
            return "Erro ao processar os parâmetros da query.";
        }
    }


    /**
     * Cria uma cópia do objeto.
     * @return Cópia da query
     */
    @Override
    public Query6 clone() {
        return new Query6(this);
    }

    /**
     * Verifica igualdade entre duas instâncias de Query6.
     * @param obj Objeto a comparar
     * @return true se forem iguais, false caso contrário
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Query6 other = (Query6) obj;
        return this.playlist_publicas == other.getPlaylistsPublicas();
    }

    /**
     * Representação textual da query.
     * @return String descritiva
     */
    @Override 
    public String toString() {
        return "Número de playlists públicas : " + this.playlist_publicas;
    }
}
