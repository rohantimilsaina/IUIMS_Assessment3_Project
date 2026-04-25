package ims.ui;

import ims.service.*;

/**
 * MainMenu - the top-level menu of the Inventory & User Management System.
 */
public class MainMenu {

    private final CategoryService  categoryService;
    private final SupplierService  supplierService;
    private final ProductService   productService;
    private final BatchService     batchService;
    private final CustomerService  customerService;
    private final EvaluationService evaluationService;

    private final CategoryMenu categoryMenu;
    private final SupplierMenu supplierMenu;
    private final ProductMenu  productMenu;
    private final BatchMenu    batchMenu;
    private final CustomerMenu customerMenu;
    private final ReportsMenu  reportsMenu;

    public MainMenu(CategoryService categoryService,
                    SupplierService supplierService,
                    ProductService productService,
                    BatchService batchService,
                    CustomerService customerService,
                    EvaluationService evaluationService) {
        this.categoryService = categoryService;
        this.supplierService = supplierService;
        this.productService  = productService;
        this.batchService    = batchService;
        this.customerService = customerService;
        this.evaluationService = evaluationService;

        this.categoryMenu = new CategoryMenu(categoryService);
        this.supplierMenu = new SupplierMenu(supplierService);
        this.productMenu  = new ProductMenu(productService, categoryService);
        this.batchMenu    = new BatchMenu(batchService, productService, supplierService);
        this.customerMenu = new CustomerMenu(customerService);
        this.reportsMenu  = new ReportsMenu(productService, customerService,
                                             supplierService, batchService, evaluationService);
    }

    public void run() {
        printWelcome();
        while (true) {
            printDashboard();
            Console.header("MAIN MENU");
            Console.info("1. Categories");
            Console.info("2. Suppliers");
            Console.info("3. Products");
            Console.info("4. Stock Batches");
            Console.info("5. Users / Customer Accounts");
            Console.info("6. Reports & Evaluation");
            Console.info("0. Exit");

            int choice = Console.readChoice(0, 6);
            switch (choice) {
                case 1 -> categoryMenu.show();
                case 2 -> supplierMenu.show();
                case 3 -> productMenu.show();
                case 4 -> batchMenu.show();
                case 5 -> customerMenu.show();
                case 6 -> reportsMenu.show();
                case 0 -> {
                    Console.header("Goodbye!");
                    Console.info("All data has been saved to the 'data/' folder.");
                    System.out.println();
                    return;
                }
            }
        }
    }

    private void printDashboard() {
        int lowStock  = productService.getLowStock().size();
        int overLimit = customerService.getOverLimit().size();
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║              SYSTEM DASHBOARD                    ║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.printf("  ║  Categories : %-5d    Suppliers  : %-12d║%n",
                categoryService.count(), supplierService.count());
        System.out.printf("  ║  Products   : %-5d    Batches    : %-12d║%n",
                productService.count(), batchService.count());
        System.out.printf("  ║  Users      : %-5d    Low Stock  : %-12d║%n",
                customerService.count(), lowStock);
        System.out.printf("  ║  Evaluations: %-5d    Pending Q  : %-12d║%n",
                evaluationService.count(), evaluationService.pendingCount());
        if (lowStock > 0 || overLimit > 0) {
            System.out.println("  ╠══════════════════════════════════════════════════╣");
            if (lowStock > 0)
                System.out.printf("  ║  ⚠ WARNING: %d product(s) are low on stock!%-7s║%n", lowStock, "");
            if (overLimit > 0)
                System.out.printf("  ║  ⚠ WARNING: %d user(s) over credit limit!%-9s║%n", overLimit, "");
        }
        System.out.println("  ╚══════════════════════════════════════════════════╝");
    }

    private void printWelcome() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║                                                  ║");
        System.out.println("  ║   INVENTORY & USER MANAGEMENT SYSTEM  v2.0       ║");
        System.out.println("  ║                                                  ║");
        System.out.println("  ║  Manage Products, Stock, Suppliers, Users       ║");
        System.out.println("  ║                                                  ║");
        System.out.println("  ╚══════════════════════════════════════════════════╝");
        System.out.println();
        Console.info("Data directory: data/");
        Console.info("Query results saved to: data/query_results.txt");
        Console.info("Evaluation transactions saved to: data/evaluation_events.txt");
        System.out.println();
    }
}
