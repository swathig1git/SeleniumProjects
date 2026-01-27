package org.SAKSTests;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.POJO.ProductType;
import org.SAKSPages.SAKSProductsFilterPage;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.utils.SAKSVerifyUtils.verifyExpectedButtons;

public class SAKSProductFilterCommonCucumber {
    private WebDriver driver;
    private SAKSProductsFilterPage saksProductsFilterPage;
    private Map<String, ProductType> productMap;

    @Before
    public void setUp() throws IOException {
        // Assuming BaseTestSAKS has initializeDiver method
        driver = new BaseTestSAKS().initializeDiver();
        saksProductsFilterPage = new SAKSProductsFilterPage(driver);
        // Initialize productMap with sample data or load from somewhere
        productMap = new HashMap<>();
        // Add sample ProductType objects here
        // For example:
        // ProductType product1 = new ProductType("ExampleProduct1", "url", browseByList, filterList);
        // productMap.put("ExampleProduct1", product1);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Given("the SAKS application is launched")
    public void launchApplication() throws IOException {
        // Already done in setUp
    }

    @When("I launch the page for product {string}")
    public void launchPage(String productName) {
        ProductType product = productMap.get(productName);
        new BaseTestSAKS().launchPage(product);
    }

    @When("I launch the category page for product {string}")
    public void launchCategoryPage(String productName) {
        ProductType product = productMap.get(productName);
        driver.get(product.getCategoryUrl());
    }

    @When("I scroll until products are visible with count {int}")
    public void scrollUntilProductsVisible(int count) {
        saksProductsFilterPage.scrollUntilProductsVisible(count);
    }

    @When("I wait for popup watcher to finish with timeout {int}")
    public void waitForPopupWatcher(int timeout) {
        new BaseTestSAKS().waitForPopupWatcherToFinish(timeout);
    }

    @When("I update price range from {int} to {int}")
    public void updatePriceRange(int minPrice, int maxPrice) {
        saksProductsFilterPage.updatePriceRange(minPrice, maxPrice);
    }

    @Then("the browse by buttons should match the expected list for {string}")
    public void verifyBrowseByButtons(String productName) {
        ProductType product = productMap.get(productName);
        boolean result = verifyExpectedButtons(driver, saksProductsFilterPage.getBrowseByButtons(), product.getBrowseByList());
        Assert.assertTrue(result, "Browse By buttons validation failed for " + productName);
    }

    @Then("the filter buttons should match the expected list for {string}")
    public void verifyFilterButtons(String productName) {
        ProductType product = productMap.get(productName);
        boolean result = verifyExpectedButtons(driver, saksProductsFilterPage.getFilterButtons(), product.getFilterList());
        Assert.assertTrue(result, "Filters validation failed for " + productName);
    }

    @Then("all product current prices should be between {int} and {int} for {string}")
    public void verifyPricesInRange(int minPrice, int maxPrice, String productName) {
        List<Double> productCurrentPrices = saksProductsFilterPage.getAllProductCurrentPrices();
        boolean allPricesInRange = productCurrentPrices.stream()
                .allMatch(price -> price >= minPrice && price <= maxPrice);
        Assert.assertTrue(allPricesInRange, "Some product prices are outside the range for " + productName);
    }

    @Then("the product list should be empty for {string}")
    public void verifyEmptyList(String productName) {
        Assert.assertTrue(saksProductsFilterPage.isEmptyList(), "Product list is not empty for " + productName);
    }
}
