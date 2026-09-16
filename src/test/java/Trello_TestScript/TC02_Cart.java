package Trello_TestScript;

import commonTest.TestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.List;

public class TC02_Cart extends TestBase {

    SoftAssert softAssert=new SoftAssert();


    public void navigateToProducts() {
        driver.findElement(By.linkText("Products")).click();
    }

    public void navigateToCart() {
        driver.findElement(By.linkText("Cart")).click();
    }

    @Test
    public void addSingleProductToCart() throws InterruptedException {
        navigateToProducts();
        List<WebElement> addButtons =
                driver.findElements(By.cssSelector(".btn-add-to-cart"));

        System.out.println("Add to cart buttons: " + addButtons.size());

        addButtons.get(0).click();

        Thread.sleep(1000);

        System.out.println("Current URL: " + driver.getCurrentUrl());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement counter = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("cart-counter"))
        );

        Assert.assertEquals(counter.getText(), "1");
    }
    @Test
    public void addingMultipleProduct(){
        navigateToProducts();
        List<WebElement> addButtons =
                driver.findElements(By.cssSelector(".btn-add-to-cart"));

        System.out.println("Add to cart buttons: " + addButtons.size());
        addButtons.get(0).click();
        addButtons.get(7).click();
        addButtons.get(2).click();
        addButtons.get(3).click();
        addButtons.get(1).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement counter = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("cart-counter"))
        );
        System.out.println(counter.getText());
        Assert.assertTrue(Integer.parseInt(counter.getText()) > 1);
    }

    @Test
    public void addSameProducttwice() throws InterruptedException {
        navigateToProducts();
        List<WebElement> addButtons =
                driver.findElements(By.cssSelector(".btn-add-to-cart"));

        System.out.println("Add to cart buttons: " + addButtons.size());

        addButtons.get(0).click();
        WebElement cart = driver.findElement(By.linkText("Cart"));
        cart.click();
        Thread.sleep(2000);
        WebElement increase = driver.findElement(By.cssSelector(".quantity-btn.increase"));
        increase.click();
        Thread.sleep(2000);

        WebElement counter = driver.findElement(By.id("cart-counter"));

        Assert.assertEquals(counter.getText().trim(), "2");


    }
    @Test
    public void verifyCartAfterRefreshing() throws InterruptedException {
        navigateToProducts();
        List<WebElement> addButtons = driver.findElements(By.cssSelector(".btn-add-to-cart"));
        addButtons.get(0).click();

        Thread.sleep(1000);
        WebElement cart = driver.findElement(By.linkText("Cart"));
        cart.click();

        WebElement cartDetail = driver.findElement(By.className("cart-item-details"));
        String cartItemDetail = cartDetail.getText();
        System.out.println(cartItemDetail);
        Thread.sleep(1000);
        driver.navigate().refresh();
        Thread.sleep(1000);

        Assert.assertEquals(cartItemDetail,driver.findElement(By.className("cart-item-details")).getText());
    }

    @Test
    public void increaseAndDecreaseProdQuality() throws InterruptedException {
        navigateToProducts();
        List<WebElement> addButtons =
                driver.findElements(By.cssSelector(".btn-add-to-cart"));

        System.out.println("Add to cart buttons: " + addButtons.size());

        addButtons.get(0).click();
        WebElement cart = driver.findElement(By.linkText("Cart"));
        cart.click();
        Thread.sleep(2000);
        WebElement increase = driver.findElement(By.cssSelector(".quantity-btn.increase"));

        WebElement counter = driver.findElement(By.id("cart-counter"));

        increase.click();
        Thread.sleep(1000);
        Assert.assertTrue(Integer.parseInt(counter.getText()) > 1);
        WebElement decrease = driver.findElement(By.cssSelector(".quantity-btn.decrease"));
        decrease.click();
        Thread.sleep(1000);
        Assert.assertEquals(Integer.parseInt(counter.getText()), 1);
    }

    @Test
    public void verifyCartTotal() throws InterruptedException {
        navigateToProducts();
        // Get all Add to Cart buttons
        List<WebElement> addButtons =
                driver.findElements(By.cssSelector(".btn-add-to-cart"));

        // Add first two products
        addButtons.get(0).click();
        addButtons.get(1).click();

        // Open Cart
        driver.findElement(By.linkText("Cart")).click();

        Thread.sleep(1000);

        // Get product prices from cart
        List<WebElement> prices =
                driver.findElements(By.className("cart-item-price"));

        System.out.println(prices.get(0).getText());
        double price1 = Double.parseDouble( prices.get(0).getText().replace("$", "") );
        double price2 = Double.parseDouble( prices.get(1).getText().replace("$", "") );

        List<WebElement> increase_button = driver.findElements(By.cssSelector(".quantity-btn.increase"));

        increase_button.get(0).click();
        Thread.sleep(1000);

        double expectedSubtotal = (price1 * 2) + price2;
        double actualSubtotal = Double.parseDouble( driver.findElement(By.id("total")) .getText() .replace("$", "") );

        Assert.assertEquals( actualSubtotal, expectedSubtotal, 0.01, "Subtotal is incorrect" );

        double tax = Double.parseDouble( driver.findElement(By.id("tax")) .getText() .replace("$", "") );
        double shipping = Double.parseDouble( driver.findElement(By.id("shipping")) .getText() .replace("$", "") );

        double expectedTotal = expectedSubtotal + tax + shipping;
        double actualTotal = Double.parseDouble( driver.findElement(By.id("total-cost")) .getText() .replace("$", "") );

        Assert.assertEquals( actualTotal, expectedTotal, 0.01, "Final cart total is incorrect" );

    }

    @Test
    public void verifyEmptyCartState(){

        //driver.get("https://ibrahim2656.github.io/E-commerce-Site/cart.html");
        navigateToCart();
        // we want selenium to execut javascript inside the webpage localStorage.removeItem("miniMartCart") is js not selenium
        JavascriptExecutor js=(JavascriptExecutor) driver;
        js.executeScript("localStorage.removeItem('miniMartCart');");
        driver.navigate().refresh();
        //verify empty cart message


        // Assert.assertEquals(driver.findElement(By.id("#cart-item-counter")).getText(),"Your Cart is empty");
        //Verify item count
        softAssert.assertEquals(driver.findElement(By.id("cart-item-counter")).getText(),"0 items");
        softAssert.assertEquals(driver.findElement(By.id("total")).getText(),"$0.00");
        softAssert.assertEquals(driver.findElement(By.id("shipping")).getText(),"$0.00");
        softAssert.assertEquals(driver.findElement(By.id("tax")).getText(),"$0.00");
        softAssert.assertEquals(driver.findElement(By.id("total-cost")).getText(),"$0.00");
        //verify checkout is disabled
        softAssert.assertFalse(driver.findElement(By.id("btn-checkout")).isEnabled());
        softAssert.assertAll();
    }
    @Test
    public void verifyProductCanBeAddedToCart(){
        //driver.get("https://ibrahim2656.github.io/E-commerce-Site/products.html");
        navigateToProducts();
        WebDriverWait wait=new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement button=wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-to-cart")));
        button.click();
    }
    @Test
    public void verifyContinueShoppingNavigation(){
        //driver.get("https://ibrahim2656.github.io/E-commerce-Site/cart.html");
        navigateToCart();
        driver.findElement(By.linkText("Continue Shopping")).click();
        String currentUrl= driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("products.html"));


    }
    @Test
    public void VerifyStartShoppingNavigationFromEmptyCart(){
        navigateToCart();
        JavascriptExecutor js=(JavascriptExecutor) driver;
        js.executeScript("localStorage.removeItem('minMartCart');");
        driver.navigate().refresh();

    }
    @Test
    public void VerifyCheckoutwithProductsinCart(){
        navigateToProducts();
        WebDriverWait wait=new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement button=wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-to-cart")));
        button.click();
        navigateToCart();
        WebElement checkout=driver.findElement(By.id("btn-checkout"));
        Assert.assertTrue(checkout.isEnabled());
        checkout.click();
        String actual=driver.getCurrentUrl();
        Assert.assertTrue(actual.contains("checkout.html"));

    }
    /*@Test
    public void VerifyOrderSummaryCalculation(){
        navigateToProducts();
        WebDriverWait wait= new WebDriverWait(driver,Duration.ofSeconds(10));
        WebElement productCard=wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-card")));
        String priceText=productCard.findElement(By.cssSelector(".product-price-final")).getText();
        //String discountText=productCard.findElement(By.cssSelector(".product-card-discount")).getText();
        double price=Double.parseDouble(priceText.replace("$",""));
        WebElement addToCart=productCard.findElement(By.cssSelector(".btn-add-to-cart"));
        addToCart.click();
        driver.navigate().to("https://ibrahim2656.github.io/E-commerce-Site/cart.html");
        String quantityText=driver.findElement(By.cssSelector(".quantity")).getText();
        int quantity=Integer.parseInt(quantityText);
        double expectedSubtotal=price*quantity;
        double taxRate=0.14;
        double expectedTax=expectedSubtotal*taxRate;
        double shipping=10.00;
        double expectedTotal=expectedSubtotal+expectedTax+shipping;
        String totalText=driver.findElement(By.id("total-cost")).getText();
        double actualTotal=Double.parseDouble(totalText.replace("$",""));
        Assert.assertEquals(actualTotal,expectedTotal,0.01);


    }
    */
    @Test
    public void VerifyDiscountedProductPriceIsDisplayedCorrectly(){
        navigateToProducts();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement productCard = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".product-card")));

        String discountText = productCard.findElement(
                        By.cssSelector(".product-card-discount"))
                .getText();

        String originalText = productCard.findElement(
                        By.cssSelector(".product-card-previous-price"))
                .getText();

        String finalPriceText = productCard.findElement(
                        By.cssSelector(".product-price-final"))
                .getText();


        double discount = Double.parseDouble(discountText.replace("%", "")
                .replace("-", "").trim());

        double originalPrice = Double.parseDouble(
                originalText.replace("$", "").trim());

        double actualFinalPrice = Double.parseDouble(
                finalPriceText.replace("$", "").trim());

        double expectedFinalPrice =
                originalPrice * (1 - discount / 100.0);

        Assert.assertEquals(
                actualFinalPrice,
                expectedFinalPrice,
                0.01);


    }
}
