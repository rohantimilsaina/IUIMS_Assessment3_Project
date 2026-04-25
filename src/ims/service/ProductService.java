package ims.service;

import ims.exception.DuplicateException;
import ims.exception.NotFoundException;
import ims.model.Product;
import ims.storage.FileStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ProductService - manages all Product operations.
 * Uses ArrayList as the in-memory data structure.
 * Persists to "products.txt" via FileStorage.
 */
public class ProductService {

    private static final String FILE = "products.txt";
    private final ArrayList<Product> products = new ArrayList<>();
    private int nextId = 1;

    public ProductService() { load(); }

    // CRUD

    public Product add(String sku, String name, String description,
                       int categoryId, String categoryName,
                       double sellingPrice, int reorderLevel) {
        if (findBySku(sku).isPresent())
            throw new DuplicateException("Product with SKU '" + sku + "' already exists.");
        Product p = new Product(nextId++, sku, name, description,
                categoryId, categoryName, sellingPrice, reorderLevel, 0, true);
        products.add(p);
        save();
        return p;
    }

    public Product getById(int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Product", id));
    }

    public Optional<Product> findBySku(String sku) {
        return products.stream()
                .filter(p -> p.getSku().equalsIgnoreCase(sku))
                .findFirst();
    }

    public List<Product> getAll() { return new ArrayList<>(products); }

    public List<Product> getByCategory(int categoryId) {
        return products.stream()
                .filter(p -> p.getCategoryId() == categoryId)
                .collect(Collectors.toList());
    }

    public List<Product> getLowStock() {
        return products.stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
    }

    public List<Product> search(String keyword) {
        String kw = keyword.toLowerCase();
        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(kw)
                        || p.getSku().toLowerCase().contains(kw)
                        || p.getDescription().toLowerCase().contains(kw)
                        || p.getCategoryName().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    public Product update(int id, String name, String description,
                          int categoryId, String categoryName,
                          double sellingPrice, int reorderLevel) {
        Product p = getById(id);
        p.setName(name);
        p.setDescription(description);
        p.setCategoryId(categoryId);
        p.setCategoryName(categoryName);
        p.setSellingPrice(sellingPrice);
        p.setReorderLevel(reorderLevel);
        save();
        return p;
    }

    public void delete(int id) {
        Product p = getById(id);
        if (p.getTotalStock() > 0)
            throw new IllegalStateException(
                "Cannot delete product with remaining stock (" + p.getTotalStock() + " units). " +
                "Delete or deplete all batches first.");
        products.remove(p);
        save();
    }

    /** Called by BatchService whenever a batch is added or removed. */
    public void adjustStock(int productId, int delta) {
        Product p = getById(productId);
        p.setTotalStock(Math.max(0, p.getTotalStock() + delta));
        save();
    }

    public boolean exists(int id) {
        return products.stream().anyMatch(p -> p.getId() == id);
    }

    public int count() { return products.size(); }

    //  File I/O 

    public void save() {
        List<String> lines = products.stream()
                .map(Product::toFileLine)
                .collect(Collectors.toList());
        FileStorage.writeFile(FILE, lines);
    }

    public void load() {
        products.clear();
        nextId = 1;
        for (String line : FileStorage.readFile(FILE)) {
            try {
                Product p = Product.fromFileLine(line);
                products.add(p);
                if (p.getId() >= nextId) nextId = p.getId() + 1;
            } catch (Exception e) {
                System.err.println("[ProductService] Skipping bad line: " + e.getMessage());
            }
        }
    }
}
