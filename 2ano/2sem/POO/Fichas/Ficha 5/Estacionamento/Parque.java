package Estacionamento;

import java.util.*;

public class Parque {
    private String nome;  // Nome do parque
    private Map<String, Lugar> lugares;  // Mapa de lugares (matricula -> Lugar)

    // Construtor
    public Parque(String nome) {
        this.nome = nome;
        this.lugares = new HashMap<>();
    }

    // Método que devolve todas as matrículas dos lugares ocupados
    public Set<String> matriculasOcupadas() {
        return lugares.keySet();
    }

    // Método que regista uma nova ocupação de lugar
    public void registaLugar(Lugar l) {
        lugares.put(l.getMatricula(), l);
    }

    // Método que remove o lugar de uma dada matrícula
    public void removeLugar(String matricula) {
        lugares.remove(matricula);
    }

    // Método que altera o tempo disponível de um lugar para uma dada matrícula
    public void alteraMinutos(String matricula, int novosMinutos) {
        Lugar lugar = lugares.get(matricula);
        if (lugar != null) {
            lugar.setMinutos(novosMinutos);
        }
    }

    // Método que devolve a quantidade total de minutos atribuídos
    public int totalMinutos() {
        int total = 0;
        for (Lugar lugar : lugares.values()) {
            total += lugar.getMinutos();
        }
        return total;
    }

    // Versão com iterador interno para totalMinutos
    public int totalMinutosComIteradorInterno() {
        return lugares.values().stream().mapToInt(Lugar::getMinutos).sum();
    }

    // Método que verifica se existe lugar atribuído a uma dada matrícula
    public boolean existeLugar(String matricula) {
        return lugares.containsKey(matricula);
    }

    // Método que cria uma lista com as matrículas com tempo atribuído > x, em que o lugar seja permanente
    public List<String> lugaresComMaisTempoPermanente(int x) {
        List<String> resultado = new ArrayList<>();
        for (Lugar lugar : lugares.values()) {
            if (lugar.isPermanente() && lugar.getMinutos() > x) {
                resultado.add(lugar.getMatricula());
            }
        }
        return resultado;
    }

    // Versão com iterador externo para lugares com mais tempo
    public List<String> lugaresComMaisTempoPermanenteComIteradorExterno(int x) {
        List<String> resultado = new ArrayList<>();
        Iterator<Lugar> it = lugares.values().iterator();
        while (it.hasNext()) {
            Lugar lugar = it.next();
            if (lugar.isPermanente() && lugar.getMinutos() > x) {
                resultado.add(lugar.getMatricula());
            }
        }
        return resultado;
    }

    // Método que devolve uma cópia dos lugares
    public Map<String, Lugar> copiaLugares() {
        Map<String, Lugar> copia = new HashMap<>();
        for (Map.Entry<String, Lugar> entry : lugares.entrySet()) {
            copia.put(entry.getKey(), entry.getValue().clone());
        }
        return copia;
    }

    // Método que devolve a informação de um lugar para uma dada matrícula
    public Lugar getLugar(String matricula) {
        return lugares.get(matricula);
    }

    // Método toString para exibir informações do parque
    @Override
    public String toString() {
        return "Parque [nome=" + nome + ", lugares=" + lugares + "]";
    }
}