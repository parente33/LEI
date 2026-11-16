import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CarRental {
    private Map<String, Carro> frota; // Map com matrícula do carro como chave

    public CarRental() {
        this.frota = new HashMap<>();
    }

    public CarRental(Map<String, Carro> fronta) {
        this.frota = frota.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,e->cloneCarro(e.getValue())));
    }

    public CarRental(CarRental outroCarRental) {
        this.frota = outroCarRental.frota.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,e->cloneCarro(e.getValue())));
    }

    private Carro cloneCarro(Carro c) {
        if (c instanceof CarroCombustao) return new CarroCombustao((CarroCombustao) c);
        if (c instanceof CarroEletrico) return new CarroEletrico((CarroEletrico) c);
        return new Carro(c);
    }

    public boolean existeCarro(String cod) {
        return frota.containsKey(cod);
    }

    public int quantos() {
        return frota.size();
    }

    public int quantos(String marca) {
        int c = 0;
        for (Carro carro : frota.values()) {
            if (carro.getMarca() != null && carro.getMarca().equals(marca)) c++;
        }

        return c;
    }

    public Carro getCarro(String cod) {
        Carro carro = frota.get(cod);
        if (carro == null) return null;

        if (carro instanceof CarroCombustao) {
            return new CarroCombustao((CarroCombustao) carro);
        } else if (carro instanceof CarroEletrico) {
            return new CarroEletrico((CarroEletrico) carro);
        } else {
            return new Carro(carro);
        }
    }

    public void adiciona(Carro v) {
        if (v != null) {
            String cod = v.getMatricula();
            Carro copia;
            if (v instanceof CarroCombustao) {
                copia = new CarroCombustao((CarroCombustao) v);
            } else if (v instanceof CarroEletrico) {
                copia = new CarroEletrico((CarroEletrico) v);
            } else {
                copia = new Carro(v);
            }
            frota.put(cod,copia);
        }
    }

    public List<Carro> getCarros() {
        List<Carro> lista = new ArrayList<>();
        for (Carro c : frota.values()) {
            if (c instanceof CarroCombustao) {
                lista.add(new CarroCombustao((CarroCombustao) c));
            } else if (c instanceof CarroEletrico) {
                lista.add(new CarroEletrico((CarroEletrico) c));
            } else {
                lista.add(new Carro(c));
            }
        }
        return lista;
    }

    public void adiciona(Set<Carro> vs) {
        for (Carro v : vs) {
            if (v != null) {
                String cod = v.getMatricula();
                Carro copia;
                if (v instanceof CarroCombustao) {
                    copia = new CarroCombustao((CarroCombustao) v);
                } else if (v instanceof CarroEletrico) {
                    copia = new CarroEletrico((CarroEletrico) v);
                } else {
                    copia = new Carro(v);
                }
                frota.put(cod,copia);
            }
        }
    }

    public void registarViagem(String codCarro, int numKms) {
        Carro c = frota.get(codCarro);
        if (c == null) {
            throw new IllegalArgumentException("Carro com matrícula \"" + codCarro + "\" não existe na frota.");
        }
        c.registarViagem(numKms);
    }

    public void atestarCarro(String codCarro) {
        Carro c = frota.get(codCarro);
        if (c == null) {
            throw new IllegalArgumentException("Carro com matrícula \"" + codCarro + "\" não existe na frota.");
        }
        c.abastecerOuCarregar();
    }

    public Carro obterCarroMaisEconomico() {
        Carro carroMaisEconomico = null;
        double menorCusto = Double.MAX_VALUE;

        for (Carro carro : frota.values()) {
            double custoPorKm = carro.custoPorKm();

            if (custoPorKm < menorCusto) {
                menorCusto = custoPorKm;
                carroMaisEconomico = carro;
            }
        }
        return carroMaisEconomico;
    }

    public double obterMediaCustoPorKm() {
        if (frota.isEmpty()) return 0.0;

        double soma = 0.0;
        int total = 0;

        for (Carro carro : frota.values()) {
            soma += carro.custoPorKm();
            total++;
        }

        return soma / total;
    }

    public int custoRealPorKm(String cod) {
        Carro c = frota.get(cod);
        if (c == null) {
            throw new IllegalArgumentException("Carro com matrícula \"" + cod + "\" não existe na frota.");
        }
        double custo = c.custoRealPorKm();
        return (int) Math.round(custo);
    }

    public Set<Carro> carrosComAlcance(int kms) {
        Set<Carro> result = new HashSet<>();

        for (Carro carro : frota.values()) {
            if (carro.getAuton() >= kms) {
                if (carro instanceof CarroCombustao) {
                    result.add(new CarroCombustao((CarroCombustao) carro));
                } else if (carro instanceof CarroEletrico) {
                    result.add(new CarroEletrico((CarroEletrico) carro));
                } else {
                    result.add(new Carro(carro));
                }
            }
        }
        return result;
    }

    public Set<CarroEletrico> comBateriaDe(int nivelMinimo) {
        Set<CarroEletrico> result = new HashSet<>();

        for (Carro carro : frota.values()) {
            if (carro instanceof CarroEletrico) {
                CarroEletrico ce = (CarroEletrico) carro;

                double autonomia = ce.getAuton(); // em km
                double consumo = ce.getConsumoKWh100Km(); // em kWh/100km
                double bateriaTotal = ce.getDimensaoBateria(); // em kWh

                double energiaDisponivel = (autonomia * consumo) / 100.0;
                double percentagem = (energiaDisponivel / bateriaTotal) * 100.0;

                if (percentagem >= nivelMinimo) {
                    result.add(new CarroEletrico(ce));
                }
            }
        }
        return result;
    }
}