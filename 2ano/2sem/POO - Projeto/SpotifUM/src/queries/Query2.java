package queries;

import java.util.List;
import java.io.Serializable;

import entidades.Artista;

public class Query2 implements QuerySolver, Serializable{

    private Artista artista; 
    private int nReproducoes;

    /**
     * Construtor por omissão
     */
    public Query2() {
        this.artista = new Artista();
        this.nReproducoes = -1;
    }

    /**
     * Construtor parameterizado da Query2 
     * @param artista Objeto Artista 
     * @param nReproducoes Numero de reproduçoes
     */
    public Query2(Artista artista, int nReproducoes) {
        this.artista = artista.clone(); 
        this.nReproducoes = nReproducoes;
    }

    /**
     * Construtor de cópia
     * @param other Objeto Query2 a ser copiado
     */
    public Query2(Query2 other) {
        this.artista = other.getArtista(); 
        this.nReproducoes = other.getnReproducoes();
    }

    /**
     * Devolve o artista
     * @return Artista
     */
    public Artista getArtista() {return this.artista.clone();}

    /**
     * Atualza o artista
     * @param artista Artista para atualizar a Query2
     */
    public void setArtista(Artista artista) {this.artista = artista.clone(); }

    /**
     * Devolve o numero de reproducoes
     * @return Numero de reproducoes
     */
    public int getnReproducoes() {return this.nReproducoes;}

    /**
     * Atualiza o numero de reproducoes
     * @param nReproducoes Numero de reproducoes para atualizar a Query2
     */
    public void setnReproducoes(int nReproducoes) {this.nReproducoes = nReproducoes;}

    /**
     * Método que compara um Artista com o atual segundo o maior numero de reproducoes
     * Caso seja maior, o artista é atualizado
     * @param a Objeto Artista a comparar
     */
    public void compara(Artista a){
        int reproducoes = a.getnReproducoes();

        if (this.nReproducoes < a.getnReproducoes()) {
            this.artista = a; 
            this.nReproducoes = reproducoes;
        }
    }

    /**
     * Metodo que recebe os objetos da App e os encaminha
     * @param o Objeto que caso seja Artista vai ser comparado 
     */
    public void listener(Object o) {
        if(o instanceof Artista) {
            Artista a =(Artista) o;
            this.compara(a);
        }
    }

    /**
     * Método que define qual o listener a ser usado pelo Query2
     * @param Q Objeto QueryManager com os listeners 
     */
    public void call(QueryManager q) {
        q.addArtistaListener(this);
    }

    /**
     * Método que responde á query 
     * @param extra_info Lista com informaçao extra caso necessario
     * @return String com a resposta
     */
    public String respondeQuery(List<Object> extra_info){
        return "O intérprete mais escutado é: "+ this.artista.getNome() + " com um total de " + this.nReproducoes + " reproduções!!";
    }
    

    /**
     * Implementação do método de clonagem da Query2
     * @return Objeto do tipo Query2
     */
    @Override
    public Query2 clone() {return new Query2(this);}

    /**
     * Metodo toString
     */
    @Override
    public String toString() {
        return "Query2{" +
           "    artista=" + artista +
            ", nReproducoes=" + nReproducoes +
            '}';
    }
    /**
     * Implementaçãodo método de igualdade entre duas Query2
     * @param Query2 para ser comparada com o recetor
     * @return Booleano ture ou false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Query2 query2 = (Query2) o;
        return nReproducoes == query2.getnReproducoes() && this.artista.equals(query2.getArtista());
    }
}