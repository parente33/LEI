import java.time.LocalDate;

public class TesteEncomenda {
    public static void main(String[] args) {
        LinhaEncomenda linha1 = new LinhaEncomenda("P001", "Produto 1", 100.0, 2, 23, 10);
        LinhaEncomenda linha2 = new LinhaEncomenda("P002", "Produto 2", 50.0, 3, 23, 5);

        LinhaEncomenda[] linhas = {linha1, linha2};
        Encomenda encomenda = new Encomenda("Cliente 1", 123456789, "Rua 1", 1001, LocalDate.now(), linhas);

        System.out.println("Valor Total: " + encomenda.calculaValorTotal());
        System.out.println("Valor Total Desconto: " + encomenda.calculaValorDesconto());
        System.out.println("Número Total de Produtos: " + encomenda.numeroTotalProdutos());
        System.out.println("Produto P001 existe? " + encomenda.existeProdutoEncomenda("P001"));

        Encomenda encomendaClonada = encomenda.clone();
        System.out.println("Encomenda Clonada: " + encomendaClonada);

        System.out.println("Encomendas são iguais? " + encomenda.equals(encomendaClonada));

        LinhaEncomenda linha3 = new LinhaEncomenda("P003", "Produto 3", 75.0, 1, 23, 15);
        encomenda.adicionaLinha(linha3);
        System.out.println("Número Total de Produtos (após adicionar): " + encomenda.numeroTotalProdutos());

        encomenda.removeProduto("P001");
        System.out.println("Número Total de Produtos (após remover): " + encomenda.numeroTotalProdutos());
    }
}