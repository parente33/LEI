package queries;

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;
import java.util.List;

import entidades.Musica;

public class Query5 implements QuerySolver,Serializable{

    private Map<String,Musica> musicas_guardadas;
    private Map<String,Integer> generos;

    /**
     * Construtor por omissão
     */
    public Query5() {
        generos = new HashMap<>();
        musicas_guardadas = new HashMap<>();
    }

    /**
     * Construtor parameterizado
     * @param generos Estrutura com generos 
     * @param musicas_guardadas Estrutura com as musicas ja avaliadas
     */
    public Query5(Map<String,Integer> generos ,Map<String,Musica> musicas_guardadas) {
        this.generos = new HashMap<>(generos);
        this.musicas_guardadas = new HashMap<>(musicas_guardadas);
    }
    /**
     * Construtor de cópia
     * @param q Objeto Query5 a ser copiado
     */
    public Query5(Query5 q) {
        this.generos = q.getGeneros();
        this.musicas_guardadas = q.getMusicas();
    }

    /**
     * Devolve uma estrutura Map com os generos e suas reproducoes
     * @return Estrutura com os Generos
     */
    public Map<String,Integer> getGeneros() {return new HashMap<>(this.generos);}

    /**
     *Atualiza uma estrutura Map com os generos e suas reproducoes
     * @param generos Estrutura para atualizar 
     */
    public void setGeneros(Map<String, Integer> generos) {this.generos = new HashMap<>(generos);}

    /**
     * Devolve uma estrutura Map com as musicas ja avaliadas
     *@return Estrutura com as musicas
     */
    public Map<String,Musica> getMusicas() {return new HashMap<>(this.musicas_guardadas);}

    /**
     * Atualiza as musicas avaliadas
     * @param musicas Estrutura com as musicas
     */
    public void setMusicas(Map<String, Musica   > musicas) {this.musicas_guardadas = new HashMap<>(musicas);}


    /**
     * Caso a musica nao tenho ainda sido avaliada adiciona no seu genero o numero de reproduçoes
     * caso ja tenha sido avaliada o numero de reproducoes do genero é incrementado
     * @param m Musica
     */
    public void adiciona(Musica m) {
        String tipo = m.getGenero().name();
        if (this.musicas_guardadas.containsKey(m.getNome())) generos.put(tipo, generos.get(tipo)+ 1);
        else {
            this.musicas_guardadas.put(m.getNome(),m);
            this.generos.put(tipo,m.getReproducoes());
        }
    }

    /**
     * Devolve o genero mais reproduzido
     * @return String com o nome do genero mais reproduzido
     */
    public String maisProduzido() {
        String generoMaisReproduzido = null;
        int max = -1;
    
        for (Map.Entry<String, Integer> entry : generos.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                generoMaisReproduzido = entry.getKey();
            }
        }
    
        return generoMaisReproduzido;
    }
    
    /**
     * Metodo que recebe os objetos da App e os encaminha
     * @param o Objeto que caso seja Musica vai adicionado 
     */
    public void listener(Object o) {
        if(o instanceof Musica) {
            Musica m = (Musica) o; 
            this.adiciona(m);;
        }
    }

    /**
     * Método que define qual o listener a ser usado pelo Query2
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
    public String respondeQuery(List<Object> extra_info) {
        if (this.generos.isEmpty()) return "Nenhum tipo foi reproduzido";
        return "O tipo de música mais reproduzida é: " + this.maisProduzido();
    } 

    /**
     * Implementação do método de clonagem da Query5
     * @return Objeto do tipo Query5
     */
    @Override
    public Query5 clone() {
        return new Query5(this);
    }

    /**
     * Metodo toString
     */
    @Override
    public String toString() {
        return "Query5{" +
               "generos=" + generos.toString() +
               '}';
    }

    /**
     * Implementaçãodo método de igualdade entre duas Query5
     * @param Query5 para ser comparada com o recetor
     * @return Booleano ture ou false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Query5 other = (Query5) obj;
        return this.generos.equals(other.getGeneros()) && this.musicas_guardadas.equals(other.getMusicas());
    }
    
}
