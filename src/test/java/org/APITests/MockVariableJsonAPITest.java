package org.APITests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.POJO.AddressDetails;
import org.POJO.PersonalDetails;
import org.openqa.selenium.devtools.v140.autofill.model.Address;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.utils.JsonSchemaValidatorUtil;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class MockVariableJsonAPITest {

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
    public void validatePOJOToJsonNoAddr() throws JsonProcessingException {
// Assuming schema file "addresses-schema.json" in src/test/resources
        PersonalDetails personalDetails = new PersonalDetails("name1", null);

        String schemaFile = "addresses-schema.json";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(personalDetails);


        boolean isValid = JsonSchemaValidatorUtil.validateJsonAgainstSchema(schemaFile, jsonString);
        Assert.assertTrue(isValid);


        given()
                .body(personalDetails)
                .when()
                .post("/postVariableJson")
                .then()
                .log().all()
                .assertThat()
                .body("msg", equalTo("Success"));
    }
    @Test
    public void validatePOJOToJsonNoStreet() throws JsonProcessingException {
// Assuming schema file "addresses-schema.json" in src/test/resources
        AddressDetails addr1 = new AddressDetails( "city1");
        AddressDetails addr2 = new AddressDetails( "city2");
        ArrayList<AddressDetails> allAddresses = new ArrayList<>();
        allAddresses.add(addr1);
        allAddresses.add(addr2);
        PersonalDetails personalDetails = new PersonalDetails("name1", allAddresses);

        String schemaFile = "addresses-schema.json";
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        String jsonString = mapper.writeValueAsString(personalDetails);


        boolean isValid = JsonSchemaValidatorUtil.validateJsonAgainstSchema(schemaFile, jsonString);
        Assert.assertFalse(isValid);

    }


    @Test
    public void validatePOJOToJson2addr() throws JsonProcessingException {
// Assuming schema file "addresses-schema.json" in src/test/resources
        AddressDetails addr1 = new AddressDetails("street1", "city1");
        AddressDetails addr2 = new AddressDetails("street2", "city2");
        ArrayList<AddressDetails> allAddresses = new ArrayList<>();
        allAddresses.add(addr1);
        allAddresses.add(addr2);
        PersonalDetails personalDetails = new PersonalDetails("name1", allAddresses);

        String schemaFile = "addresses-schema.json";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(personalDetails);


        boolean isValid = JsonSchemaValidatorUtil.validateJsonAgainstSchema(schemaFile, jsonString);
        Assert.assertTrue(isValid);


        given()
                .body(personalDetails)
                .when()
                .post("/postVariableJson")
                .then()
                .log().all()
                .assertThat()
                .body("msg", equalTo("Success"));
    }

    @Test
    public void validatePostRequestVariableJson2addr() throws JsonProcessingException {
// Assuming schema file "addresses-schema.json" in src/test/resources
        String schemaFile = "addresses-schema.json";
        String sampleJson = """
                            {
                              "name": "John Doe",
                              "allAddresses": [
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
                .post("/postVariableJson")
        .then()
                .log().all()
                .assertThat()
                .body("msg", equalTo("Success"));
    }

    @Test
    public void validatePostRequestVariableJson5addr() throws JsonProcessingException {
// Assuming schema file "addresses-schema.json" in src/test/resources
        String schemaFile = "addresses-schema.json";
        String sampleJson = """
                            {
                              "name": "John Doe",
                              "allAddresses": [
                                {"street": "123 Main St", "city": "Anytown"},
                                {"street": "456 Oak Ave", "city": "Othertown"},
                                {"street": "456 Oak Ave", "city": "Othertown"},
                                {"street": "456 Oak Ave", "city": "Othertown"},
                                {"street": "456 Oak Ave", "city": "Othertown"}
                              ]
                            }
                            """;

        boolean isValid = JsonSchemaValidatorUtil.validateJsonAgainstSchema(schemaFile, sampleJson);
        Assert.assertTrue(isValid);


        given()
                .body(sampleJson)
                .when()
                .post("/postVariableJson")
                .then()
                .log().all()
                .assertThat()
                .body("msg", equalTo("Success"));
    }

    @Test
    public void validatePostRequestVariableJsonNoStreet() throws JsonProcessingException {
// Assuming schema file "addresses-schema.json" in src/test/resources
        String schemaFile = "addresses-schema.json";
        String sampleJson = """
                            {
                              "name": "John Doe",
                              "allAddresses": [
                                {"street": "123 Main St", "city": "Anytown"},
                                {"street": "456 Oak Ave", "city": "Othertown"},
                                {"street": "456 Oak Ave", "city": "Othertown"},
                                {"city": "Othertown"}
                              ]
                            }
                            """;

        boolean isValid = JsonSchemaValidatorUtil.validateJsonAgainstSchema(schemaFile, sampleJson);
        Assert.assertFalse(isValid);

    }

    @Test
    public void validatePostRequestVariableJsonNoCity() throws JsonProcessingException {
// Assuming schema file "addresses-schema.json" in src/test/resources
        String schemaFile = "addresses-schema.json";
        String sampleJson = """
                            {
                              "name": "John Doe",
                              "allAddresses": [
                                {"street": "123 Main St", "city": "Anytown"},
                                {"street": "456 Oak Ave", "city": "Othertown"},
                                {"street": "456 Oak Ave", "city": "Othertown"},
                                {"street": "456 Oak Ave", "city": "Othertown"},
                                {"street": "456 Oak Ave"}
                              ]
                            }
                            """;

        boolean isValid = JsonSchemaValidatorUtil.validateJsonAgainstSchema(schemaFile, sampleJson);
        Assert.assertFalse(isValid);

    }

    @Test
    public void validatePostRequestVariableJsonNoName() throws JsonProcessingException {
// Assuming schema file "addresses-schema.json" in src/test/resources
        String schemaFile = "addresses-schema.json";
        String sampleJson = """
                            {
                              
                              "allAddresses": [
                                {"street": "123 Main St", "city": "Anytown"},
                                {"street": "456 Oak Ave", "city": "Othertown"},
                                {"street": "456 Oak Ave", "city": "Othertown"},
                                {"street": "456 Oak Ave", "city": "Othertown"},
                                {"street": "456 Oak Ave", "city": "Othertown"}
                              ]
                            }
                            """;

        boolean isValid = JsonSchemaValidatorUtil.validateJsonAgainstSchema(schemaFile, sampleJson);
        Assert.assertFalse(isValid);


//        given()
//                .body(sampleJson)
//                .when()
//                .post("/postVariableJson")
//                .then()
//                .log().all()
//                .assertThat()
//                .body("msg", equalTo("Success"));
    }

    @Test
    public void validatePostRequestVariableJsonNoAddr() throws JsonProcessingException {
// Assuming schema file "addresses-schema.json" in src/test/resources
        String schemaFile = "addresses-schema.json";
        String sampleJson = """
                            {
                              "name": "John Doe",
                              "addresses": [
                              ]
                            }
                            """;

        boolean isValid = JsonSchemaValidatorUtil.validateJsonAgainstSchema(schemaFile, sampleJson);
        Assert.assertTrue(isValid);


        given()
                .body(sampleJson)
                .when()
                .post("/postVariableJson")
                .then()
                .log().all()
                .assertThat()
                .body("msg", equalTo("Success"));
    }


}
