import java.util.*;
import java.util.concurrent.*;

/**
 * Real-Time Website Analytics Dashboard
 * Version: 1.0
 *
 * Features:
 * - Tracks page views and unique visitors
 * - Tracks traffic sources
 * - Maintains top 10 pages
 * - Simulates real-time updates every 5 seconds
 */

class AnalyticsDashboard {

    private Map<String, Integer> pageViews;
    private Map<String, Set<String>> uniqueVisitors;
    private Map<String, Integer> trafficSources;

    public AnalyticsDashboard() {
        pageViews = new HashMap<>();
        uniqueVisitors = new HashMap<>();
        trafficSources = new HashMap<>();
    }

    // Process a single page view event
    public void processEvent(String url, String userId, String source) {
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        uniqueVisitors.putIfAbsent(url, new HashSet<>());
        uniqueVisitors.get(url).add(userId);

        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
    }

    // Display top N pages by total views
    public void displayTopPages(int topN) {
        System.out.println("\nTop Pages:");

        pageViews.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(topN)
                .forEach(e -> {
                    String url = e.getKey();
                    int views = e.getValue();
                    int unique = uniqueVisitors.get(url).size();
                    System.out.println(url + " - " + views + " views (" + unique + " unique)");
                });
    }

    // Display traffic sources
    public void displayTrafficSources() {
        System.out.println("\nTraffic Sources:");
        int total = trafficSources.values().stream().mapToInt(Integer::intValue).sum();
        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            double percent = total == 0 ? 0 : (entry.getValue() * 100.0 / total);
            System.out.printf("%s: %.0f%%\n", entry.getKey(), percent);
        }
    }

    // Display dashboard
    public void displayDashboard(int topN) {
        displayTopPages(topN);
        displayTrafficSources();
    }
}

// ---------------- MAIN ----------------

public class Main {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("===============================================");
        System.out.println("Real-Time Website Analytics Dashboard");
        System.out.println("Version 1.0");
        System.out.println("===============================================");

        AnalyticsDashboard dashboard = new AnalyticsDashboard();

        // Simulated incoming events
        String[][] events = {
                {"/article/breaking-news", "user_123", "google"},
                {"/article/breaking-news", "user_456", "facebook"},
                {"/sports/championship", "user_123", "direct"},
                {"/article/breaking-news", "user_789", "google"},
                {"/sports/championship", "user_222", "facebook"},
                {"/tech/new-gadget", "user_333", "google"},
                {"/article/breaking-news", "user_101", "direct"}
        };

        // Process events
        for (String[] event : events) {
            dashboard.processEvent(event[0], event[1], event[2]);
        }

        // Display dashboard (top 10 pages)
        dashboard.displayDashboard(10);

        System.out.println("===============================================");
    }
}