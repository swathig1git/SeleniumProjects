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
import static org.utils.SAKSVerifyUtils.verifyBrowseByButtons;


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
//        System.out.println("Running test for: " + product.getName());
//        System.out.println("URL: " + product.getCategoryUrl());
//        System.out.println("Filters: " + product.getFilterList());
//        System.out.println("Browse By: " + product.getBrowseByList());

        SAKSProductsFilterPage saksProductsFilterPage = new SAKSProductsFilterPage(driver);
        saksProductsFilterPage.scrollUntilProductsVisible(4);
        boolean result = verifyBrowseByButtons(driver, saksProductsFilterPage.getBrowseByButtons(), product.getBrowseByList());
        Assert.assertTrue(result, "Browse By buttons validation failed for " + product.getName());
    }


    @Test (groups={"regression"}, timeOut = 40000)
    public void productDesignerFilterTest() throws InterruptedException, IOException {

        SAKSProductsFilterPage saksProductsFilterPage = new SAKSProductsFilterPage(driver);
        //waitForPopupWatcherToFinish(20000);
        String designerName = "16Arlington";
        saksProductsFilterPage.selectDesigner(designerName);


        saksProductsFilterPage.waitForURLToContain("brand");
        String actualUrl = driver.getCurrentUrl();
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("brand"));

        // Scroll until 40 products visible
        saksProductsFilterPage.scrollUntilProductsVisible(40);

        // Get ALL visible designer names
        List<String> designerNames = saksProductsFilterPage.getVisibleDesignerNames();

        // Assert EVERY one is the correct designer
        Assert.assertTrue(designerNames.stream().allMatch(name -> name.equals(designerName)),
                "Not all products are by " + designerName + ". Found: " + designerNames);

        // Sanity check: Did we load enough?
        Assert.assertTrue(designerNames.size() >= 20,
                "Expected at least 20 products for reliable verification, but only " + designerNames.size() + " loaded");


            Thread.sleep(5000);

    }

}
