public class TesteStack {
    public static void main(String[] args) {
        Stack minhaStack = new Stack();

        minhaStack.push("Primeiro");
        minhaStack.push("Segundo");
        minhaStack.push("Terceiro");
        System.out.println("Topo da stack: " + minhaStack.top());

        System.out.println("Comprimento da stack: " + minhaStack.length());
        System.out.println("A stack está vazia? " + minhaStack.empty());

        minhaStack.iteratorInterno();
        minhaStack.iteratorExterno();

        minhaStack.pop();
        System.out.println("Topo da stack após pop: " + minhaStack.top());
        System.out.println("Comprimento da stack após pop: " + minhaStack.length());
    }
}