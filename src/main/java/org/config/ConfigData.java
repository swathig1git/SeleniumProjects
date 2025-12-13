package org.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.POJO.ProductType;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigData {

    private static List<ProductType> productTypes;   // Holds the list from JSON
    private static boolean isLoaded = false;         // Prevents re-loading

    /**
     * Load product-types.json file only once.
     */
    public static void loadConfig() throws IOException {
        if (isLoaded) return; // Avoid rereading

        ObjectMapper mapper = new ObjectMapper();

        // Update the path if your JSON is inside resources/
        File file = new File("src/test/resources/product-types.json");

        productTypes = mapper.readValue(file, new TypeReference<List<ProductType>>() {});
        isLoaded = true;
    }

    /**
     * Returns all product types.
     */
    public static List<ProductType> getProductTypes() {
        return productTypes;
    }

    /**
     * Fetch a product type by name
     */
    public static ProductType getProductByName(String name) {
        return productTypes.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
