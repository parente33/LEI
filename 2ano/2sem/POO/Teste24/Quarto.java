import java.io.Serializable;

public abstract class Quarto implements Serializable {
    private String numeroQuarto;
    

    public abstract double precoPorDia(); // determina o valor do preço do quarto
                                          // que é anunciado pelo hotel

}