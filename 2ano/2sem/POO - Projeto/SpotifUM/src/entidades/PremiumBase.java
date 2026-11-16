  package entidades;

  import java.io.Serializable;

  public class PremiumBase implements Plano, Serializable {
      private final String nome = "PremiumBase"; 
      private final int pontos = 10; 
      private final boolean premium = true;
      private final double percentagem = 0.0;

    /**
     * Obtém o nome do plano.
     * @return Nome do plano.
     */
      public String getNome()        {return this.nome;}

    /**
     * Obtém os pontos atribuídos pelo plano por reprodução
     * @return Número de pontos.
     */
      public int getPontos()         {return this.pontos;}

    /**
     * Verifica se o plano é premium.
     * @return True se for premium, false caso contrário.
     */
      public boolean isPremium()     {return this.premium;}

    /**
     * Obtém a percentagem a acrescentar por reprodução associada ao plano.
     * @return Percentagem
     */
      public double getPercentagem() {return this.percentagem;}

    /**
     * Cria uma cópia do plano.
     * @return Nova instância de PremiumBase.
     */
      @Override
      public PremiumBase clone() {return new PremiumBase();}

    /**
     * Representação textual do plano.
     * @return String com os dados do plano.
     */
      @Override
      public String toString() {
        StringBuffer sb = new StringBuffer(); 
        sb.append("Nome: ").append(this.nome);
        sb.append(" ; Pontos: ").append(this.pontos); 
        sb.append(" ; Premium: ").append(this.premium);

        return sb.toString();
      }

    /**
     * Verifica se dois planos PremiumBase são iguais.
     * @param o Objeto a comparar.
     * @return True se forem iguais, false caso contrário.
     */
      @Override
      public boolean equals(Object o) {
          if (this == o) return true;
          if (o==null || o.getClass() != this.getClass()) return false;
          PremiumBase p = (PremiumBase) o;
          return this.pontos == p.getPontos() && this.premium == p.isPremium() && this.nome.equals(p.getNome());
      }
  }
