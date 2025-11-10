package org.frameworktest;
import io.restassured.http.Cookies;
import org.openqa.selenium.Cookie;
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.http.ContentType;
import static io.restassured.RestAssured.given;
import java.io.IOException;


public class EcommerceAPITest{
    WebDriver driver;

    @BeforeClass
    public void setup() {
        // Set path to chromedriver if needed
        driver = new ChromeDriver();
        RestAssured.baseURI = "https://ecommerce-playground.lambdatest.io";  // sample test API
    }

    @Test(groups={"regression"})
    public void testCreateUserAndVerifyUI() {

        // --- Step 1: POST API call ---
        String requestBody = "{\n" +
                "  \"email\": \"play.wright6@gmail.com\",\n" +
                "  \"password\": \"pass123\"\n" +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/index.php?route=account/login")
                .then()
                .statusCode(200)
                .extract().response();

        //System.out.println("Response: " + response.asPrettyString());

    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}
