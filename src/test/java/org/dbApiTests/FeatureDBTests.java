package org.dbApiTests;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.dbpojo.Feature;
import org.dbpojo.Product;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.sql.*;

public class FeatureDBTests {
    Statement stmt;
    Connection conn;

    @BeforeMethod
    public void initialize() throws SQLException {
        conn = DriverManager.getConnection(    "jdbc:mysql://localhost:3306/testdb?useSSL=false&serverTimezone=UTC",
                "root",
                "Password123");
        stmt = conn.createStatement();
    }


    @Test
    public void verifyAddProductAndFeatureTest(){
        Product product = new Product();
        product.setName("Feature Test1");
        product.setPrice(5.0);

        Product createdProduct = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products/add")
                .then()
                .statusCode(200)
                .extract()
                .as(Product.class);

        Long productId = createdProduct.getId();

        Feature feature = new Feature();
        feature.setName("API Test1");
        feature.setDescription("API Test feature");

        Feature createdFeature = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(feature)
                .when()
                .post("/products/{id}/features", productId)
                .then()
                .statusCode(200)
                .extract()
                .as(Feature.class);

        assertEquals(feature.getName(), createdFeature.getName());
        assertEquals(feature.getDescription(), createdFeature.getDescription());
        assertNotNull(createdFeature.getId());

    }
}
