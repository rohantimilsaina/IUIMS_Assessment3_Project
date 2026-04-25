package ims.storage;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileStorage - handles all reading and writing of data to text files.
 * All data is stored in a 'data/' folder as pipe-delimited text files.
 * Demonstrates proper file I/O with exception handling.
 */
public class FileStorage {

    public static final String DATA_DIR = "data";

    static {
        // Create the data directory if it does not exist
        new File(DATA_DIR).mkdirs();
    }

    /**
     * Write a list of lines to a file.
     * Overwrites the file entirely each time (simple approach for this app).
     */
    public static void writeFile(String filename, List<String> lines) {
        File file = new File(DATA_DIR + File.separator + filename);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[Storage] ERROR writing " + filename + ": " + e.getMessage());
        }
    }

    /**
     * Read all non-empty, non-comment lines from a file.
     * Returns an empty list if the file does not exist.
     */
    public static List<String> readFile(String filename) {
        List<String> lines = new ArrayList<>();
        File file = new File(DATA_DIR + File.separator + filename);
        if (!file.exists()) return lines;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("[Storage] ERROR reading " + filename + ": " + e.getMessage());
        }
        return lines;
    }

    /**
     * Append query results to a shared query log file.
     * Satisfies the "save query results" assignment requirement.
     */
    public static void saveQueryResult(String queryName, List<String> results) {
        File file = new File(DATA_DIR + File.separator + "query_results.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("=== " + queryName + " ===");
            writer.newLine();
            writer.write("Run at: " + java.time.LocalDateTime.now());
            writer.newLine();
            writer.write("Results: " + results.size() + " record(s)");
            writer.newLine();
            writer.write("---");
            writer.newLine();
            for (String r : results) {
                writer.write(r);
                writer.newLine();
            }
            writer.newLine();
        } catch (IOException e) {
            System.err.println("[Storage] ERROR saving query results: " + e.getMessage());
        }
    }
}
