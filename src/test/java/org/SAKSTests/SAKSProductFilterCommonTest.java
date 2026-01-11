package org.SAKSTests;

import org.POJO.ProductType;
import org.SAKSPages.SAKSProductsFilterPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.testng.Assert.assertTrue;
import static org.utils.SAKSVerifyUtils.verifyExpectedButtons;


public class SAKSProductFilterCommonTest extends BaseTestSAKS {
    @BeforeMethod(alwaysRun = true)
    public void launchApplication() throws IOException {
        driver = initializeDiver();
        //driver.get("https://ca.saks.com/en-ca/women/clothing");
//        removeOneTrustBanner();
//        startPopupWatcher();

    }
    @Test(groups={"regression"}, dataProvider = "productData")
    public void browseByMenuTest(ProductType product) {
        launchPage(product);
        driver.get(product.getCategoryUrl());

        SAKSProductsFilterPage saksProductsFilterPage = new SAKSProductsFilterPage(driver);
        saksProductsFilterPage.scrollUntilProductsVisible(4);
        boolean result = verifyExpectedButtons(driver, saksProductsFilterPage.getBrowseByButtons(), product.getBrowseByList());
        Assert.assertTrue(result, "Browse By buttons validation failed for " + product.getName());
    }

    @Test(groups={"regression"}, dataProvider = "productData")
    public void filtersTest(ProductType product) {
        launchPage(product);
        driver.get(product.getCategoryUrl());

        SAKSProductsFilterPage saksProductsFilterPage = new SAKSProductsFilterPage(driver);
        saksProductsFilterPage.scrollUntilProductsVisible(4);
        boolean result = verifyExpectedButtons(driver, saksProductsFilterPage.getFilterButtons(), product.getFilterList());
        Assert.assertTrue(result, "Filters validation failed for " + product.getName());
    }

    @Test(groups={"regression"}, dataProvider = "productData")
    public void priceFilterTest(ProductType product) {
        launchPage(product);
        driver.get(product.getCategoryUrl());
        waitForPopupWatcherToFinish(20000);

        SAKSProductsFilterPage saksProductsFilterPage = new SAKSProductsFilterPage(driver);
        saksProductsFilterPage.scrollUntilProductsVisible(4);
        Integer minPrice = 500;
        Integer maxPrice = 6000;

        saksProductsFilterPage.updatePriceRange(minPrice, maxPrice);

        saksProductsFilterPage.scrollUntilProductsVisible(8);
        List<Double> productCurrentPrices = saksProductsFilterPage.getAllProductCurrentPrices();

        boolean allPricesInRange = productCurrentPrices.stream()
                .allMatch(price -> price >= minPrice && price <= maxPrice);

        Assert.assertTrue(
                allPricesInRange,
                "Some product prices are outside the range for " + product.getName()
        );

    }

    @Test(groups={"regression"}, dataProvider = "productData")
    public void priceFilterOutOfRangeTest(ProductType product) {
        launchPage(product);
        driver.get(product.getCategoryUrl());
        waitForPopupWatcherToFinish(20000);
        //waitForCookieWatcherToFinish(30000);

        SAKSProductsFilterPage saksProductsFilterPage = new SAKSProductsFilterPage(driver);
        saksProductsFilterPage.scrollUntilProductsVisible(4);
        Integer minPrice = 1000000;
        Integer maxPrice = 2000000;

        saksProductsFilterPage.updatePriceRange(minPrice, maxPrice);
        Assert.assertTrue(
                saksProductsFilterPage.isEmptyList(),
                "Some product prices are in the range for " + product.getName()
        );

    }


}
