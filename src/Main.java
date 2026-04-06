import java.util.*;

/**
 * Autocomplete System for Search Engine
 * Version: 1.0
 *
 * Features:
 * - Stores search queries with frequency counts
 * - Returns top suggestions for any prefix
 * - Updates frequencies based on new searches
 * - Optimized with Trie + Min-Heap for top K
 */

class TrieNode {
    Map<Character, TrieNode> children;
    boolean isEnd;
    String query;

    public TrieNode() {
        children = new HashMap<>();
        isEnd = false;
        query = null;
    }
}

class AutocompleteSystem {

    private TrieNode root;
    private Map<String, Integer> frequencyMap;
    private final int topK;

    public AutocompleteSystem(int topK) {
        root = new TrieNode();
        frequencyMap = new HashMap<>();
        this.topK = topK;
    }

    // Insert query into Trie and frequency map
    public void insertQuery(String query, int freq) {
        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + freq);

        TrieNode node = root;
        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        node.isEnd = true;
        node.query = query;
    }

    // Get top K suggestions for a prefix
    public List<String> getSuggestions(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return new ArrayList<>();
        }

        PriorityQueue<Map.Entry<String, Integer>> minHeap = new PriorityQueue<>(
                (a, b) -> a.getValue().equals(b.getValue())
                        ? b.getKey().compareTo(a.getKey())
                        : a.getValue() - b.getValue()
        );

        dfs(node, minHeap);

        List<String> result = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            result.add(minHeap.poll().getKey());
        }
        Collections.reverse(result);
        return result;
    }

    private void dfs(TrieNode node, PriorityQueue<Map.Entry<String, Integer>> heap) {
        if (node.isEnd) {
            Map.Entry<String, Integer> entry = Map.entry(node.query, frequencyMap.get(node.query));
            heap.offer(entry);
            if (heap.size() > topK) heap.poll();
        }
        for (TrieNode child : node.children.values()) {
            dfs(child, heap);
        }
    }

    // Update frequency of a query (new search)
    public void updateFrequency(String query) {
        insertQuery(query, 1);
    }
}

// ---------------- MAIN ----------------

public class Main {

    public static void main(String[] args) {

        System.out.println("===============================================");
        System.out.println("Autocomplete System");
        System.out.println("Version 1.0");
        System.out.println("===============================================");

        AutocompleteSystem auto = new AutocompleteSystem(5);

        // Sample queries and initial frequencies
        auto.insertQuery("java tutorial", 1234567);
        auto.insertQuery("javascript", 987654);
        auto.insertQuery("java download", 456789);
        auto.insertQuery("java 21 features", 10);
        auto.insertQuery("jav bus", 100);

        // Autocomplete for prefix "jav"
        System.out.println("Suggestions for prefix 'jav':");
        List<String> suggestions = auto.getSuggestions("jav");
        for (int i = 0; i < suggestions.size(); i++) {
            String query = suggestions.get(i);
            System.out.printf("%d. %s (%d searches)\n", i + 1, query, auto.frequencyMap.get(query));
        }

        // Update frequency
        System.out.println("\nUpdating frequency for 'java 21 features'...");
        auto.updateFrequency("java 21 features");
        auto.updateFrequency("java 21 features");

        System.out.println("Frequency after updates: " + auto.frequencyMap.get("java 21 features"));
    }
}