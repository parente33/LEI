package entidades;

import java.io.Serializable;

public class PremiumTop implements Plano, Serializable {
    private final String nome = "PremiumTop"; 
    private final int pontos = 0; 
    private final boolean premium = true;
    private final double percentagem = 0.025;

    /**
     * Obtém o nome do plano.
     * @return Nome do plano.
     */
    public String getNome()        {return this.nome;}

    /**
     * Obtém os pontos atribuídos pelo plano.
     * @return Número de pontos.
     */
    public int getPontos()         {return this.pontos;}

    /**
     * Verifica se o plano é premium.
     * @return True se for premium, false caso contrário.
     */
    public boolean isPremium()     {return this.premium;}

    /**
     * Obtém a percentagem de desconto associada ao plano.
     * @return Percentagem de desconto.
     */
    public double getPercentagem() {return this.percentagem;}

    /**
     * Cria uma cópia do plano.
     * @return Nova instância de PremiumTop.
     */
    @Override
    public PremiumTop clone() {return new PremiumTop();}

    /**
     * Representação textual do plano.
     * @return String com os dados do plano.
     */
    @Override
    public String toString() {
      StringBuffer sb = new StringBuffer(); 
      sb.append("Nome: ").append(this.nome);
      sb.append(" ; Pontos: ").append(this.pontos); 
      sb.append(" ; Premium: " ).append(this.premium);

      return sb.toString();
    }

    /**
     * Verifica se dois planos PremiumTop são iguais.
     * @param o Objeto a comparar.
     * @return True se forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o==null || o.getClass() != this.getClass()) return false;
        PremiumTop p = (PremiumTop) o;
        return this.pontos == p.getPontos() && this.premium == p.isPremium() && this.nome.equals(p.getNome());
    }
}
