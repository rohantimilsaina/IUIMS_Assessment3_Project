package ims.model;

/**
 * Supplier - a vendor who provides product batches.
 * Extends BaseModel demonstrating INHERITANCE.
 */
public class Supplier extends BaseModel {

    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private boolean active;

    public Supplier(int id, String name, String contactPerson,
                    String email, String phone, boolean active) {
        super(id);
        this.name          = name;
        this.contactPerson = contactPerson;
        this.email         = email;
        this.phone         = phone;
        this.active        = active;
    }

    // Getters & Setters 
    public String getName()          { return name; }
    public String getContactPerson() { return contactPerson; }
    public String getEmail()         { return email; }
    public String getPhone()         { return phone; }
    public boolean isActive()        { return active; }

    public void setName(String name)                   { this.name = name; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public void setEmail(String email)                 { this.email = email; }
    public void setPhone(String phone)                 { this.phone = phone; }
    public void setActive(boolean active)              { this.active = active; }

    //  Serialization 
    /**
     * Format: id|name|contactPerson|email|phone|active
     */
    @Override
    public String toFileLine() {
        return getId() + "|" + name + "|" + contactPerson + "|"
                + email + "|" + phone + "|" + active;
    }

    public static Supplier fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        return new Supplier(
                Integer.parseInt(p[0].trim()),
                p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim(),
                Boolean.parseBoolean(p[5].trim())
        );
    }

    // Display 
    @Override
    public void printDetails() {
        System.out.println("  ┌─────────────────────────────────────────┐");
        System.out.printf("  │  ID             : %-23d│%n", getId());
        System.out.printf("  │  Name           : %-23s│%n", name);
        System.out.printf("  │  Contact Person : %-23s│%n", contactPerson);
        System.out.printf("  │  Email          : %-23s│%n", email);
        System.out.printf("  │  Phone          : %-23s│%n", phone);
        System.out.printf("  │  Status         : %-23s│%n", active ? "ACTIVE" : "INACTIVE");
        System.out.println("  └─────────────────────────────────────────┘");
    }

    @Override
    public String getSummary() {
        return String.format("  [%3d]  %-22s  %-20s  %-15s  %s",
                getId(), name, contactPerson, phone, active ? "ACTIVE" : "INACTIVE");
    }

    @Override
    public String toString() { return name; }
}
