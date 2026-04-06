import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * E-commerce Flash Sale Inventory Manager
 * Version: 1.0
 *
 * Features:
 * - Real-time stock tracking
 * - O(1) stock lookup
 * - Thread-safe purchase operations
 * - FIFO waiting list
 */
class InventoryManager {

    // productId -> stock count
    private Map<String, AtomicInteger> stockMap;

    // productId -> waiting list (userIds in FIFO order)
    private Map<String, LinkedHashMap<Integer, Integer>> waitingListMap;

    public InventoryManager() {
        stockMap = new HashMap<>();
        waitingListMap = new HashMap<>();
    }

    // Initialize product stock
    public void addProduct(String productId, int stockCount) {
        stockMap.put(productId, new AtomicInteger(stockCount));
        waitingListMap.put(productId, new LinkedHashMap<>());
    }

    // Check stock in O(1)
    public int checkStock(String productId) {
        AtomicInteger stock = stockMap.get(productId);
        return stock != null ? stock.get() : 0;
    }

    // Attempt purchase (thread-safe)
    public synchronized String purchaseItem(String productId, int userId) {
        AtomicInteger stock = stockMap.get(productId);
        LinkedHashMap<Integer, Integer> waitingList = waitingListMap.get(productId);

        if (stock == null) return "Product not found";

        if (stock.get() > 0) {
            stock.decrementAndGet();
            return "Success: " + stock.get() + " units remaining";
        } else {
            // Add to waiting list FIFO
            if (!waitingList.containsKey(userId)) {
                waitingList.put(userId, waitingList.size() + 1);
            }
            int position = waitingList.get(userId);
            return "Added to waiting list, position #" + position;
        }
    }

    // Display waiting list for product
    public void displayWaitingList(String productId) {
        LinkedHashMap<Integer, Integer> waitingList = waitingListMap.get(productId);
        System.out.println("\nWaiting List for " + productId + ":");
        for (Map.Entry<Integer, Integer> entry : waitingList.entrySet()) {
            System.out.println("UserId: " + entry.getKey() + " → Position #" + entry.getValue());
        }
    }
}

// ---------------- MAIN ----------------

public class Main {

    public static void main(String[] args) {

        InventoryManager manager = new InventoryManager();

        String productId = "IPHONE15_256GB";
        manager.addProduct(productId, 5); // simulate 5 units for testing

        System.out.println("Initial Stock: " + manager.checkStock(productId));

        // Simulate multiple users purchasing
        int[] userIds = {12345, 67890, 11111, 22222, 33333, 44444};

        for (int userId : userIds) {
            String result = manager.purchaseItem(productId, userId);
            System.out.println("User " + userId + ": " + result);
        }

        // Display waiting list
        manager.displayWaitingList(productId);

        System.out.println("\nFinal Stock: " + manager.checkStock(productId));
    }
}