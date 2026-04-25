package ims.ui;

import ims.model.Customer;
import ims.service.CustomerService;
import ims.storage.FileStorage;

import java.util.List;

/**
 * CustomerMenu - text-based interface for user/customer management.
 */
public class CustomerMenu {

    private final CustomerService service;

    public CustomerMenu(CustomerService service) {
        this.service = service;
    }

    public void show() {
        while (true) {
            Console.header("USER / CUSTOMER ACCOUNT MANAGEMENT");
            Console.info("1. List All Users");
            Console.info("2. Add User");
            Console.info("3. View User Details (by ID)");
            Console.info("4. Update User");
            Console.info("5. Delete User");
            Console.info("6. Search Users (name/email/phone/ID)");
            Console.info("7. Block / Unblock User");
            Console.info("8. Record Payment");
            Console.info("9. Users Over Credit Limit");
            Console.info("0. Back to Main Menu");

            int choice = Console.readChoice(0, 9);
            try {
                switch (choice) {
                    case 1 -> listAll();
                    case 2 -> add();
                    case 3 -> view();
                    case 4 -> update();
                    case 5 -> delete();
                    case 6 -> search();
                    case 7 -> toggleBlock();
                    case 8 -> recordPayment();
                    case 9 -> overLimit();
                    case 0 -> { return; }
                }
            } catch (Exception e) {
                Console.error(e.getMessage());
                Console.pause();
            }
        }
    }

    private void listAll() {
        Console.section("All Users (" + service.count() + " total)");
        List<Customer> list = service.getAll();
        if (list.isEmpty()) Console.info("No users found.");
        else list.forEach(c -> System.out.println(c.getSummary()));
        Console.pause();
    }

    private void add() {
        Console.section("Add User");
        String first  = Console.readString("  First Name     : ");
        String last   = Console.readString("  Last Name      : ");
        String email  = Console.readString("  Email          : ");
        String phone  = Console.readOptional("  Phone          : ");
        String addr   = Console.readOptional("  Address        : ");
        double limit  = Console.readDouble("  Credit Limit   : $");

        Customer c = service.add(first, last, email, phone, addr, limit);
        Console.success("User '" + c.getFullName() + "' added with ID " + c.getId());
        Console.pause();
    }

    private void view() {
        Console.section("View User");
        int id = Console.readInt("  User ID : ");
        service.getById(id).printDetails();
        Console.pause();
    }

    private void update() {
        Console.section("Update User");
        int id = Console.readInt("  User ID : ");
        Customer c = service.getById(id);
        Console.info("Current: " + c.getFullName() + " | " + c.getEmail());

        String first = Console.readString("  First Name     : ");
        String last  = Console.readString("  Last Name      : ");
        String email = Console.readString("  Email          : ");
        String phone = Console.readOptional("  Phone          : ");
        String addr  = Console.readOptional("  Address        : ");
        double limit = Console.readDouble("  Credit Limit   : $");

        service.update(id, first, last, email, phone, addr, limit);
        Console.success("User updated.");
        Console.pause();
    }

    private void delete() {
        Console.section("Delete User");
        int id = Console.readInt("  User ID : ");
        Customer c = service.getById(id);
        if (!Console.confirm("  Delete '" + c.getFullName() + "'?")) {
            Console.info("Cancelled.");
        } else {
            service.delete(id);
            Console.success("User deleted.");
        }
        Console.pause();
    }

    private void search() {
        Console.section("Search Users");
        String kw = Console.readString("  Keyword (name/email/phone/ID) : ");
        List<Customer> results = service.search(kw);
        Console.info("Found " + results.size() + " result(s):");
        if (results.isEmpty()) Console.info("No users matched '" + kw + "'.");
        else results.forEach(c -> System.out.println(c.getSummary()));

        List<String> lines = results.stream().map(Customer::getSummary).toList();
        FileStorage.saveQueryResult("User Search: " + kw, lines);
        Console.info("[Results saved to data/query_results.txt]");
        Console.pause();
    }

    private void toggleBlock() {
        Console.section("Block / Unblock User");
        int id = Console.readInt("  User ID : ");
        Customer c = service.getById(id);
        Console.info("Current status: " + c.getStatus());

        if (c.getStatus() == Customer.Status.BLOCKED) {
            if (Console.confirm("  Unblock '" + c.getFullName() + "'?")) {
                service.setStatus(id, Customer.Status.ACTIVE);
                Console.success("User unblocked.");
            }
        } else {
            if (Console.confirm("  Block '" + c.getFullName() + "'?")) {
                service.setStatus(id, Customer.Status.BLOCKED);
                Console.success("User blocked.");
            }
        }
        Console.pause();
    }

    private void recordPayment() {
        Console.section("Record Payment");
        int id = Console.readInt("  User ID : ");
        Customer c = service.getById(id);
        Console.info("Current balance: $" + String.format("%.2f", c.getCurrentBalance()));
        double amount = Console.readDouble("  Payment Amount : $");
        service.recordPayment(id, amount);
        Console.success("Payment of $" + String.format("%.2f", amount)
                + " recorded for " + c.getFullName() + ".");
        Console.pause();
    }

    private void overLimit() {
        Console.section("Users Over Credit Limit");
        List<Customer> results = service.getOverLimit();
        Console.info("Users exceeding credit limit: " + results.size());
        if (results.isEmpty()) Console.info("No users are over their credit limit.");
        else results.forEach(c -> System.out.println(c.getSummary()));

        List<String> lines = results.stream().map(Customer::getSummary).toList();
        FileStorage.saveQueryResult("Users Over Credit Limit", lines);
        Console.info("[Results saved to data/query_results.txt]");
        Console.pause();
    }
}
