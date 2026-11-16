import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CasaInteligente {
    /* Ex 6 */
    private Map<String, SmartDevice> dispositivos; // mapeamento por ID
    private Map<String, List<String>> dispositivosPorDivisao; // divisão → lista de IDs

    public CasaInteligente(Collection<SmartDevice> devices) {
        this.dispositivos = new HashMap<>();
        this.dispositivosPorDivisao = new HashMap<>();

        for (SmartDevice d : devices) {
            this.dispositivos.put(d.getId(), d.clone()); // composição → clone
            // Assume que os getters estão bem definidos
        }
    }

    public void remove(String id) throws DeviceNotFoundException {
        if (!this.dispositivos.containsKey(id)) {
            throw new DeviceNotFoundException("Dispositivo não existe: " + id);
        }
        this.dispositivos.remove(id);
        
        for (List<String> listaIds : this.dispositivosPorDivisao.values()) {
            listaIds.remove(id);
        }
    }

    public Iterator<SmartDevice> devicesPorConsumoCrescente() {
        List<SmartDevice> l = this.dispositivos.values()
                                .stream()
                                .map(SmartDevice::clone)
                                .collect(Collectors.toList());

        Collections.sort(l);

        return l.iterator();
    }

    public String divisaoMaisEconomica() {
        String divMaisEcon = null;
        double menorConsumo = Double.MAX_VALUE;

        for (Map.Entry<String,List<String>> e : dispositivosPorDivisao.entrySet()) {
            String div = e.getKey();
            List<String> ids = e.getValue();

            double consumoTotal = 0;
            for (String id : ids) {
                SmartDevice sd = dispositivos.get(id);
                if (sd != null) {
                    consumoTotal += sd.totalConsumo();
                }
            }

            if (consumoTotal < menorConsumo) {
                menorConsumo = consumoTotal;
                divMaisEcon = div;
            } else if (consumoTotal == menorConsumo && divMaisEcon != null) {
                if (div.compareTo(divMaisEcon) > 0) {
                    divMaisEcon = div;
                }
            }
        }
        return divMaisEcon;
    }

    /* Ex 12 */
    public void alteraInfo(Consumer<SmartBulbDimmable> bd) {
        for (SmartDevice d : this.dispositivos.values()) {
            if (d instanceof SmartBulbDimmable) {
                bd.accept((SmartBulbDimmable) d);
            }
        }
    }

    Consumer<SmartBulbDimmable> reduzirPara25 = b -> {
        double atual = b.getIntensidade();
        b.setIntensidade(atual * 0.25);
    };
    /* Ex 12 */


    /* Ex 13 */
    public boolean apenasNumaDivisao() {
        Set<String> vistos = new HashSet<>();

        for (List<String> lista : dispositivosPorDivisao.values()) {
            for (String id : lista) {
                if (!vistos.add(id)) return false;
            }
        }
        return true;
    }
    /* Ex 13 */

    /* Ex 14 */
    public boolean gravaEmFichObjetos(String fich) throws FileNotFoundException, IOException {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fich))) {
            for (SmartDevice d : dispositivos.values()) {
                if (d instanceof SmartSpeaker) {
                    os.writeObject(d);
                }
            }
        }
        return true;
    }
    /* Ex 14 */
}