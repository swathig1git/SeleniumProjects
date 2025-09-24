package org.frameworktest;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

public class BaseTest {
    public WebDriver driver;
    LandingPage landingPage;

    public WebDriver initializeDiver() throws IOException {
        Properties prop= new Properties();
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+
                "//src//main//java//org//resources//GlobalData.properties");
        prop.load(fis);

        String browserName = System.getProperty("browser") != null ? System.getProperty("browser")
                                                                    : prop.getProperty("browser");

        if(browserName.equalsIgnoreCase("chrome")) {
             driver = new ChromeDriver();
        }
        else {
             driver = new FirefoxDriver();
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return driver;
    }

    @BeforeMethod (alwaysRun = true)
    public void launchApplication() throws IOException {
        driver = initializeDiver();
        landingPage = new LandingPage(driver);
        landingPage.goTo();
        return;

    }
    @AfterMethod (alwaysRun = true)
    public void tearDown(){
        driver.quit();
    }

    public String getScreenShot(String testCaseName) throws IOException {
        TakesScreenshot ts = (TakesScreenshot)driver ;
        File source = ts.getScreenshotAs(OutputType.FILE);
        File file = new File(System.getProperty("user.dir") + "//reports//"+
                testCaseName +".png");
        FileUtils.copyFile(source, file);
        return System.getProperty("user.dir") + "//reports//" +".png";
    }


}
