package org.SAKSPages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AbstractComponentsSAKS {
    WebDriver driver;

    public AbstractComponentsSAKS(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    public void waitForElementToAppear(By findBy){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(findBy));

    }

    public void waitForWebElementToAppear(WebElement findBy){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOf(findBy));

    }

    public void waitForWebElementToBePresent(By findBy){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.presenceOfElementLocated(findBy));

    }

    public void waitForElementToDisappear(WebElement element){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.invisibilityOf(element));

    }

    public void waitForURLToContain(String url){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains(url));

    }

    public void hover(WebElement element){
        Actions actions = new Actions(driver);
        actions.moveToElement(element).perform();

    }

    public void scrollIntoView(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                element);
    }

    public void scrollIntoView(By locator) {
        WebElement element = driver.findElement(locator);
        waitForWebElementToBePresent(locator);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                element);
    }

}
