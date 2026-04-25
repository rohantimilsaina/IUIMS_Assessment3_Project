import ims.service.*;
import ims.ui.MainMenu;

/**
 * Main - entry point for the Inventory & User Management System.
 */
public class Main {

    public static void main(String[] args) {
        CategoryService categoryService = new CategoryService();
        SupplierService supplierService = new SupplierService();
        ProductService  productService  = new ProductService();
        BatchService    batchService    = new BatchService(productService);
        CustomerService customerService = new CustomerService();
        EvaluationService evaluationService = new EvaluationService();

        MainMenu menu = new MainMenu(
                categoryService,
                supplierService,
                productService,
                batchService,
                customerService,
                evaluationService
        );

        menu.run();
    }
}
