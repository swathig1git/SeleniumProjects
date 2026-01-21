package org.SephoraTests;

import org.POJO.ProductTypeSephora;
import org.config.ConfigDataSephora;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

import java.time.Duration;
import java.util.List;

public class BaseTestSephora {
    public WebDriver driver;

    public WebDriver initializeDriver(){
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return driver;
    }

    @AfterMethod(alwaysRun = true)
    public void teardown(){driver.quit();}

    public void launchPage(ProductTypeSephora productTypeSephora){
        driver.get(productTypeSephora.getCategoryUrl());
    }

    @DataProvider (name = "productDataSephora")
    public Object[][] productTypeSephora(){
        List<ProductTypeSephora> list = ConfigDataSephora.getProductTypeSephora();
        Object[][] data = new Object[list.size()][1];

        for (int i = 0; i < list.size(); i++) {
            data[i][0] = list.get(i);
        }
        return data;

    }
}
