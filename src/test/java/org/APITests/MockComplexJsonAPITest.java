package org.APITests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class MockComplexJsonAPITest {

    @BeforeClass
    public void requestResponseBuild() {

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder()
                .setBaseUri("https://b9511f4a-46c0-4709-8531-53de5a5f4ee2.mock.pstmn.io")
                .addHeader("x-mock-match-request-body", "true")
                .addHeader("x-mock-response-code", "200")
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
    public void validatePostRequestComplexJson() throws JsonProcessingException {
    List<Integer> idArrayList = new LinkedList<>();
    idArrayList.add(5);
    idArrayList.add(6);

    LinkedHashMap<String, Object> batterHashmap1 = new LinkedHashMap<>();
    batterHashmap1.put("id","1001");
    batterHashmap1.put("type", "regular");

    LinkedHashMap<String, Object> batterHashmap2 = new LinkedHashMap<>();
    batterHashmap2.put("id", idArrayList);
    batterHashmap2.put("type", "Chocolate");

    LinkedList<LinkedHashMap<String , Object>>  batterList = new LinkedList<>();
    batterList.add(batterHashmap1);
    batterList.add(batterHashmap2);

    HashMap <String, Object> batterMap = new HashMap<>();
    batterMap.put("batter", batterList);

    //----------------------------------------------------------------
    List<String> typeList = new LinkedList<>();
    typeList.add("test1");
    typeList.add("test2");

    LinkedHashMap<String, Object> toppingHashMap1 = new LinkedHashMap<>();
    toppingHashMap1.put("id", "5001");
    toppingHashMap1.put("type", "none");

    LinkedHashMap<String, Object> toppingHashMap2 = new LinkedHashMap<>();
    toppingHashMap2.put("id","5002");
    toppingHashMap2.put("type",typeList);

    List<LinkedHashMap<String, Object>> toppingList = new LinkedList<>();
    toppingList.add(toppingHashMap1);
    toppingList.add(toppingHashMap2);


    //-----------------------------------------------------------------
    LinkedHashMap<String, Object> finalJson = new LinkedHashMap<>();
    finalJson.put("id","0002");
    finalJson.put("type","donut");
    finalJson.put("name","cake");
    finalJson.put("ppu", 0.55);
    finalJson.put("batters", batterMap);
    finalJson.put("topping", toppingList);

        System.out.println(new ObjectMapper().writeValueAsString(finalJson));



        given()
                .body(finalJson)
        .when()
                .post("/postComplexJson")
        .then()
                .log().all()
                .assertThat()
                .body("msg", equalTo("Success"));
    }


}
