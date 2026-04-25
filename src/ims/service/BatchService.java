package ims.service;

import ims.exception.NotFoundException;
import ims.model.ProductBatch;
import ims.storage.FileStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * BatchService - manages ProductBatch operations.
 *
 * Uses a LinkedList to support FIFO (First-In, First-Out) stock deduction:
 * when stock is sold, the oldest batch is consumed first.
 *
 * Persists to "batches.txt" via FileStorage.
 */
public class BatchService {

    private static final String FILE = "batches.txt";

    // LinkedList allows efficient FIFO operations
    private final LinkedList<ProductBatch> batches = new LinkedList<>();
    private int nextId = 1;

    private final ProductService productService;

    public BatchService(ProductService productService) {
        this.productService = productService;
        load();
    }

    //  CRUD 

    /** Receive a new stock shipment for a product. */
    public ProductBatch receive(int productId, int supplierId, String supplierName,
                                String poNumber, int quantity, double costPrice,
                                String expiryDate) {
        // Validating product exists
        productService.getById(productId);  // throws if not found
        String productName = productService.getById(productId).getName();
        String today = LocalDate.now().toString();

        ProductBatch batch = new ProductBatch(
                nextId++, productId, productName,
                supplierId, supplierName, poNumber,
                quantity, quantity, costPrice,
                expiryDate, today
        );
        batches.add(batch);
        productService.adjustStock(productId, quantity);
        save();
        return batch;
    }

    public ProductBatch getById(int id) {
        return batches.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Batch", id));
    }

    public List<ProductBatch> getAll() { return new ArrayList<>(batches); }

    public List<ProductBatch> getByProduct(int productId) {
        return batches.stream()
                .filter(b -> b.getProductId() == productId)
                .collect(Collectors.toList());
    }

    public List<ProductBatch> getAvailableByProduct(int productId) {
        return batches.stream()
                .filter(b -> b.getProductId() == productId && b.isAvailable())
                .collect(Collectors.toList());
    }

    public List<ProductBatch> getBySupplier(int supplierId) {
        return batches.stream()
                .filter(b -> b.getSupplierId() == supplierId)
                .collect(Collectors.toList());
    }

    /**
     * Deduct stock using FIFO — consumes oldest available batches first.
     * @return list of batches that were affected
     */
    public List<ProductBatch> deductFifo(int productId, int quantityNeeded) {
        // Getting available batches for product, sorted by ID ascending (FIFO = oldest first)
        List<ProductBatch> available = batches.stream()
                .filter(b -> b.getProductId() == productId && b.isAvailable())
                .sorted(Comparator.comparingInt(ProductBatch::getId))
                .collect(Collectors.toList());

        // Checking total available
        int totalAvail = available.stream().mapToInt(ProductBatch::getRemainingQty).sum();
        if (totalAvail < quantityNeeded)
            throw new IllegalStateException(
                "Insufficient stock. Requested: " + quantityNeeded
                + ", Available: " + totalAvail);

        List<ProductBatch> affected = new ArrayList<>();
        int remaining = quantityNeeded;

        for (ProductBatch b : available) {
            if (remaining <= 0) break;
            int take = Math.min(b.getRemainingQty(), remaining);
            b.deduct(take);
            remaining -= take;
            affected.add(b);
        }

        productService.adjustStock(productId, -quantityNeeded);
        save();
        return affected;
    }

    public void updateBatch(int id, String poNumber, double costPrice, String expiryDate) {
        ProductBatch b = getById(id);
        b.setPoNumber(poNumber);
        b.setCostPrice(costPrice);
        b.setExpiryDate(expiryDate);
        save();
    }

    public void delete(int id) {
        ProductBatch b = getById(id);
        // Deducting the remaining stock from the product total
        productService.adjustStock(b.getProductId(), -b.getRemainingQty());
        batches.remove(b);
        save();
    }

    public int count() { return batches.size(); }

    //  File I/O

    public void save() {
        List<String> lines = batches.stream()
                .map(ProductBatch::toFileLine)
                .collect(Collectors.toList());
        FileStorage.writeFile(FILE, lines);
    }

    public void load() {
        batches.clear();
        nextId = 1;
        for (String line : FileStorage.readFile(FILE)) {
            try {
                ProductBatch b = ProductBatch.fromFileLine(line);
                batches.add(b);
                if (b.getId() >= nextId) nextId = b.getId() + 1;
            } catch (Exception e) {
                System.err.println("[BatchService] Skipping bad line: " + e.getMessage());
            }
        }
    }
}
