package org.frameworktest;

import org.POJO.CartProductDetails;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CartPageEcommerce extends AbstractComponentsEcommerce{

    @FindBy(css="tr td div input")
    List<WebElement> quantity;

    @FindBy(css="#content tbody tr")
    List<WebElement> rows;

    public CartPageEcommerce(WebDriver driver){
        super(driver);
        this.driver = driver;
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
    }
    public int getQuantityOfProductId(String productId) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Wait for all rows to appear
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".table-responsive tbody tr")));

        for (WebElement row : rows) {
            try {
                System.out.println("----- ROW HTML START -----");
                System.out.println(row.getAttribute("outerHTML"));
                System.out.println("----- ROW HTML END -----");

                // Get the product link in the second column
                List<WebElement> productLinks = row.findElements(By.cssSelector("td:nth-child(2) a"));
                if (productLinks.isEmpty()) {
                    continue; // move to next row
                }

                WebElement productLink = productLinks.get(0);
                String href = productLink.getAttribute("href");

                if (href == null || !href.contains("product_id=")) {
                    continue; // skip invalid rows
                }

                String currentProductId = href.split("product_id=")[1];

                if (!currentProductId.equals(productId)) {
                    continue; // skip if not matching
                }

                // Wait for the quantity input to be visible
                List<WebElement> inputs = row.findElements(By.cssSelector("input[name^='quantity']"));
                if (inputs.isEmpty()) {
                    throw new NoSuchElementException("Quantity input not found for productId " + productId);
                }

                WebElement input = wait.until(ExpectedConditions.visibilityOf(inputs.get(0)));

                // Return the quantity as integer
                return Integer.parseInt(input.getAttribute("value").trim());

            } catch (NoSuchElementException | IndexOutOfBoundsException e) {
                // Skip rows that don't have the expected structure
                continue;
            }
        }

        throw new NoSuchElementException("Product ID " + productId + " not found in cart");
    }
    public Map<String, CartProductDetails> getCartProducts() {
        Map<String, CartProductDetails> productMap = new HashMap<>();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        for (WebElement row : rows) {
            System.out.println("----- ROW HTML START -----");
            System.out.println(row.getAttribute("outerHTML"));
            System.out.println("----- ROW HTML END -----");

            List<WebElement> productLinks = row.findElements(By.cssSelector("td:nth-child(2) a"));
            if (productLinks.isEmpty()) {
                continue; // move to next row
            }

            WebElement productLink = productLinks.get(0);
            String href = productLink.getAttribute("href");
            if (href == null || !href.contains("product_id=")) {
                continue; // skip invalid rows
            }

            String productId = href.split("product_id=")[1];

            List<WebElement> quantityInputs = row.findElements(By.cssSelector("input[name^='quantity']"));
            if (quantityInputs.isEmpty()) {
                continue; // no input -> subtotal row, skip
            }

            WebElement input = quantityInputs.get(0);
            int quantity = Integer.parseInt(input.getAttribute("value").trim());
            String name = input.getAttribute("name"); // e.g. quantity[166334]
            String cartId = name.replaceAll("[^0-9]", "");

            productMap.put(productId, new CartProductDetails(productId, cartId, quantity));
        }

        return productMap;
    }


}
