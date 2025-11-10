package org.frameworktest;
import org.testng.annotations.Test;

import java.io.IOException;


public class SubmitOrderEcommerceTest extends BaseTestEcommerce{
    @Test (groups={"regression"})
    public void orderEndToEnd() throws InterruptedException, IOException {

        LoginPageEcommerce loginPageEcommerce = landingPageEcommerce.goToLoginPage();
        loginPageEcommerce.login("play.wright6@gmail.com", "pass123");
        ProductsPageEcommerce productsPageEcommerce = loginPageEcommerce.goToApple();
        productsPageEcommerce.addProductToCart("iPod Touch");
        productsPageEcommerce.viewCartFromNotification();
        Thread.sleep(5000);

    }
}
