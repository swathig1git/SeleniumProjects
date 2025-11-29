package org.SAKSPages;

import org.SAKSPages.AbstractComponentsSAKS;
import org.frameworktest.ProductCatalogue;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class SAKSHomePage extends AbstractComponentsSAKS {
    @FindBy(xpath="//a[text()='Designers']/parent::div")
    WebElement designerDropdown;

    @FindBy(xpath="//a[text()='Shop All Designers']")
    WebElement shopAllDesigners;

    @FindBy(xpath="//a[text()='Designers']/parent::div//div[text()='Featured Designers']")
    WebElement featuredDesigners;

    @FindBy(xpath="//a[text()='Designers']/parent::div//a[@data-testid='megamenu-navigation-dropdown-link']")
    List<WebElement> allFeaturedDesigners;

    @FindBy(xpath="//a[text()='Clothing' and @aria-controls='clothing-submenu']")
    WebElement clothingDropdown;

    @FindBy(xpath="//button[text() = 'SHOP SAKS CANADA']")
    WebElement shopSaksCanada;

    @FindBy(xpath="//a[text()='Men']")
    WebElement men;

    By allFeaturedDesignersBy = By.xpath("//a[text()='Designers']/parent::div//a[@data-testid='megamenu-navigation-dropdown-link']");


    public SAKSHomePage(WebDriver driver){
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public By getAllFeaturedDesignersBy() {
        return allFeaturedDesignersBy;
    }

    public WebElement getDesignerDropdown() {
        return designerDropdown;
    }

    public List<WebElement> getAllFeaturedDesigners() {
        return allFeaturedDesigners;
    }

    public WebElement getFeaturedDesigners() {
        return featuredDesigners;
    }

    public WebElement getMen() {
        return men;
    }

    public void clickOnBrandName(String brandName){
        String xpathBrandName = brandName.contains("'")
                ? "//a[text()=\"Designers\"]/parent::div//a[@data-testid='megamenu-navigation-dropdown-link' and text()=\"" + brandName + "\"]"
                : "//a[text()='Designers']/parent::div//a[@data-testid='megamenu-navigation-dropdown-link' and text()='" + brandName + "']";

        this.driver.findElement(By.xpath(xpathBrandName)).click();

    }
}
