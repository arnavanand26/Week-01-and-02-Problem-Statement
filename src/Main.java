import java.util.*;
import java.util.concurrent.*;

/**
 * DNS Resolver Cache with TTL
 * Version: 1.0
 *
 * Features:
 * - Stores domain → IP mappings
 * - TTL-based expiration
 * - LRU eviction when cache is full
 * - Hit/miss statistics
 */

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000L;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class DNSCache {

    private final int capacity;
    private LinkedHashMap<String, DNSEntry> cache;
    private int hits = 0;
    private int misses = 0;

    public DNSCache(int capacity) {
        this.capacity = capacity;

        // accessOrder=true enables LRU eviction
        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };

        // Start background thread to remove expired entries every second
        ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();
        cleaner.scheduleAtFixedRate(this::cleanExpiredEntries, 1, 1, TimeUnit.SECONDS);
    }

    private void cleanExpiredEntries() {
        Iterator<Map.Entry<String, DNSEntry>> iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, DNSEntry> entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
            }
        }
    }

    // Simulated upstream DNS query
    private String queryUpstream(String domain) {
        // In reality, would perform DNS lookup
        return "172.217." + new Random().nextInt(255) + "." + new Random().nextInt(255);
    }

    public String resolve(String domain, int ttlSeconds) {
        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits++;
            return "Cache HIT → " + entry.ipAddress;
        }

        misses++;
        String ip = queryUpstream(domain);
        cache.put(domain, new DNSEntry(domain, ip, ttlSeconds));
        return "Cache MISS → Query upstream → " + ip + " (TTL: " + ttlSeconds + "s)";
    }

    public void getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);
        System.out.printf("Cache Stats → Hits: %d, Misses: %d, Hit Rate: %.2f%%\n", hits, misses, hitRate);
    }

    public void displayCache() {
        System.out.println("\n--- Current Cache Entries ---");
        for (Map.Entry<String, DNSEntry> e : cache.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue().ipAddress + " (expires in " +
                    ((e.getValue().expiryTime - System.currentTimeMillis()) / 1000) + "s)");
        }
    }
}

// ---------------- MAIN ----------------

public class Main {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("===============================================");
        System.out.println("DNS Cache Resolver System");
        System.out.println("Version 1.0");
        System.out.println("===============================================");

        DNSCache dnsCache = new DNSCache(3); // max 3 entries

        System.out.println(dnsCache.resolve("google.com", 5));
        System.out.println(dnsCache.resolve("facebook.com", 5));
        System.out.println(dnsCache.resolve("youtube.com", 5));

        Thread.sleep(1000);
        System.out.println(dnsCache.resolve("google.com", 5)); // should be HIT

        Thread.sleep(6000); // wait for TTL to expire
        System.out.println(dnsCache.resolve("google.com", 5)); // MISS due to expiration

        System.out.println(dnsCache.resolve("twitter.com", 5)); // triggers LRU eviction if full

        dnsCache.displayCache();
        dnsCache.getCacheStats();
        System.out.println("===============================================");
    }
}