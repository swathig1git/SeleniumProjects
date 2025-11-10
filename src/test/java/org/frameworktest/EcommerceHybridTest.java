package org.frameworktest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.POJO.CartProductDetails;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import org.utils.SessionState;
import org.utils.SessionSyncUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;


public class EcommerceHybridTest extends BaseTestEcommerce{

    @BeforeMethod
    public void setup(){
        LoginPageEcommerce loginPageEcommerce = landingPageEcommerce.goToLoginPage();
        loginPageEcommerce.login("play.wright6@gmail.com", "pass123");

        state = SessionSyncUtil.captureSessionFromSelenium(driver);
    }
    @Test(groups={"regression"})
    public void uiAPIHybridTest() {

        // 3️⃣ Use session for authenticated API call
        Response addItemResponse = SessionSyncUtil.applySessionToApi(state)
                .baseUri("https://ecommerce-playground.lambdatest.io")
                .header("Referer", "https://ecommerce-playground.lambdatest.io/index.php?route=product/manufacturer/info&manufacturer_id=8")
                .header("X-Requested-With", "XMLHttpRequest")
                .contentType("application/x-www-form-urlencoded")
                .formParam("product_id", "36")
                .formParam("quantity", "1")
                .when()
                .post("/index.php?route=checkout/cart/add")
                .then()
                .extract().response();

        System.out.println("Add to cart response: " + addItemResponse.asPrettyString());

        // Step 4️⃣: Open a *new browser window* with same cookies to verify UI
        driver.navigate().to("https://ecommerce-playground.lambdatest.io/index.php?route=checkout/cart");
        driver.navigate().refresh();
    }
    @Test(groups={"regression"})
    public void updateCartWithOneItemWithAPITest() {

        // 3️⃣ Use session for authenticated API call
        Response addItemResponse = SessionSyncUtil.applySessionToApi(state)
                .baseUri("https://ecommerce-playground.lambdatest.io")
                .header("Referer", "https://ecommerce-playground.lambdatest.io/index.php?route=product/manufacturer/info&manufacturer_id=8")
                .header("X-Requested-With", "XMLHttpRequest")
                .contentType("application/x-www-form-urlencoded")
                .formParam("product_id", "36")
                .formParam("quantity", "1")
                .when()
                .post("/index.php?route=checkout/cart/add")
                .then()
                .extract().response();

        System.out.println("Add to cart response: " + addItemResponse.asPrettyString());

        Response cartPage = SessionSyncUtil.applySessionToApi(state)
                .baseUri("https://ecommerce-playground.lambdatest.io")
                .header("Referer", "https://ecommerce-playground.lambdatest.io/index.php?route=product/manufacturer/info&manufacturer_id=8")
                .when()
                .get("/index.php?route=checkout/cart")
                .then()
                .extract().response();

        System.out.println("Get to cart response: " + cartPage.asPrettyString());
        String cartReponseHtml = cartPage.asString();

        Pattern pattern = Pattern.compile("name=\"quantity\\[(\\d+)]\"");
        Matcher matcher = pattern.matcher(cartReponseHtml);
        String cartId="";
        if (matcher.find()) {
            cartId = matcher.group(1);
            System.out.println("Cart ID: " + cartId);
        }



        Response editItemResponse = SessionSyncUtil.applySessionToApi(state)
                .baseUri("https://ecommerce-playground.lambdatest.io")
                .header("Referer", "https://ecommerce-playground.lambdatest.io/index.php?route=checkout/cart")
                .header("X-Requested-With", "XMLHttpRequest")
                .contentType("application/x-www-form-urlencoded")
                .formParam("quantity[" + cartId + "]", "10")
                .when()
                .post("/index.php?route=checkout/cart/edit")
                .then()
                .extract().response();

        System.out.println("Edit to cart response: " + addItemResponse.asPrettyString());

        // Step 4️⃣: Open a *new browser window* with same cookies to verify UI
        driver.navigate().to("https://ecommerce-playground.lambdatest.io/index.php?route=checkout/cart");
        driver.navigate().refresh();

        // Optionally, you can verify if the cart contains expected item
        System.out.println("Opened cart page to verify item.");

    }

    @Test(groups={"regression"})
    public void updateCartWithMultipleItemsWithAPITest() {

        addItemsToCart("36", 1);
        addItemsToCart("40", 1);

        List<String> cartIds = getCartIds();

        RequestSpecification req = SessionSyncUtil.applySessionToApi(state)
                .baseUri("https://ecommerce-playground.lambdatest.io")
                .contentType("application/x-www-form-urlencoded")
                .header("Referer", "https://ecommerce-playground.lambdatest.io/index.php?route=checkout/cart")
                .header("X-Requested-With", "XMLHttpRequest");

        for (String id : cartIds) {
            req.formParam("quantity[" + id + "]", "20");
        }

        Response editItemResponse = req
                .when()
                .post("/index.php?route=checkout/cart/edit")
                .then()
                .extract().response();

        //System.out.println("Edit to cart response: " + editItemResponse.asPrettyString());

        // Step 4️⃣: Open a *new browser window* with same cookies to verify UI
        driver.navigate().to("https://ecommerce-playground.lambdatest.io/index.php?route=checkout/cart");
        driver.navigate().refresh();

        //System.out.println("Current URL: " + driver.getCurrentUrl());

        CartPageEcommerce cartPageEcommerce = new CartPageEcommerce(driver);

        for (WebElement q : cartPageEcommerce.quantity) {
            String text = q.getAttribute("value").trim();
            Assert.assertEquals(text, "20", "Quantity is not 20 for element: " + q);
        }
    }

    @Test(groups={"regression"})
    public void updateSpecificItemInCartWithAPITest() throws InterruptedException {

        addItemsToCart("36", 1);
        addItemsToCart("40", 1);

        driver.navigate().to("https://ecommerce-playground.lambdatest.io/index.php?route=checkout/cart");
        driver.navigate().refresh();

        //System.out.println(driver.getPageSource());

        CartPageEcommerce cartPageEcommerce = new CartPageEcommerce(driver);
        Map<String, CartProductDetails> productMap = cartPageEcommerce.getCartProducts();
        CartProductDetails cartProduct = productMap.get("36");

        if (cartProduct != null)
            cartProduct.setQuantity(20);

        RequestSpecification req = SessionSyncUtil.applySessionToApi(state)
                .baseUri("https://ecommerce-playground.lambdatest.io")
                .contentType("application/x-www-form-urlencoded")
                .header("Referer", "https://ecommerce-playground.lambdatest.io/index.php?route=checkout/cart")
                .header("X-Requested-With", "XMLHttpRequest");

        for (CartProductDetails product : productMap.values()) {
            req.formParam("quantity[" + product.getCartId() + "]", String.valueOf(product.getQuantity()));
        }

        Response editItemResponse = req
                .when()
                .post("/index.php?route=checkout/cart/edit")
                .then()
                .extract().response();

        //System.out.println("Edit to cart response: " + editItemResponse.asPrettyString());

        // Step 4️⃣: Open a *new browser window* with same cookies to verify UI
        driver.navigate().to("https://ecommerce-playground.lambdatest.io/index.php?route=checkout/cart");
        driver.navigate().refresh();

        int quantity = cartPageEcommerce.getQuantityOfProductId("36");
        Assert.assertEquals(quantity, 20);
        quantity = cartPageEcommerce.getQuantityOfProductId("40");
        Assert.assertEquals(quantity, 1);
    }

    @Test(groups={"regression"})
    public void deleteCartWithOneItemWithAPITest() {

        addItemsToCart("36", 5);

        Response cartPage = SessionSyncUtil.applySessionToApi(state)
                .baseUri("https://ecommerce-playground.lambdatest.io")
                .header("Referer", "https://ecommerce-playground.lambdatest.io/index.php?route=product/manufacturer/info&manufacturer_id=8")
                .when()
                .get("/index.php?route=checkout/cart")
                .then()
                .extract().response();

        //System.out.println("Get to cart response: " + cartPage.asPrettyString());
        String cartResponseHtml = cartPage.asString();

        Pattern pattern = Pattern.compile("name=\"quantity\\[(\\d+)]\"");
        Matcher matcher = pattern.matcher(cartResponseHtml);
        String cartId="";
        if (matcher.find()) {
            cartId = matcher.group(1);
            System.out.println("Cart ID: " + cartId);
        }



        Response deleteItemResponse = SessionSyncUtil.applySessionToApi(state)
                .baseUri("https://ecommerce-playground.lambdatest.io")
                .header("X-Requested-With", "XMLHttpRequest")
                .contentType("application/x-www-form-urlencoded")
                .formParam("key" , cartId)
                .log().all()
                .when()
                .post("index.php?route=checkout/cart/remove")
                .then()
                .log().ifError()
                .extract().response();

        System.out.println("Delete from cart response: " + deleteItemResponse.asPrettyString());
        System.out.println("Status: " + deleteItemResponse.getStatusCode());
        System.out.println("Headers: " + deleteItemResponse.getHeaders());
        System.out.println("Body: " + deleteItemResponse.getBody().asString());
        System.out.println("Content-Type: " + deleteItemResponse.getHeader("Content-Type"));
        System.out.println("CArt id : " +cartId);


        viewCart();

    }


//    @AfterMethod
//    public void tearDown() {
//        System.out.println("EcommerceHybridTest teardown start");
//       // driver.quit();
//    }
}
