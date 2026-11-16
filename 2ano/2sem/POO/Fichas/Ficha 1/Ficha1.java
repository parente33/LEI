public class Ficha1 {

    public double celsiusParaFarenheit(double celsius) {
        return celsius * 9/5 + 32;
    }

    public int maximoNumeros(int a, int b) {
        return (a > b) ? a : b;
        // return Math.max(a,b);
    }

    public String criaDescricaoConta(String nome, double saldo) {
        return "Name: " + nome + "; Saldo: " + saldo;
    }
    
    public double eurosParaLibras(double valor, double taxaConversao) {
        return valor*taxaConversao;
    }

    public long factorial(int num) {
        while(num >= 0) {
            num = num*(num-1);
            num--;
        }
        return num;
    }
}