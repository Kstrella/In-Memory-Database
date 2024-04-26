import java.util.HashMap;
import java.util.Map;

public class InMemoryDB {
    private Map<String, Integer> db;
    private Map<String, Integer> transactionLog;
    private boolean transactionActive;

    public InMemoryDB() {
        db = new HashMap<>();
        transactionLog = new HashMap<>();
        transactionActive = false;
    }

    public Integer get(String key) {
        if (transactionActive && transactionLog.containsKey(key)) {
            return transactionLog.get(key);
        }
        return db.getOrDefault(key, null);
    }

    public void put(String key, int value) throws Exception {
        if (!transactionActive) {
            throw new Exception("Transaction not in progress. Cant 'put'");
        }
        transactionLog.put(key, value);
    }

    public void beginTransaction() throws Exception {
        if (transactionActive) {
            throw new Exception("Transaction in progress");
        }
        transactionActive = true;
        transactionLog = new HashMap<>();
    }

    public void commit() throws Exception {
        if (!transactionActive) {
            throw new Exception("No transaction to commit");
        }
        db.putAll(transactionLog);
        transactionActive = false;
        transactionLog.clear();
    }

    public void rollback() throws Exception {
        if (!transactionActive) {
            throw new Exception("No transaction to rollback");
        }
        transactionActive = false;
        transactionLog.clear();
    }

    public static void main(String[] args) {
        try {
            InMemoryDB inmemoryDB = new InMemoryDB();

            System.out.println(inmemoryDB.get("A"));

            try {
                inmemoryDB.put("A", 5);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            inmemoryDB.beginTransaction();
            inmemoryDB.put("A", 5);
            System.out.println(inmemoryDB.get("A"));
            inmemoryDB.put("A", 6);
            inmemoryDB.commit();

            System.out.println(inmemoryDB.get("A"));

            try {
                inmemoryDB.commit();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            try {
                inmemoryDB.rollback();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            System.out.println(inmemoryDB.get("B"));
            inmemoryDB.beginTransaction();
            inmemoryDB.put("B", 10);
            inmemoryDB.rollback();

            System.out.println(inmemoryDB.get("B"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
