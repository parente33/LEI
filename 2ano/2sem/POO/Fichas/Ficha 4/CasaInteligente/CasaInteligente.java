import java.util.*;
import java.util.stream.Collectors;

public class CasaInteligente {
    private List<Lampada> lampadas;

    // Construtor
    public CasaInteligente() {
        this.lampadas = new ArrayList<>();
    }

    // i. Adiciona uma lâmpada à casa
    public void addLampada(Lampada l) {
        this.lampadas.add(l);
    }

    // ii. Liga lâmpada no modo máximo
    public void ligaLampadaNormal(int index) {
        if (index >= 0 && index < lampadas.size()) {
            lampadas.get(index).lampON();
        }
    }

    // iii. Liga lâmpada em modo ECO
    public void ligaLampadaEco(int index) {
        if (index >= 0 && index < lampadas.size()) {
            lampadas.get(index).lampECO();
        }
    }

    // iv. Conta quantas estão em modo ECO
    public int qtEmEco() {
        int count = 0;
        for (Lampada l : lampadas) {
            if (l.getEstado().equals("ECO")) count++;
        }
        return count;
    }

    // v. Remove uma lâmpada pelo índice
    public void removeLampada(int index) {
        if (index >= 0 && index < lampadas.size()) {
            lampadas.remove(index);
        }
    }

    // vi. Liga todas as lâmpadas em modo ECO
    public void ligaTodasEco() {
        for (Lampada l : lampadas) {
            l.lampECO();
        }
    }

    // Liga todas em modo máximo
    public void ligaTodasMax() {
        for (Lampada l : lampadas) {
            l.lampON();
        }
    }

    // vii. Consumo total da casa (desde a criação)
    public double consumoTotal() {
        double total = 0.0;
        for (Lampada l : lampadas) {
            total += l.totalConsumo();
        }
        return total;
    }

    // viii. Lâmpada com maior consumo total
    public Lampada maisGastadora() {
        Lampada mais = null;
        double max = -1;
        for (Lampada l : lampadas) {
            double consumo = l.totalConsumo();
            if (consumo > max) {
                max = consumo;
                mais = l;
            }
        }
        return mais;
    }

    // ix. Conjunto de lâmpadas em modo ECO
    public Set<Lampada> lampadasEmModoEco() {
        return lampadas.stream()
                .filter(l -> l.getEstado().equals("ECO"))
                .collect(Collectors.toSet());
    }

    // x. Reset do consumo de todas as lâmpadas
    public void reset() {
        for (Lampada l : lampadas) {
            l.resetConsumo();
        }
    }

    // xi. Podium de economia (3 com menor consumo total)
    public Set<Lampada> podiumEconomia() {
        return lampadas.stream()
                .sorted(Comparator.comparingDouble(Lampada::totalConsumo))
                .limit(3)
                .collect(Collectors.toCollection(LinkedHashSet::new)); // mantém ordem
    }
}