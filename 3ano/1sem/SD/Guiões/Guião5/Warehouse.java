import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class WarehouseSequencial {
    private Map<String, Product> map =  new HashMap<String, Product>();

    private class Product { int quantity = 0; }

    private Product get(String item) {
        Product p = map.get(item);
        if (p != null) return p;
        p = new Product();
        map.put(item, p);
        return p;
    }

    public void supply(String item, int quantity) {
        Product p = get(item);
        p.quantity += quantity;
    }

    // Alterado para estar correto sequencialmente
    public void consume(Set<String> items) {
        for (String s : items) {
            Product p = get(s);
            if (p.quantity <= 0) throw new IllegalStateException("Item " + s + " não disponível");
        }

        for (String s : items) get(s).quantity--;
    }
}

// Ex 1.1
class WarehouseEgoista {
    private Map<String, Product> map =  new HashMap<String, Product>();
    private final ReentrantLock lock = new ReentrantLock();

    private class Product {
        int quantity = 0;
        final Condition hasStock = lock.newCondition();
    }

    private Product get(String item) {
        Product p = map.get(item);
        if (p != null) return p;
        p = new Product();
        map.put(item, p);
        return p;
    }

    public void supply(String item, int quantity) throws InterruptedException {
        lock.lock();
        try {
            Product p = get(item);
            p.quantity += quantity;
            p.hasStock.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void consume(Set<String> items) throws InterruptedException {
        lock.lock();
        try {
            while (true) {
                boolean allAvailable = true;
                List<Product> productsToLock = new ArrayList<>();

                for (String s : items) {
                    Product p = get(s);
                    if (p.quantity <= 0) {
                        allAvailable = false;
                        break;
                    }
                    productsToLock.add(p);
                }

                if (allAvailable) {
                    for (String s : items) get(s).quantity--;
                    return;
                }

                for (String s : items) {
                    Product p = get(s);
                    p.hasStock.await();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}

// Ex 1.2
class WarehouseCooperativa {
    private Map<String, Product> map = new HashMap<String, Product>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition cond = lock.newCondition();

    private class Product { int quantity = 0; }

    private Product get(String item) {
        Product p = map.get(item);
        if (p != null) return p;
        p = new Product();
        map.put(item, p);
        return p;
    }

    public void supply(String item, int quantity) throws InterruptedException {
        lock.lock();
        try {
            Product p = get(item);
            p.quantity += quantity;
            cond.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void consume(Set<String> items) throws InterruptedException {
        lock.lock();
        try {
            while (!canConsume(items)) cond.await();

            for (String s : items) {
                get(s).quantity--;
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean canConsume(Set<String> items) {
        for (String s : items) {
            Product p = get(s);
            if (p.quantity <= 0) return false;
        }
        return true;
    }
}

// Ex 3
class WarehouseCooperativaSemStarvation {
    private Map<String, Product> map = new HashMap<String, Product>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition cond = lock.newCondition();

    private final Queue<ConsumeRequest> pendingRequests = new LinkedList<>();

    private class Product { int quantity = 0; }

    private class ConsumeRequest {
        final Set<String> items;
        boolean fulfilled = false;

        ConsumeRequest(Set<String> items) {
            this.items = new HashSet<>(items);
        }

        boolean canBeFulfilled() {
            for (String item : items) {
                Product p = get(item);
                if (p.quantity <= 0) return false;
            }
            return true;
        }

        void fulfill() {
            for (String item : items)
                get(item).quantity--;
            fulfilled = true;
        }
    }

    private Product get(String item) {
        Product p = map.get(item);
        if (p != null) return p;
        p = new Product();
        map.put(item, p);
        return p;
    }

    public void supply(String item, int quantity) throws InterruptedException {
        lock.lock();
        try {
            Product p = get(item);
            p.quantity += quantity;

            processPendingRequests();

        } finally {
            lock.unlock();
        }
    }

    public void consume(Set<String> items) throws InterruptedException {
        lock.lock();
        try {
            ConsumeRequest request = new ConsumeRequest(items);
            pendingRequests.add(request);

            processPendingRequests();

            while (!request.fulfilled) {
                cond.await();
            }

            pendingRequests.remove(request);

        } finally {
            lock.unlock();
        }
    }

    private void processPendingRequests() {
        Iterator<ConsumeRequest> iterator = pendingRequests.iterator();
        while (iterator.hasNext()) {
            ConsumeRequest request = iterator.next();
            if (request.canBeFulfilled()) {
                request.fulfill();
                iterator.remove();
                cond.signalAll();
                break;
            }
        }
    }
}