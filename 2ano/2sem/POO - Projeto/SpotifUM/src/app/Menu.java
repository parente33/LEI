package app;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Menu {
    private String titulo;
    private List<String> opcoes;
    private List<Handler> handlers;
    private static final Scanner sc = new Scanner(System.in);

    
    /**
     * Interface funcional que representa uma ação associada a uma opção do menu.
     */
    @FunctionalInterface
    public interface Handler {
        void execute();
    }
    /**
    * Construtor do menu com um título.
    * @param titulo Título a ser exibido no menu.
    */
    public Menu(String titulo) {
        this.titulo = titulo != null ? titulo : "";
        this.opcoes = new ArrayList<>();
        this.handlers = new ArrayList<>();
    }

    /**
     * Retorna o scanner utilizado para entrada de dados.
     * @return Scanner utilizado pelo menu.
     */
    public static Scanner getScanner() { return sc; }

    /**
     * Adiciona uma nova opção ao menu com uma ação padrão.
     * @param descricao Descrição da opção.
     */
    public void adicionarOpcao(String descricao) {
        adicionarOpcao(descricao, () -> System.out.println("Opção ainda não disponível."));
    }

     /**
     * Adiciona uma nova opção ao menu com uma ação personalizada.
     * @param descricao Descrição da opção.
     * @param handler Ação a ser executada quando a opção for escolhida.
     */
    public void adicionarOpcao(String descricao, Handler handler) {
        opcoes.add(descricao);
        handlers.add(handler);
    }

    
       /**
     * Limpa o terminal/console.
     */
    public static void limparConsola() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

     /**
     * Executa o menu, permitindo ao utilizador escolher uma opção e executando a ação correspondente.
     */
    public void executar() {
        boolean sair = false;
        while (!sair) {
            System.out.println("\n" + titulo);
            for (int i = 0; i < opcoes.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, opcoes.get(i));
            }
            System.out.printf("%d. Sair\n", opcoes.size() + 1);
            System.out.print("Escolha uma opção: ");

            try {
                int escolha = Integer.parseInt(sc.nextLine());
                if (escolha >= 1 && escolha <= opcoes.size()) {
                    handlers.get(escolha - 1).execute();
                    sair = true;
                } else if (escolha == opcoes.size() + 1) {
                    sair = true;
                } else {
                    System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Insira um número inteiro.");
            }
        }
    }
}