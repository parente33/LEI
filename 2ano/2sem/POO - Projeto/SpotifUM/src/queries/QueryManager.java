package queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import java.io.Serializable;


import entidades.Musica;
import entidades.Artista; 
import entidades.Utilizador;


public class QueryManager implements Serializable {
    private QuerySolver [] queries;
    private List<QuerySolver> listener_musicas;
    private List<QuerySolver> listener_artistas;
    private List<QuerySolver> listener_utilizador;
    private static final int queries_number = 7;


    /**
     * Construtor por omissão
     */
    public QueryManager(){
        this.listener_musicas = new ArrayList<>();
        this.listener_artistas = new ArrayList<>();
        this.listener_utilizador = new ArrayList<>();
        this.queries = new QuerySolver[queries_number];
        this.queries[0] = new Query1();
        this.queries[1] = new Query2();
        this.queries[2] = new Query3(); 
        this.queries[3] = new Query4();
        this.queries[4] = new Query5();
        this.queries[5] = new Query6();
        this.queries[6] = new Query7();
        this.callQueries();

    }
    /**
     * Construtor parameterizado
     * @param q1 Objeto Query1
     * @param q2 Objeto Query2
     * @param q3 Objeto Query3
     * @param q4 Objeto Query4
     * @param q5 Objeto Query5
     * @param q6 Objeto Query6
     * @param q7 Objeto Query7
     */
    public QueryManager(QuerySolver q1, QuerySolver q2, QuerySolver q3, QuerySolver q4, QuerySolver q5, QuerySolver q6, QuerySolver q7){
        this.listener_musicas = new ArrayList<>();
        this.listener_artistas = new ArrayList<>();
        this.listener_utilizador = new ArrayList<>();
        this.queries = new QuerySolver[queries_number];
        this.queries[0] = q1.clone();
        this.queries[1] = q2.clone();
        this.queries[2] = q3.clone(); 
        this.queries[3] = q4.clone();
        this.queries[4] = q5.clone(); 
        this.queries[5] = q6.clone(); 
        this.queries[6] = q7.clone(); 

        this.callQueries();
    }
    /**
     * Construtor de copia
     * @param q Objeto QueryManager 
     */
    public QueryManager(QueryManager q) {
        this.listener_musicas = new ArrayList<>();
        this.listener_artistas = new ArrayList<>();
        this.listener_utilizador = new ArrayList<>();
        QuerySolver [] queries_other = q.getQueries();
        this.queries = new QuerySolver[queries_number];
        this.queries[0] = queries_other[0];
        this.queries[1] = queries_other[1];
        this.queries[2] = queries_other[2]; 
        this.queries[3] = queries_other[3];
        this.queries[4] = queries_other[4];
        this.queries[5] = queries_other[5];
        this.queries[6] = queries_other[6];
        this.queries[7] = queries_other[7];

        
        this.callQueries();
    }
    /**
     * Metodo que invoca o metodo call de cada Query
     */
    public void callQueries() {
        for (QuerySolver q : this.queries) q.call(this);
    }

    /**
     * Devolve a lista com os pointer das queries que sao ListenerMusica
     * @return Lista de pointers de Queries
     */
    public List<QuerySolver> getListenerMusica() {return new ArrayList<>(this.listener_musicas);}
    
     /**
     * Devolve a lista com os pointer das queries que sao ListenerArtista
     * @return Lista de pointers de Queries
     */
    public List<QuerySolver> getListenerArtista() {return new ArrayList<>(this.listener_artistas);}

     /**
     * Devolve a lista com os pointer das queries que sao ListenerUtilizador
     * @return Lista de pointers de Queries
     */
    public List<QuerySolver> getListenerUtilizador() {return new ArrayList<>(this.listener_utilizador);}

    /**
     * Devolve um array com deep copy dos pointers das queries
     * @return
     */
    public QuerySolver[] getQueries() {
        QuerySolver[] copia = new QuerySolver[this.queries.length];
        for (int i = 0; i < this.queries.length; i++) {
            copia[i] = this.queries[i].clone(); 
        }
        return copia;
    }
        
    /**
     * Adiciona um pointer de um QuerySolver ao listener 
     * @param q QueySOlver
     */
    public void addMusicaListener(QuerySolver q) {
        if (q==null) return;
        this.listener_musicas.add(q);
    }

     /**
     * Adiciona um pointer de um QuerySolver ao listener 
     * @param q QueySOlver
     */
    public void addArtistaListener(QuerySolver q) {
        if (q==null) return;
        this.listener_artistas.add(q);
    }

     /**
     * Adiciona um pointer de um QuerySolver ao listener 
     * @param q QueySOlver
     */
    public void addUtilizadorListener(QuerySolver q) {
        if (q==null) return;
        this.listener_utilizador.add(q);
    }

    /**
     * Meotod que invoca a funcao listener dos pointers presentes na lista 
     * @param listener Lista com os pointers das QuerySOlver 
     * @param o Objeto a ser usado pelas Queries
     */
    private void callListeners(List<QuerySolver> listener,Object o) {
        for (QuerySolver q : listener) if (q!= null) q.listener(o);
    }

    /**
     * Metodo que encaminha o objeto para os listeners
     * @param o Objeto a ser usado
     */
    public void queryManageradd(Object o) {
        if (o instanceof Musica) this.callListeners(this.listener_musicas,o);
        if (o instanceof Artista) this.callListeners(this.listener_artistas,o);
        if (o instanceof Utilizador) this.callListeners(this.listener_utilizador,o);
    }

    /**
     * Metodo que ordena a resoluçao de uma query 
     * @param query_number Numero da query
     * @param extra_info Informaçao extra
     * @return String com a resposta 
     */
    public String solveQuery(int query_number,List<Object> extra_info) {
        if (query_number>0 && query_number<= queries_number) return queries[query_number-1].respondeQuery(extra_info);
        else return "Query não implementada";
    }

    /**
     * Implementaçãodo método de igualdade entre duas QueryManager
     * @param QueryManager para ser comparada com o recetor
     * @return Booleano ture ou false
     */
   @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        QueryManager other = (QueryManager) obj;

        return Arrays.equals(this.queries, other.getQueries()) && this.listener_musicas.equals(other.getListenerMusica()) && this.listener_artistas.equals(other.getListenerArtista()) && this.listener_utilizador.equals(other.getListenerUtilizador());
    }

    /**
     * Implementação do método de clonagem da QueryManager
     * @return Objeto do tipo QueryManager
     */
    @Override
    public QueryManager clone() {return new QueryManager(this);}

    /**
     * Metodo toString
     */
    @Override
    public String toString() {return "Queries {\n" + Arrays.toString(queries) + "}";}
    
    
}