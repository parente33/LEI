package entidades;
public interface Plano {
    String getNome();  // Nome da subscrição
    int getPontos();   // Pontos por música
    boolean isPremium(); // Vantagens Premium
    double getPercentagem(); // Percentagem a adicionar por reprodução
}