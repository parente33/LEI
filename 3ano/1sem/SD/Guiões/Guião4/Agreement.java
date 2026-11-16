import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Agreement {
    private final int nParties;
    private int waitingParties;
    private int generation;
    
    private int currentMax = Integer.MIN_VALUE;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition cond = lock.newCondition();

    Agreement(int n) {
        this.nParties = n;
        this.waitingParties = 0;
        this.generation = 0;
    }

    int propose(int choice) throws InterruptedException {
        lock.lock();
        try {
            int gen = generation;

            if (choice > currentMax) currentMax = choice;

            waitingParties++;

            if (waitingParties < nParties) {
                while (generation == gen) cond.await();
                return currentMax;
            } else {
                int result = currentMax;

                waitingParties = 0;
                currentMax = Integer.MIN_VALUE;
                generation++;

                cond.signalAll();
                return result;
            }
        } finally {
            lock.unlock();
        }
    }
}