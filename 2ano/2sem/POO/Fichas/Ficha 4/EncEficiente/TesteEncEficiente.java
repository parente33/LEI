import java.time.LocalDate;
import Ficha3.LinhaEncomenda.LinhaEncomenda;

public class TesteEncEficiente {
    public static void main(String[] args) {
        LinhaEncomenda l1 = new LinhaEncomenda("P001", "Caneta", 2.0, 10, 0.1);
        LinhaEncomenda l2 = new LinhaEncomenda("P002", "Caderno", 5.0, 5, 0.2);

        EncEficiente encomenda = new EncEficiente("João", 123456789, "Rua A", LocalDate.now());
        encomenda.adicionaLinha(l1);
        encomenda.adicionaLinha(l2);

        System.out.println(encomenda);

        System.out.println("Valor Total: " + encomenda.calculaValorTotal());
        System.out.println("Valor Desconto: " + encomenda.calculaValorDesconto());
        System.out.println("Total Produtos: " + encomenda.numeroTotalProdutos());
        System.out.println("Contém 'P001'? " + encomenda.existeProdutoEncomenda("P001"));

        encomenda.removeProduto("P001");
        System.out.println("Após remover 'P001':");
        System.out.println(encomenda);
    }
}