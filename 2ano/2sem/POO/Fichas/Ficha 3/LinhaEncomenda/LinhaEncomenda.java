/**
 * Classe que implementa uma linha de encomenda
 */
package Ficha3.LinhaEncomenda;

public class LinhaEncomenda {
    private String ref;
    private String descr;
    private double preco_pre_imp;
    private int quant, taxa, desc;

    public LinhaEncomenda() {
        this.ref = "";
        this.descr = "";
        this.preco_pre_imp = 0.0;
        this.quant = 0;
        this.taxa = 0;
        this.desc = 0;
    }

    public LinhaEncomenda(String refer, String desc, double preco, int quant, int taxa, int desconto) {
        this.ref = refer;
        this.descr = desc;
        this.preco_pre_imp = preco;
        this.quant = quant;
        this.taxa = taxa;
        this.desc = desconto;
    }

    public LinhaEncomenda(LinhaEncomenda umaLinha) {
        this.ref = umaLinha.getRef();
        this.descr = umaLinha.getDescr();
        this.preco_pre_imp = umaLinha.getPrecoPreImp();
        this.quant = umaLinha.getQuant();
        this.taxa = umaLinha.getTaxa();
        this.desc = umaLinha.getDesc();
    }

    public String getRef() {
        return this.ref;
    }

    public void setRef(String novaRef) {
        this.ref = novaRef;
    }

    public String getDescr() {
        return this.descr;
    }

    public void setDescr(String novaDescr) {
        this.descr = novaDescr;
    }

    public double getPrecoPreImp() {
        return this.preco_pre_imp;
    }

    public void setPrecoPreImp(double novoPreco) {
        this.preco_pre_imp = novoPreco;
    }

    public int getQuant() {
        return this.quant;
    }

    public void setQuant(int novaQuant) {
        this.quant = novaQuant;
    }

    public int getTaxa() {
        return this.taxa;
    }

    public void setTaxa(int novaTaxa) {
        this.taxa = novaTaxa;
    }

    public int getDesc() {
        return this.desc;
    }

    public void setDesc(int novoDesc) {
        this.desc = novoDesc;
    }

    public double calculaValorLinhaEnc() {
        double precoComDesc = preco_pre_imp * (1 - (desc / 100.0));
        double precoComDescTotal = precoComDesc * quant;
        double precoFinal = precoComDescTotal * (1 + (taxa / 100.0));

        return precoFinal;
    }

    public double calculaValorDesconto() {
        return preco_pre_imp * (desc / 100.0) * quant;
    }
}