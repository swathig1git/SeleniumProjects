package org.SAKSTests;
import org.SAKSPages.SAKSHomePage;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.utils.SAKSStringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SAKSHomePageTest extends BaseTestSAKS {
    @BeforeMethod(alwaysRun = true)
    public void launchApplication() throws IOException {
        driver = initializeDiver();
        driver.get("https://ca.saks.com/en-ca/");
        removeOneTrustBanner();
        startPopupWatcher();

    }
    @Test (groups={"regression"}, timeOut = 40000)
    public void womenDesignerDropDownVerification() throws InterruptedException, IOException {

        SAKSHomePage saksHomePage = new SAKSHomePage(driver);
        waitForPopupWatcherToFinish(20000);
        saksHomePage.hover(saksHomePage.getDesignerDropdown());
        saksHomePage.waitForWebElementToAppear(saksHomePage.getFeaturedDesigners());
        Assert.assertTrue(saksHomePage.getFeaturedDesigners().isDisplayed(), "Element is not visible!");
    }

    @Test (groups={"regression"}, timeOut = 40000)
    public void menDesignerDropDownVerification() throws InterruptedException, IOException {

        SAKSHomePage saksHomePage = new SAKSHomePage(driver);
        saksHomePage.waitForWebElementToAppear(saksHomePage.getMen());
        saksHomePage.getMen().click();
        waitForPopupWatcherToFinish(20000);
        saksHomePage.hover(saksHomePage.getDesignerDropdown());
        saksHomePage.waitForWebElementToAppear(saksHomePage.getFeaturedDesigners());
        Assert.assertTrue(saksHomePage.getFeaturedDesigners().isDisplayed(), "Element is not visible!");
    }

    @Test (groups={"regression"}, timeOut = 40000)
    public void womenDesignerBrandPageOpenTest() throws InterruptedException, IOException {

        SAKSHomePage saksHomePage = new SAKSHomePage(driver);
        waitForPopupWatcherToFinish(20000);
        saksHomePage.hover(saksHomePage.getDesignerDropdown());
        saksHomePage.waitForWebElementToAppear(saksHomePage.getFeaturedDesigners());
        List <WebElement> featuredDesigners = saksHomePage.getAllFeaturedDesigners();
        ArrayList<String> designerNames = new ArrayList<>();
        for(WebElement designer: featuredDesigners){
            designerNames.add(designer.getText());
        }
        System.out.println(designerNames);
        for (int i=0; i<10; i++){

            saksHomePage.hover(saksHomePage.getDesignerDropdown());
            saksHomePage.waitForWebElementToAppear(saksHomePage.getFeaturedDesigners());
            saksHomePage.clickOnBrandName(designerNames.get(i));
            String designerURL = SAKSStringUtils.convertBrandNametoURL(designerNames.get(i));
            System.out.println("designerName after = " + designerURL);

            String expectedUrl = "https://ca.saks.com/en-ca/women/designers/" + designerURL;
            saksHomePage.waitForURLToContain(expectedUrl);
            String actualUrl = driver.getCurrentUrl();
            Assert.assertEquals(actualUrl, expectedUrl, "URLs did not match.");
            driver.navigate().back();
            expectedUrl = "https://ca.saks.com/en-ca/";
            saksHomePage.waitForURLToContain(expectedUrl);
            actualUrl = driver.getCurrentUrl();
            Assert.assertEquals(actualUrl, expectedUrl, "URLs did not match.");

        }
    }

}
