import java.util.*;

class VideoData {
    String videoId;
    String data;

    VideoData(String videoId, String data) {
        this.videoId = videoId;
        this.data = data;
    }
}

public class Main {

    // L1 Cache (LRU using LinkedHashMap)
    static LinkedHashMap<String, VideoData> L1Cache =
            new LinkedHashMap<String, VideoData>(10000, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                    return size() > 10000;
                }
            };

    // L2 Cache (SSD reference)
    static HashMap<String, String> L2Cache = new HashMap<>();

    // L3 Database (simulated)
    static HashMap<String, VideoData> L3Database = new HashMap<>();

    // Access counter
    static HashMap<String, Integer> accessCount = new HashMap<>();

    // Statistics
    static int L1Hits = 0;
    static int L2Hits = 0;
    static int L3Hits = 0;

    static int L1Miss = 0;
    static int L2Miss = 0;

    // Get Video from cache hierarchy
    public static VideoData getVideo(String videoId) {

        long start = System.currentTimeMillis();

        // L1 Check
        if (L1Cache.containsKey(videoId)) {
            L1Hits++;
            System.out.println("L1 Cache HIT (0.5ms)");
            return L1Cache.get(videoId);
        }

        L1Miss++;
        System.out.println("L1 Cache MISS");

        // L2 Check
        if (L2Cache.containsKey(videoId)) {

            L2Hits++;
            System.out.println("L2 Cache HIT (5ms)");

            VideoData video = L3Database.get(videoId);

            promoteToL1(video);

            return video;
        }

        L2Miss++;
        System.out.println("L2 Cache MISS");

        // L3 Database
        if (L3Database.containsKey(videoId)) {

            L3Hits++;
            System.out.println("L3 Database HIT (150ms)");

            VideoData video = L3Database.get(videoId);

            L2Cache.put(videoId, "SSD_PATH");

            accessCount.put(videoId, 1);

            return video;
        }

        System.out.println("Video not found");
        return null;
    }

    // Promote video to L1
    public static void promoteToL1(VideoData video) {

        int count = accessCount.getOrDefault(video.videoId, 0) + 1;
        accessCount.put(video.videoId, count);

        if (count > 2) {
            L1Cache.put(video.videoId, video);
            System.out.println("Promoted to L1 Cache");
        }
    }

    // Cache invalidation
    public static void invalidateCache(String videoId) {

        L1Cache.remove(videoId);
        L2Cache.remove(videoId);
        accessCount.remove(videoId);

        System.out.println("Cache invalidated for " + videoId);
    }

    // Statistics
    public static void getStatistics() {

        int total = L1Hits + L2Hits + L3Hits;

        double L1Rate = (total == 0) ? 0 : (L1Hits * 100.0 / total);
        double L2Rate = (total == 0) ? 0 : (L2Hits * 100.0 / total);
        double L3Rate = (total == 0) ? 0 : (L3Hits * 100.0 / total);

        System.out.println("Cache Statistics →");
        System.out.println("L1 Hit Rate: " + String.format("%.2f", L1Rate) + "%");
        System.out.println("L2 Hit Rate: " + String.format("%.2f", L2Rate) + "%");
        System.out.println("L3 Hit Rate: " + String.format("%.2f", L3Rate) + "%");
    }

    public static void main(String[] args) {

        // Sample Database
        L3Database.put("video_123", new VideoData("video_123", "Movie Data"));
        L3Database.put("video_999", new VideoData("video_999", "Another Movie"));

        getVideo("video_123");
        getVideo("video_123");
        getVideo("video_999");

        getStatistics();

        invalidateCache("video_123");
    }
}