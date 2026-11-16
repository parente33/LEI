import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Barrier {

    private int nParties;
    private int waitingParties;
    private int generation; // adicionado no ex2
    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();
    
    Barrier (int N) {
        this.nParties = N;
        this.waitingParties = 0;
        this.generation = 0; // adicionado no ex2
    };

    void await() throws InterruptedException {
        lock.lock();
        try {
            int gen = generation; // adicionado no ex2
            waitingParties++;
            if (waitingParties < nParties) {
                while (generation == gen) { /* adicionado no ex2 */ /* while (waitingParties < nParties) { removido no ex2 */
                    cond.await();
                }        
            } else {
                waitingParties = 0; // adicionado no ex2
                generation++; // adicionado no ex2
                cond.signalAll();
            }
        } finally {
            lock.unlock();
        }
    };
}