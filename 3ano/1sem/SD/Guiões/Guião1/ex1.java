class Increment implements Runnable {
    private final long I = 100;

    @Override
    public void run() {
        for (long i = 0; i <= I; i++)
            System.out.println("Thread - " + Thread.currentThread().threadId() + ": " + i);
    }
}

class ex1 {
    public static void main(String[] args) {
        int N = 10;
        Thread[] threads = new Thread[N];

        for (int i = 0; i < N; i++) {
            threads[i] = new Thread(new Increment());
            threads[i].start();
        }

        for (int j = 0; j < N; j++) {
            try {
                threads[j].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Fim\n");
    }
}