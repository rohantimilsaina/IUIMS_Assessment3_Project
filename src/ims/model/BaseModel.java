package ims.model;

/**
 * Abstracting base class for all entities in the Inventory Management System.
 * Demonstrating ABSTRACTION and ENCAPSULATION.
 * Every entity has an ID and must know how to serialize/display itself.
 */
public abstract class BaseModel {

    private int id;

    public BaseModel(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    /** Serializeing entity to a single pipe-delimited line for file storage. */
    public abstract String toFileLine();

    /** Printing full details of this entity to the console. */
    public abstract void printDetails();

    /** One-line summary used in list views. */
    public abstract String getSummary();
}
