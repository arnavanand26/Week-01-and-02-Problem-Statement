import java.util.*;

/**
 * Plagiarism Detection System
 * Version: 1.0
 *
 * Features:
 * - Breaks documents into n-grams
 * - Maps n-grams to documents using HashMap
 * - Calculates similarity percentages
 * - Detects potential plagiarism
 */

class PlagiarismDetector {

    private int nGramSize;
    private Map<String, Set<String>> nGramMap; // n-gram -> document IDs

    public PlagiarismDetector(int nGramSize) {
        this.nGramSize = nGramSize;
        nGramMap = new HashMap<>();
    }

    // Extract n-grams from document
    private List<String> extractNGrams(String content) {
        String[] words = content.split("\\s+");
        List<String> nGrams = new ArrayList<>();
        for (int i = 0; i <= words.length - nGramSize; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < nGramSize; j++) {
                sb.append(words[i + j]);
                if (j != nGramSize - 1) sb.append(" ");
            }
            nGrams.add(sb.toString());
        }
        return nGrams;
    }

    // Analyze a document against existing documents
    public void analyzeDocument(String docId, String content) {
        List<String> nGrams = extractNGrams(content);
        Map<String, Integer> matches = new HashMap<>();

        for (String nGram : nGrams) {
            Set<String> docs = nGramMap.getOrDefault(nGram, new HashSet<>());
            for (String existingDoc : docs) {
                matches.put(existingDoc, matches.getOrDefault(existingDoc, 0) + 1);
            }
            docs.add(docId);
            nGramMap.put(nGram, docs);
        }

        System.out.println("Document: " + docId);
        System.out.println("Extracted n-grams: " + nGrams.size());

        if (matches.isEmpty()) {
            System.out.println("No matches found.\n");
            return;
        }

        for (Map.Entry<String, Integer> entry : matches.entrySet()) {
            double similarity = (entry.getValue() * 100.0) / nGrams.size();
            String status = similarity > 50 ? "PLAGIARISM DETECTED" : "suspicious";
            System.out.printf("Matches with %s → %d n-grams → Similarity: %.1f%% (%s)\n",
                    entry.getKey(), entry.getValue(), similarity, status);
        }
        System.out.println();
    }
}

// ---------------- MAIN ----------------

public class Main {

    public static void main(String[] args) {

        System.out.println("===============================================");
        System.out.println("Plagiarism Detection System");
        System.out.println("Version 1.0");
        System.out.println("===============================================");

        PlagiarismDetector detector = new PlagiarismDetector(5); // 5-grams

        // Sample documents
        String doc1 = "The quick brown fox jumps over the lazy dog";
        String doc2 = "A quick brown fox jumped over the lazy dog in the park";
        String doc3 = "An entirely different document with unique words";

        // Analyze documents sequentially
        detector.analyzeDocument("essay_001.txt", doc1);
        detector.analyzeDocument("essay_002.txt", doc2);
        detector.analyzeDocument("essay_003.txt", doc3);
    }
}