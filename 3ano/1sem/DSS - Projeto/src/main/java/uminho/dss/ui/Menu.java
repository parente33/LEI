package uminho.dss.ui;

import java.util.*;

public class Menu {

    public interface Handler {
        void execute();
    }

    public interface PreCondition {
        boolean validate();
    }

    private static Scanner is = new Scanner(System.in);

    private String title;
    private List<String> options;
    private List<PreCondition> available;
    private List<Handler> handlers;

    public Menu() {
        this.title = "Menu";
        this.options = new ArrayList<>();
        this.available = new ArrayList<>();
        this.handlers = new ArrayList<>();
    }

    public Menu(String title, List<String> options) {
        this.title = title;
        this.options = new ArrayList<>(options);
        this.available = new ArrayList<>();
        this.handlers = new ArrayList<>();
        this.options.forEach(s -> {
            this.available.add(() -> true);
            this.handlers.add(() -> System.out.println("\n  Opcao ainda nao implementada!\n"));
        });
    }

    public void option(String name, PreCondition p, Handler h) {
        this.options.add(name);
        this.available.add(p);
        this.handlers.add(h);
    }

    public String getTitle() {
        return this.title;
    }

    public List<String> getOptions() {
        return new ArrayList<>(this.options);
    }

    public List<PreCondition> getAvailables() {
        return new ArrayList<>(this.available);
    }

    public List<Handler> getHandlers() {
        return new ArrayList<>(this.handlers);
    }

    public void setPreCondition(int i, PreCondition p) {
        this.available.set(i - 1, p);
    }

    public void setHandler(int i, Handler h) {
        this.handlers.set(i - 1, h);
    }

    public void run() {
        int op;
        do {
            show();
            op = readOption();
            if (op > 0 && op <= this.options.size()) {
                if (this.available.get(op - 1).validate()) {
                    this.handlers.get(op - 1).execute();
                } else {
                    System.out.println("\n  Opcao nao disponivel! Tente novamente.\n");
                }
            }
        } while (op != 0);
    }

    private void show() {
        System.out.println("\n*** " + this.title + " ***");
        for (int i = 0; i < this.options.size(); i++) {
            System.out.print(i + 1);
            System.out.print(" - ");
            System.out.println(this.available.get(i).validate() ? this.options.get(i) : "---");
        }
        System.out.println("0 - Sair");
    }

    private int readOption() {
        int op;
        System.out.print("Opcao: ");
        try {
            String line = is.nextLine();
            op = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            op = -1;
        }

        if (op < 0 || op > this.options.size()) {
            System.out.println("\n  Opcao invalida! Tente novamente.\n");
            op = -1;
        }

        return op;
    }

    public static int readIntSafe(java.util.Scanner scanner, int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.print("  Entrada nao pode estar vazia. Tente novamente: ");
                    continue;
                }
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("  Opcao invalida. Introduza um numero entre %d e %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("  Entrada invalida. Introduza um numero valido: ");
            }
        }
    }

    public String readString(String prompt) {
        System.out.print(prompt);
        return is.nextLine();
    }

    public int readInt(String prompt, String error, int min, int max) {
        for (;;) {
            try {
                System.out.print(prompt);
                String input = is.nextLine().trim();
                if (input.isEmpty()) {
                    if (error != null)
                        System.err.println(error);
                    continue;
                }
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                if (error != null)
                    System.err.println(error);
            } catch (NumberFormatException e) {
                if (error != null)
                    System.err.println(error);
            }
        }
    }
}
