import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Hotel implements Serializable {
    private String nome;
    private Map<String,Quarto> quartos; // chave: nº do quarto
    private Map<String,List<Registo>> registos; // chave: número do quarto → lista de registos de ocupação

    public Hotel(Iterator<Quarto> quartos) {
        this.registos = new HashMap<>();
        this.quartos = new HashMap<>();

        for (quartos.hasNext()) {
            Quarto q = quartos.next();
            String num = q.getNumeroQuarto(); // assume-se que existe

            this.quartos.put(num,q);
            this.registos.put(num,new ArrayList<>());
        }
    }

    public void adicionaRegisto(LocalDate entrada, LocalDate saida, String numQuarto) throws IllegalArgumentException, NoSuchElementException {
        if (!quartos.containsKey(numQuarto)) {
            throw new NoSuchElementException("Quarto com nº: " + numQuarto + "não existe");
        }

        if (entrada == null || saida == null || entrada.isAfter(saida) || entrada.equals(saida)) {
            throw new IllegalArgumentException("Datas de entrada/saída inválidas");
        }

        Quarto quarto = quartos.get(numQuarto);
        Registo novoRegisto = new Registo(entrada,saida,quarto);

        registos.get(numQuarto).add(novoRegisto);
    }

    public int totalDiasReservados() {
        return registos.values().stream()
            .flatMap(List::stream)
            .mapToInt(Registo::numDiasReserva)
            .sum();
    }

    public List<Quarto> quartosLivres(LocalDate entrada, LocalDate saida) throws SemDisponibilidadeException {}

    public void reservaQuartoSenior(LocalDate entrada, LocalDate saida, String numQuarto, List<Integer> idadesOcupantes) throws IllegalArgumentException, NoSuchElementException {
        if (!quartos.containsKey(numQuarto)) {
            throw new NoSuchElementException("Quarto não existe");
        }

        Quarto q = quartos.get(numQuarto);

        if (!(q instanceof QuartoDuploSenior)) {
            throw new IllegalArgumentException("Quarto é do tipo errado");
        }

        boolean todosSeniores = idadesOcupantes.stream().allMatch(idade -> idade > 60);
        if (!todosSeniores) {
            throw new IllegalArgumentException("Devem ser todos séniores");
        }
        
        Registo r = new Registo(entrada, saida, q);
        registos.get(numQuarto).add(r);
    }
}