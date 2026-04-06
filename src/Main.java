import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Distributed Rate Limiter for API Gateway
 * Version: 1.0
 *
 * Features:
 * - Token bucket per client
 * - Burst handling up to max tokens
 * - Automatic refill over time
 * - Denies requests when limit exceeded
 */

class TokenBucket {
    private final int maxTokens;
    private final long refillIntervalMs; // refill interval in ms
    private AtomicInteger tokens;
    private long lastRefillTime;

    public TokenBucket(int maxTokens, long refillIntervalMs) {
        this.maxTokens = maxTokens;
        this.refillIntervalMs = refillIntervalMs;
        this.tokens = new AtomicInteger(maxTokens);
        this.lastRefillTime = System.currentTimeMillis();
    }

    // synchronized to prevent race conditions
    public synchronized boolean allowRequest() {
        refillTokens();
        if (tokens.get() > 0) {
            tokens.decrementAndGet();
            return true;
        } else {
            return false;
        }
    }

    private void refillTokens() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;
        if (elapsed >= refillIntervalMs) {
            tokens.set(maxTokens);
            lastRefillTime = now;
        }
    }

    public int getRemainingTokens() {
        refillTokens();
        return tokens.get();
    }
}

class RateLimiter {
    private Map<String, TokenBucket> clientBuckets;
    private final int maxRequestsPerHour;

    public RateLimiter(int maxRequestsPerHour) {
        clientBuckets = new HashMap<>();
        this.maxRequestsPerHour = maxRequestsPerHour;
    }

    public String checkRateLimit(String clientId) {
        TokenBucket bucket = clientBuckets.get(clientId);
        if (bucket == null) {
            bucket = new TokenBucket(maxRequestsPerHour, 3600 * 1000); // 1 hour refill
            clientBuckets.put(clientId, bucket);
        }

        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getRemainingTokens() + " requests remaining)";
        } else {
            return "Denied (0 requests remaining, retry after 3600s)";
        }
    }

    public String getRateLimitStatus(String clientId) {
        TokenBucket bucket = clientBuckets.get(clientId);
        if (bucket == null) {
            return "{used: 0, limit: " + maxRequestsPerHour + ", reset: 3600}";
        }
        int used = maxRequestsPerHour - bucket.getRemainingTokens();
        return "{used: " + used + ", limit: " + maxRequestsPerHour + ", reset: 3600}";
    }
}

// ---------------- MAIN ----------------

public class Main {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("===============================================");
        System.out.println("Distributed API Rate Limiter");
        System.out.println("Version 1.0");
        System.out.println("===============================================");

        RateLimiter limiter = new RateLimiter(5); // 5 requests per hour (demo)

        String clientId = "abc123";

        for (int i = 1; i <= 7; i++) {
            System.out.println("Request " + i + ": " + limiter.checkRateLimit(clientId));
        }

        System.out.println("Rate Limit Status: " + limiter.getRateLimitStatus(clientId));

        // Simulate waiting for refill
        System.out.println("\nWaiting 1 second for refill simulation...");
        Thread.sleep(1000);

        System.out.println("Request after refill: " + limiter.checkRateLimit(clientId));
        System.out.println("Rate Limit Status: " + limiter.getRateLimitStatus(clientId));
    }
}