import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    int time; // minutes from start of day

    Transaction(int id, int amount, String merchant, String account, int time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

public class Main {

    static List<Transaction> transactions = new ArrayList<>();

    // Classic Two-Sum
    public static void findTwoSum(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                Transaction pair = map.get(complement);
                System.out.println("findTwoSum → (" + pair.id + ", " + t.id + ")");
                return;
            }

            map.put(t.amount, t);
        }

        System.out.println("No pair found");
    }

    // Two-Sum within 1 hour window (60 minutes)
    public static void findTwoSumWithTimeWindow(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            Iterator<Map.Entry<Integer, Transaction>> it = map.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<Integer, Transaction> entry = it.next();
                if (t.time - entry.getValue().time > 60) {
                    it.remove();
                }
            }

            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                Transaction pair = map.get(complement);
                System.out.println("findTwoSumWithTimeWindow → (" + pair.id + ", " + t.id + ")");
                return;
            }

            map.put(t.amount, t);
        }

        System.out.println("No pair found in time window");
    }

    // K-Sum using recursion
    public static void findKSum(int k, int target) {
        List<Transaction> result = new ArrayList<>();
        kSumHelper(0, k, target, result);
    }

    static void kSumHelper(int start, int k, int target, List<Transaction> current) {

        if (k == 0 && target == 0) {
            System.out.print("findKSum → (");
            for (Transaction t : current) {
                System.out.print(t.id + " ");
            }
            System.out.println(")");
            return;
        }

        if (k == 0 || start == transactions.size()) return;

        for (int i = start; i < transactions.size(); i++) {

            current.add(transactions.get(i));

            kSumHelper(
                    i + 1,
                    k - 1,
                    target - transactions.get(i).amount,
                    current
            );

            current.remove(current.size() - 1);
        }
    }

    // Duplicate detection
    public static void detectDuplicates() {

        HashMap<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        for (String key : map.keySet()) {

            List<Transaction> list = map.get(key);

            if (list.size() > 1) {

                System.out.print("detectDuplicates → {amount:" +
                        list.get(0).amount +
                        ", merchant:" +
                        list.get(0).merchant +
                        ", accounts:[");

                for (Transaction t : list) {
                    System.out.print(t.account + " ");
                }

                System.out.println("]}");
            }
        }
    }

    public static void main(String[] args) {

        transactions.add(new Transaction(1, 500, "StoreA", "acc1", 600));
        transactions.add(new Transaction(2, 300, "StoreB", "acc2", 615));
        transactions.add(new Transaction(3, 200, "StoreC", "acc3", 630));
        transactions.add(new Transaction(4, 500, "StoreA", "acc2", 640));

        findTwoSum(500);

        findTwoSumWithTimeWindow(500);

        findKSum(3, 1000);

        detectDuplicates();
    }
}