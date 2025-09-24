package org.frameworktest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class ErrorValidationTest extends BaseTest{
    @Test (groups={"ErrorHandling"})
    public void loginErrorValidationTest() throws InterruptedException, IOException {

        ProductCatalogue productCatalogue = landingPage.loginApplication("swathi.g12025@gmail.com", "RahulShetty");
        String errorMessage = landingPage.getErrorMessage();
        Assert.assertEquals("Incorrect email or password.", errorMessage);

    }
    @Test  (groups={"ErrorHandling"})
    public void productErrorValidationTest() throws InterruptedException, IOException {
        ProductCatalogue productCatalogue = landingPage.loginApplication("swathi.g12025@gmail.com", "RahulShetty123$");

        List<WebElement> products = productCatalogue.getProductList();

        String productName = "ZARA COAT 3";

        WebElement prod = productCatalogue.getProductByName(productName);
        productCatalogue.addProductToCart(productName);
        CartPage cartPage = productCatalogue.goToCartPage();
        Boolean match = cartPage.verifyProductDisplay("ZARA COAT 33");
        Assert.assertFalse(match);

    }
    }
