package org.frameworktest;
import org.Data.DataReader;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class SubmitOrder extends BaseTest{
    DataReader dataReader = new DataReader();
    @Test (dataProvider = "getData", groups="Purchase")
    public void orderEndToEnd(HashMap<String , String> input) throws InterruptedException, IOException {
        ProductCatalogue productCatalogue = landingPage.loginApplication(
                input.get("email"), input.get("password"));

        List<WebElement> products = productCatalogue.getProductList();


        WebElement prod = productCatalogue.getProductByName(input.get("productName"));
        productCatalogue.addProductToCart(input.get("productName"));
        CartPage cartPage = productCatalogue.goToCartPage();
        Boolean match = cartPage.verifyProductDisplay(input.get("productName"));
        Assert.assertTrue(match);
        CheckoutPage checkoutPage = cartPage.goToCheckout();
        checkoutPage.selectCountry("india");
        ConfirmPage confirmPage = checkoutPage.actionSubmit();


        String confirmMessage = confirmPage.getConfirmMessage();
        Assert.assertTrue(confirmMessage.equalsIgnoreCase("Thankyou for the order."));

    }

    @DataProvider
    public Object[][] getData() throws IOException {
        List <HashMap<String, String>> data = dataReader.getJsonDataToMap("//src//test//java//org//Data//PurchaseOrder.json");

        return new Object[][] {{data.get(0)}, {data.get(1)}};
    }

//    public Object[][] getData(){
//        HashMap<String, String> map = new HashMap<String, String>();
//        map.put("email","swathi.g12025@gmail.com");
//        map.put("password", "RahulShetty123$" );
//        map.put("productName", "ZARA COAT 3");
//
//        HashMap<String, String> map1 = new HashMap<String, String>();
//        map1.put("email","swathi.abc@gmail.com");
//        map1.put("password", "RahulShetty1$");
//        map1.put("productName", "ADIDAS ORIGINAL");
//        return new Object[][] {{map}, {map1}};
//    }

//    public Object[][] getData(){
//        return new Object[][] { {"swathi.g12025@gmail.com", "RahulShetty123$", "ZARA COAT 3"} ,
//                {"swathi.abc@gmail.com", "RahulShetty1$", "ADIDAS ORIGINAL"} };
//    }
}
