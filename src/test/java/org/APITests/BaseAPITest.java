package org.APITests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class BaseAPITest {

    static {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    /**
     * Generic method to get paginated results
     *
     * @param resource API resource (posts, comments, albums, photos, todos)
     * @param limit    items per page
     * @return list of all items' IDs
     */
    public List<Integer> getAllIdsWithPagination(String resource, int limit) {
        int page = 1;
        List<Integer> allIds = new ArrayList<>();

        while (true) {
            Response response = given()
                    .queryParam("_page", page)
                    .queryParam("_limit", limit)
                    .when()
                    .get("/" + resource)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            List<Integer> ids = response.jsonPath().getList("id");

            if (ids.isEmpty()) {
                break;
            }

            allIds.addAll(ids);
            page++;
        }

        return allIds;
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
