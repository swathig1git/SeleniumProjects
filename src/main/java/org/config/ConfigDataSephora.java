package org.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.POJO.ProductTypeSephora;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ConfigDataSephora {
    private static List<ProductTypeSephora> productTypeSephora;
    private static boolean isLoaded = false;

    public static void loadConfigSephora() throws IOException {
        if (isLoaded) return;

        ObjectMapper mapper = new ObjectMapper();
        File file = new File("src/test/resources/productTypesSephora.json");
        productTypeSephora = mapper.readValue(file, new TypeReference<List<ProductTypeSephora>>(){});
    }

    public static List<ProductTypeSephora> getProductTypeSephora(){
        return productTypeSephora;
    }

    public static ProductTypeSephora getProductTypeSephoraByName(String name){
        return productTypeSephora.stream()
                .filter(p->p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
