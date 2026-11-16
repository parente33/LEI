package entidades;

import java.io.Serializable;

public class Artista implements Serializable {
    private String nome; 
    private boolean isgroup;   
    private int nReproducoes; 

    /**
     * Construtor por omissão de Artista
     */
    public Artista() {
        this.nome = "";
        this.isgroup = false; 
        this.nReproducoes = 0; 
    }
    /**
     * Construtor parametrizado de Artista
     * @param nome Nome do artista
     * @param isgroup Booleano que indica se é um grupo
     * @param nReproducoes Número de reproduções
     */
    public Artista(String nome, boolean isgroup,int nReproducoes) {
        this.nome = nome;
        this.isgroup = isgroup; 
        this.nReproducoes = nReproducoes;
    }

    /**
     * Construtor de cópia de Artista
     * @param a Outro objeto do tipo Artista
     */
    public Artista(Artista a) {
        this.nome = a.getNome(); 
        this.isgroup = a.getIsGroup();
        this.nReproducoes = a.getnReproducoes();
    }


    /**
     * Incrementa o número de reproduções do artista
     */
    public void addReproducao() {
        this.nReproducoes++;
    }
    // Getters 

     /**
     * Devolve o nome do artista
     * @return Nome do artista
     */
    public String  getNome()    {return this.nome;   } 

    /**
     * Devolve se o artista é um grupo
     * @return true se for grupo, false caso contrário
     */
    public boolean getIsGroup() {return this.isgroup;}

     /**
     * Devolve o número de reproduções do artista
     * @return Número de reproduções
     */
    public int     getnReproducoes() {return this.nReproducoes;} 
    
    
    // Setters 


    /**
     * Atualiza o nome do artista
     * @param nome Novo nome do artista
     */
    public void setNome(String nome)        {if(nome != null) this.nome = nome;} 

     /**
     * Atualiza o estado do artista (grupo ou não)
     * @param isgroup true se for grupo, false caso contrário
     */
    public void setIsGroup(boolean isgroup) {this.isgroup= isgroup;}

     /**
     * Atualiza o número de reproduções do artista
     * @param nReproducoes Novo número de reproduções
     */
    public void setnReproducoes(int nReproducoes) {this.nReproducoes = nReproducoes;}

      /**
     * Implementação do método de clonagem de Artista
     * @return Objeto do tipo Artista
     */
    @Override 
    public Artista clone() {return new Artista(this);} 

    /**
     * Implementação do método de igualdade entre dois artistas
     * @param o Objeto a comparar
     * @return true se forem iguais, false caso contrário
     */
    @Override 
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artista a = (Artista) o;
        return this.nome.equals(a.getNome()) && this.isgroup == a.getIsGroup() && this.nReproducoes == a.getnReproducoes();
    }

    /**
     * Método toString
     * @return Representação textual do artista
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Artista{\n");
        sb.append("Nome: ").append(this.nome+  "\n");
        sb.append("Grupo: ").append(this.isgroup+ "\n");
        sb.append("Reproduçoes: ").append(this.nReproducoes);
        sb.append("}\n");
        return sb.toString();
    }

}