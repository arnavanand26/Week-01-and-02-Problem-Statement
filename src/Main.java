import java.util.*;

/**
 * Social Media Username Availability Checker
 *
 * Features:
 * - O(1) username lookup using HashMap
 * - Tracks username attempt frequency
 * - Suggests alternative usernames
 * - Retrieves most attempted username
 */

class UsernameService {

    // username -> userId (simulating registered users)
    private Map<String, Integer> registeredUsers;

    // username -> attempt count
    private Map<String, Integer> attemptFrequency;

    public UsernameService() {
        registeredUsers = new HashMap<>();
        attemptFrequency = new HashMap<>();
    }

    // Simulate existing users
    public void registerUser(String username, int userId) {
        registeredUsers.put(username, userId);
    }

    // O(1) availability check
    public boolean checkAvailability(String username) {

        // Track frequency
        attemptFrequency.put(username,
                attemptFrequency.getOrDefault(username, 0) + 1);

        return !registeredUsers.containsKey(username);
    }

    // Suggest alternatives if taken
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        if (!registeredUsers.containsKey(username)) {
            suggestions.add(username);
            return suggestions;
        }

        // Append numbers
        for (int i = 1; i <= 3; i++) {
            String suggestion = username + i;
            if (!registeredUsers.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        // Replace underscore with dot (if exists)
        if (username.contains("_")) {
            String modified = username.replace("_", ".");
            if (!registeredUsers.containsKey(modified)) {
                suggestions.add(modified);
            }
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {
        String mostAttempted = null;
        int max = 0;

        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }

        return mostAttempted + " (" + max + " attempts)";
    }
}

// ---------------- MAIN ----------------

public class Main {

    public static void main(String[] args) {

        UsernameService service = new UsernameService();

        // Simulated existing users
        service.registerUser("john_doe", 101);
        service.registerUser("admin", 1);
        service.registerUser("guest", 102);

        System.out.println("Check Availability:");

        System.out.println("john_doe → " +
                service.checkAvailability("john_doe"));

        System.out.println("jane_smith → " +
                service.checkAvailability("jane_smith"));

        // Simulate multiple attempts for popularity tracking
        for (int i = 0; i < 5; i++)
            service.checkAvailability("admin");

        for (int i = 0; i < 3; i++)
            service.checkAvailability("john_doe");

        System.out.println("\nSuggestions for 'john_doe':");
        System.out.println(service.suggestAlternatives("john_doe"));

        System.out.println("\nMost Attempted Username:");
        System.out.println(service.getMostAttempted());
    }
}