package org.POJO;
import java.util.List;

public class ProductBrand {

    private String name;
    private List<String> brands;

    // Default constructor
    public ProductBrand() {
    }

    // Parameterized constructor
    public ProductBrand(String name, List<String> brands) {
        this.name = name;
        this.brands = brands;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBrands() {
        return brands;
    }

    public void setBrands(List<String> brands) {
        this.brands = brands;
    }

    @Override
    public String toString() {
        return "ProductCategory{" +
                "name='" + name + '\'' +
                ", brands=" + brands +
                '}';
    }
}
