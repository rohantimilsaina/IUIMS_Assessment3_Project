package ims.model;

/**
 * Customer - a buyer who can purchase products on credit.
 * Extends BaseModel demonstrating INHERITANCE.
 * Tracks credit limit and outstanding balance.
 */
public class Customer extends BaseModel {

    public enum Status { ACTIVE, INACTIVE, BLOCKED }

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private double creditLimit;    // maximum allowed outstanding balance
    private double currentBalance; // current amount owed
    private Status status;

    public Customer(int id, String firstName, String lastName, String email,
                    String phone, String address,
                    double creditLimit, double currentBalance, Status status) {
        super(id);
        this.firstName      = firstName;
        this.lastName       = lastName;
        this.email          = email;
        this.phone          = phone;
        this.address        = address;
        this.creditLimit    = creditLimit;
        this.currentBalance = currentBalance;
        this.status         = status;
    }

    // Getters & Setters
    public String getFirstName()     { return firstName; }
    public String getLastName()      { return lastName; }
    public String getFullName()      { return firstName + " " + lastName; }
    public String getEmail()         { return email; }
    public String getPhone()         { return phone; }
    public String getAddress()       { return address; }
    public double getCreditLimit()   { return creditLimit; }
    public double getCurrentBalance(){ return currentBalance; }
    public Status getStatus()        { return status; }
    public double getAvailableCredit(){ return creditLimit - currentBalance; }

    public void setFirstName(String firstName)       { this.firstName = firstName; }
    public void setLastName(String lastName)         { this.lastName = lastName; }
    public void setEmail(String email)               { this.email = email; }
    public void setPhone(String phone)               { this.phone = phone; }
    public void setAddress(String address)           { this.address = address; }
    public void setCreditLimit(double creditLimit)   { this.creditLimit = creditLimit; }
    public void setCurrentBalance(double balance)    { this.currentBalance = balance; }
    public void setStatus(Status status)             { this.status = status; }

    public boolean canPurchase(double amount) {
        return status == Status.ACTIVE && (currentBalance + amount) <= creditLimit;
    }

    // Serialization 
    /**
     * Format: id|firstName|lastName|email|phone|address|creditLimit|currentBalance|status
     */
    @Override
    public String toFileLine() {
        return getId() + "|" + firstName + "|" + lastName + "|"
                + email + "|" + phone + "|" + address + "|"
                + String.format("%.2f", creditLimit) + "|"
                + String.format("%.2f", currentBalance) + "|"
                + status.name();
    }

    public static Customer fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        return new Customer(
                Integer.parseInt(p[0].trim()),
                p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim(), p[5].trim(),
                Double.parseDouble(p[6].trim()),
                Double.parseDouble(p[7].trim()),
                Status.valueOf(p[8].trim())
        );
    }

    // Display 
    @Override
    public void printDetails() {
        System.out.println("  ┌──────────────────────────────────────────────┐");
        System.out.printf("  │  ID              : %-26d│%n", getId());
        System.out.printf("  │  Name            : %-26s│%n", getFullName());
        System.out.printf("  │  Email           : %-26s│%n", email);
        System.out.printf("  │  Phone           : %-26s│%n", phone);
        System.out.printf("  │  Address         : %-26s│%n", shorten(address, 26));
        System.out.printf("  │  Credit Limit    : $%-25.2f│%n", creditLimit);
        System.out.printf("  │  Current Balance : $%-25.2f│%n", currentBalance);
        System.out.printf("  │  Available Credit: $%-25.2f│%n", getAvailableCredit());
        System.out.printf("  │  Status          : %-26s│%n", status.name());
        System.out.println("  └──────────────────────────────────────────────┘");
    }

    @Override
    public String getSummary() {
        return String.format("  [%3d]  %-24s  %-25s  Bal:$%-8.2f  %s",
                getId(), getFullName(), email, currentBalance, status.name());
    }

    private String shorten(String s, int max) {
        if (s == null || s.isEmpty()) return "-";
        return s.length() > max ? s.substring(0, max - 3) + "..." : s;
    }

    @Override
    public String toString() { return getFullName(); }
}
