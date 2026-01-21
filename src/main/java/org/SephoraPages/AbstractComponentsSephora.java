package org.SephoraPages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AbstractComponentsSephora {
    WebDriver driver;
    public AbstractComponentsSephora(WebDriver driver){

        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void waitForWebElementTpAppear(WebElement element){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void hover(WebElement element){

        waitForWebElementTpAppear(element);
        Actions actions = new Actions(driver);
        actions.moveToElement(element).perform();
    }
}
