import java.time.LocalDate;
import java.util.Arrays;

public class Encomenda {
    private String nome;
    private int num_fiscal;
    private String morada;
    private int num_encom;
    private LocalDate data;
    private LinhaEncomenda[] linhasEnc;

    public Encomenda() {
        this.nome = "";
        this.num_fiscal = 0;
        this.morada = "";
        this.num_encom = 0;
        this.data = LocalDate.now();
        this.linhasEnc = new LinhaEncomenda[0];
    }

    public Encomenda(String nome, int num_fiscal, String morada, int num_encom, LocalDate data, LinhaEncomenda[] linha) {
        this.nome = nome;
        this.num_fiscal = num_fiscal;
        this.morada = morada;
        this.num_encom = num_encom;
        this.data = data;
        
        int len = linha.length;
        LinhaEncomenda[] linhas = new LinhaEncomenda[len];
        for (int i = 0; i < len; i++) {
            linhas[i] = linha[i];
        }
        this.linhasEnc = linhas;
    }

    public Encomenda(Encomenda novaEncomenda) {
        this.nome = novaEncomenda.getNome();
        this.num_fiscal = novaEncomenda.getNumFiscal();
        this.morada = novaEncomenda.getMorada();
        this.num_encom = novaEncomenda.getNumEncom();
        this.data = novaEncomenda.getData();
        this.linhasEnc = novaEncomenda.getLinhas();
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String novoNome) {
        this.nome = novoNome;
    }

    public int getNumFiscal() {
        return this.num_fiscal;
    }

    public void setNumFiscal(int novoNumFiscal) {
        this.num_fiscal = novoNumFiscal;
    }

    public String getMorada() {
        return this.morada;
    }

    public void setMorada(String novaMorada) {
        this.morada = novaMorada;
    }

    public int getNumEncom() {
        return this.num_encom;
    }

    public void setNumEncom(int novoNumEncom) {
        this.num_encom = novoNumEncom;
    }

    public LocalDate getData() {
        return this.data;
    }

    public void setData(LocalDate novaData) {
        this.data = novaData;
    }

    public LinhaEncomenda[] getLinhas() {
        return this.linhasEnc;
    }

    public void setLinhas(LinhaEncomenda[] novaLinha) {
        this.linhasEnc = novaLinha;
    }

    public double calculaValorTotal() {
        double valor = 0.0;
        for (LinhaEncomenda linha : this.linhasEnc) {
            if (linha != null) {
                valor += linha.calculaValorLinhaEnc();
            }
        }
        return valor;
    }

    public double calculaValorDesconto() {
        double valor = 0.0;
        for (LinhaEncomenda linha : this.linhasEnc) {
            if (linha != null) {
                valor += linha.calculaValorDesconto();
            }
        }
        return valor;
    }

    public int numeroTotalProdutos() {
        int valor = 0;
        for (LinhaEncomenda linha : this.linhasEnc) {
            if (linha != null) {
                valor += linha.getQuant();
            }
        }
        return valor;
    }

    public boolean existeProdutoEncomenda(String refProduto) {
        for (LinhaEncomenda linha : this.linhasEnc) {
            if (linha != null && linha.getRef().equals(refProduto)) {
                return true;
            }
        }
        return false;
    }

    public void adicionaLinha(LinhaEncomenda linha) {
        LinhaEncomenda[] novasLinhas = new LinhaEncomenda[this.linhasEnc.length + 1];
        System.arraycopy(this.linhasEnc, 0, novasLinhas, 0, this.linhasEnc.length);
        novasLinhas[this.linhasEnc.length] = linha;
        this.linhasEnc = novasLinhas;
    }

    public void removeProduto(String codProd) {
        int index = -1;
        for (int i = 0; i < this.linhasEnc.length; i++) {
            if (this.linhasEnc[i].getRef().equals(codProd)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            LinhaEncomenda[] novasLinhas = new LinhaEncomenda[this.linhasEnc.length - 1];
            for (int i = 0, j = 0; i < this.linhasEnc.length; i++) {
                if (i != index) {
                    novasLinhas[j++] = this.linhasEnc[i];
                }
            }
            this.linhasEnc = novasLinhas;
        }
    }

    // Método clone() para criar uma cópia profunda da Encomenda
    @Override
    public Encomenda clone() {
        LinhaEncomenda[] linhasClone = new LinhaEncomenda[this.linhasEnc.length];
        for (int i = 0; i < this.linhasEnc.length; i++) {
            linhasClone[i] = this.linhasEnc[i].clone();  // Assumindo que LinhaEncomenda também implementa clone()
        }
        return new Encomenda(this.nome, this.num_fiscal, this.morada, this.num_encom, this.data, linhasClone);
    }

    // Método toString() para exibir a encomenda
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Encomenda [")
            .append("Número: ").append(num_encom)
            .append(", Cliente: ").append(nome)
            .append(", Número Fiscal: ").append(num_fiscal)
            .append(", Morada: ").append(morada)
            .append(", Data: ").append(data)
            .append(", Linhas: ");
        
        for (LinhaEncomenda linha : linhasEnc) {
            sb.append("\n  ").append(linha.toString());
        }
        
        sb.append("\n]");
        return sb.toString();
    }

    // Método equals() para comparar duas encomendas
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Encomenda other = (Encomenda) obj;
        return num_encom == other.num_encom &&
               num_fiscal == other.num_fiscal &&
               nome.equals(other.nome) &&
               morada.equals(other.morada) &&
               data.equals(other.data) &&
               Arrays.equals(linhasEnc, other.linhasEnc);
    }
}