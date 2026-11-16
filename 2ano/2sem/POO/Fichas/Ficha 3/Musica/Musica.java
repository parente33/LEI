import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe que representa uma Música com os seus atributos e funcionalidades associadas.
 * Inclui nome, autor, intérprete, letra (poema), música (notação), duração e número de vezes escutada.
 */
public class Musica {
    // Variáveis de instância
    private String nome;
    private String interprete;
    private String autor;
    private String editora;
    private String[] letra;
    private String[] musica;
    private int duracao;
    private int vezesEscutada;

    /**
     * Construtor por omissão
     */
    public Musica() {
        this.nome = "";
        this.interprete = "";
        this.autor = "";
        this.editora = "";
        this.letra = new String[0];
        this.musica = new String[0];
        this.duracao = 0;
        this.vezesEscutada = 0;
    }

    /**
     * Construtor parametrizado.
     * @param nome Nome da música
     * @param interprete Nome do intérprete
     * @param autor Nome do autor
     * @param editora Nome da editora
     * @param letra Linhas do poema (letra da música)
     * @param musica Linhas de notação musical
     * @param duracao Duração da música em segundos
     */
    public Musica(String nome, String interprete, String autor, String editora,
                  String[] letra, String[] musica, int duracao) {
        this.nome = nome;
        this.interprete = interprete;
        this.autor = autor;
        this.editora = editora;
        this.letra = Arrays.copyOf(letra, letra.length);;
        this.musica = Arrays.copyOf(musica, musica.length);;
        this.duracao = duracao;
        this.vezesEscutada = 0;
    }

    /**
     * Construtor de cópia.
     * @param umaMusica Outra instância de Música a copiar
     */
    public Musica(Musica umaMusica) {
        this.nome = umaMusica.getNome();
        this.interprete = umaMusica.getInterprete();
        this.autor = umaMusica.getAutor();
        this.editora = umaMusica.getEditora();
        this.letra = Arrays.copyOf(outra.getLetra(), outra.getLetra().length);
        this.musica = Arrays.copyOf(outra.getMusica(), outra.getMusica().length);
        this.duracao = umaMusica.getDuracao();
        this.vezesEscutada = umaMusica.getVezesEscutadas();
    }

    // Getters e setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getInterprete() { return interprete; }
    public void setInterprete(String interprete) { this.interprete = interprete; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getEditora() { return editora; }
    public void setEditora(String editora) { this.editora = editora; }

    public String[] getLetra() { return Arrays.copyOf(letra, letra.length); }
    public void setLetra(String[] letra) { this.letra = Arrays.copyOf(letra, letra.length); }

    public String[] getMusica() { return Arrays.copyOf(musica, musica.length); }
    public void setMusica(String[] musica) { this.musica = Arrays.copyOf(musica, musica.length); }

    public int getDuracao() { return duracao; }
    public void setDuracao(int duracao) { this.duracao = duracao; }

    public int getVezesEscutadas() { return vezesEscutada; }
    public void setVezesEscutadas(int vezesEscutada) { this.vezesEscutada = vezesEscutada; }

    /**
     * Retorna uma representação textual da música.
     * @return String descritiva da música
     */
    public String toString() {
        return "Música: " + nome + " | Interprete: " + interprete + 
               " | Autor: " + autor + " | Editora: " + editora +
               " | Duração: " + duracao + "s | Escutada: " + vezesEscutada + "x";
    }

    /**
     * Verifica se esta música é igual a outra.
     * @param o Objeto a comparar
     * @return true se for igual, false caso contrário
     */

    public boolean equals(Object o) {
        if (this == o) return true;
        if ((o == null) || (this.getClass() != o.getClass())) return false;
        Musica m = (Musica) o;
        return nome.equals(m.nome) && interprete.equals(m.interprete) &&
               autor.equals(m.autor) && editora.equals(m.editora) &&
               Arrays.equals(letra, m.letra) && Arrays.equals(musica, m.musica) &&
               duracao == m.duracao && vezesEscutada == m.vezesEscutada;
    }
    
    /**
     * Cria uma cópia desta instância.
     * @return Uma nova instância igual à atual
     */
    public Musica clone() {
        return new Musica(this);
    }

    /**
     * Devolve o número de linhas do poema (letra da música).
     * @return Número de linhas do poema
     */
    public int qtsLinhasPoema() {
        return letra.length;
    }

    /**
     * Calcula o número total de caracteres existentes na letra.
     * @return Número total de caracteres do poema
     */
    public int numeroCarateres() {
        int total = 0;
        for (String linha : letra) {
            total += linha.length();
        }
        return total;
    }

    /**
     * Adiciona uma nova linha à letra, numa posição específica.
     * @param posicao Índice onde inserir a linha (0 a n)
     * @param novaLinha Conteúdo da nova linha a inserir
     */
    public void addLetra(int posicao, String novaLinha) {
        if (posicao < 0 || posicao > letra.length) posicao = letra.length;
        String[] novaLetra = new String[letra.length + 1];
        for (int i = 0, j = 0; i < novaLetra.length; i++) {
            if (i == posicao) {
                novaLetra[i] = novaLinha;
            } else {
                novaLetra[i] = letra[j++];
            }
        }
        letra = novaLetra;
    }

    /**
     * Devolve a linha do poema com mais caracteres.
     * @return Linha mais longa da letra
     */
    public String linhaMaisLonga() {
        String maisLonga = "";
        for (String linha : letra) {
            if (linha.length() > maisLonga.length()) {
                maisLonga = linha;
            }
        }
        return maisLonga;
    }
    
    /**
     * Devolve as 3 letras (a-z) mais utilizadas no poema.
     * @return Array com as três letras mais frequentes
     */
    public String[] letrasMaisUtilizadas() {
        Map<Character, Integer> freq = new HashMap<>();
        for (String linha : letra) {
            for (char c : linha.toLowerCase().toCharArray()) {
                if (Character.isLetter(c)) {
                    freq.put(c, freq.getOrDefault(c, 0) + 1);
                }
            }
        }

        return freq.entrySet().stream()
            .sorted((a, b) -> b.getValue() - a.getValue())
            .limit(3)
            .map(e -> String.valueOf(e.getKey()))
            .toArray(String[]::new);
    }
}