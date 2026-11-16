import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bank {
    // private final ReentrantLock l = new ReentrantLock(); // removed in Ex4

    private static class Account {
        private int balance;
        private final ReentrantLock rl = new ReentrantLock();

        Lock getLock() { return this.rl; } // Ex4

        Account(int balance) { this.balance = balance; }

        /* Sem locks
        int balance() { return balance; }
        */

        // Com locks (ex1)
        int balance() {
            rl.lock();
            try {
                return balance;
            } finally {
                rl.unlock();
            }
        }

        /* Sem locks
        boolean deposit(int value) {
            balance += value;
            return true;
        }
        */

        // Com locks (ex1)
        boolean deposit(int value) {
            rl.lock();
            try {
                balance += value;
                return true;
            } finally {
                rl.unlock();
            }
        }

        /* Sem locks
        boolean withdraw(int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
        */

        // Com locks (ex1)
        boolean withdraw(int value) {
            rl.lock();
            try {
                if (value > balance)
                    return false;
                balance -= value;
                return true;
            } finally {
                rl.unlock();
            }
        }
    }

    // Bank slots and vector of accounts
    private final int slots;
    private Account[] av;

    public Bank(int n) {
        slots = n;
        av = new Account[slots];
        for (int i=0; i<slots; i++) av[i]=new Account(0);
    }

    // Account balance
    public int balance(int id) {
        if (id < 0 || id >= slots)
            return 0;
        return av[id].balance();
    }

    // Deposit
    public boolean deposit(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        return av[id].deposit(value);
    }

    // Withdraw; fails if no such account exists or insufficient balance
    public boolean withdraw(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        return av[id].withdraw(value);
    }

    /*
    // Ex2 (added locks in ex3)
    public boolean transfer (int from, int to, int value) {
        l.lock();
        try {
            if (from < 0 || from >= slots || to < 0 || to >= slots)
                return false;
            return av[from].withdraw(value) && av[to].deposit(value);
        } finally {
            l.unlock();
        }
    }
    */

    // Ex4 implementation
    public boolean transfer(int from, int to, int value) {
        if (from < 0 || from >= slots || to < 0 || to >= slots)
            return false;

        Lock l1 = av[from].getLock();
        Lock l2 = av[to].getLock();

        if (from < to) {
            l1.lock();
            l2.lock();
        } else {
            l2.lock();
            l1.lock();
        }

        try {
            return av[from].withdraw(value) && av[to].deposit(value);
        } finally {
            if (from < to) {
                l1.unlock();
                l2.unlock();
            } else {
                l2.unlock();
                l1.unlock();
            }
        }
    }

    /*
    // Ex2 (added locks in ex3)
    public int totalBalance() {
        l.lock();
        try {
            int ttb = 0;
            for (Account a : av) {
                ttb += a.balance();
            }
            return ttb;
        } finally {
            l.unlock();
        }
    }
     */

    // Ex4 implementation
    public int totalBalance() {
        for (Account a : av) {
            a.getLock().lock();
        }

        try {
            int ttb = 0;
            for (Account a : av) {
                ttb += a.balance;
            }
            return ttb;
        } finally {
            for (int i = slots - 1; i >= 0; i--) {
                av[i].getLock().unlock();
            }
        }
    }
}
