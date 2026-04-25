package ims.ui;

import ims.model.Category;
import ims.model.Product;
import ims.service.CategoryService;
import ims.service.ProductService;
import ims.storage.FileStorage;

import java.util.List;

/**
 * ProductMenu - text-based interface for Product management.
 */
public class ProductMenu {

    private final ProductService  productService;
    private final CategoryService categoryService;

    public ProductMenu(ProductService productService, CategoryService categoryService) {
        this.productService  = productService;
        this.categoryService = categoryService;
    }

    public void show() {
        while (true) {
            Console.header("PRODUCT MANAGEMENT");
            Console.info("1. List All Products");
            Console.info("2. Add Product");
            Console.info("3. View Product Details");
            Console.info("4. Update Product");
            Console.info("5. Delete Product");
            Console.info("6. Search Products");
            Console.info("7. Products by Category");
            Console.info("8. Low Stock Alert");
            Console.info("0. Back to Main Menu");

            int choice = Console.readChoice(0, 8);
            try {
                switch (choice) {
                    case 1 -> listAll();
                    case 2 -> add();
                    case 3 -> view();
                    case 4 -> update();
                    case 5 -> delete();
                    case 6 -> search();
                    case 7 -> byCategory();
                    case 8 -> lowStock();
                    case 0 -> { return; }
                }
            } catch (Exception e) {
                Console.error(e.getMessage());
                Console.pause();
            }
        }
    }

    private void listAll() {
        Console.section("All Products (" + productService.count() + " total)");
        List<Product> list = productService.getAll();
        if (list.isEmpty()) Console.info("No products found.");
        else list.forEach(p -> System.out.println(p.getSummary()));
        Console.pause();
    }

    private void add() {
        Console.section("Add Product");

        // Showing categories for reference
        Console.info("Available Categories:");
        categoryService.getAll().forEach(c -> Console.info(c.getSummary()));
        System.out.println();

        int catId = Console.readInt("  Category ID    : ");
        Category cat = categoryService.getById(catId);

        String sku   = Console.readString("  SKU            : ").toUpperCase();
        String name  = Console.readString("  Name           : ");
        String desc  = Console.readOptional("  Description    : ");
        double price = Console.readDouble("  Selling Price  : $");
        int reorder  = Console.readNonNegInt("  Reorder Level  : ");

        Product p = productService.add(sku, name, desc, catId, cat.getName(), price, reorder);
        Console.success("Product '" + p.getName() + "' added with ID " + p.getId());
        Console.pause();
    }

    private void view() {
        Console.section("View Product");
        int id = Console.readInt("  Product ID : ");
        productService.getById(id).printDetails();
        Console.pause();
    }

    private void update() {
        Console.section("Update Product");
        int id = Console.readInt("  Product ID : ");
        Product p = productService.getById(id);
        Console.info("Current: " + p.getName() + " | SKU: " + p.getSku() + " | Price: $" + p.getSellingPrice());
        System.out.println();

        Console.info("Available Categories:");
        categoryService.getAll().forEach(c -> Console.info(c.getSummary()));
        System.out.println();

        int catId   = Console.readInt("  Category ID   : ");
        Category cat = categoryService.getById(catId);
        String name = Console.readString("  Name          : ");
        String desc = Console.readOptional("  Description   : ");
        double price = Console.readDouble("  Selling Price : $");
        int reorder  = Console.readNonNegInt("  Reorder Level : ");

        productService.update(id, name, desc, catId, cat.getName(), price, reorder);
        Console.success("Product updated.");
        Console.pause();
    }

    private void delete() {
        Console.section("Delete Product");
        int id = Console.readInt("  Product ID : ");
        Product p = productService.getById(id);
        if (!Console.confirm("  Delete '" + p.getName() + "'?")) {
            Console.info("Cancelled.");
        } else {
            productService.delete(id);
            Console.success("Product deleted.");
        }
        Console.pause();
    }

    private void search() {
        Console.section("Search Products");
        String kw = Console.readString("  Keyword : ");
        List<Product> results = productService.search(kw);
        Console.info("Found " + results.size() + " result(s):");
        if (results.isEmpty()) Console.info("No products matched '" + kw + "'.");
        else results.forEach(p -> System.out.println(p.getSummary()));
        List<String> lines = results.stream().map(Product::getSummary).toList();
        FileStorage.saveQueryResult("Product Search: " + kw, lines);
        Console.info("[Results saved to data/query_results.txt]");
        Console.pause();
    }

    private void byCategory() {
        Console.section("Products by Category");
        Console.info("Available Categories:");
        categoryService.getAll().forEach(c -> Console.info(c.getSummary()));
        System.out.println();
        int catId = Console.readInt("  Category ID : ");
        Category cat = categoryService.getById(catId);
        List<Product> results = productService.getByCategory(catId);
        Console.info("Products in '" + cat.getName() + "': " + results.size());
        if (results.isEmpty()) Console.info("No products in this category.");
        else results.forEach(p -> System.out.println(p.getSummary()));
        List<String> lines = results.stream().map(Product::getSummary).toList();
        FileStorage.saveQueryResult("Products in Category: " + cat.getName(), lines);
        Console.info("[Results saved to data/query_results.txt]");
        Console.pause();
    }

    private void lowStock() {
        Console.section("Low Stock Alert");
        List<Product> results = productService.getLowStock();
        Console.info("Products at or below reorder level: " + results.size());
        if (results.isEmpty()) Console.info("All products are sufficiently stocked.");
        else results.forEach(p -> System.out.println(p.getSummary()));
        List<String> lines = results.stream().map(Product::getSummary).toList();
        FileStorage.saveQueryResult("Low Stock Report", lines);
        Console.info("[Results saved to data/query_results.txt]");
        Console.pause();
    }
}
