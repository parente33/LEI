public class TesteSuporte {
    public static void main(String[] args) {
        SistemadeSuporte sistema = new SistemadeSuporte();

        PedidodeSuporte pedido1 = new PedidodeSuporte("Maria", "Erro de Rede", "Problema com a conexão");
        PedidodeSuporte pedido2 = new PedidodeSuporte("João", "Erro no Software", "O software não abre");
        sistema.inserePedido(pedido1);
        sistema.inserePedido(pedido2);

        sistema.resolvePedido(pedido1, "Carlos", "Ajuste na configuração de rede");
        sistema.resolvePedido(pedido2, "Ana", "Reinstalação do software");

        System.out.println("Colaborador Top: " + sistema.colaboradorTop());

        sistema.resolvidos().forEach(System.out::println);
    }
}
