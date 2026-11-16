package entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Utilizador implements Serializable {

    private String nome;
    private String email;
    private String morada;
    private Plano plano;
    private double pontos;
    private List<String> playlists; // Biblioteca de playlits
    private List<String> albuns;   // Biblioteca de albuns
    private List<Historico> historicoReproducoes;

    /**
     * Construtor por omissão de Utilizador.
     */
    public Utilizador() {
        this.nome = "";
        this.email = "";
        this.morada = "";
        this.plano = new Free();
        this.pontos = 0;
        this.playlists = new ArrayList<>();
        this.albuns = new ArrayList<>();
        this.historicoReproducoes = new ArrayList<>();
    }

    /**
     * Construtor parametrizado de Utilizador.
     * @param nome Nome do utilizador
     * @param email Email do utilizador
     * @param morada Morada do utilizador
     * @param plano Plano associado
     * @param pontos Pontos acumulados
     * @param playlists Lista de playlists
     * @param albuns Lista de álbuns
     * @param historico Lista do histórico de audição
     */
    public Utilizador(String nome, String email, String morada, Plano plano, double pontos, List<String>playlists,List<String>albuns, List<Historico>historico) {
        this.nome = nome != null ? nome : "";
        this.email = email != null ? email : "";
        this.morada = morada != null ? morada : "";
        this.plano = plano != null ? plano : new Free();
        this.pontos = pontos;
        this.playlists = playlists != null ? new ArrayList<>(playlists) : new ArrayList<>();
        this.albuns = albuns != null ? new ArrayList<>(albuns) : new ArrayList<>();
        this.historicoReproducoes = historico != null ? new ArrayList<>(historico) : new ArrayList<>();
    }

    /**
     * Construtor de cópia de Utilizador.
     * @param u Utilizador a copiar
     */
    public Utilizador(Utilizador u) {
        this.nome = u.getNome();
        this.email = u.getEmail();
        this.morada = u.getMorada();
        this.plano = u.getPlano();
        this.pontos = u.getPontos();
        this.playlists = u.getPlaylists();
        this.albuns = u.getAlbuns();
        this.historicoReproducoes = u.getHistorico();
    }

    // Getters

    /**
     * Devolve o nome do Utilizador
     * @return Nome do utilizador
     */
    public String getNome() { return this.nome; }

    /**
     * Devolve o email do utilizador
     * @return Email do utilizador
     */
    public String getEmail() { return this.email; }

    /**
     * Devolve a morada do Utilizador
     * @return Morada do Utilizador
     */
    public String getMorada() { return this.morada; }

    /**
     * Devolve o Plano do Utilizador
     * @return Plano do Utilizador
     */
    public Plano getPlano() { return this.plano; }

    /**
     * Devolve os pontos do Utilizador
     * @return Pontos do utilizador
     */
    public double getPontos() { return this.pontos; }

    /**
     * Devolve a biblioteca de Playlists do Utilizador
     * @return Copia da lista de Playlists do Utilizador
     */
    public List<String> getPlaylists() { return new ArrayList<>(this.playlists); }

    /**
     * Devolve a biblioteca de Albuns do Utilizador
     * @return Copia da lista de albuns do utilizador
     */
    public List<String> getAlbuns() { return new ArrayList<>(this.albuns); }

    /**
     * Devolve o historico do Utilizador
     * @return Copia da lista com o historico do utilizador
     */
    public List<Historico> getHistorico() { return copiaHistoricos(this.historicoReproducoes); }

    // Setters

    /**
     * Atualiza o nome do Utilizador
     * @param novoNome Novo nome
     */
    public void setNome(String novoNome) { if (novoNome != null) this.nome = novoNome; }

    /**
     * Atualiza o email do Utilizador
     * @param novoEmail Novo email
     */
    public void setEmail(String novoEmail) { if (novoEmail != null) this.email = novoEmail; }

    /**
     * Atualiza a morada do Utilizador
     * @param novaMorada Nova morada
     */
    public void setMorada(String novaMorada) { if (novaMorada != null) this.morada = novaMorada; }

    /**
     * Atualiza o Plano do Utilizador
     * @param novoPlano Novo Plano
     */
    public void setPlano(Plano novoPlano) { if (novoPlano != null) this.plano = novoPlano; }

    /**
     * Atualiza os pontos do Utilizador
     * @param novosPontos Novos Pontos
     */
    public void setPontos(double novosPontos) { this.pontos = novosPontos; }

    /**
     * Atualiza as playlists do Utilizador
     * @param p Nova lista das Playlists
     */
    public void setPlaylists(List<String> p) { if (p != null) this.playlists = new ArrayList<>(p); }

    /**
     * Atualiza os albuns do Utilizador
     * @param a Nova lista de albuns
     */
    public void setAlbuns(List<String> a) { if (a != null) this.albuns = new ArrayList<>(a); }

    /**
     * Atualiza a lista de historico do Utilizador
     * @param hist Nova lista de historico
     */
    public void setHistorico(List<Historico> hist) { if (hist != null) this.historicoReproducoes = copiaHistoricos(hist); }

    /**
     * Adiciona uma playlist ao utilizador.
     * @param playlist Nome da playlist
     * @return true se adicionada, false se já existia
     */
    public boolean addPlaylist(String playlist) {
        if (playlist != null && this.existePlaylist(playlist)) return false;
        this.playlists.add(playlist);
        return true;
    }

    /**
     * Remove uma playlist do utilizador.
     * @param playlist Nome da playlist
     * @return true se removida, false se não existia
     */
    public boolean remPlaylist(String playlist) {
        List<String> nova = new ArrayList<>();
        for (String s : this.playlists) if (!playlist.equals(s)) nova.add(s);
        if (this.playlists.equals(nova)) return false;
        this.playlists = nova;
        return true;
    }

    /**
     * Adiciona um álbum ao utilizador.
     * @param album Nome do álbum
     * @return true se adicionado, false se já existia
     */
    public boolean addAlbum(String album) {
        if (album != null && this.existeAlbum(album)) return false;
        this.albuns.add(album);
        return true;
    }

    /**
     * Remove um álbum do utilizador.
     * @param album Nome do álbum
     * @return true se removido, false se não existia
     */
    public boolean remAlbum(String album) {
        List<String> nova = new ArrayList<>();
        for (String s : this.albuns) if (!album.equals(s)) nova.add(s);
        if (this.albuns.equals(nova)) return false;
        this.albuns = nova;
        return true;
    }

    /**
     * Acrescenta pontos ao utilizador.
     * @param pontos Valor de pontos a acrescentar
     */
    public void addPontos(double pontos) {
        this.pontos += pontos;
    }

    /**
     * Adiciona uma música ao histórico de reprodução.
     * @param musica Nome da música
     */
    public void addHistorico(String musica) {
        Historico h = new Historico(musica);
        this.historicoReproducoes.add(h);
    }

    /**
     * Adiciona um objeto Historico ao histórico.
     * @param h Objeto Historico
     */
    public void addHistorico(Historico h) {
        this.historicoReproducoes.add(h);
    }

    /**
     * Verifica se o utilizador possui um álbum.
     * @param album Nome do álbum
     * @return true se existir
     */
    public boolean existeAlbum(String album) {
        for (String a : this.albuns) if (a.equals(album)) return true;
        return false;
    }

    /**
     * Verifica se o utilizador possui uma playlist.
     * @param playlist Nome da playlist
     * @return true se existir
     */
    public boolean existePlaylist(String playlist) {
        for (String p : this.playlists) if (p.equals(playlist)) return true;
        return false;
    }

    /**
     * Devolve uma cópia do utilizador.
     * @return Clone do utilizador
     */
    @Override
    public Utilizador clone() {
        return new Utilizador(this);
    }

    /**
     * Compara dois utilizadores.
     * @param o Objeto a comparar
     * @return true se forem iguais
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizador)) return false;
        Utilizador other = (Utilizador) o;
        return this.nome.equals(other.getNome()) &&
               this.email.equals(other.getEmail()) &&
               this.morada.equals(other.getMorada()) &&
               this.plano.equals(other.getPlano()) &&
               this.pontos == other.getPontos() &&
               this.playlists.equals(other.getPlaylists()) &&
               this.albuns.equals(other.getAlbuns()) &&
               this.historicoReproducoes.equals(this.getHistorico());
    }

    /**
     * Representação textual do utilizador.
     * @return String com dados do utilizador
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Utilizador {\n");
        sb.append("Nome: ").append(nome).append("\n");
        sb.append("Email: ").append(email).append("\n");
        sb.append("Morada: ").append(morada).append("\n");
        sb.append("Plano: ").append(plano).append("\n");
        sb.append("Pontos: ").append(pontos).append("\n");
        sb.append("Playlists : ").append(playlists).append("\n");
        sb.append("Albuns: ").append(this.albuns).append("\n");
        sb.append("Histórico de audição: ").append(historicoReproducoes).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Cria cópia profunda da lista de históricos.
     * @param lista Lista original
     * @return Nova lista com clones
     */
    private static List<Historico> copiaHistoricos(List<Historico> lista) {
        if (lista == null ) return new ArrayList<>();
        List<Historico> nova = new ArrayList<>();
        for (Historico h: lista) if (h != null) nova.add(h.clone());
        return nova;
    }
}
