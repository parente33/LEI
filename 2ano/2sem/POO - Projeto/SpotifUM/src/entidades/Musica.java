package entidades;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Musica implements Serializable {
    private String nome;
    private String interprete;
    private String editora;
    private String letra;
    private String album;
    private List<String> partitura;
    private Genero genero;
    private int duracao;
    private int nReproducoes;
    private boolean explicita; 
    private boolean multimedia; 
    public enum Genero {POP, RAP,ROCK,JAZZ,ELETRONIC,FUNK};

    /**
     * Construtor por omissão da classe Musica.
     */
    public Musica() {
        this.nome = ""; 
        this.interprete = "";
        this.editora = "";
        this.letra = "";
        this.album = "";
        this.partitura = new ArrayList<>();
        this.genero = null;
        this.duracao = 0;
        this.nReproducoes = 0;
        this.explicita = false; 
        this.multimedia = false;
    }

    /**
     * Construtor parametrizado da classe Musica.
     * @param nome Nome da música
     * @param interprete Nome do intérprete
     * @param editora Nome da editora
     * @param letra Letra da música
     * @param album Nome do álbum
     * @param partitura Lista de notas da partitura
     * @param genero Género musical
     * @param duracao Duração da música em segundos
     * @param reproducoes Número de reproduções
     * @param explicita Indica se a música é explícita
     * @param multimedia Indica se a música tem conteúdo multimédia
     */
    public Musica(String nome, String interprete, String editora, String letra,String album, List<String> partitura, Genero genero, int duracao,int reproducoes, boolean explicita, boolean multimedia) {
        this.nome = nome != null ? nome : ""; // ou apenas a string
        this.interprete = interprete != null ? interprete : "";
        this.editora = editora != null ? editora : "";
        this.letra = letra != null ? letra : "";
        this.album = album != null ? album : "";
        this.partitura = partitura != null ? new ArrayList<>(partitura): new ArrayList<>();
        this.genero = genero;
        this.duracao = duracao;
        this.nReproducoes = reproducoes;
        this.explicita = false;
        this.multimedia = false;
        this.explicita = explicita; 
        this.multimedia = multimedia;
    }

    /**
     * Construtor de cópia.
     * @param m Objeto Musica a copiar
     */
    public Musica (Musica m) {
        this.nome = m.getNome();
        this.interprete = m.getInterprete(); 
        this.editora = m.getEditora(); 
        this.letra = m.getLetra(); 
        this.album = m.getAlbum();
        this.partitura = m.getPartitura();
        this.genero = m.getGenero(); 
        this.duracao = m.getDuracao(); 
        this.nReproducoes = m.getReproducoes();
        this.explicita = m.isExplicita();
        this.multimedia = m.isMultimedia();
    }

    // Getters

    /**
     * Devolve o nome da música.
     */
    public String getNome() { return this.nome; }

    /**
     * Devolve o nome do intérprete.
     */
    public String getInterprete() { return this.interprete; }

    /**
     * Devolve a editora da música.
     */
    public String getEditora() { return this.editora; }


    /**
     * Devolve a letra da música.
     */
    public String getLetra() { return this.letra; }

    /**
     * Devolve o álbum da música.
     */
    public String getAlbum() {return this.album;}

    /**
     * Devolve uma cópia da partitura.
     */
    public List<String> getPartitura() { return new ArrayList<>(this.partitura); }

    /**
     * Devolve o género musical.
     */
    public Genero getGenero() { return this.genero; }

    /**
     * Devolve a duração da música.
     */
    public int getDuracao() { return this.duracao; }

    /**
     * Devolve o número de reproduções.
     */
    public int getReproducoes() { return this.nReproducoes; }

     /**
     * Verifica se a música é explícita.
     */
    public boolean isExplicita() { return this.explicita; }

     /**
     * Verifica se a música tem conteúdo multimédia.
     */
    public boolean isMultimedia() { return this.multimedia; }

    // Setters

     /**
     * Atualiza o nome da música.
     */
    public void setNome(String novoNome) { this.nome = novoNome != null ? novoNome : ""; } 

     /**
     * Atualiza o intérprete.
     * @param novoInterprete Novo interprete 
     */
    public void setInterprete(String novoInterprete) { this.interprete = novoInterprete != null ? novoInterprete : ""; }

    /**
     * Atualiza a editora
     * @param novaEditora Nova editora
     */
    public void setEditora(String novaEditora) { this.editora = novaEditora != null ? novaEditora : ""; }

    /**
     * Atualiza a letra
     * @param novaLetra Nova letra
     */
    public void setLetra(String novaLetra) { this.letra = novaLetra != null ? novaLetra : ""; }

    /**
     * Atualiza o album 
     * @param album Novo nome do album
     */
    public void setAlbum(String album) {this.album = album != null ? album : "";}

    /**
     * Atualiza a partitura
     * @param novaPartitura Nova partitura
     */
    public void setPartitura(List<String> novaPartitura) {
        this.partitura = novaPartitura != null ? new ArrayList<>(novaPartitura) : new ArrayList<>();
    }

    /**
     * Atualiza o género
     * @param novoGenero Novo genero
     */
    public void setGenero(Genero novoGenero) {if(novoGenero != null) this.genero = novoGenero;}

    /**
     * Atualiza a duração
     * @param novaDuracao Nova duração
     */
    public void setDuracao(int novaDuracao) { this.duracao = Math.max(0, novaDuracao); } // Evita durações negativas

    /**
     * Atualiza a explicidade
     * @param explicita Nova explicidade
     */
    public void setExplicita(boolean explicita) { this.explicita = explicita; }
    /**
     * Atualiza se a musica contem multimedia
     * @param multimedia Boolean que diz se tem multimedia
     */
    public void setMultimedia(boolean multimedia) { this.multimedia = multimedia; }


    /**
     * Incrementa o número de reproduções em 1.
     */
    public void addReproducao() {this.nReproducoes++;}


     /**
     * Cria uma cópia do objeto.
     * @return Nova Musica com os mesmos dados
     */
    @Override
    public Musica clone() {
    return new Musica(this);
    }
        
      /**
     * Verifica se duas músicas são iguais.
     * @param o Objeto a comparar
     * @return true se forem iguais, false caso contrário
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Musica other = (Musica) o;
        return this.nome.equals(other.getNome()) && 
               this.interprete.equals(other.getInterprete()) &&
               this.editora.equals(other.getEditora()) && 
               this.letra.equals(other.getLetra()) && 
               this.album.equals(other.getAlbum()) && 
               this.partitura.equals(other.getPartitura()) && 
               this.genero.equals(other.getGenero()) && 
               this.duracao == other.getDuracao() && 
               this.nReproducoes == other.nReproducoes;
                
    }
    
    /**
     * Representação textual da música.
     * @return String com os detalhes da música
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Música {\n");
        sb.append("Nome: ").append(this.nome).append("\n");
        sb.append("Intérprete: ").append(this.interprete).append("\n");
        sb.append("Editora: ").append(this.editora).append("\n");
        sb.append("Letra: ").append(this.letra).append("\n");
        sb.append("Album: ").append(this.album).append("\n");
        sb.append("Partitura: ").append(this.partitura).append("\n");
        sb.append("Genero: ").append(this.genero).append("\n");
        sb.append("Duracao: ").append(this.duracao).append("\n");
        sb.append("Nº reproduções: ").append(this.nReproducoes).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Converte uma string para o género musical correspondente.
     * @param s String a converter
     * @return Género musical correspondente ou null se inválido
     */
    public static Genero toGenero(String s) {
        if (s == null) return null;
        try {
            return Genero.valueOf(s.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}