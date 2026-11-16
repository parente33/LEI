import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ManagerImpl implements Manager {
    /* Versão merdinha
	private Lock l = new ReentrantLock();
	private Condition cond = l.newCondition();
	
    int maxMin = 5;

	private List<String> players = new ArrayList<>();
	
	public List<String> join(String name, int minPlayers) throws InterruptedException {
		l.lock();
		try {
			players.add(name);
			maxMin = Math.max(maxMin, minPlayers);
			if (players.size() == maxMin) cond.signalAll();
			else {
				while (players.size() < maxMin) cond.await();
			}
			return players;
		} finally {
			l.unlock();
		}
	}
    */

    public Raid join(String name, int minPlayers) throws InterruptedException {
        
    }
}