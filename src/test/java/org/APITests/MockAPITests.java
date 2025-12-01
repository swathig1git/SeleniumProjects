package org.APITests;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

public class MockAPITests {

    @Test
    public static void get200Test() {

        // Base URI of the API
        RestAssured.baseURI = "https://b9511f4a-46c0-4709-8531-53de5a5f4ee2.mock.pstmn.io";

        // Perform GET request
        Response response = RestAssured
                .given()
                    .header("x-mock-response-code", "200")
                .when()
                    .get("/get");

        // Print response body
        System.out.println("Response Body:");
        response.prettyPrint();

        // Print status code
        System.out.println("Status Code: " + response.getStatusCode());
    }

    @Test
    public static void get503Test() {

        // Base URI of the API
        RestAssured.baseURI = "https://b9511f4a-46c0-4709-8531-53de5a5f4ee2.mock.pstmn.io";

        // Perform GET request
        Response response = RestAssured
                .given()
                .header("x-mock-response-code", "503")
                .when()
                .get("/get");

        // Print response body
        System.out.println("Response Body:");
        response.prettyPrint();

        // Print status code
        System.out.println("Status Code: " + response.getStatusCode());
    }




}
