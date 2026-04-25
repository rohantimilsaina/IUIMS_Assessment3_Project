package ims.service;

import ims.exception.DuplicateException;
import ims.exception.NotFoundException;
import ims.model.Supplier;
import ims.storage.FileStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * SupplierService - manages all Supplier operations.
 * Uses ArrayList as the in-memory data structure.
 * Persists to "suppliers.txt" via FileStorage.
 */
public class SupplierService {

    private static final String FILE = "suppliers.txt";
    private final ArrayList<Supplier> suppliers = new ArrayList<>();
    private int nextId = 1;

    public SupplierService() { load(); }

    // CRUD 

    public Supplier add(String name, String contactPerson,
                        String email, String phone) {
        if (findByName(name).isPresent())
            throw new DuplicateException("Supplier '" + name + "' already exists.");
        Supplier s = new Supplier(nextId++, name, contactPerson, email, phone, true);
        suppliers.add(s);
        save();
        return s;
    }

    public Supplier getById(int id) {
        return suppliers.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Supplier", id));
    }

    public Optional<Supplier> findByName(String name) {
        return suppliers.stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public List<Supplier> getAll() { return new ArrayList<>(suppliers); }

    public List<Supplier> getActive() {
        return suppliers.stream().filter(Supplier::isActive).collect(Collectors.toList());
    }

    public Supplier update(int id, String name, String contactPerson,
                           String email, String phone) {
        Supplier s = getById(id);
        if (!s.getName().equalsIgnoreCase(name) && findByName(name).isPresent())
            throw new DuplicateException("Supplier '" + name + "' already exists.");
        s.setName(name);
        s.setContactPerson(contactPerson);
        s.setEmail(email);
        s.setPhone(phone);
        save();
        return s;
    }

    public void setActive(int id, boolean active) {
        Supplier s = getById(id);
        s.setActive(active);
        save();
    }

    public void delete(int id) {
        Supplier s = getById(id);
        suppliers.remove(s);
        save();
    }

    public List<Supplier> search(String keyword) {
        String kw = keyword.toLowerCase();
        return suppliers.stream()
                .filter(s -> s.getName().toLowerCase().contains(kw)
                        || s.getContactPerson().toLowerCase().contains(kw)
                        || s.getEmail().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    public boolean exists(int id) {
        return suppliers.stream().anyMatch(s -> s.getId() == id);
    }

    public int count() { return suppliers.size(); }

    // File I/O 

    public void save() {
        List<String> lines = suppliers.stream()
                .map(Supplier::toFileLine)
                .collect(Collectors.toList());
        FileStorage.writeFile(FILE, lines);
    }

    public void load() {
        suppliers.clear();
        nextId = 1;
        for (String line : FileStorage.readFile(FILE)) {
            try {
                Supplier s = Supplier.fromFileLine(line);
                suppliers.add(s);
                if (s.getId() >= nextId) nextId = s.getId() + 1;
            } catch (Exception e) {
                System.err.println("[SupplierService] Skipping bad line: " + e.getMessage());
            }
        }
    }
}
