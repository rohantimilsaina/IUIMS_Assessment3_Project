package ims.ui;

import ims.model.Category;
import ims.service.CategoryService;
import ims.storage.FileStorage;

import java.util.List;

/**
 * CategoryMenu - text-based interface for Category management.
 * Demonstrates POLYMORPHISM through consistent menu pattern used across all menus.
 */
public class CategoryMenu {

    private final CategoryService service;

    public CategoryMenu(CategoryService service) {
        this.service = service;
    }

    public void show() {
        while (true) {
            Console.header("CATEGORY MANAGEMENT");
            Console.info("1. List All Categories");
            Console.info("2. Add Category");
            Console.info("3. View Category Details");
            Console.info("4. Update Category");
            Console.info("5. Delete Category");
            Console.info("6. Search Categories");
            Console.info("0. Back to Main Menu");

            int choice = Console.readChoice(0, 6);
            try {
                switch (choice) {
                    case 1 -> listAll();
                    case 2 -> add();
                    case 3 -> view();
                    case 4 -> update();
                    case 5 -> delete();
                    case 6 -> search();
                    case 0 -> { return; }
                }
            } catch (Exception e) {
                Console.error(e.getMessage());
                Console.pause();
            }
        }
    }

    private void listAll() {
        Console.section("All Categories (" + service.count() + " total)");
        List<Category> list = service.getAll();
        if (list.isEmpty()) { Console.info("No categories found."); }
        else list.forEach(c -> System.out.println(c.getSummary()));
        Console.pause();
    }

    private void add() {
        Console.section("Add Category");
        String name = Console.readString("  Name        : ");
        String desc = Console.readOptional("  Description : ");
        Category c = service.add(name, desc);
        Console.success("Category '" + c.getName() + "' created with ID " + c.getId());
        Console.pause();
    }

    private void view() {
        Console.section("View Category");
        int id = Console.readInt("  Category ID : ");
        Category c = service.getById(id);
        c.printDetails();
        Console.pause();
    }

    private void update() {
        Console.section("Update Category");
        int id = Console.readInt("  Category ID : ");
        Category c = service.getById(id);
        Console.info("Current name: " + c.getName() + " | Current desc: " + c.getDescription());
        String name = Console.readString("  New Name        : ");
        String desc = Console.readOptional("  New Description : ");
        service.update(id, name, desc);
        Console.success("Category updated successfully.");
        Console.pause();
    }

    private void delete() {
        Console.section("Delete Category");
        int id = Console.readInt("  Category ID : ");
        Category c = service.getById(id);
        if (!Console.confirm("  Delete '" + c.getName() + "'?")) {
            Console.info("Cancelled.");
        } else {
            service.delete(id);
            Console.success("Category deleted.");
        }
        Console.pause();
    }

    private void search() {
        Console.section("Search Categories");
        String kw = Console.readString("  Keyword : ");
        List<Category> results = service.search(kw);
        Console.info("Found " + results.size() + " result(s):");
        if (results.isEmpty()) {
            Console.info("No categories matched '" + kw + "'.");
        } else {
            results.forEach(c -> System.out.println(c.getSummary()));
        }
        // Save query results to file
        List<String> lines = results.stream().map(Category::getSummary).toList();
        FileStorage.saveQueryResult("Category Search: " + kw, lines);
        Console.info("[Results saved to data/query_results.txt]");
        Console.pause();
    }
}
