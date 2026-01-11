package org.dbpojo;

public class Product {
    private Long id;

    private String name;
    private Double price;
    public Product() {}

    public Product(Long id, String name, Double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // getters & setters
    // Getters (needed for JSON serialization)
    public Long getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }

    // Optionally setters if you plan to update
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(Double price) { this.price = price; }
}