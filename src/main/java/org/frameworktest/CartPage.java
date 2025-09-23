package org.frameworktest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class CartPage extends AbstractComponents{
    @FindBy(css=".cartSection h3")
    List<WebElement> cartProducts;

    @FindBy(css=".totalRow button")
    WebElement checkOut;


    public CartPage(WebDriver driver){
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public CheckoutPage goToCheckout(){
        checkOut.click();
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        return checkoutPage;

    }
    public Boolean verifyProductDisplay(String productName){
        Boolean match = cartProducts.stream().anyMatch(
                cartProduct->cartProduct.getText().equalsIgnoreCase(productName));
        return match;
    }
}
