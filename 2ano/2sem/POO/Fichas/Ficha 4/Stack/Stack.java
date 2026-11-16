import java.util.LinkedList;
import java.util.Iterator;

public class Stack {
    private LinkedList<String> stack;

    // Construtor da stack
    public Stack() {
        this.stack = new LinkedList<>();
    }

    // Método (a): retorna o elemento no topo da stack
    public String top() {
        if (empty()) {
            throw new IllegalStateException("A stack está vazia");
        }
        return stack.getFirst();  // Retorna o primeiro elemento, que é o topo da stack
    }

    // Método (b): insere um novo elemento no topo da stack
    public void push(String s) {
        stack.addFirst(s);  // Insere no topo (primeira posição)
    }

    // Método (c): remove o elemento do topo da stack
    public void pop() {
        if (empty()) {
            throw new IllegalStateException("A stack está vazia");
        }
        stack.removeFirst();  // Remove o topo da stack (primeiro elemento)
    }

    // Método (d): determina se a stack está vazia
    public boolean empty() {
        return stack.isEmpty();
    }

    // Método (e): determina o comprimento da stack
    public int length() {
        return stack.size();
    }

    // Iterador interno - percorre todos os elementos da stack
    public void iteratorInterno() {
        System.out.println("Iterador Interno:");
        for (String s : stack) {  // Utilizando iterador interno (for-each)
            System.out.println(s);
        }
    }

    // Iterador externo - percorre todos os elementos da stack
    public void iteratorExterno() {
        System.out.println("Iterador Externo:");
        Iterator<String> iterator = stack.iterator();  // Obtém o iterador externo
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}