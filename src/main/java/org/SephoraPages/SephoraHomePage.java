package org.SephoraPages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class SephoraHomePage extends AbstractComponentsSephora{

    By newBy = By.id("top_nav_drop_0_trigger");
    public SephoraHomePage(WebDriver driver){
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void hoverOnTopMenuItem(int menuNumber){
        WebElement menu = driver.findElement(By.id("top_nav_drop_" + menuNumber + "_trigger"));
        hover(menu);
    }
}
