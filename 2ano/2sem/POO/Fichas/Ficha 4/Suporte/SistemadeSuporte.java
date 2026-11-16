import java.time.LocalDateTime;
import java.util.*;

public class SistemadeSuporte {
    private Queue<PedidodeSuporte> pedidos; // Usamos uma fila para garantir que os pedidos sejam atendidos na ordem de chegada
    private Map<String, Integer> colaboradores; // Para contar o número de pedidos resolvidos por cada colaborador

    public SistemadeSuporte() {
        pedidos = new LinkedList<>();
        colaboradores = new HashMap<>();
    }

    // i. Insere um novo pedido no sistema
    public void inserePedido(PedidodeSuporte pedido) {
        pedidos.add(pedido);
    }

    // ii. Procura um pedido de suporte dado o usuário e a data
    public PedidodeSuporte procuraPedido(String user, LocalDateTime data) {
        for (PedidodeSuporte pedido : pedidos) {
            if (pedido.getUsuario().equals(user) && pedido.getDataHoraPedido().equals(data)) {
                return pedido;
            }
        }
        return null;
    }

    // iii. Resolve um pedido de suporte
    public void resolvePedido(PedidodeSuporte pedido, String tecnico, String info) {
        pedido.resolvePedido(tecnico, info);
        colaboradores.put(tecnico, colaboradores.getOrDefault(tecnico, 0) + 1);
    }

    // iv. Lista os pedidos já resolvidos
    public List<PedidodeSuporte> resolvidos() {
        List<PedidodeSuporte> resolvidos = new ArrayList<>();
        for (PedidodeSuporte pedido : pedidos) {
            if (pedido.getDataHoraResolucao() != null) {
                resolvidos.add(pedido);
            }
        }
        return resolvidos;
    }

    // v. Determina o colaborador que mais resolveu pedidos
    public String colaboradorTop() {
        return colaboradores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // vi. Lista os pedidos resolvidos num intervalo de tempo
    public List<PedidodeSuporte> resolvidos(LocalDateTime inicio, LocalDateTime fim) {
        List<PedidodeSuporte> resolvidos = new ArrayList<>();
        for (PedidodeSuporte pedido : pedidos) {
            if (pedido.getDataHoraResolucao() != null &&
                !pedido.getDataHoraResolucao().isBefore(inicio) &&
                !pedido.getDataHoraResolucao().isAfter(fim)) {
                resolvidos.add(pedido);
            }
        }
        return resolvidos;
    }

    // vii. Calcula o tempo médio de resolução de todos os pedidos
    public double tempoMedioResolucao() {
        long totalTempo = 0;
        long count = 0;
        for (PedidodeSuporte pedido : pedidos) {
            long tempoResolucao = pedido.tempoResolucaoEmMinutos();
            if (tempoResolucao != -1) {
                totalTempo += tempoResolucao;
                count++;
            }
        }
        return count > 0 ? totalTempo / (double) count : 0;
    }

    // viii. Calcula o tempo médio de resolução dos pedidos dentro de um intervalo
    public double tempoMedioResolucao(LocalDateTime inicio, LocalDateTime fim) {
        long totalTempo = 0;
        long count = 0;
        for (PedidodeSuporte pedido : pedidos) {
            if (pedido.getDataHoraResolucao() != null &&
                !pedido.getDataHoraResolucao().isBefore(inicio) &&
                !pedido.getDataHoraResolucao().isAfter(fim)) {
                long tempoResolucao = pedido.tempoResolucaoEmMinutos();
                totalTempo += tempoResolucao;
                count++;
            }
        }
        return count > 0 ? totalTempo / (double) count : 0;
    }

    // ix. Encontra o pedido que demorou mais a ser resolvido
    public PedidodeSuporte pedidoMaisLongo() {
        return pedidos.stream()
                .filter(pedido -> pedido.getDataHoraResolucao() != null)
                .max(Comparator.comparingLong(PedidodeSuporte::tempoResolucaoEmMinutos))
                .orElse(null);
    }

    // x. Encontra o pedido mais longo resolvido em um intervalo de tempo
    public PedidodeSuporte pedidoMaisLongo(LocalDateTime inicio, LocalDateTime fim) {
        return pedidos.stream()
                .filter(pedido -> pedido.getDataHoraResolucao() != null &&
                        !pedido.getDataHoraResolucao().isBefore(inicio) &&
                        !pedido.getDataHoraResolucao().isAfter(fim))
                .max(Comparator.comparingLong(PedidodeSuporte::tempoResolucaoEmMinutos))
                .orElse(null);
    }

    // xi. Encontra o pedido que demorou menos a ser resolvido
    public PedidodeSuporte pedidoMaisCurto() {
        return pedidos.stream()
                .filter(pedido -> pedido.getDataHoraResolucao() != null)
                .min(Comparator.comparingLong(PedidodeSuporte::tempoResolucaoEmMinutos))
                .orElse(null);
    }

    // xii. Encontra o pedido mais curto resolvido em um intervalo de tempo
    public PedidodeSuporte pedidoMaisCurto(LocalDateTime inicio, LocalDateTime fim) {
        return pedidos.stream()
                .filter(pedido -> pedido.getDataHoraResolucao() != null &&
                        !pedido.getDataHoraResolucao().isBefore(inicio) &&
                        !pedido.getDataHoraResolucao().isAfter(fim))
                .min(Comparator.comparingLong(PedidodeSuporte::tempoResolucaoEmMinutos))
                .orElse(null);
    }
}