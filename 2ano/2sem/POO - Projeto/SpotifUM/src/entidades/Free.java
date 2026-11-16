package entidades;

import java.io.Serializable;

public class Free implements Plano, Serializable {
    private final String nome = "Free"; 
    private final int pontos = 5; 
    private final boolean premium = false;
    private final double percentagem =0.0;


    /**
     * Devolve o nome do plano
     * @return Nome do plano
     */
    public String getNome()        {return this.nome;}

     /**
     * Devolve o número de pontos associados ao plano
     * @return Número de pontos
     */
    public int getPontos()         {return this.pontos;}

     /**
     * Indica se o plano é premium
     * @return false, pois este plano não é premium
     */
    public boolean isPremium()     {return this.premium;}

     /**
     * Devolve a percentagem de desconto do plano
     * @return Percentagem de desconto (0.0)
     */
    public double getPercentagem() {return this.percentagem;}

    /**
     * Implementação do método de clonagem do plano Free
     * @return Novo objeto do tipo Free
     */
    @Override
    public Free clone() {return new Free();}

     /**
     * Representação textual do plano Free
     * @return String com as informações do plano
     */
    @Override
    public String toString() {
      StringBuffer sb = new StringBuffer(); 
      sb.append("Nome: ").append(this.nome);
      sb.append(" ; Pontos :").append(this.pontos); 
      sb.append(" ; Premium :" ).append(this.premium);
      return sb.toString();
    }

    /**
     * Verifica se dois planos Free são iguais
     * @param o Objeto a comparar
     * @return true se forem iguais, false caso contrário
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o==null || o.getClass() != this.getClass()) return false;
        Free free = (Free) o;
        return this.pontos == free.getPontos() && this.premium == free.isPremium() && this.nome.equals(free.getNome()) && this.percentagem == free.getPercentagem();
    }
}
