package org.frameworktest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.List;

public class ProductsPageEcommerce extends AbstractComponentsEcommerce{

    @FindBy(css=".product-thumb")
    List<WebElement> productCards;

    @FindBy(xpath="//div[@id='notification-box-top']//div//a[contains(.,'View Cart ')]")
    WebElement viewCart;

    public ProductsPageEcommerce(WebDriver driver){
        super(driver);
        this.driver = driver;
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
    }

    public void addProductToCart(String productName){

        productCards.stream()
                .filter(card->card.findElement(By.cssSelector("h4.title a")).getText()
                        .equalsIgnoreCase(productName))
                .findFirst()
                .ifPresent(card->{
                    Actions actions = new Actions(driver);
                    actions.moveToElement(card).perform();
                    card.findElement(By.cssSelector("button.btn-cart")).click();
                    System.out.println(productName + " added to cart.");
                });

    }

    public void viewCartFromNotification(){
        waitForElementsToAppear(viewCart);
        viewCart.click();
    }


}
