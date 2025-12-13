package org.APITests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.POJO.Product;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.utils.JsonSchemaValidatorUtil.validateJsonAgainstSchema;

public class ComplexJsonTesting extends ComplexJsonValidation {
    @Test
    public void complexJsonValidateSchemaSuccessTest(){


        Response response = given()
                .when()
                .get("products/" + 1)
                .then()
                .statusCode(200)
                .extract()
                .response();

        String jsonString = response.asString();
        Assert.assertTrue(validateJsonAgainstSchema("productSchema.json", jsonString));

    }

    @Test
    public void complexJsonValidateSchemaFailureTest(){
        Response response = given()
                .when()
                .get("products/missingParameters")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String jsonString = response.asString();
        Assert.assertFalse(validateJsonAgainstSchema("productSchema.json", jsonString));

    }

    @Test
    public void complexJsonValidateNoReviewsTest(){
        Response response = given()
                .when()
                .get("products/missingReviews")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String jsonString = response.asString();
        Assert.assertTrue(validateJsonAgainstSchema("productSchema.json", jsonString));

    }
}
