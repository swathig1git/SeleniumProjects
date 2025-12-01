package org.APITests;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class MockJsonAPITest {

    @BeforeClass
    public void requestResponseBuild() {

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder()
                .setBaseUri("https://b9511f4a-46c0-4709-8531-53de5a5f4ee2.mock.pstmn.io")
                .addHeader("x-mock-match-request-body", "true")
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL);

        RestAssured.requestSpecification = requestSpecBuilder.build();

        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL);

        RestAssured.responseSpecification = responseSpecBuilder.build();
    }

    @Test
    public void validatePostRequestJsonArrayAsList(){
        HashMap<String, String> obj5001 = new HashMap<>();
        obj5001.put("id", "5001");
        obj5001.put("type", "none");

        HashMap<String, String> obj5002 = new HashMap<>();
        obj5002.put("id", "5002");
        obj5002.put("type", "cake");

        List<HashMap<String, String>> jsonList = new ArrayList<HashMap<String, String>>();
        jsonList.add(obj5001);
        jsonList.add(obj5002);

        given()
                .body(jsonList)
        .when()
                .post("/post")
        .then()
                .log().all()
                .assertThat()
                .body("msg", equalTo("Success"));
    }


}
