/**
 * Classe que implementa um Sensor
 * Mede a pressão numa divisão. A pressão nunca pode ser negativa.
 */
public class Sensor {
    // Variável de instância
    private double pressao;

    /**
     * Construtor por omissão
     */
    public Sensor() {
        this.pressao = 0.0;
    }

    /**
     * Construtor parametrizado
     * @param valor Valor inicial da pressão (>= 0). Se for negativo, assume 0.0.
     */
    public Sensor(double valor) {
        if (valor >= 0) {
            this.pressao = valor;
        } else {
            this.pressao = 0.0;
        }
    }

    /**
     * Construtor de cópia
     * @param umSensor Outro objeto Sensor
     */
    public Sensor(Sensor umSensor) {
        this.pressao = umSensor.getPressao();
    }

    /**
     * @return Valor da pressão
     */
    public double getPressao() {
        return this.pressao;
    }

    /**
     * Atualiza o valor da pressão, se for não-negativo
     * @param valor Novo valor da pressão
     * @return true se foi possível atualizar, false caso contrário
     */
    public boolean setPressao(double valor) {
        if (valor >= 0) {
            this.pressao = valor;
            return true;
        }
        return false;
    }

    /**
     * Representação textual do sensor
     * @return String com o valor da pressão
     */
    public String toString() {
        return "x =  " + this.pressao;
    }

    /**
     * Verifica se dois sensores são iguais
     * @param o Objeto a comparar
     * @return true se tiverem a mesma pressão
     */
    public boolean equals(Object o) {
        if (this == o) return true;

        if ((o == null) || (this.getClass() != o.getClass())) return false;

        Sensor s = (Sensor) o;
        return (this.pressao == s.getPressao());
    }

    /**
     * Cria uma cópia do sensor
     * @return Novo objeto Sensor com os mesmos dados
     */
    public Sensor clone() {
        return new Sensor(this);
    }
}