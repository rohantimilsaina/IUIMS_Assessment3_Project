package ims.ui;

import ims.model.*;
import ims.service.*;
import ims.storage.FileStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * ReportsMenu - Periodic evaluation and status monitoring.
 */
public class ReportsMenu {

    private final ProductService  productService;
    private final CustomerService customerService;
    private final SupplierService supplierService;
    private final BatchService    batchService;
    private final EvaluationService evaluationService;

    public ReportsMenu(ProductService productService,
                       CustomerService customerService,
                       SupplierService supplierService,
                       BatchService batchService,
                       EvaluationService evaluationService) {
        this.productService  = productService;
        this.customerService = customerService;
        this.supplierService = supplierService;
        this.batchService    = batchService;
        this.evaluationService = evaluationService;
    }

    public void show() {
        while (true) {
            Console.header("REPORTS & PERIODIC EVALUATION");
            Console.info("1. Run Full System Evaluation");
            Console.info("2. Low Stock Report");
            Console.info("3. Credit Limit Violations");
            Console.info("4. Good Standing Users");
            Console.info("5. Inactive Supplier Report");
            Console.info("6. Full Inventory Report");
            Console.info("7. Evaluation Event History");
            Console.info("8. Polymorphism Demo");
            Console.info("0. Back to Main Menu");

            int choice = Console.readChoice(0, 8);
            try {
                switch (choice) {
                    case 1 -> runFullEvaluation();
                    case 2 -> lowStockReport();
                    case 3 -> creditViolationReport();
                    case 4 -> goodStandingReport();
                    case 5 -> inactiveSupplierReport();
                    case 6 -> fullInventoryReport();
                    case 7 -> evaluationHistory();
                    case 8 -> polymorphismDemo();
                    case 0 -> { return; }
                }
            } catch (Exception e) {
                Console.error(e.getMessage());
                Console.pause();
            }
        }
    }

    private void runFullEvaluation() {
        Console.section("FULL SYSTEM EVALUATION — Automatic Actions");
        List<String> log = new ArrayList<>();

        List<Product> lowStock = productService.getLowStock();
        if (lowStock.isEmpty()) {
            Console.info("[PASS] All products have sufficient stock.");
            log.add("[PASS] All products have sufficient stock.");
        } else {
            Console.info("[CHECK] Low stock products: " + lowStock.size());
            for (Product p : lowStock) {
                String msg = "Product " + p.getName() + " is low on stock: "
                        + p.getTotalStock() + " left, reorder level " + p.getReorderLevel();
                evaluationService.queuePenalty("Product", p.getId(), p.getName(), msg);
                Console.info("  => " + msg);
                log.add(msg);
            }
        }

        List<Customer> overLimit = customerService.getOverLimit();
        if (overLimit.isEmpty()) {
            Console.info("[PASS] No users are over their credit limit.");
            log.add("[PASS] No users are over their credit limit.");
        } else {
            Console.info("[CHECK] Users over credit limit: " + overLimit.size());
            for (Customer c : overLimit) {
                if (c.getStatus() != Customer.Status.BLOCKED) {
                    customerService.setStatus(c.getId(), Customer.Status.BLOCKED);
                }
                String msg = "User blocked for exceeding limit. Balance $"
                        + String.format("%.2f", c.getCurrentBalance()) + " / Limit $"
                        + String.format("%.2f", c.getCreditLimit());
                evaluationService.queuePenalty("User", c.getId(), c.getFullName(), msg);
                Console.info("  => " + c.getFullName() + " BLOCKED");
                log.add(msg + " => " + c.getFullName());
            }
        }

        List<Customer> goodStanding = customerService.getAll().stream()
                .filter(c -> c.getCurrentBalance() == 0 && c.getStatus() == Customer.Status.ACTIVE)
                .toList();
        if (goodStanding.isEmpty()) {
            Console.info("[PASS] No reward-eligible users right now.");
            log.add("[PASS] No reward-eligible users right now.");
        } else {
            Console.info("[CHECK] Good standing users: " + goodStanding.size());
            for (Customer c : goodStanding) {
                double newLimit = c.getCreditLimit() * 1.10;
                c.setCreditLimit(newLimit);
                String msg = "Reward applied: 10% credit limit increase to $"
                        + String.format("%.2f", newLimit);
                evaluationService.queueReward("User", c.getId(), c.getFullName(), msg);
                Console.info("  => " + c.getFullName() + " rewarded");
                log.add(msg + " => " + c.getFullName());
            }
            customerService.save();
        }

        List<Supplier> inactive = supplierService.getAll().stream().filter(s -> !s.isActive()).toList();
        if (inactive.isEmpty()) {
            Console.info("[PASS] All suppliers are active.");
            log.add("[PASS] All suppliers are active.");
        } else {
            for (Supplier s : inactive) {
                String msg = "Inactive supplier requires review.";
                evaluationService.queueStatus("Supplier", s.getId(), s.getName(), msg);
                log.add(msg + " => " + s.getName());
            }
            Console.info("[CHECK] Inactive suppliers flagged: " + inactive.size());
        }

        List<EvaluationEvent> processed = evaluationService.processPending();
        Console.info("─────────────────────────────────────────");
        Console.info("Evaluation complete. Events processed: " + processed.size());
        log.add("Processed events: " + processed.size());
        FileStorage.saveQueryResult("Full System Evaluation", log);
        Console.info("[Saved summary to data/query_results.txt]");
        Console.pause();
    }

    private void lowStockReport() {
        Console.section("Low Stock Report");
        List<Product> results = productService.getLowStock();
        if (results.isEmpty()) {
            Console.info("All products are adequately stocked.");
        } else {
            Console.info("Products needing restock: " + results.size());
            results.forEach(p -> {
                System.out.println(p.getSummary());
                Console.info("  Recommended reorder quantity: " + (p.getReorderLevel() * 2));
            });
        }
        FileStorage.saveQueryResult("Low Stock Report", results.stream().map(Product::getSummary).toList());
        Console.info("[Saved to data/query_results.txt]");
        Console.pause();
    }

    private void creditViolationReport() {
        Console.section("Credit Limit Violations");
        List<Customer> results = customerService.getOverLimit();
        if (results.isEmpty()) {
            Console.info("No users are over their credit limit.");
        } else {
            Console.info("Users exceeding credit limit: " + results.size());
            for (Customer c : results) {
                System.out.println(c.getSummary());
                double excess = c.getCurrentBalance() - c.getCreditLimit();
                Console.info("  Exceeded by: $" + String.format("%.2f", excess));
            }
        }
        FileStorage.saveQueryResult("Credit Violations Report", results.stream().map(Customer::getSummary).toList());
        Console.info("[Saved to data/query_results.txt]");
        Console.pause();
    }

    private void goodStandingReport() {
        Console.section("Good Standing Users");
        List<Customer> results = customerService.getAll().stream()
                .filter(c -> c.getCurrentBalance() == 0 && c.getStatus() == Customer.Status.ACTIVE)
                .toList();
        if (results.isEmpty()) {
            Console.info("No users currently have a zero balance and active status.");
        } else {
            Console.info("Reward-eligible users: " + results.size());
            results.forEach(c -> {
                System.out.println(c.getSummary());
                Console.info("  Eligible action: 10% credit limit increase");
            });
        }
        FileStorage.saveQueryResult("Good Standing Users", results.stream().map(Customer::getSummary).toList());
        Console.info("[Saved to data/query_results.txt]");
        Console.pause();
    }

    private void inactiveSupplierReport() {
        Console.section("Inactive Supplier Report");
        List<Supplier> inactive = supplierService.getAll().stream()
                .filter(s -> !s.isActive()).toList();
        if (inactive.isEmpty()) {
            Console.info("All suppliers are currently active.");
        } else {
            Console.info("Inactive suppliers: " + inactive.size());
            inactive.forEach(s -> System.out.println(s.getSummary()));
        }
        FileStorage.saveQueryResult("Inactive Suppliers Report", inactive.stream().map(Supplier::getSummary).toList());
        Console.info("[Saved to data/query_results.txt]");
        Console.pause();
    }

    private void fullInventoryReport() {
        Console.section("Full Inventory Report");
        List<Product> all = productService.getAll();
        double totalValue = 0;
        List<String> lines = new ArrayList<>();

        Console.info(String.format("  %-10s  %-25s  %6s  %8s  %10s",
                "SKU", "Name", "Stock", "Price", "Value"));
        Console.info("  " + "─".repeat(65));

        for (Product p : all) {
            double value = p.getTotalStock() * p.getSellingPrice();
            totalValue += value;
            String line = String.format("  %-10s  %-25s  %6d  $%7.2f  $%9.2f%s",
                    p.getSku(), p.getName(), p.getTotalStock(),
                    p.getSellingPrice(), value, p.isLowStock() ? " ⚠" : "");
            System.out.println(line);
            lines.add(line);
        }
        String total = String.format("  Total inventory value: $%.2f", totalValue);
        Console.info("  " + "─".repeat(65));
        Console.info(total);
        lines.add(total);
        FileStorage.saveQueryResult("Full Inventory Report", lines);
        Console.info("[Saved to data/query_results.txt]");
        Console.pause();
    }

    private void evaluationHistory() {
        Console.section("Evaluation Event History");
        List<EvaluationEvent> history = evaluationService.getRecent(25);
        if (history.isEmpty()) {
            Console.info("No evaluation events have been recorded yet.");
        } else {
            Console.info("Showing up to 25 most recent events:");
            history.forEach(e -> System.out.println(e.getSummary()));
        }
        FileStorage.saveQueryResult("Evaluation Event History", history.stream().map(EvaluationEvent::getSummary).toList());
        Console.info("[Saved to data/query_results.txt]");
        Console.pause();
    }

    private void polymorphismDemo() {
        Console.section("Polymorphism Demo — printDetails() on mixed entity types");
        Console.info("Calling printDetails() on different subtypes via BaseModel reference:");
        System.out.println();

        List<BaseModel> entities = new ArrayList<>();
        if (!productService.getAll().isEmpty()) entities.add(productService.getAll().get(0));
        if (!supplierService.getAll().isEmpty()) entities.add(supplierService.getAll().get(0));
        if (!customerService.getAll().isEmpty()) entities.add(customerService.getAll().get(0));
        if (!batchService.getAll().isEmpty()) entities.add(batchService.getAll().get(0));
        if (!evaluationService.getAll().isEmpty()) entities.add(evaluationService.getAll().get(0));

        for (BaseModel entity : entities) {
            Console.info("Type: " + entity.getClass().getSimpleName());
            entity.printDetails();
        }
        Console.info("This demonstrates runtime polymorphism using method overriding.");
        Console.pause();
    }
}
