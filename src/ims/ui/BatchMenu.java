package ims.ui;

import ims.model.Product;
import ims.model.ProductBatch;
import ims.model.Supplier;
import ims.service.BatchService;
import ims.service.ProductService;
import ims.service.SupplierService;
import ims.storage.FileStorage;

import java.util.List;

/**
 * BatchMenu - text-based interface for ProductBatch (stock) management.
 * Demonstrates FIFO deduction when selling stock.
 */
public class BatchMenu {

    private final BatchService   batchService;
    private final ProductService productService;
    private final SupplierService supplierService;

    public BatchMenu(BatchService batchService,
                     ProductService productService,
                     SupplierService supplierService) {
        this.batchService   = batchService;
        this.productService = productService;
        this.supplierService = supplierService;
    }

    public void show() {
        while (true) {
            Console.header("STOCK BATCH MANAGEMENT");
            Console.info("1. List All Batches");
            Console.info("2. Receive Stock (New Batch)");
            Console.info("3. View Batch Details");
            Console.info("4. Batches for a Product");
            Console.info("5. Deduct Stock (FIFO)");
            Console.info("6. Update Batch");
            Console.info("7. Delete Batch");
            Console.info("8. Batches by Supplier");
            Console.info("0. Back to Main Menu");

            int choice = Console.readChoice(0, 8);
            try {
                switch (choice) {
                    case 1 -> listAll();
                    case 2 -> receive();
                    case 3 -> view();
                    case 4 -> byProduct();
                    case 5 -> deductFifo();
                    case 6 -> update();
                    case 7 -> delete();
                    case 8 -> bySupplier();
                    case 0 -> { return; }
                }
            } catch (Exception e) {
                Console.error(e.getMessage());
                Console.pause();
            }
        }
    }

    private void listAll() {
        Console.section("All Batches (" + batchService.count() + " total)");
        List<ProductBatch> list = batchService.getAll();
        if (list.isEmpty()) Console.info("No batches found.");
        else list.forEach(b -> System.out.println(b.getSummary()));
        Console.pause();
    }

    private void receive() {
        Console.section("Receive Stock Batch");

        Console.info("Available Products:");
        productService.getAll().forEach(p -> Console.info(p.getSummary()));
        System.out.println();
        int productId = Console.readInt("  Product ID      : ");
        productService.getById(productId); // validates existence

        Console.info("Available Suppliers (enter 0 to skip):");
        supplierService.getAll().forEach(s -> Console.info(s.getSummary()));
        System.out.println();
        int supplierId = Console.readNonNegInt("  Supplier ID (0=none) : ");
        String supplierName = supplierId > 0 ? supplierService.getById(supplierId).getName() : "";

        String po       = Console.readOptional("  PO Number (optional) : ");
        int qty         = Console.readInt("  Quantity Received    : ");
        double cost     = Console.readDouble("  Cost Price per unit  : $");
        String expiry   = Console.readOptional("  Expiry Date (yyyy-MM-dd or blank): ");

        ProductBatch b = batchService.receive(productId, supplierId, supplierName, po, qty, cost, expiry);
        Console.success("Batch #" + b.getId() + " received: " + qty
                + " units of '" + productService.getById(productId).getName() + "'");
        Console.pause();
    }

    private void view() {
        Console.section("View Batch");
        int id = Console.readInt("  Batch ID : ");
        batchService.getById(id).printDetails();
        Console.pause();
    }

    private void byProduct() {
        Console.section("Batches for a Product");
        int pid = Console.readInt("  Product ID : ");
        Product p = productService.getById(pid);
        List<ProductBatch> list = batchService.getByProduct(pid);
        Console.info("Batches for '" + p.getName() + "': " + list.size());
        if (list.isEmpty()) Console.info("No batches found.");
        else list.forEach(b -> System.out.println(b.getSummary()));
        List<String> lines = list.stream().map(ProductBatch::getSummary).toList();
        FileStorage.saveQueryResult("Batches for product: " + p.getName(), lines);
        Console.info("[Results saved to data/query_results.txt]");
        Console.pause();
    }

    private void deductFifo() {
        Console.section("Deduct Stock (FIFO)");
        Console.info("Available Products:");
        productService.getAll().forEach(p -> Console.info(p.getSummary()));
        System.out.println();
        int pid = Console.readInt("  Product ID   : ");
        Product p = productService.getById(pid);
        Console.info("Current stock: " + p.getTotalStock() + " units");
        int qty = Console.readInt("  Qty to deduct: ");

        List<ProductBatch> affected = batchService.deductFifo(pid, qty);
        Console.success(qty + " units deducted from '" + p.getName() + "' (FIFO across "
                + affected.size() + " batch(es)).");
        affected.forEach(b -> Console.info(
                "  Batch #" + b.getId() + " remaining: " + b.getRemainingQty()));
        Console.pause();
    }

    private void update() {
        Console.section("Update Batch");
        int id = Console.readInt("  Batch ID : ");
        ProductBatch b = batchService.getById(id);
        Console.info("Current PO: " + b.getPoNumber() + " | Cost: $" + b.getCostPrice()
                + " | Expiry: " + b.getExpiryDate());
        String po     = Console.readOptional("  PO Number  : ");
        double cost   = Console.readDouble("  Cost Price : $");
        String expiry = Console.readOptional("  Expiry Date (yyyy-MM-dd or blank) : ");
        batchService.updateBatch(id, po, cost, expiry);
        Console.success("Batch updated.");
        Console.pause();
    }

    private void delete() {
        Console.section("Delete Batch");
        int id = Console.readInt("  Batch ID : ");
        ProductBatch b = batchService.getById(id);
        Console.info("This will remove " + b.getRemainingQty() + " units from '"
                + b.getProductName() + "'.");
        if (!Console.confirm("  Confirm delete?")) {
            Console.info("Cancelled.");
        } else {
            batchService.delete(id);
            Console.success("Batch #" + id + " deleted.");
        }
        Console.pause();
    }

    private void bySupplier() {
        Console.section("Batches by Supplier");
        Console.info("Available Suppliers:");
        supplierService.getAll().forEach(s -> Console.info(s.getSummary()));
        System.out.println();
        int sid = Console.readInt("  Supplier ID : ");
        Supplier s = supplierService.getById(sid);
        List<ProductBatch> list = batchService.getBySupplier(sid);
        Console.info("Batches from '" + s.getName() + "': " + list.size());
        if (list.isEmpty()) Console.info("No batches from this supplier.");
        else list.forEach(b -> System.out.println(b.getSummary()));
        List<String> lines = list.stream().map(ProductBatch::getSummary).toList();
        FileStorage.saveQueryResult("Batches from supplier: " + s.getName(), lines);
        Console.info("[Results saved to data/query_results.txt]");
        Console.pause();
    }
}
