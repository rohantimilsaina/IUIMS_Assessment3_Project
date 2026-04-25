package ims.service;

import ims.exception.DuplicateException;
import ims.exception.NotFoundException;
import ims.model.Customer;
import ims.storage.FileStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * CustomerService - manages all user/customer account operations.
 * Uses ArrayList as the in-memory data structure.
 * Persists to "users.txt" via FileStorage (with legacy support for customers.txt).
 */
public class CustomerService {

    private static final String FILE = "users.txt";
    private static final String LEGACY_FILE = "customers.txt";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+() -]{0,20}$");

    private final ArrayList<Customer> customers = new ArrayList<>();
    private int nextId = 1;

    public CustomerService() { load(); }

    public Customer add(String firstName, String lastName, String email,
                        String phone, String address,
                        double creditLimit) {
        validateNames(firstName, lastName);
        validateEmail(email);
        validatePhone(phone);
        if (findByEmail(email).isPresent()) {
            throw new DuplicateException("User with email '" + email + "' already exists.");
        }
        Customer c = new Customer(nextId++, firstName.trim(), lastName.trim(),
                email.trim(), phone.trim(), address.trim(), creditLimit, 0.0, Customer.Status.ACTIVE);
        customers.add(c);
        save();
        return c;
    }

    public Customer getById(int id) {
        return customers.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User", id));
    }

    public Optional<Customer> findByEmail(String email) {
        return customers.stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }

    public List<Customer> getAll() { return new ArrayList<>(customers); }

    public List<Customer> getByStatus(Customer.Status status) {
        return customers.stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Customer> search(String keyword) {
        String kw = keyword.toLowerCase().trim();
        return customers.stream()
                .filter(c -> c.getFullName().toLowerCase().contains(kw)
                        || c.getEmail().toLowerCase().contains(kw)
                        || c.getPhone().toLowerCase().contains(kw)
                        || String.valueOf(c.getId()).equals(kw))
                .collect(Collectors.toList());
    }

    public Customer update(int id, String firstName, String lastName,
                           String email, String phone, String address, double creditLimit) {
        validateNames(firstName, lastName);
        validateEmail(email);
        validatePhone(phone);
        Customer c = getById(id);
        if (!c.getEmail().equalsIgnoreCase(email) && findByEmail(email).isPresent()) {
            throw new DuplicateException("Email '" + email + "' is already used by another user.");
        }
        c.setFirstName(firstName.trim());
        c.setLastName(lastName.trim());
        c.setEmail(email.trim());
        c.setPhone(phone.trim());
        c.setAddress(address.trim());
        c.setCreditLimit(creditLimit);
        save();
        return c;
    }

    public void setStatus(int id, Customer.Status status) {
        Customer c = getById(id);
        c.setStatus(status);
        save();
    }

    public void delete(int id) {
        Customer c = getById(id);
        customers.remove(c);
        save();
    }

    public void addToBalance(int id, double amount) {
        Customer c = getById(id);
        c.setCurrentBalance(c.getCurrentBalance() + amount);
        save();
    }

    public void recordPayment(int id, double amount) {
        Customer c = getById(id);
        double newBalance = Math.max(0, c.getCurrentBalance() - amount);
        c.setCurrentBalance(newBalance);
        save();
    }

    public List<Customer> getOverLimit() {
        return customers.stream()
                .filter(c -> c.getCurrentBalance() > c.getCreditLimit())
                .collect(Collectors.toList());
    }

    public int count() { return customers.size(); }

    public void save() {
        List<String> lines = customers.stream()
                .map(Customer::toFileLine)
                .collect(Collectors.toList());
        FileStorage.writeFile(FILE, lines);
    }

    public void load() {
        customers.clear();
        nextId = 1;
        List<String> lines = FileStorage.readFile(FILE);
        if (lines.isEmpty()) {
            lines = FileStorage.readFile(LEGACY_FILE);
        }
        for (String line : lines) {
            try {
                Customer c = Customer.fromFileLine(line);
                customers.add(c);
                if (c.getId() >= nextId) nextId = c.getId() + 1;
            } catch (Exception e) {
                System.err.println("[CustomerService] Skipping bad line: " + e.getMessage());
            }
        }
        save();
    }

    private void validateNames(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name and last name are required.");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
    }

    private void validatePhone(String phone) {
        if (phone != null && !phone.isBlank() && !PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new IllegalArgumentException("Please enter a valid phone number.");
        }
    }
}
