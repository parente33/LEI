import java.util.concurrent.locks.ReentrantLock;

class Bank {

    private static class Account {
        private int balance;
        private final ReentrantLock l = new ReentrantLock(); // added in ex3

        Account(int balance) {
            this.balance = balance;
        }

        /* Ex2
        int balance() { return balance; }
         */

        int balance() {
            l.lock();
            try {
                return balance;
            } finally {
                l.unlock();
            }
        }

        /* Ex2
        boolean deposit(int value) {
            balance += value;
            return true;
        }
        */
        boolean deposit(int value) {
            l.lock();
            try {
                balance += value;
                return true;
            } finally {
                l.unlock();
            }
        }
    }

    // Our single account, for now
    private Account savings = new Account(0);

    // Account balance
    public int balance() {
        return savings.balance();
    }

    // Deposit
    boolean deposit(int value) {
        return savings.deposit(value);
    }

    /* Main for ex2 and ex3
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Use: Java Bank <N> <I> <V>");
            return;
        }

        int N = Integer.parseInt(args[0]);
        int I = Integer.parseInt(args[1]);
        int V = Integer.parseInt(args[2]);

        Bank bank = new Bank();
        Thread[] threads = new Thread[N];

        for (int i = 0; i < N; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < I; j++) { bank.deposit(V); }
            });
            threads[i].start();
        }

        for (int t = 0; t < N; t++) {
            try {
                threads[t].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int finalBalance = bank.balance();
        int expected = N * I * V;

        System.out.println("Final balance: " + finalBalance);
        System.out.println("xBalance: " + expected);
    }
    */
}

class VelTests {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Use: Java Bank Tests <N>");
            return;
        }

        int N = Integer.parseInt(args[0]);
        int OPS = 1000000; // operations per second
        int V = 1;

        Bank bank = new Bank();
        Thread[] threads = new Thread[N];

        long start = System.nanoTime();

        for (int i = 0; i < N; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < OPS; j++) bank.deposit(V);
            });
            threads[i].start();
        }

        for (int k = 0; k < N; k++) {
            try {
                threads[k].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long end = System.nanoTime();
        long elapsedTime = (end - start) / 1000000;

        System.out.println("Nr of threads: " + N);
        System.out.println("Final balance: " + bank.balance());
        System.out.println("xBalance: " + (N * OPS * V));
        System.out.println("Elapsed Time: " + elapsedTime + " ms");
    }
}