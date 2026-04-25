package ims.model;

/**
 * ProductBatch - one shipment/lot of a product from a supplier.
 * Extends BaseModel demonstrating INHERITANCE.
 *
 * Business rules:
 *   - remainingQty never goes below 0
 *   - Batches are consumed FIFO (oldest first) when stock is deducted
 *   - Each batch has its own cost price (important for profit tracking)
 */
public class ProductBatch extends BaseModel {

    private int    productId;
    private String productName;   // for displaying
    private int    supplierId;    // 0 = no supplier
    private String supplierName;  // for displaying
    private String poNumber;      // purchase order number
    private int    quantity;      // original quantity received
    private int    remainingQty;  // current remaining (never < 0)
    private double costPrice;
    private String expiryDate;    // "yyyy-MM-dd" or "" if none
    private String receivedDate;  // date this batch was recorded

    public ProductBatch(int id, int productId, String productName,
                        int supplierId, String supplierName, String poNumber,
                        int quantity, int remainingQty, double costPrice,
                        String expiryDate, String receivedDate) {
        super(id);
        this.productId    = productId;
        this.productName  = productName;
        this.supplierId   = supplierId;
        this.supplierName = supplierName;
        this.poNumber     = poNumber;
        this.quantity     = quantity;
        this.remainingQty = remainingQty;
        this.costPrice    = costPrice;
        this.expiryDate   = expiryDate;
        this.receivedDate = receivedDate;
    }

    // Getters & Setters
    public int    getProductId()    { return productId; }
    public String getProductName()  { return productName; }
    public int    getSupplierId()   { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public String getPoNumber()     { return poNumber; }
    public int    getQuantity()     { return quantity; }
    public int    getRemainingQty() { return remainingQty; }
    public double getCostPrice()    { return costPrice; }
    public String getExpiryDate()   { return expiryDate; }
    public String getReceivedDate() { return receivedDate; }

    public void setPoNumber(String poNumber)   { this.poNumber = poNumber; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }
    public void setExpiryDate(String date)     { this.expiryDate = date; }
    public void setSupplierName(String name)   { this.supplierName = name; }
    public void setProductName(String name)    { this.productName = name; }

    public boolean isAvailable() { return remainingQty > 0; }
    public boolean isDepleted()  { return remainingQty == 0; }

    /**
     * Deduct stock from this batch.
     * Throws IllegalArgumentException if insufficient quantity.
     */
    public void deduct(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deduct amount must be positive.");
        if (amount > remainingQty)
            throw new IllegalArgumentException(
                "Not enough stock in batch #" + getId()
                + ". Available: " + remainingQty + ", requested: " + amount);
        remainingQty -= amount;
    }

    // Serialization
    /**
     * Format: id|productId|productName|supplierId|supplierName|poNumber|
     *         quantity|remainingQty|costPrice|expiryDate|receivedDate
     */
    @Override
    public String toFileLine() {
        return getId() + "|" + productId + "|" + productName + "|"
                + supplierId + "|" + supplierName + "|" + poNumber + "|"
                + quantity + "|" + remainingQty + "|"
                + String.format("%.2f", costPrice) + "|"
                + expiryDate + "|" + receivedDate;
    }

    public static ProductBatch fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        return new ProductBatch(
                Integer.parseInt(p[0].trim()),
                Integer.parseInt(p[1].trim()), p[2].trim(),
                Integer.parseInt(p[3].trim()), p[4].trim(),
                p[5].trim(),
                Integer.parseInt(p[6].trim()),
                Integer.parseInt(p[7].trim()),
                Double.parseDouble(p[8].trim()),
                p[9].trim(), p[10].trim()
        );
    }

    // Display
    @Override
    public void printDetails() {
        System.out.println("  ┌──────────────────────────────────────────────┐");
        System.out.printf("  │  Batch ID      : %-28d│%n", getId());
        System.out.printf("  │  Product       : %-28s│%n", productName);
        System.out.printf("  │  Supplier      : %-28s│%n", supplierName.isEmpty() ? "-" : supplierName);
        System.out.printf("  │  PO Number     : %-28s│%n", poNumber.isEmpty() ? "-" : poNumber);
        System.out.printf("  │  Qty Received  : %-28d│%n", quantity);
        System.out.printf("  │  Qty Remaining : %-28d│%n", remainingQty);
        System.out.printf("  │  Cost Price    : $%-27.2f│%n", costPrice);
        System.out.printf("  │  Expiry Date   : %-28s│%n", expiryDate.isEmpty() ? "N/A" : expiryDate);
        System.out.printf("  │  Received On   : %-28s│%n", receivedDate);
        System.out.printf("  │  Status        : %-28s│%n", isDepleted() ? "DEPLETED" : "AVAILABLE");
        System.out.println("  └──────────────────────────────────────────────┘");
    }

    @Override
    public String getSummary() {
        String status = isDepleted() ? "DEPLETED" : "AVAILABLE";
        return String.format("  [%3d]  %-22s  Rcvd:%-5d  Rem:%-5d  $%-7.2f  %s",
                getId(), shorten(productName, 22), quantity, remainingQty, costPrice, status);
    }

    private String shorten(String s, int max) {
        if (s == null || s.isEmpty()) return "-";
        return s.length() > max ? s.substring(0, max - 3) + "..." : s;
    }
}
