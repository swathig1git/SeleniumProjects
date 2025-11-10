package org.frameworktest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class LandingPageEcommerce extends AbstractComponentsEcommerce{

    @FindBy(xpath="//li[contains(@class,'dropdown')]//span[contains(.,'My account')]/parent::div/parent::a")
    WebElement myAccount;

    @FindBy(xpath="//span[contains(.,'Login')]/parent::div/parent::a")
    WebElement loginButton;

    public LandingPageEcommerce(WebDriver driver){
        super(driver);
        this.driver = driver;
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
    }



    public void goTo(){
        driver.get("https://ecommerce-playground.lambdatest.io/");
    }

    public LoginPageEcommerce goToLoginPage(){

        Actions actions = new Actions(driver);
        actions.moveToElement(myAccount).perform();
        loginButton.click();

        return new LoginPageEcommerce(driver);
    }

}
