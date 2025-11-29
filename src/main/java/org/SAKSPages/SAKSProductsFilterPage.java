package org.SAKSPages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SAKSProductsFilterPage extends AbstractComponentsSAKS {
    @FindBy(xpath="//h4//div[text()='Colour']")
    WebElement colourFilter;

    @FindBy(xpath="//button[@name='Green']")
    WebElement greenColourFilter;
//    @FindBy(css="a.CardImage__imageWrapper")
//    WebElement productCards;
    By productBrandNameBy = By.xpath("//div[contains(@data-testid,'product-card')]//h4");
    @FindBy(xpath="//div[text()='Only at Saks']")
    WebElement onlyAtSaks;
    By designerFilterBy = By.xpath("//div[text()='Designers']");
    @FindBy(xpath="//div[text()='Designers']")
    WebElement designerFilter;
    @FindBy(xpath="//div[text()='10 brand options']/following-sibling::div/button")
    List<WebElement> designerOptions;
    By designerOptionsBy = By.xpath("//div[text()='10 brand options']/following-sibling::div/button");

    @FindBy(xpath="//div[contains(text(),'brand options')]/following-sibling::div/button")
    WebElement allDesignerOptions;
    @FindBy(xpath="//button[text()='View All']")
    WebElement designerViewAll;
    @FindBy(xpath="//div[text()='Size']")
    WebElement sizeFilter;
    @FindBy(xpath="//button[text()='XX-Small, 00']")
    WebElement sizeXXSmall;
    @FindBy(xpath="//div[contains(@class,'ProductCardHeader__productCardBrandName')]")
    WebElement brandName;

    By filterSelectedButton = By.xpath("//button[contains(@class,'FiltersSelectedButton__button')]");


    public SAKSProductsFilterPage(WebDriver driver){
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public int getBrandOptionsCount() {
        By brandOptionBy = By.xpath("//div[@role='status' and contains(text(), 'brand options')]");
        waitForWebElementToBePresent(brandOptionBy);
        WebElement statusElement = driver.findElement(brandOptionBy);
        String text = statusElement.getText();  // e.g. "645 brand options"

        return Integer.parseInt(text.replaceAll("\\D", ""));
    }

    public void clickDesignerOption(int designerNumber) {
        scrollIntoView(designerOptionsBy);
        List<WebElement> options = driver.findElements(designerOptionsBy);

        // Safety check (optional)
        if (designerNumber < 0 || designerNumber >= options.size()) {
            throw new IllegalArgumentException(
                    "designerNumber out of range. Available: " + options.size()
            );
        }

        options.get(designerNumber).click();
    }


    public WebElement getDesignerFilter() {
        scrollIntoView(designerFilter);
        return designerFilter;
    }


    public By getDesignerOptionsBy() {
        return designerOptionsBy;
    }


//    public By getProductCardsBy() {
//        return productCardsBy;
//    }
//
//    public List<WebElement> getProductCards(){
//
//        scrollIntoView(filterSelectedButton);
//        return driver.findElements(productCardsBy);
//    }
// This is the KEY method — returns the designer name for each visible product

    public List<String> getVisibleDesignerNames() {
        return driver.findElements(productBrandNameBy)
                .stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .collect(Collectors.toList());
    }

    public void scrollToLoadMoreProducts() {
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollBy(0, window.innerHeight);");
    }

    public void waitForNewProductsToLoad(int previousCount) {
        new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(d -> driver.findElements(productBrandNameBy).size() > previousCount);
    }
    public void selectDesignerFilter() {


        WebElement designerFilterButton = new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(ExpectedConditions.presenceOfElementLocated(designerFilterBy));

        // Scroll the exact button into view (center is best for fixed headers)
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", designerFilterButton);

        // Extra safety: wait until it's actually clickable
        Objects.requireNonNull(new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.elementToBeClickable(designerFilterButton)))
                .click();
    }

    public void selectDesigner(String designerName) {
        WebElement designerFilterButton = new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(ExpectedConditions.presenceOfElementLocated(designerFilterBy));

        // Scroll the exact button into view (center is best for fixed headers)
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", designerFilterButton);

        // Extra safety: wait until it's actually clickable
        Objects.requireNonNull(new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.elementToBeClickable(designerFilterButton)))
                .click();

        By designerButtonBy = By.xpath(
                String.format("//div[text()='10 brand options']/following-sibling::div/button[text()='%s']",
                        designerName.trim())
        );

        WebElement designerButton = new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(ExpectedConditions.presenceOfElementLocated(designerButtonBy));

        // Scroll the exact button into view (center is best for fixed headers)
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", designerButton);

        // Extra safety: wait until it's actually clickable
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(designerButton))
                .click();
    }

    // Scrolls until at least maxToCheck products are visible (or no more load)
    public void scrollUntilProductsVisible(int maxToCheck) {
        int previousCount = 0;

        while (true) {
            // Scroll to load more
            scrollToLoadMoreProducts();

            // Wait for new products to appear
            waitForNewProductsToLoad(previousCount);

            // Get current visible count
            int currentCount = getVisibleDesignerNames().size();

            // Stop if we hit the target or nothing new loaded
            if (currentCount >= maxToCheck || currentCount == previousCount) {
                break;
            }

            previousCount = currentCount;
        }
    }
    // Optional: wait until at least one new product appeared
//    public void waitForNewProductsToLoad(int previousCount) {
//        new WebDriverWait(driver, Duration.ofSeconds(8))
//                .until(d -> driver.findElements(By.cssSelector(".product-card")).size() > previousCount);
//    }
}
