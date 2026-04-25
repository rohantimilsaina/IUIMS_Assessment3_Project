package ims.model;

/**
 * Category - groups products into logical sections (e.g., Electronics, Food).
 * Extends BaseModel demonstrating INHERITANCE.
 */
public class Category extends BaseModel {

    private String name;
    private String description;

    public Category(int id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    // Getters & Setters 
    public String getName()        { return name; }
    public String getDescription() { return description; }
    public void setName(String name)               { this.name = name; }
    public void setDescription(String description) { this.description = description; }

    //  Serialization 
    /**
     * Format: id|name|description
     */
    @Override
    public String toFileLine() {
        return getId() + "|" + name + "|" + description;
    }

    public static Category fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        return new Category(Integer.parseInt(p[0].trim()), p[1].trim(), p[2].trim());
    }

    // ── Display ───────────────────────────────────────────────────────────
    @Override
    public void printDetails() {
        System.out.println("  ┌─────────────────────────────────────┐");
        System.out.printf("  │  ID          : %-21d│%n", getId());
        System.out.printf("  │  Name        : %-21s│%n", name);
        System.out.printf("  │  Description : %-21s│%n", shorten(description, 21));
        System.out.println("  └─────────────────────────────────────┘");
    }

    @Override
    public String getSummary() {
        return String.format("  [%3d]  %-20s  %s", getId(), name, shorten(description, 40));
    }

    private String shorten(String s, int max) {
        if (s == null || s.isEmpty()) return "-";
        return s.length() > max ? s.substring(0, max - 3) + "..." : s;
    }

    @Override
    public String toString() { return name; }
}
