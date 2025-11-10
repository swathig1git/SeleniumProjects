package org.frameworktest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class LoginPageEcommerce extends AbstractComponentsEcommerce{
    @FindBy(css="#input-email")
    WebElement emailElement;

    @FindBy(css="#input-password")
    WebElement passwordElement;

    @FindBy(css="input[type='submit']")
    WebElement loginElement;

    public LoginPageEcommerce(WebDriver driver){
        super(driver);
        this.driver = driver;
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
    }

    public void login(String username, String password){

        emailElement.sendKeys(username);
        passwordElement.sendKeys(password);
        loginElement.click();

    }

}
