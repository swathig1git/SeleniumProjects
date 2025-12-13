package org.APITests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.POJO.Product;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.utils.JsonSchemaValidatorUtil.validateJsonAgainstSchema;

public class ComplexJsonValidation {

    static {
        RestAssured.baseURI = "https://b9511f4a-46c0-4709-8531-53de5a5f4ee2.mock.pstmn.io";
    }

    public Product getProduct(int productId) {
        List<Integer> allIds = new ArrayList<>();

            Response response = given()
                    .when()
                    .get("products/" + productId)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            return response.as(Product.class);

    }


    /**
     * Verify no duplicate IDs in list
     */
    public void verifyNoDuplicates(List<Integer> ids) {
        long uniqueCount = ids.stream().distinct().count();
        Assert.assertEquals(uniqueCount, ids.size(), "Duplicate IDs found!");
    }

    /**
     * Verify page size for first page
     */
    public void verifyPageSize(String resource, int limit) {
        Response response = given()
                .queryParam("_page", 1)
                .queryParam("_limit", limit)
                .when()
                .get("/" + resource)
                .then()
                .statusCode(200)
                .extract()
                .response();

        List<Integer> ids = response.jsonPath().getList("id");
        Assert.assertTrue(ids.size() <= limit, "Page size exceeded limit");
    }
}
