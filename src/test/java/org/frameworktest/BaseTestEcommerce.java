package org.frameworktest;

import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.utils.SessionState;
import org.utils.SessionSyncUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseTestEcommerce {
    public WebDriver driver;
    LandingPageEcommerce landingPageEcommerce;
    SessionState state;

    public WebDriver initializeDiver() throws IOException {
        Properties prop= new Properties();
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+
                "//src//main//java//org//resources//GlobalData.properties");
        prop.load(fis);

        String browserName = System.getProperty("browser") != null ? System.getProperty("browser")
                                                                    : prop.getProperty("browser");

        if(browserName.equalsIgnoreCase("chrome")) {

             driver = new ChromeDriver();
        } else if (browserName.equalsIgnoreCase("Headless")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("headless");
            driver = new ChromeDriver(options);

        } else {
             driver = new FirefoxDriver();
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return driver;
    }

    @BeforeMethod(alwaysRun = true)
    public void launchApplication() throws IOException {
        driver = initializeDiver();
        landingPageEcommerce = new LandingPageEcommerce(driver);
        landingPageEcommerce.goTo();
        return;

    }
    @AfterMethod(alwaysRun = true)
    public void tearDown(){
        deleteAllItemsFromCart();
        driver.quit();
    }

    public void addItemsToCart(String productId, int quantity){
        // 3️⃣ Use session for authenticated API call
        Response addItemResponse = SessionSyncUtil.applySessionToApi(state)
                .baseUri("https://ecommerce-playground.lambdatest.io")
                .header("Referer", "https://ecommerce-playground.lambdatest.io/index.php?route=product/manufacturer/info&manufacturer_id=8")
                .header("X-Requested-With", "XMLHttpRequest")
                .contentType("application/x-www-form-urlencoded")
                .formParam("product_id", productId)
                .formParam("quantity", quantity)
                .when()
                .post("/index.php?route=checkout/cart/add")
                .then()
                .extract().response();

        //System.out.println("Add to cart response: " + addItemResponse.asPrettyString());

    }

    public void deleteAllItemsFromCart(){

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

        List<String> cartIds = new ArrayList<>();

        while (matcher.find()) {
            cartIds.add(matcher.group(1));
        }

        if (cartIds.isEmpty()) {
            System.out.println("🟢 No items found in the cart.");
            return;
        }

        //System.out.println("🛒 Found " + cartIds.size() + " items in the cart: " + cartIds);

        // Step 3️⃣: Delete each item one by one
        for (String cartId : cartIds) {
            Response deleteItemResponse = SessionSyncUtil.applySessionToApi(state)
                    .baseUri("https://ecommerce-playground.lambdatest.io")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("key", cartId)
                    .log().all()
                    .when()
                    .post("index.php?route=checkout/cart/remove")
                    .then()
                    .log().ifError()
                    .extract().response();

           /* System.out.println("🗑️ Deleted item with cart ID: " + cartId);
            System.out.println("Status: " + deleteItemResponse.getStatusCode());
            System.out.println("Delete from cart response: " + deleteItemResponse.asPrettyString());
            System.out.println("Status: " + deleteItemResponse.getStatusCode());
            System.out.println("Headers: " + deleteItemResponse.getHeaders());
            System.out.println("Body: " + deleteItemResponse.getBody().asString());
            System.out.println("Content-Type: " + deleteItemResponse.getHeader("Content-Type"));*/
        }

        // Step 4️⃣: Open a *new browser window* with same cookies to verify UI
        driver.navigate().to("https://ecommerce-playground.lambdatest.io/index.php?route=checkout/cart");
        driver.navigate().refresh();
    }

    public void viewCart(){
        // Step 4️⃣: Open a *new browser window* with same cookies to verify UI
        driver.navigate().to("https://ecommerce-playground.lambdatest.io/index.php?route=checkout/cart");
        driver.navigate().refresh();

        //System.out.println("Opened cart page to verify item.");
    }

    public List<String> getCartIds(){
        Response cartPage = SessionSyncUtil.applySessionToApi(state)
                .baseUri("https://ecommerce-playground.lambdatest.io")
                .header("Referer", "https://ecommerce-playground.lambdatest.io/index.php?route=product/manufacturer/info&manufacturer_id=8")
                .when()
                .get("/index.php?route=checkout/cart")
                .then()
                .extract().response();

        String cartResponseHtml = cartPage.asString();

        Pattern pattern = Pattern.compile("name=\"quantity\\[(\\d+)]\"");
        Matcher matcher = pattern.matcher(cartResponseHtml);

        List<String> cartIds = new ArrayList<>();

        while (matcher.find()) {
            String cartId = matcher.group(1);
            cartIds.add(cartId);
        }

        System.out.println("Found cart IDs: " + cartIds);

        return cartIds;
    }

}
