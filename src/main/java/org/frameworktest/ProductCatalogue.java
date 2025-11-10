package org.frameworktest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class ProductCatalogue extends AbstractComponents{
    public ProductCatalogue(WebDriver driver){
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
//            wait.until(ExpectedConditions.visibilityOfElementLocated(
//                    By.cssSelector(".mb-3")));

    @FindBy(css=".mb-3")
    List<WebElement> products;



    By productBy = By.cssSelector(".mb-3");
    By addToCart = By.cssSelector(".card-body button:last-of-type");
    By toastMessage = By.cssSelector("#toast-container");

    public List<WebElement> getProductList(){
        waitForElementToAppear(productBy);
        return products;
    }
    public WebElement getProductByName(String productName){

        WebElement prod = getProductList().stream().filter(product->
                        product.findElement(By.cssSelector("b")).getText().equals(productName))
                .findFirst().orElse(null);

        return prod;
    }

    public void addProductToCart(String productName){

        WebElement prod = getProductByName(productName);
        prod.findElement(addToCart).click();
        waitForElementToAppear(toastMessage);
        waitForElementToDisappear(spinner);
    }
}
