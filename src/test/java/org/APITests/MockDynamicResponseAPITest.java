package org.APITests;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.utils.JsonSchemaValidatorUtil;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class MockDynamicResponseAPITest {

    @BeforeClass
    public void requestResponseBuild() {

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder()
                .setBaseUri("https://b9511f4a-46c0-4709-8531-53de5a5f4ee2.mock.pstmn.io")
                //.addHeader("x-mock-match-request-body", "true") -- for variable lengths, no body-matching, only schema matching that happens in java
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
    public void validatePostRequestVariableJson() throws JsonProcessingException {
// Assuming schema file "addresses-schema.json" in src/test/resources
        String schemaFile = "addresses-schema.json";
        String sampleJson = """
                            {
                              "name": "John Doe",
                              "addresses": [
                                {"street": "123 Main St", "city": "Anytown"},
                                {"street": "456 Oak Ave", "city": "Othertown"}
                              ]
                            }
                            """;

        boolean isValid = JsonSchemaValidatorUtil.validateJsonAgainstSchema(schemaFile, sampleJson);
        Assert.assertTrue(isValid);


        given()
                .body(sampleJson)
        .when()
                .post("/dynamicResponseJson")
        .then()
                .log().all()
                .assertThat()
                .body("msg", equalTo("Success"));
    }


}
