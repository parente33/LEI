/**
 * Classe que implementa um Círculo num plano2D
 * As coordenadas do centro são doubles
 */
public class Circulo {
    
    // Variáveis de instância
    private double x;
    private double y;
    private double raio;
    
    /**
     * Construtores da classe Circulo
     * Declaração dos construtores por omissão (vazio),
     * parametrizado e de cópia
     */

    /**
     * Construtor por omissão de Circulo
     */
    public Circulo() {
        this.x = 0.0;
        this.y = 0.0;
        this.raio = 0.0;
    }

    /**
     * Construtor parametrizado de Circulo
     * Aceita como parâmetros os valores para cada coordenada
     */
    public Circulo(double cx, double cy, double raio) {
        this.x = cx;
        this.y = cy;
        this.raio = raio;
    }

    /**
     * Construtor de cópia de Circulo
     * Aceita como parâmetro outro Circulo e utiliza os métodos
     * de acesso aos valores das variáveis de instância
     */
    public Circulo(Circulo umCirculo) {
        this.x = umCirculo.getX();
        this.y = umCirculo.getY();
        this.raio = umCirculo.getRaio();
    }

    /**
     * Métodos de instância
     */
    
    /**
     * @return Valor da coordenada do centro em x
     */
    public double getX() {
        return this.x;
    }

    /**
     * @return Valor da coordenada do centro em y
     */
    public double getY() {
        return this.y;
    }

    /**
     * @return Valor do raio do círculo
     */
    public double getRaio() {
        return this.raio;
    }

    /**
     * Atualiza o valor da coordenada do centro em x
     * @param novoX novo valor da coordenada em x
     */
    public void setX(double novoX) {
        this.x = novoX;
    }

    /**
     * Atualiza o valor da coordenada do centro em y
     * @param novoY novo valor da coordenada em y
     */
    public void setY(double novoY) {
        this.y = novoY;
    }

    /**
     * Atualiza o valor do raio do círculo
     * @param novoRaio novo valor do raio
     */
    public void setRaio(double novoRaio) {
        this.raio = novoRaio;
    }

    /**
     * Devolve a representação em String do Circulo
     * @return String com as coordenadas do centro (em x e y), e o seu raio
     */
    public String toString() {
        return "x =  " + this.x + ", y = " + this.y + ", raio = " + this.raio;
    }

    /**
     * Implementa a igualdade entre dois circulos
     * Reescrita do método equals que todos os objetos possuem
     * 
     * @param o O objeto que é comparado com o recetor da mensagem
     * @return boolean resultado da comparação entre o parâmetro e o recetor
     */
    public boolean equals(Object o) {
        if (this == o) return true;

        if ((o == null) || (this.getClass() != o.getClass())) return false;

        Circulo c = (Circulo) o;
        return (this.x == c.getX() && this.y == c.getY() && this.raio == c.getRaio());
    }

    /**
     * Desloca o circulo no espaço
     * @param cx nova coordenada em x do centro
     * @param cy nova coordenada em y do centro
     */
    public void alteraCentro(double cx, double cy) {
        setX(cx);
        setY(cy);
    }

    /**
     * Calcula a area do circulo
     * @return
     */
    public double calculaArea() {
        return Math.PI * Math.pow(this.raio, 2);
    }

    public double calculaPerimetro() {
        return 2 * Math.PI * this.raio;
    }

    public Circulo clone() {
        return new Circulo(this);
    }
}