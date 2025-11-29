package org.SAKSPages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class SAKSProductsDisplayPage extends AbstractComponentsSAKS {
    @FindBy(xpath="//button[text()='00']")
    WebElement size00;

    @FindBy(xpath="//button[text()='XX-Small']")
    WebElement sizeXXS;

    @FindBy(xpath="//button[@aria-label='X-Small']")
    WebElement sizeXS;

    @FindBy(xpath="//div[@itemprop='brand']")
    WebElement brand;

    @FindBy(xpath="//button[text()='Add to Bag']")
    WebElement addToBag;

    @FindBy(xpath = "//button[text()='Select a size']")
    private WebElement selectSizeButton;

    @FindBy(xpath = "//div[@class='MissingOption__container']")
    private WebElement plsSelectSizeMsg;

    @FindBy(xpath = "//button[@data-testid='decrement-btn']")
    private WebElement decrementButton;

    @FindBy(xpath = "//button[@data-testid='increment-btn']")
    private WebElement incrementButton;

    @FindBy(xpath = "//div[@class='MiniCart__title']")
    private WebElement miniCart;

    @FindBy(xpath = "//div[@id='STATUSCODE_STOCK_QUANTITY_EXCEEDED']")
    private WebElement stockQtyExceeded;

    @FindBy(xpath = "//div[contains(@class,'ProductCarousel__small')]//div[@data-index='4']//img")
    private WebElement productImage;

    @FindBy(xpath = "//div[contains(@class,'ProductCarousel__small')]//button[not(contains(@class, 'slick-arrow'))]")
    private List<WebElement> buttons;

    @FindBy(xpath = "//div[contains(@class,'ProductCarousel__small')]//button[contains(@class,'slick-prev')]")
    private WebElement previousImageButton;

    @FindBy(xpath = "//div[contains(@class,'ProductCarousel__small')]//button[contains(@class,'slick-next')]")
    private WebElement nextImageButton;

    @FindBy(xpath = "//div[contains(@class,'ProductCarousel__small')]//button[not(contains(@class, 'slick-arrow'))]//img")
    private List<WebElement> buttonImages;

    public SAKSProductsDisplayPage(WebDriver driver){
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

 }
