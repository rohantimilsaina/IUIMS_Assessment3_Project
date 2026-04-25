package ims.service;

import ims.exception.DuplicateException;
import ims.exception.NotFoundException;
import ims.model.Category;
import ims.storage.FileStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CategoryService - manages all Category operations.
 * Uses ArrayList as the in-memory data structure.
 * Persists to "categories.txt" via FileStorage.
 */
public class CategoryService {

    private static final String FILE = "categories.txt";
    private final ArrayList<Category> categories = new ArrayList<>();
    private int nextId = 1;

    public CategoryService() { load(); }

    // CRUD 

    public Category add(String name, String description) {
        if (findByName(name).isPresent())
            throw new DuplicateException("Category '" + name + "' already exists.");
        Category c = new Category(nextId++, name, description);
        categories.add(c);
        save();
        return c;
    }

    public Category getById(int id) {
        return categories.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Category", id));
    }

    public Optional<Category> findByName(String name) {
        return categories.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public List<Category> getAll() { return new ArrayList<>(categories); }

    public Category update(int id, String newName, String newDescription) {
        Category c = getById(id);
        // Naming uniqueness check (allow keeping same name)
        if (!c.getName().equalsIgnoreCase(newName) && findByName(newName).isPresent())
            throw new DuplicateException("Category '" + newName + "' already exists.");
        c.setName(newName);
        c.setDescription(newDescription);
        save();
        return c;
    }

    public void delete(int id) {
        Category c = getById(id);
        categories.remove(c);
        save();
    }

    public List<Category> search(String keyword) {
        String kw = keyword.toLowerCase();
        return categories.stream()
                .filter(c -> c.getName().toLowerCase().contains(kw)
                        || c.getDescription().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    public boolean exists(int id) {
        return categories.stream().anyMatch(c -> c.getId() == id);
    }

    public int count() { return categories.size(); }

    // File I/O 

    public void save() {
        List<String> lines = categories.stream()
                .map(Category::toFileLine)
                .collect(Collectors.toList());
        FileStorage.writeFile(FILE, lines);
    }

    public void load() {
        categories.clear();
        nextId = 1;
        for (String line : FileStorage.readFile(FILE)) {
            try {
                Category c = Category.fromFileLine(line);
                categories.add(c);
                if (c.getId() >= nextId) nextId = c.getId() + 1;
            } catch (Exception e) {
                System.err.println("[CategoryService] Skipping bad line: " + e.getMessage());
            }
        }
    }
}
