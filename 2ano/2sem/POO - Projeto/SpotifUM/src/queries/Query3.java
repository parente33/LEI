package queries;

import entidades.Utilizador;
import entidades.Historico;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Query3 implements QuerySolver, Serializable {

    private Utilizador utilizadorMaisAtivo;
    private int totalReproducoes;

    /**
     * Construtor por omissão
     */
    public Query3() {
        this.utilizadorMaisAtivo = new Utilizador();
        this.totalReproducoes = 0;
    }

    /**
     * Construtor parameterizado da Query3
     * @param u Objeto Utilizador
     * @param totalReproducoes Numero de reproduçoes
     */
    public Query3(Utilizador u, int totalReproducoes) {
        this.utilizadorMaisAtivo = u;
        this.totalReproducoes = totalReproducoes;
    }

    /**
     * Construtor de cópia da Query3
     * @param q Objeto Query3 a ser copiado
     */
    public Query3(Query3 q) {
        this.utilizadorMaisAtivo = q.getUtilizadorMaisAtivo();
        this.totalReproducoes = q.getTotalReproducoes();
    }   

    /**
     * Devolve o utilizador atual da Query3
     * @return Objeto Utilizador
     */
    public Utilizador getUtilizadorMaisAtivo() {
        return utilizadorMaisAtivo.clone();
    }

    /** 
     * Devolve o numero de reproducoes
     * @return numero de reproducoes
     */
    public int getTotalReproducoes() {
        return totalReproducoes;
    }

    /**
     * Metodo que percorre uma estrutura Map e compara qual o utilizador no periodo valido com mais reproducoes
     * @param utilizadores Estrutura Map com utilizadores
     * @param inicio LocalDateTime com o periodo de inicio 
     * @param fim LocalDateTime com o periodo de fim
     */
    public void calcula(Map<String, Utilizador> utilizadores, LocalDateTime inicio, LocalDateTime fim) {
        int max = 0;
        Utilizador maisAtivo = null;

        for (Utilizador u : utilizadores.values()) {
            int count = 0;
            List<Historico> historico = u.getHistorico();

            for (Historico h : historico) {
                LocalDateTime data = h.getDataHora();
                boolean dentroDoPeriodo = 
                    (inicio == null || !data.isBefore(inicio)) &&
                    (fim == null || !data.isAfter(fim));

                if (dentroDoPeriodo) {
                    count++;
                }
            }

            if (count > max) {
                max = count;
                maisAtivo = u;
            }
        }

        this.totalReproducoes = max;
        this.utilizadorMaisAtivo = maisAtivo != null ? maisAtivo.clone() : new Utilizador();
    }

    /**
     * Método que responde á query 
     * @param extra_info Lista com informaçao extra caso necessario
     * @return String com a resposta
     */
    public String respondeQuery(List<Object> extra_info) {
        if (extra_info == null || extra_info.size() < 3) {
            return "Informação extra insuficiente para executar a query.";
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Utilizador> utilizadores = (Map<String, Utilizador>) extra_info.get(0);
            LocalDateTime inicio = (LocalDateTime) extra_info.get(1);
            LocalDateTime fim = (LocalDateTime) extra_info.get(2);


            this.calcula(utilizadores,inicio,fim);

        } catch (Exception e) {
            return "Erro ao processar os parâmetros da query.";
        }

        if (utilizadorMaisAtivo == null || totalReproducoes == 0) {
            return "Nenhuma reprodução encontrada no período.";
        }

        return "Utilizador que mais músicas ouviu: " + utilizadorMaisAtivo.getNome() +
               " com " + totalReproducoes + " músicas ouvidas.";
    }


    public void listener(Object o) {
        
    }

    
    public void call(QueryManager q) {
    }

    /**
     * Implementação do método de clonagem da Query3
     * @return Objeto do tipo Query3
     */
    @Override
    public Query3 clone() { return new Query3(this);}

     /**
     * Implementaçãodo método de igualdade entre duas Query3
     * @param Query3 para ser comparada com o recetor
     * @return Booleano ture ou false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Query3 q = (Query3) o;
        return this.totalReproducoes == q.getTotalReproducoes() &&
               this.utilizadorMaisAtivo.equals(q.getUtilizadorMaisAtivo());
    }

    /**
     * Método toString
     */
    @Override
    public String toString() {
        return "Query3{" +
               "utilizador=" + utilizadorMaisAtivo.getNome() +
               ", totalReproducoes=" + totalReproducoes +
               '}';
    }
}
