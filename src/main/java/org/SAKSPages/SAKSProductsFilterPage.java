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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SAKSProductsFilterPage extends AbstractComponentsSAKS {
    By productBrandNameBy = By.xpath("//div[contains(@data-testid,'product-card')]//h4");
    By designerFilterBy = By.xpath("//div[text()='Designers']");
    @FindBy(xpath="//div[text()='Designers']")
    WebElement designerFilter;
    By designerOptionsBy = By.xpath("//div[text()='10 brand options']/following-sibling::div/button");
    By browseByButtonsBy = By.cssSelector("[data-testid$='sideNavigation-sideNavigation-2'] button");
    By filtersBy = By.cssSelector(".FiltersSidebar__button");

    By priceFilterBy = By.cssSelector("button[aria-label='Price']");
    By priceMinBy = By.cssSelector("#min");
    By priceMaxBy = By.cssSelector("#max");
    By updatePriceButtonBy = By.xpath("//button[text()='Update price']");
    By productCurrentPrice = By.cssSelector("[data-testid*='currentPrice']");
    By emptyList = By.cssSelector(".EmptyListingFallback__textWrapper");


    public Boolean isEmptyList(){

        WebElement emptyListMessage = driver.findElement(emptyList);
        waitForWebElementToAppear(emptyListMessage);
        return emptyListMessage.isDisplayed();
    }
    public SAKSProductsFilterPage(WebDriver driver){
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public List<Double> getAllProductCurrentPrices(){
        List<WebElement> priceElements = driver.findElements(productCurrentPrice);
        List<String> prices = new ArrayList<>();

        for (WebElement price : priceElements) {
            prices.add(price.getText().trim());
        }

// Print all prices
        prices.forEach(System.out::println);

        List<Double> priceValues = priceElements.stream()
                .map(e -> e.getText().replaceAll("[^0-9.]", "")) // remove $ , etc
                .filter(s -> !s.isEmpty())
                .map(Double::parseDouble)
                .toList();

        priceValues.forEach(System.out::println);

        return priceValues;

    }
    public void updatePriceRange(Integer minPrice, Integer maxPrice){
        scrollIntoView(priceFilterBy);
        waitForWebElementToBePresent(priceFilterBy);
        WebElement priceFilter = driver.findElement(priceFilterBy);
        priceFilter.click();

        scrollIntoView(priceMinBy);
        waitForWebElementToBePresent(priceMinBy);
        WebElement priceMin = driver.findElement(priceMinBy);
        priceMin.sendKeys(minPrice.toString());

        scrollIntoView(priceMaxBy);
        waitForWebElementToBePresent(priceMaxBy);
        WebElement priceMax = driver.findElement(priceMaxBy);
        priceMax.sendKeys(maxPrice.toString());

        scrollIntoView(updatePriceButtonBy);
        waitForWebElementToBePresent(updatePriceButtonBy);
        WebElement  updatePriceButton = driver.findElement(updatePriceButtonBy);
        updatePriceButton.click();
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

    public List<WebElement> getBrowseByButtons() {
        return getWebElements(browseByButtonsBy);
    }

    public List<WebElement> getFilterButtons() {
        return getWebElements(filtersBy);
    }

    public List<WebElement> getWebElements(By elementBy) {
        scrollIntoView(driver.findElement(elementBy));
        return driver.findElements(elementBy);
    }


    public WebElement getDesignerFilter() {
        scrollIntoView(designerFilter);
        return designerFilter;
    }


    public By getDesignerOptionsBy() {
        return designerOptionsBy;
    }

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
    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver)
        .executeScript("arguments[0].scrollIntoView(true);", element);
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
