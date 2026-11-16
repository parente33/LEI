import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Registo implements Comparable<Registo>, Serializable {
    // variáveis
    private static int contador = 0;
    private int numeroRegisto;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private Quarto quarto;
    private List<Integer> idadesOcupantes;

    public Registo(LocalDate inicio, LocalDate saida, Quarto quarto) {

    }

    public int numDiasReserva() {} // devolve o número de dias deste
                                      // registo de ocupação do quarto

    // determina o valor a pagar pela ocupação do quarto, tendo em conta
    // os descontos que se decida aplicar
    public double valorAPagar() {
        double precoBase = quarto.precoPorDia();
        int dias = numDiasReserva();

        if (quarto instanceof QuartoDuploSenior && idadesOcupantes != null) {
            int somaIdades = idadesOcupantes.stream().mapToInt(Integer::intValue).sum();
            double desconto = 0.25 * somaIdades;
            double precoComDesconto = precoBase - desconto;
            return Math.max(precoComDesconto, 0) * dias;
        }
        
        return precoBase * dias;
    }
                                      

}