package entidades;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Playlist implements Serializable {
    private String nome;
    private List<String> musicas;
    private boolean publica;
    
     /**
     * Construtor por omissão.
     */
    public Playlist() {
        this.nome = "";
        this.musicas = new ArrayList<>();
        this.publica = false;
    }

    /**
     * Construtor parametrizado.
     * @param nome Nome da playlist.
     * @param musicas Lista de músicas.
     * @param publica Indica se a playlist é pública.
     */
    public Playlist(String nome, List<String> musicas, boolean publica) {
        this.nome = nome != null ? nome : "";
        this.musicas = musicas != null ? new ArrayList<>(musicas) : new ArrayList<>();
        this.publica = publica;
    }

     /**
     * Construtor de cópia.
     * @param playlist Playlist a copiar.
     */
    public Playlist (Playlist playlist) {
        this.nome    = playlist.getNome();
        this.musicas = playlist.getMusicas();
        this.publica = playlist.isPublica();  
    }

    // Getters

    /**
     * Obtém o nome da playlist.
     * @return Nome da playlist.
     */
    public String getNome()          { return this.nome; }

    /**
     * Obtém a lista de músicas da playlist.
     * @return Lista de músicas.
     */
    public List<String> getMusicas() { return new ArrayList<>(this.musicas);}

    /**
     * Verifica se a playlist é pública.
     * @return True se for pública, false caso contrário.
     */
    public boolean isPublica()       { return this.publica; }

    // Setters

       /**
     * Define o nome da playlist.
     * @param novoNome Novo nome da playlist.
     */
    public void setNome(String novoNome)         {if(novoNome != null) this.nome = novoNome;}

      /**
     * Define a lista de músicas.
     * @param musicas Nova lista de músicas.
     */
    public void setMusicas(List<String> musicas) {if(musicas != null )this.musicas = new ArrayList<>(musicas);}

    /**
     * Define a visibilidade da playlist.
     * @param publica True se for pública, false caso contrário.
     */
    public void setPrivacidade(boolean publica)  { this.publica = publica; }

    /**
     * Adiciona uma música à playlist.
     * @param m Nome da música a adicionar.
     */
    public void addMusica (String m) {if(m!= null) this.musicas.add(m);}

    /**
     * Remove uma música da playlist.
     * @param m Nome da música a remover.
     * @return True se a música foi removida, false caso contrário.
     */
    public boolean remMusica (String m) {
        List <String> nova = new ArrayList<>();
        for(String s : this.musicas) if (!m.equals(s)) nova.add(s);
        if (this.musicas.equals(nova)) return false;
        this.musicas = nova;
        return true;
    }

    /**
     * Devolve uma cópia da lista de músicas em ordem aleatória.
     * @return Lista de músicas baralhada.
     */
    public List<String> getMusicasEmOrdemAleatoria() {
        List<String> copia = new ArrayList<>(this.musicas);
        Collections.shuffle(copia);
        return copia;
    }

    /**
     * Cria uma cópia da playlist.
     * @return Cópia da playlist.
     */
    @Override
    public Playlist clone() {return new Playlist(this);}

    /**
     * Verifica se duas playlists são iguais.
     * @param o Objeto a comparar.
     * @return True se forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playlist other = (Playlist) o;
        return this.nome.equals(other.nome) &&
               this.musicas.equals(other.musicas); 
    }

    /**
     * Representação textual da playlist.
     * @return String com os dados da playlist.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Playlist {\n");
        sb.append("Nome: ").append(this.nome).append("\n");
        sb.append("Musicas: ").append(this.musicas).append("\n");
        sb.append("Publica: ").append(this.publica).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}