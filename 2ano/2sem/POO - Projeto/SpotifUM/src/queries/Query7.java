package queries;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import entidades.Utilizador;

public class Query7 implements QuerySolver,Serializable{
    Utilizador utilizador; 
    int nrPlaylists; 

    /**
     * Construtor por omissao
     */
    public Query7() {
        this.utilizador = new Utilizador();
        this.nrPlaylists = -1;
    }

    /**
     * Construtor parameterizado 
     * @param utilizador Objeto Utilizador
     * @param nrPlaylist Numero de paylists
     */
    public Query7(Utilizador utilizador, int nrPlaylist) {
        this.utilizador = utilizador; 
        this.nrPlaylists = nrPlaylist;
    }

    /**
     * Construtor de copia
     * @param q7 Objeto Query7
     */
    public Query7(Query7 q7) {
        this.utilizador = q7.getUtilizador();
        this.nrPlaylists = q7.getNrPlaylists();
    } 
    /**
     * Devolve copia do utilizador atual
     * @return Utilizador
     */
    public Utilizador getUtilizador() {return this.utilizador.clone();} 

    /**
     * Devolve o numero de playlists do utilizador atual 
     * @return numero de playlists
     */
    public int getNrPlaylists() {return this.nrPlaylists;} 

    /**
     * Atualiza o utilizador atual 
     * @param u Utilizador
     */
    public void setUtilizador(Utilizador u) {if(u!=null) this.utilizador = u;} 

    /**
     * Atualiza o numero de playlists 
     * @param nr Numero de playlists
     */
    public void setNrPlaylists(int nr) {this.nrPlaylists = nr;}

   
    public void call(QueryManager q){}

    public void listener(Object o) {}

    /**
     * Metodo que percorre uma estrutura de utilizadores e calcula qual o Utilizador tem mais playlists
     * Atualiza Query7 com esse Utilizador
     * @param utilizadores Estrutura com os Utilizadores
     */
    public void calcula(Map<String, Utilizador> utilizadores) {
    for (Utilizador u : utilizadores.values()) {
        if (u == null || u.getPlaylists() == null) continue;

        long count = u.getPlaylists().stream()
                      .filter(Objects::nonNull)
                      .count();

        if (count > this.nrPlaylists) {
            this.nrPlaylists = (int) count;
            this.utilizador = u;
        }
    }
}

    /**
     * Método que responde á query 
     * @param extra_info Lista com informaçao extra caso necessario
     * @return String com a resposta
     */
    public String respondeQuery(List<Object> extra_info) {
        if (extra_info == null || extra_info.size() < 1) {
            return "Informação extra insuficiente para executar a query.";
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Utilizador> utilizadores = (Map<String, Utilizador>) extra_info.get(0);
            this.calcula(utilizadores);

        } catch (Exception e) {
            return "Erro ao processar os parâmetros da query.";
        }
        return "Utilizador com mais playlists: " + this.utilizador.getNome() + " com " + this.nrPlaylists + " playlists!!"; 
    }

    /**
     * Implementação do método de clonagem da Query7
     * @return Objeto do tipo Query7
     */
    @Override
    public Query7 clone() {return new Query7(this);} 

    /**
     * Implementaçãodo método de igualdade entre duas Query7
     * @param Query7 para ser comparada com o recetor
     * @return Booleano ture ou false
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Query7 other = (Query7) obj;
        return this.nrPlaylists == other.getNrPlaylists() && this.utilizador.equals(other.getUtilizador());
    }

    /**
     * Metodo toString
     */
    @Override
    public String toString() {
        return "O utilizador com mais playlists é " + this.utilizador.getNome() + " com "+ this.nrPlaylists + " playlists.";
    }
    
}