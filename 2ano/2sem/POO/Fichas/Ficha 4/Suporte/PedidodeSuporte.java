import java.time.LocalDateTime;

public class PedidodeSuporte {
    private String usuario;
    private LocalDateTime dataHoraPedido;
    private String assunto;
    private String descricao;
    private String tecnico;
    private LocalDateTime dataHoraResolucao;
    private String informacaoResolucao;

    // Construtor
    public PedidodeSuporte(String usuario, String assunto, String descricao) {
        this.usuario = usuario;
        this.assunto = assunto;
        this.descricao = descricao;
        this.dataHoraPedido = LocalDateTime.now();
    }

    // Getters e Setters
    public String getUsuario() {
        return usuario;
    }

    public LocalDateTime getDataHoraPedido() {
        return dataHoraPedido;
    }

    public String getAssunto() {
        return assunto;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getTecnico() {
        return tecnico;
    }

    public LocalDateTime getDataHoraResolucao() {
        return dataHoraResolucao;
    }

    public String getInformacaoResolucao() {
        return informacaoResolucao;
    }

    public void resolvePedido(String tecnico, String informacaoResolucao) {
        this.tecnico = tecnico;
        this.dataHoraResolucao = LocalDateTime.now();
        this.informacaoResolucao = informacaoResolucao;
    }

    public long tempoResolucaoEmMinutos() {
        if (dataHoraResolucao != null) {
            return java.time.Duration.between(dataHoraPedido, dataHoraResolucao).toMinutes();
        }
        return -1; // Pedido ainda não resolvido
    }

    @Override
    public String toString() {
        return "Pedido de Suporte [Usuario: " + usuario + ", Assunto: " + assunto + ", Resolvido: " +
                (dataHoraResolucao != null ? "Sim" : "Não") + "]";
    }
}
