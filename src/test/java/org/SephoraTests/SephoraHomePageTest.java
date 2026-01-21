package org.SephoraTests;

import org.POJO.ProductTypeSephora;
import org.SephoraPages.SephoraHomePage;
import org.config.ConfigData;
import org.config.ConfigDataSephora;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;

public class SephoraHomePageTest extends BaseTestSephora{

    @BeforeSuite
    public void beforeSuite() throws IOException {
        ConfigDataSephora.loadConfigSephora();
    }

    @BeforeMethod(alwaysRun = true)
    public void launchApplication(){
        driver = initializeDriver();
    }

    @Test (dataProvider = "productDataSephora")
    public void verifyHomeAllMenuHover(ProductTypeSephora productTypeSephora) throws InterruptedException {
        launchPage(productTypeSephora);
        SephoraHomePage sephoraHomePage = new SephoraHomePage(driver);
        for(int i=0; i<11; i++){
            sephoraHomePage.hoverOnTopMenuItem(i);


        }


    }
}
