package org.SAKSTests;

import org.SAKSPages.SAKSProductsFilterPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.testng.Assert.assertTrue;


public class SAKSProductDisplayTest extends BaseTestSAKS {
    @BeforeMethod(alwaysRun = true)
    public void launchApplication() throws IOException {
        driver = initializeDiver();
        driver.get("https://ca.saks.com/en-ca/women/clothing");
        removeOneTrustBanner();
        startPopupWatcher();

    }

    @Test (groups={"regression"}, timeOut = 40000)
    public void productDisplayTest() throws InterruptedException, IOException {

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
