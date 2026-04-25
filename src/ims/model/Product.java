package ims.model;

/**
 * Product - an item tracked in inventory.
 * Extends BaseModel demonstrating INHERITANCE.
 * Stock quantity is derived from ProductBatches (not stored here directly).
 */
public class Product extends BaseModel {

    private String sku;
    private String name;
    private String description;
    private int    categoryId;
    private String categoryName;   // storing for display convenience
    private double sellingPrice;
    private int    reorderLevel;   // alerting threshold for low stock
    private int    totalStock;     // updating whenever batches change
    private boolean active;

    public Product(int id, String sku, String name, String description,
                   int categoryId, String categoryName,
                   double sellingPrice, int reorderLevel,
                   int totalStock, boolean active) {
        super(id);
        this.sku          = sku;
        this.name         = name;
        this.description  = description;
        this.categoryId   = categoryId;
        this.categoryName = categoryName;
        this.sellingPrice = sellingPrice;
        this.reorderLevel = reorderLevel;
        this.totalStock   = totalStock;
        this.active       = active;
    }

    // Getters & Setters 
    public String getSku()          { return sku; }
    public String getName()         { return name; }
    public String getDescription()  { return description; }
    public int    getCategoryId()   { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public double getSellingPrice() { return sellingPrice; }
    public int    getReorderLevel() { return reorderLevel; }
    public int    getTotalStock()   { return totalStock; }
    public boolean isActive()       { return active; }

    public void setName(String name)               { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategoryId(int id)              { this.categoryId = id; }
    public void setCategoryName(String name)       { this.categoryName = name; }
    public void setSellingPrice(double price)      { this.sellingPrice = price; }
    public void setReorderLevel(int level)         { this.reorderLevel = level; }
    public void setTotalStock(int stock)           { this.totalStock = stock; }
    public void setActive(boolean active)          { this.active = active; }

    public void addStock(int qty)    { this.totalStock += qty; }
    public void removeStock(int qty) { this.totalStock = Math.max(0, this.totalStock - qty); }

    public boolean isLowStock() { return totalStock <= reorderLevel; }

    // Serialization 
    /**
     * Format: id|sku|name|description|categoryId|categoryName|sellingPrice|reorderLevel|totalStock|active
     */
    @Override
    public String toFileLine() {
        return getId() + "|" + sku + "|" + name + "|" + description + "|"
                + categoryId + "|" + categoryName + "|"
                + String.format("%.2f", sellingPrice) + "|"
                + reorderLevel + "|" + totalStock + "|" + active;
    }

    public static Product fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        return new Product(
                Integer.parseInt(p[0].trim()),
                p[1].trim(), p[2].trim(), p[3].trim(),
                Integer.parseInt(p[4].trim()), p[5].trim(),
                Double.parseDouble(p[6].trim()),
                Integer.parseInt(p[7].trim()),
                Integer.parseInt(p[8].trim()),
                Boolean.parseBoolean(p[9].trim())
        );
    }

    // Display 
    @Override
    public void printDetails() {
        String stockFlag = isLowStock() ? "  *** LOW STOCK ***" : "";
        System.out.println("  ┌──────────────────────────────────────────────┐");
        System.out.printf("  │  ID            : %-28d│%n", getId());
        System.out.printf("  │  SKU           : %-28s│%n", sku);
        System.out.printf("  │  Name          : %-28s│%n", name);
        System.out.printf("  │  Description   : %-28s│%n", shorten(description, 28));
        System.out.printf("  │  Category      : %-28s│%n", categoryName);
        System.out.printf("  │  Selling Price : $%-27.2f│%n", sellingPrice);
        System.out.printf("  │  Total Stock   : %-28s│%n", totalStock + stockFlag);
        System.out.printf("  │  Reorder Level : %-28d│%n", reorderLevel);
        System.out.printf("  │  Active        : %-28s│%n", active ? "Yes" : "No");
        System.out.println("  └──────────────────────────────────────────────┘");
    }

    @Override
    public String getSummary() {
        String lowFlag = isLowStock() ? " ⚠" : "";
        return String.format("  [%3d]  %-10s  %-25s  Qty:%-6d  $%-8.2f  %s%s",
                getId(), sku, shorten(name, 25), totalStock,
                sellingPrice, active ? "ACTIVE" : "INACTIVE", lowFlag);
    }

    private String shorten(String s, int max) {
        if (s == null || s.isEmpty()) return "-";
        return s.length() > max ? s.substring(0, max - 3) + "..." : s;
    }

    @Override
    public String toString() { return name + " (" + sku + ")"; }
}
