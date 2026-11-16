package queries;

import java.util.List;
import java.io.Serializable;

import entidades.Musica;

public class Query1 implements QuerySolver, Serializable{
    
    private Musica musica;
    private int nReproducoes;

    /**
     * Construtor por omissão da Query1
     */
    public Query1() {
        this.musica = new Musica();
        this.nReproducoes = 0; 
    }

    /**
     * Construtor parameterizado da Query1
     * @param musica Objeto Musica 
     * @param nReproducoes numero de reproduçoes 
     */
    public Query1(Musica musica,int nReproducoes) {
        this.musica =musica.clone();
        this.nReproducoes = nReproducoes;
    }

    /**
     * Construtor de cópia.
     * @param query1 Objeto a copiar
     */
    public  Query1(Query1 query1) {
        this.musica = query1.getMusica();
        this.nReproducoes = query1.getnReproducoes();
    }

    /**
     * Devolve uma cópia da música.
     * @return Música clonada
     */
    public Musica getMusica() {return this.musica.clone();}

     /**
     * Atualiza a música.
     * @param m Nova Musica
     */
    public void   setMusica(Musica m) {this.musica = m.clone();}

    /**
     * Devolve o número de reproduções.
     * @return Número de reproduções
     */
    public int    getnReproducoes() {return this.nReproducoes;}
    /**
     * Atualiza o número de reproduções.
     * @param reproducoes Número de reproduções
     */
    public void   setnReproducoes(int reproducoes) {this.nReproducoes = reproducoes;}

    /**
     * Implementaçãodo método de igualdade entre duas Query1
     * @param Query1 para ser comparada com o recetor
     * @return Booleano ture ou false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Query1 other = (Query1) obj;
        return this.nReproducoes == other.getnReproducoes() &&
               this.musica.equals(other.getMusica());
    }
    
    /**
     * Método toString
     */
    @Override
    public String toString() {
        return "Query1{" +
               "musica=" + musica.toString() +
               ", nReproducoes=" + nReproducoes +
               '}';
    }
    
    /**
     * Implementação do método de clonagem da Query1
     * @return Objeto do tipo Query1
     */
    @Override
    public Query1 clone() { return new Query1(this);}
   
    
    /**
     Método que compara um Objeto Musica com a musica atual segundo o numero de reproducoes
     Caso seja maior, a musica é atualizada
     * @param m Objeto musica a ser comparado
     */
    public void compara(Musica m) {
        int reproducoes = m.getReproducoes();
        if (this.nReproducoes < reproducoes) {
            this.musica = m; 
            this.nReproducoes = reproducoes;
        }
    }

    /**
     * Metodo que recebe os objetos da App e os encaminha
     * @param o Objeto que caso seja Musica vai ser comparado 
     */
    public void listener(Object o) {
        if(o instanceof Musica) {
            Musica m = (Musica) o; 
            this.compara(m);
        }
    }

    /**
     * Método que define qual o listener a ser usado pelo Query1
     * @param Q Objeto QueryManager com os listeners 
     */
    public void call(QueryManager q) {
        q.addMusicaListener(this);
    }
    
    /**
     * Método que responde á query 
     * @param extra_info Lista com informaçao extra caso necessario
     * @return String com a resposta
     */
    public String respondeQuery(List<Object> extra_info) {return "A Musica mais reproduzida é: "+this.musica.getNome() + " com um total de " + this.nReproducoes+ " reproduções!!";} 
    
}