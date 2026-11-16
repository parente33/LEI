package queries;

import java.util.List;
import java.io.Serializable;

import entidades.Utilizador;

public class Query4 implements QuerySolver, Serializable {

    private Utilizador utilizador;
    private double pontos;
    /**
     * Construtor por omissão
     */
    public Query4() {
        this.utilizador = new Utilizador();
        this.pontos = -1.0;
    }
    
    /**
     * Construtor parameterizado da 
     * @param utilizador Objeto Utilizador
     * @param pontos Pontos
     */
    public Query4(Utilizador utilizador, double pontos) {
        this.utilizador = utilizador.clone();
        this.pontos = pontos;
    }

    /**
     * Construtor de cópia
     * @param q Objeto Query4 a ser copiado
     */
    public Query4(Query4 q) {
        this.utilizador = q.getUtilizador();
        this.pontos = q.getPontos();
    }

    /**
     * Devolve o utilizador atual da query4
     * @return Objeto Utilizador
     */
    public Utilizador getUtilizador() {
        return this.utilizador.clone();
    }

    /**
     * Atualiza o Utilizador da Query4
     * @param utilizador Objeto Utilizador
     */
    public void setUtilizador(Utilizador utilizador) {
        this.utilizador = utilizador.clone();
    }

    /**
     * Devolve os pontos do utilizador atual da Query4
     * @return ponots do Utilizador
     */
    public double getPontos() {
        return this.pontos;
    }
    /**
     * Atualiza os pontos do utilizador atual da Query4
     * @param pontos Pontos 
     */
    public void setPontos(double pontos) {
        this.pontos = pontos;
    }

    /**
     * Implementaçãodo método de igualdade entre duas Query4
     * @param Query1 para ser comparada com o recetor
     * @return Booleano ture ou false
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Query4 other = (Query4) obj;
        return this.pontos == other.pontos &&
               this.utilizador.equals(other.getUtilizador());
    }

    /**
    * Método toString
    */
    @Override
    public String toString() {
        return "Query4{" +
               "utilizador=" + utilizador.toString() +
               ", pontos=" + pontos +
               '}';
    }

    /**
     * Implementação do método de clonagem da Query4
     * @return Objeto do tipo Query4
     */
    @Override
    public Query4 clone() {
        return new Query4(this);
    }

    /**
     * Método que compara um Utilizador com o atual segundo o maior numero de pontos
     * Caso seja maior, o Utilizador é atualizado
     * @param u Objeto Utilizador a comparar
     */
    public void compara(Utilizador u) {
        double pontosU = u.getPontos();
        if (this.pontos < pontosU) {
            this.utilizador = u;
            this.pontos = pontosU;
        }
    }

    /**
     * Metodo que recebe os objetos da App e os encaminha
     * @param o Objeto que caso seja Utilizador vai ser comparado 
     */
    public void listener(Object o) {
        if (o instanceof Utilizador) {
            Utilizador u = (Utilizador) o;
            this.compara(u);
        }
    }

    /**
     * Método que define qual o listener a ser usado pelo Query4
     * @param Q Objeto QueryManager com os listeners 
     */
    public void call(QueryManager q) {
        q.addUtilizadorListener(this);
    }

    /**
     * Método que responde á query 
     * @param extra_info Lista com informaçao extra caso necessario
     * @return String com a resposta
     */
    @Override
    public String respondeQuery(List<Object> extra_info) {
        return "O utilizador com mais pontos é: " +
               this.utilizador.getNome() +
               " com " + this.pontos + " pontos!";
    }
}
