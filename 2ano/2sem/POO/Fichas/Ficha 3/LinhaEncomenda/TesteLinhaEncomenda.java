public class TesteLinhaEncomenda {
    public static void main(String[] args) {
        LinhaEncomenda linha = new LinhaEncomenda("P1234", "Produto Teste", 100.0, 2, 23, 10);
        
        System.out.println("Valor total da linha de encomenda: " + linha.calculaValorLinhaEnc()); // valor com imposto e desconto
        System.out.println("Valor do desconto: " + linha.calculaValorDesconto()); // valor do desconto
    }
}