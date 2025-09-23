package org.frameworktest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;


public class StandAloneTest {
    @Test
    public void orderEndtoEnd() throws InterruptedException {
        WebDriver driver = new ChromeDriver();

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        LandingPage landingPage = new LandingPage(driver);
        landingPage.goTo();
        ProductCatalogue productCatalogue = landingPage.loginApplication("swathi.g12025@gmail.com", "RahulShetty123$");

        List<WebElement> products = productCatalogue.getProductList();

        String productName = "ZARA COAT 3";

        WebElement prod = productCatalogue.getProductByName(productName);
        productCatalogue.addProductToCart(productName);
        CartPage cartPage = productCatalogue.goToCartPage();
        Boolean match = cartPage.verifyProductDisplay(productName);
        Assert.assertTrue(match);
        CheckoutPage checkoutPage = cartPage.goToCheckout();
        checkoutPage.selectCountry("india");
        ConfirmPage confirmPage = checkoutPage.actionSubmit();


        String confirmMessage = confirmPage.getConfirmMessage();
        Assert.assertTrue(confirmMessage.equalsIgnoreCase("Thankyou for the order."));


        Thread.sleep(2000);
        driver.quit();

    }
}
