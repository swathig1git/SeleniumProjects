package org.SAKSTests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.POJO.ProductBrand;
import org.POJO.ProductType;
import org.config.ConfigData;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class BaseTestSAKS {
    public WebDriver driver;
    private Thread popupWatcherThread;
    private Thread cookieWatcherThread;
    private AtomicBoolean stopPopUpWatcher = new AtomicBoolean(false);
    private AtomicBoolean stopCookieWatcher = new AtomicBoolean(false);

    public WebDriver initializeDiver() throws IOException {
        Properties prop= new Properties();
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+
                "//src//main//java//org//resources//GlobalData.properties");
        prop.load(fis);

        String browserName = System.getProperty("browser") != null ? System.getProperty("browser")
                                                                    : prop.getProperty("browser");

        if(browserName.equalsIgnoreCase("chrome")) {

             driver = new ChromeDriver();
        } else if (browserName.equalsIgnoreCase("Headless")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("headless");
            driver = new ChromeDriver(options);

        } else {
             driver = new FirefoxDriver();
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return driver;
    }
    @BeforeSuite
    public void beforeSuite() throws IOException {
        ConfigData.loadConfig();
    }

    @DataProvider(name = "productData")
    public Object[][] provideProductData() {
        List<ProductType> list = ConfigData.getProductTypes();
        Object[][] data = new Object[list.size()][1];

        for (int i = 0; i < list.size(); i++) {
            data[i][0] = list.get(i);
        }
        return data;
    }


    @AfterMethod(alwaysRun = true)
    public void tearDown(){
        driver.quit();
    }


    public void removeOneTrustBanner() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("""
        const removeBanner = () => {
            ['#onetrust-consent-sdk', '#onetrust-banner-sdk', '#onetrust-pc-sdk', 
             '.onetrust-pc-dark-filter', '#ot-sdk-cookie-policy'].forEach(sel => {
                document.querySelectorAll(sel).forEach(el => el.remove());
            });
        };
        removeBanner();
        new MutationObserver(removeBanner).observe(document.body || document, {
            childList: true, subtree: true
        });
        """);
    }

    public void startPopupWatcher() {
        stopPopUpWatcher.set(false); // allow watcher to run

        popupWatcherThread = new Thread(() -> {
            while (!stopPopUpWatcher.get()) {
                try {
                    WebElement popupBtn = driver.findElement(
                            By.xpath("//button[text()='SHOP SAKS CANADA']")
                    );

                    if (popupBtn.isDisplayed()) {
                        popupBtn.click();
                        stopPopUpWatcher.set(true);
                        return;
                    }


                    Thread.sleep(200); // similar to Playwright polling

                } catch (Exception ignored) {
                    // popup not present yet
                }

                // If browser is closed, stop watcher
                try {
                    driver.getTitle();
                } catch (Exception e) {
                    stopPopUpWatcher.set(true);
                    return;
                }
            }
        });

        popupWatcherThread.start();
    }

    public void waitForPopupWatcherToFinish(long timeoutMillis) {
        long start = System.currentTimeMillis();

        while (!stopPopUpWatcher.get()) {
            if (System.currentTimeMillis() - start > timeoutMillis) {
                throw new RuntimeException("Timed out waiting for popup watcher to finish");
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }
    }

    public void startCookieWatcher() {
        stopCookieWatcher.set(false); // allow watcher to run

        cookieWatcherThread = new Thread(() -> {
            while (!stopCookieWatcher.get()) {
                try {
                    WebElement rejectCookieButton = driver.findElement(
                            By.xpath("//button[@id='onetrust-reject-all-handler']")
                    );

                    if (rejectCookieButton.isDisplayed()) {
                        rejectCookieButton.click();
                        stopCookieWatcher.set(true);
                        return;
                    }


                    Thread.sleep(200); // similar to Playwright polling

                } catch (Exception ignored) {
                    // popup not present yet
                }

                // If browser is closed, stop watcher
                try {
                    driver.getTitle();
                } catch (Exception e) {
                    stopPopUpWatcher.set(true);
                    return;
                }
            }
        });

        cookieWatcherThread.start();
    }

    public void waitForCookieWatcherToFinish(long timeoutMillis) {
        long start = System.currentTimeMillis();

        while (!stopCookieWatcher.get()) {
            if (System.currentTimeMillis() - start > timeoutMillis) {
                throw new RuntimeException("Timed out waiting for popup watcher to finish");
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }
    }



    public void launchPage(ProductType productType){
        driver.get(productType.getCategoryUrl());
        startPopupWatcher();
        startCookieWatcher();
    }
}
