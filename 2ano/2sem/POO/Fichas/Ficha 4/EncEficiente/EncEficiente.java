import java.time.LocalDate;
import java.util.ArrayList;
import Ficha3.LinhaEncomenda.LinhaEncomenda;

public class EncEficiente {
    private static int contador = 1; // contador para gerar número da encomenda

    private String nome;
    private int num_fiscal;
    private String morada;
    private int num_encom;
    private LocalDate data;
    private ArrayList<LinhaEncomenda> linhasEnc;

    // Construtor por omissão
    public EncEficiente() {
        this.nome = "";
        this.num_fiscal = 0;
        this.morada = "";
        this.num_encom = contador++;
        this.data = LocalDate.now();
        this.linhasEnc = new ArrayList<>();
    }

    // Construtor parametrizado
    public EncEficiente(String nome, int num_fiscal, String morada, LocalDate data) {
        this.nome = nome;
        this.num_fiscal = num_fiscal;
        this.morada = morada;
        this.num_encom = contador++;
        this.data = data;
        this.linhasEnc = new ArrayList<>();
    }

    // Getters e setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getNumFiscal() { return num_fiscal; }
    public void setNumFiscal(int num_fiscal) { this.num_fiscal = num_fiscal; }

    public String getMorada() { return morada; }
    public void setMorada(String morada) { this.morada = morada; }

    public int getNumEncom() { return num_encom; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public List<LinhaEncomenda> getLinhas() {
        ArrayList<LinhaEncomenda> copia = new ArrayList<>();
        for (LinhaEncomenda l : linhasEnc) {
            copia.add(l.clone());
        }
        return copia;
    }

    public void setLinhas(List<LinhaEncomenda> linhas) {
        this.linhasEnc = new ArrayList<>();
        for (LinhaEncomenda l : linhas) {
            this.linhasEnc.add(l.clone());
        }
    }

    // i. Método que calcula o valor total da encomenda
    public double calculaValorTotal() {
        return linhasEnc.stream().mapToDouble(LinhaEncomenda::calculaValorLinhaEnc).sum();
    }

    // ii. Valor total dos descontos
    public double calculaValorDesconto() {
        return linhasEnc.stream().mapToDouble(LinhaEncomenda::calculaValorDesconto).sum();
    }

    // iii. Número total de produtos
    public int numeroTotalProdutos() {
        return linhasEnc.stream().mapToInt(LinhaEncomenda::getQuant).sum();
    }

    // iv. Verifica se um produto está na encomenda
    public boolean existeProdutoEncomenda(String refProduto) {
        return linhasEnc.stream().anyMatch(l -> l.getRef().equals(refProduto));
    }

    // v. Adiciona uma linha à encomenda
    public void adicionaLinha(LinhaEncomenda linha) {
        this.linhasEnc.add(linha.clone());
    }

    // vi. Remove uma linha com base na referência do produto
    public void removeProduto(String codProd) {
        linhasEnc.removeIf(l -> l.getRef().equals(codProd));
    }

    // Clone
    @Override
    public EncEficiente clone() {
        EncEficiente copia = new EncEficiente(this.nome, this.num_fiscal, this.morada, this.data);
        copia.linhasEnc = new ArrayList<>();
        for (LinhaEncomenda l : this.linhasEnc) {
            copia.linhasEnc.add(l.clone());
        }
        copia.num_encom = this.num_encom; // manter mesmo número na cópia
        return copia;
    }

    // Equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EncEficiente)) return false;
        EncEficiente that = (EncEficiente) o;
        return num_fiscal == that.num_fiscal &&
                num_encom == that.num_encom &&
                nome.equals(that.nome) &&
                morada.equals(that.morada) &&
                data.equals(that.data) &&
                linhasEnc.equals(that.linhasEnc);
    }

    // ToString
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Encomenda #").append(num_encom)
            .append(" | Cliente: ").append(nome)
            .append(" | NIF: ").append(num_fiscal)
            .append(" | Morada: ").append(morada)
            .append(" | Data: ").append(data)
            .append("\nLinhas da Encomenda:");
        for (LinhaEncomenda l : linhasEnc) {
            sb.append("\n  ").append(l);
        }
        return sb.toString();
    }
}