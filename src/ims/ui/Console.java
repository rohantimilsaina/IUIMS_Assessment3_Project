package ims.ui;

import java.util.Scanner;

/**
 * Console - shared utility for all UI menus.
 * Centralises user input reading and display formatting.
 */
public class Console {

    private static final Scanner sc = new Scanner(System.in);

    // Input readers 

    /** Read a non-empty string. Re-prompts if blank. */
    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("  [!] This field cannot be empty.");
        }
    }

    /** Read an optional string (may be empty). */
    public static String readOptional(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    /** Read a positive integer. Re-prompts on bad input. */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int v = Integer.parseInt(sc.nextLine().trim());
                if (v > 0) return v;
                System.out.println("  [!] Please enter a number greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number. Try again.");
            }
        }
    }

    /** Read a non-negative integer (0 allowed). */
    public static int readNonNegInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int v = Integer.parseInt(sc.nextLine().trim());
                if (v >= 0) return v;
                System.out.println("  [!] Please enter 0 or greater.");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number. Try again.");
            }
        }
    }

    /** Read a non-negative decimal. */
    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double v = Double.parseDouble(sc.nextLine().trim());
                if (v >= 0) return v;
                System.out.println("  [!] Please enter 0 or greater.");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number. Try again (e.g. 19.99).");
            }
        }
    }

    /** Read a menu choice in [min, max]. */
    public static int readChoice(int min, int max) {
        while (true) {
            System.out.print("  Choice [" + min + "-" + max + "]: ");
            try {
                int v = Integer.parseInt(sc.nextLine().trim());
                if (v >= min && v <= max) return v;
                System.out.println("  [!] Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid input.");
            }
        }
    }

    /** Read y/n confirmation. */
    public static boolean confirm(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String s = sc.nextLine().trim().toLowerCase();
            if (s.equals("y") || s.equals("yes")) return true;
            if (s.equals("n") || s.equals("no"))  return false;
            System.out.println("  [!] Please type y or n.");
        }
    }

    // Display helpers 

    public static void header(String title) {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════╗");
        System.out.printf("  ║  %-48s║%n", title);
        System.out.println("  ╚══════════════════════════════════════════════════╝");
    }

    public static void section(String title) {
        System.out.println();
        System.out.println("  ── " + title + " " + "─".repeat(Math.max(0, 45 - title.length())));
    }

    public static void success(String msg) {
        System.out.println("  [OK] " + msg);
    }

    public static void error(String msg) {
        System.out.println("  [ERROR] " + msg);
    }

    public static void info(String msg) {
        System.out.println("  " + msg);
    }

    public static void pause() {
        System.out.print("\n  Press Enter to continue...");
        sc.nextLine();
    }
}
