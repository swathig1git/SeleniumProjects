package org.frameworktest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CheckoutPage extends AbstractComponents{
    public CheckoutPage(WebDriver driver){
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css="[placeholder='Select Country']")
    WebElement selectCountry;

    @FindBy(xpath="(//button[contains(@class,'ta-item')])[2]")
    WebElement countrySelect;

    @FindBy(xpath="//a[contains(text(),'Place Order')]")
    WebElement placeOrder;

    By countryResults = By.cssSelector(".ta-results");
    By actionSubmit = By.cssSelector(".action__submit");
   // By actionSubmit = By.cssSelector(".action__submit");

    public void selectCountry(String country){
        Actions a = new Actions(driver);
        a.sendKeys(selectCountry, country).build().perform();
        waitForElementToAppear(countryResults);

        countrySelect.click();

    }
    public ConfirmPage actionSubmit(){
        waitForElementToAppear(actionSubmit);
        placeOrder.click();
        return new ConfirmPage(driver);
    }
}
