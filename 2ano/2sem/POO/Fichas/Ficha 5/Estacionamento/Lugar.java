package Estacionamento;

public class Lugar {
    private String matricula;  // Matrícula do veículo
    private String nome;  // Nome do proprietário
    private int minutos;  // Tempo atribuído ao lugar em minutos
    private boolean permanente;  // Indica se o lugar é permanente ou de aluguer

    // Construtor
    public Lugar(String matricula, String nome, int minutos, boolean permanente) {
        this.matricula = matricula;
        this.nome = nome;
        this.minutos = minutos;
        this.permanente = permanente;
    }

    // Getters e Setters
    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public boolean isPermanente() {
        return permanente;
    }

    public void setPermanente(boolean permanente) {
        this.permanente = permanente;
    }

    // Método toString para exibir informações do lugar
    @Override
    public String toString() {
        return "Lugar [matricula=" + matricula + ", nome=" + nome + ", minutos=" + minutos + ", permanente=" + permanente + "]";
    }

    // Método equals para comparar dois lugares
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Lugar lugar = (Lugar) obj;
        return matricula.equals(lugar.matricula);
    }

    // Método clone
    @Override
    public Lugar clone() {
        return new Lugar(this.matricula, this.nome, this.minutos, this.permanente);
    }
}