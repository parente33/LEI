import java.io.Serializable;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class HoteisPOO implements Serializable {
    private Map<String, Hotel> hoteis; // key -> nome do hotel

    public List<String> hoteisMaisOcupados() {
        return hoteis.entrySet()
                .stream()
                .map(entry -> {
                    String nomeHotel = entry.getKey();
                    Hotel hotel = entry.getValue();

                    int totalDias = hotel.totalDiasReservados();

                    return new AbstractMap.SimpleEntry<>(nomeHotel,totalDias);
                })
                .sorted((e1, e2) -> {
                    int cmp = Integer.compare(e2.getValue(), e1.getValue());
                    return (cmp != 0) ? cmp : e2.getKey().compareTo(e1.getKey());
                })
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();
    }
}