package org.APITests;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class PaginationTesting {

    public static void main(String[] args) {

        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

        int page = 1;
        int limit = 25;
        List<Integer> allIds = new ArrayList<>();

        while (true) {
            Response response = given()
                    .queryParam("_page", page)
                    .queryParam("_limit", limit)
                    .when()
                    .get("/posts")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            List<Integer> ids = response.jsonPath().getList("id");
            List<Integer> userIds = response.jsonPath().getList("userId");

            if (ids.isEmpty()) {
                break;  // No more pages
            }

            allIds.addAll(ids);
            System.out.println("Page: " + page);
            System.out.println("ids" + ids);
            System.out.println("userIds" + userIds);
            System.out.println("--");

            page++; // Next page
        }

        System.out.println("Total IDs collected: " + allIds.size());
        System.out.println(allIds);
    }
}
