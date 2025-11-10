package org.frameworktest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AbstractComponentsEcommerce {
    WebDriver driver;

    @FindBy(xpath="//span[text()='My account']/parent::div/parent::a")
    WebElement myAccount;

    @FindBy(xpath="//span[contains(.,'Mega Menu')]/parent::div/parent::a")
    WebElement megaMenu;

    @FindBy(xpath="//a[normalize-space()='Apple']")
    WebElement apple;

    public AbstractComponentsEcommerce(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
    }

    public ProductsPageEcommerce goToApple(){
        Actions actions = new Actions(driver);
        actions.moveToElement(megaMenu).perform();
        waitForElementsToAppear(apple);
        apple.click();
        return new ProductsPageEcommerce(driver);
    }

    public void waitForElementsToAppear(WebElement findBy){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOf(findBy));
    }


}
