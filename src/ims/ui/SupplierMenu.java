package ims.ui;

import ims.model.Supplier;
import ims.service.SupplierService;
import ims.storage.FileStorage;

import java.util.List;

/**
 * SupplierMenu - text-based interface for Supplier management.
 */
public class SupplierMenu {

    private final SupplierService service;

    public SupplierMenu(SupplierService service) {
        this.service = service;
    }

    public void show() {
        while (true) {
            Console.header("SUPPLIER MANAGEMENT");
            Console.info("1. List All Suppliers");
            Console.info("2. Add Supplier");
            Console.info("3. View Supplier Details");
            Console.info("4. Update Supplier");
            Console.info("5. Activate / Deactivate Supplier");
            Console.info("6. Delete Supplier");
            Console.info("7. Search Suppliers");
            Console.info("0. Back to Main Menu");

            int choice = Console.readChoice(0, 7);
            try {
                switch (choice) {
                    case 1 -> listAll();
                    case 2 -> add();
                    case 3 -> view();
                    case 4 -> update();
                    case 5 -> toggleActive();
                    case 6 -> delete();
                    case 7 -> search();
                    case 0 -> { return; }
                }
            } catch (Exception e) {
                Console.error(e.getMessage());
                Console.pause();
            }
        }
    }

    private void listAll() {
        Console.section("All Suppliers (" + service.count() + " total)");
        List<Supplier> list = service.getAll();
        if (list.isEmpty()) Console.info("No suppliers found.");
        else list.forEach(s -> System.out.println(s.getSummary()));
        Console.pause();
    }

    private void add() {
        Console.section("Add Supplier");
        String name    = Console.readString("  Company Name    : ");
        String contact = Console.readOptional("  Contact Person  : ");
        String email   = Console.readOptional("  Email           : ");
        String phone   = Console.readOptional("  Phone           : ");
        Supplier s = service.add(name, contact, email, phone);
        Console.success("Supplier '" + s.getName() + "' added with ID " + s.getId());
        Console.pause();
    }

    private void view() {
        Console.section("View Supplier");
        int id = Console.readInt("  Supplier ID : ");
        service.getById(id).printDetails();
        Console.pause();
    }

    private void update() {
        Console.section("Update Supplier");
        int id = Console.readInt("  Supplier ID : ");
        Supplier s = service.getById(id);
        Console.info("Current: " + s.getName() + " | " + s.getContactPerson() + " | " + s.getEmail());
        String name    = Console.readString("  New Company Name   : ");
        String contact = Console.readOptional("  New Contact Person : ");
        String email   = Console.readOptional("  New Email          : ");
        String phone   = Console.readOptional("  New Phone          : ");
        service.update(id, name, contact, email, phone);
        Console.success("Supplier updated.");
        Console.pause();
    }

    private void toggleActive() {
        Console.section("Activate / Deactivate Supplier");
        int id = Console.readInt("  Supplier ID : ");
        Supplier s = service.getById(id);
        boolean newState = !s.isActive();
        if (Console.confirm("  Set supplier '" + s.getName() + "' to " + (newState ? "ACTIVE" : "INACTIVE") + "?")) {
            service.setActive(id, newState);
            Console.success("Supplier status updated.");
        } else {
            Console.info("Cancelled.");
        }
        Console.pause();
    }

    private void delete() {
        Console.section("Delete Supplier");
        int id = Console.readInt("  Supplier ID : ");
        Supplier s = service.getById(id);
        if (!Console.confirm("  Delete supplier '" + s.getName() + "'?")) {
            Console.info("Cancelled.");
        } else {
            service.delete(id);
            Console.success("Supplier deleted.");
        }
        Console.pause();
    }

    private void search() {
        Console.section("Search Suppliers");
        String kw = Console.readString("  Keyword : ");
        List<Supplier> results = service.search(kw);
        Console.info("Found " + results.size() + " result(s):");
        if (results.isEmpty()) Console.info("No suppliers matched '" + kw + "'.");
        else results.forEach(s -> System.out.println(s.getSummary()));
        List<String> lines = results.stream().map(Supplier::getSummary).toList();
        FileStorage.saveQueryResult("Supplier Search: " + kw, lines);
        Console.info("[Results saved to data/query_results.txt]");
        Console.pause();
    }
}
