import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Bank {

    private static class Account {
        private final Lock accLock = new ReentrantLock(); // added in ex1
        private int balance;

        Account(int balance) { this.balance = balance; }

        void lock() { accLock.lock(); } // added in ex1
        void unlock() { accLock.unlock(); } // added in ex1

        int balance() { return balance; }

        boolean deposit(int value) {
            balance += value;
            return true;
        }
        boolean withdraw(int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    // private final Lock block = new ReentrantLock(); // added in ex1, removed in ex3
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();

    private Map<Integer, Account> map = new HashMap<Integer, Account>();
    private int nextId = 0;

    // create account and return account id
    /* Ex1
    public int createAccount(int balance) {
        block.lock();
        try {
            Account c = new Account(balance);
            int id = nextId;
            nextId += 1;
            map.put(id, c);
            return id;
        } finally {
            block.unlock();
        }
    }
    */
    // Ex3
    public int createAccount(int balance) {
        writeLock.lock();
        try {
            Account c = new Account(balance);
            int id = nextId++;
            map.put(id, c);
            return id;
        } finally {
            writeLock.unlock();
        }
    }

    // close account and return balance, or 0 if no such account
    /* Ex1
    public int closeAccount(int id) {
        block.lock();
        try {
            Account c = map.remove(id);
            if (c == null)
                return 0;

            c.lock();
            try {
                return c.balance();
            } finally {
                c.unlock();
            }
        } finally {
            block.unlock();
        }
    }
    */
    // Ex3
    public int closeAccount(int id) {
        writeLock.lock();
        try {
            Account c = map.remove(id);
            if (c == null)
                return 0;
            c.lock();
            try {
                return c.balance();
            } finally {
                c.unlock();
            }
        } finally {
            writeLock.unlock();
        }
    }

    // account balance; 0 if no such account
    /* Ex1
    public int balance(int id) {
        Account c = map.get(id);
        if (c == null)
            return 0;
        c.lock();
        try {
            return c.balance();
        } finally {
            c.unlock();
        }
    }
     */
    // Ex3
    public int balance(int id) {
        readLock.lock();
        try {
            Account c = map.get(id);
            if (c == null)
                return 0;
            c.lock();
            try {
                return c.balance();
            } finally {
                c.unlock();
            }
        } finally {
            readLock.unlock();
        }
    }

    // deposit; fails if no such account
    /* Ex1
    public boolean deposit(int id, int value) {
        Account c = map.get(id);
        if (c == null)
            return false;
        c.lock();
        try {
            return c.deposit(value);
        } finally {
            c.unlock();
        }
    }
    */
    // Ex3
    public boolean deposit(int id, int value) {
        readLock.lock();
        try {
            Account c = map.get(id);
            if (c == null)
                return false;
            c.lock();
            try {
                return c.deposit(value);
            } finally {
                c.unlock();
            }
        } finally {
            readLock.unlock();
        }
    }

    // withdraw; fails if no such account or insufficient balance
    /* Ex1
    public boolean withdraw(int id, int value) {
        Account c = map.get(id);
        if (c == null)
            return false;
        c.lock();
        try {
            return c.withdraw(value);
        } finally {
            c.unlock();
        }
    }
    */
    // Ex3
    public boolean withdraw(int id, int value) {
        readLock.lock();
        try {
            Account c = map.get(id);
            if (c == null)
                return false;
            c.lock();
            try {
                return c.withdraw(value);
            } finally {
                c.unlock();
            }
        } finally {
            readLock.unlock();
        }
    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance
    /* Ex1
    public boolean transfer(int from, int to, int value) {
        Account cfrom = map.get(from);
        Account cto = map.get(to);
        if (cfrom == null || cto == null)
            return false;

        Account fst = from < to ? cfrom : cto;
        Account snd = from < to ? cto : cfrom;

        fst.lock();
        snd.lock();
        try {
            if (!cfrom.withdraw(value))
                return false;
            return cto.deposit(value);
        } finally {
            snd.unlock();
            fst.unlock();
        }
    }
    */
    // Ex3
    public boolean transfer(int from, int to, int value) {
        readLock.lock();
        try {
            Account cfrom = map.get(from);
            Account cto   = map.get(to);
            if (cfrom == null || cto == null)
                return false;

            Account fst = from < to ? cfrom : cto;
            Account snd = from < to ? cto : cfrom;

            fst.lock();
            snd.lock();
            try {
                if (!cfrom.withdraw(value))
                    return false;
                return cto.deposit(value);
            } finally {
                snd.unlock();
                fst.unlock();
            }
        } finally {
            readLock.unlock();
        }
    }

    // sum of balances in set of accounts; 0 if some does not exist
    /* Ex1
    public int totalBalance(int[] ids) {
        int[] sorted = Arrays.copyOf(ids, ids.length);
        Arrays.sort(sorted);

        List<Account> accs = new ArrayList<>();
        for (int id : sorted) {
            Account c = map.get(id);
            if (c == null)
                return 0;
            accs.add(c);
        }

        for (Account c : accs)
            c.lock();
        try {
            int total = 0;
            for (Account c : accs)
                total += c.balance();
            return total;
        } finally {
            for (Account c : accs)
                c.unlock();
        }
    }
    */
    // Ex3
    public int totalBalance(int[] ids) {
        readLock.lock();
        try {
            int[] sorted = Arrays.copyOf(ids, ids.length);
            Arrays.sort(sorted);

            List<Account> accounts = new ArrayList<>();
            for (int id : sorted) {
                Account c = map.get(id);
                if (c == null)
                    return 0;
                accounts.add(c);
            }

            for (Account c : accounts)
                c.lock();
            try {
                int total = 0;
                for (Account c : accounts)
                    total += c.balance();
                return total;
            } finally {
                for (Account c : accounts)
                    c.unlock();
            }
        } finally {
            readLock.unlock();
        }
    }
}
