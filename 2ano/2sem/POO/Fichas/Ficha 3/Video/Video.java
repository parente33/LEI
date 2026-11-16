import java.util.ArrayList;
import java.util.Date;

/**
 * Classe que representa um vídeo no YouTube, com atributos como nome, conteúdo, data de carregamento, resolução, 
 * duração, comentários e contadores de "likes" e "dislikes".
 */
public class Video {
    private String nome;
    private byte[] conteudo;
    private Date dataCarregamento;
    private String resolucao;
    private int duracaoMinutos;
    private int duracaoSegundos;
    private ArrayList<String> comentarios;
    private int likes;
    private int dislikes;

    /** Construtor por omissão */
    public Video() {
        this.nome = "";
        this.conteudo = new byte[0];
        this.dataCarregamento = new Date();
        this.resolucao = "";
        this.duracaoMinutos = 0;
        this.duracaoSegundos = 0;
        this.comentarios = new ArrayList<>();
        this.likes = 0;
        this.dislikes = 0;
    }

    /** Construtor parametrizado */
    public Video(String nome, byte[] conteudo, Date dataCarregamento, String resolucao, int duracaoMinutos, int duracaoSegundos) {
        this.nome = nome;
        this.conteudo = conteudo;
        this.dataCarregamento = dataCarregamento;
        this.resolucao = resolucao;
        this.duracaoMinutos = duracaoMinutos;
        this.duracaoSegundos = duracaoSegundos;
        this.comentarios = new ArrayList<>();
        this.likes = 0;
        this.dislikes = 0;
    }

    /** Construtor de cópia */
    public Video(Video outroVideo) {
        this.nome = outroVideo.getNome();
        this.conteudo = outroVideo.getConteudo();
        this.dataCarregamento = outroVideo.getDataCarregamento();
        this.resolucao = outroVideo.getResolucao();
        this.duracaoMinutos = outroVideo.getDuracaoMinutos();
        this.duracaoSegundos = outroVideo.getDuracaoSegundos();
        this.comentarios = new ArrayList<>(outroVideo.getComentarios());
        this.likes = outroVideo.getLikes();
        this.dislikes = outroVideo.getDislikes();
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public byte[] getConteudo() { return conteudo; }
    public void setConteudo(byte[] conteudo) { this.conteudo = conteudo; }

    public Date getDataCarregamento() { return dataCarregamento; }
    public void setDataCarregamento(Date dataCarregamento) { this.dataCarregamento = dataCarregamento; }

    public String getResolucao() { return resolucao; }
    public void setResolucao(String resolucao) { this.resolucao = resolucao; }

    public int getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(int duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }

    public int getDuracaoSegundos() { return duracaoSegundos; }
    public void setDuracaoSegundos(int duracaoSegundos) { this.duracaoSegundos = duracaoSegundos; }

    public ArrayList<String> getComentarios() { return comentarios; }
    public void setComentarios(ArrayList<String> comentarios) { this.comentarios = comentarios; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public int getDislikes() { return dislikes; }
    public void setDislikes(int dislikes) { this.dislikes = dislikes; }

    /** Método para inserir um comentário */
    public void insereComentario(String comentario) {
        this.comentarios.add(comentario);
    }

    /** Método que calcula quantos dias se passaram desde o carregamento */
    public long qtsDiasDepois() {
        long diffInMillies = Math.abs(new Date().getTime() - this.dataCarregamento.getTime());
        return diffInMillies / (1000 * 60 * 60 * 24);
    }

    /** Método que faz um like */
    public void thumbsUp() {
        this.likes++;
    }

    /** Método que processa o conteúdo do vídeo para ser enviado para o render */
    public String processa() {
        return "Conteúdo do vídeo: " + nome + ", " + resolucao + ", " + duracaoMinutos + "min " + duracaoSegundos + "s.";
    }

    /** Método toString para representação textual */
    @Override
    public String toString() {
        return "Vídeo: " + nome + ", Resolução: " + resolucao + ", Duração: " + duracaoMinutos + "m " + duracaoSegundos + "s, " +
                "Likes: " + likes + ", Dislikes: " + dislikes;
    }

    /** Método equals para comparação de objetos */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Video video = (Video) obj;
        return nome.equals(video.nome);
    }

    /** Método clone para criar uma cópia do objeto */
    @Override
    public Video clone() {
        return new Video(this);
    }
}