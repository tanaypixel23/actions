package Trello_TestScript;

import commonTest.TestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class TC07_Checkout extends TestBase {

    By[] inputSelectors = {
            By.cssSelector("input[id='full-name']"),
            By.cssSelector("input[id='email']"),
            By.cssSelector("input[id='address']"),
            By.cssSelector("input[id='city']"),
            By.cssSelector("input[id='zip-code']")
    };

    String[] validInformation = {
            "Rahul Sharma",
            "rahul.sharma@example.com",
            "42 MG Road, Indiranagar",
            "Bengaluru",
            "56003"
    };

    public void preCondition() {
        //driver.navigate().to("https://surajkumar-ibm.github.io/Selenium-Miniproject-Application/index.html");

        WebElement addToCartButton = driver.findElement(By.cssSelector(".product-card .btn-add-to-cart"));
        addToCartButton.click();

        WebElement cartButton = driver.findElement(By.cssSelector(".icons li a[href='cart.html']"));
        cartButton.click();

        WebElement checkoutButton = driver.findElement(By.cssSelector(".btn-checkout"));
        checkoutButton.click();
    }

    public void clickOrderButton() {
        WebElement placeOrderButton =
                driver.findElement(By.cssSelector(".btn-place-order"));

        placeOrderButton.click();
    }

    @Test
    public void checkoutPageLoadsCorrectly() {

        //Pre-condition
        //driver.navigate().to("https://surajkumar-ibm.github.io/Selenium-Miniproject-Application/index.html");

        WebElement addToCartButton = driver.findElement(By.cssSelector(".product-card .btn-add-to-cart"));
        addToCartButton.click();

        WebElement cartButton = driver.findElement(By.cssSelector(".icons li a[href='cart.html']"));
        cartButton.click();

        WebElement checkoutButton = driver.findElement(By.cssSelector(".btn-checkout"));
        checkoutButton.click();

        //Tests
        Assert.assertEquals(driver.getCurrentUrl(),"https://surajkumar-ibm.github.io/Selenium-Miniproject-Application/checkout.html");

        By[] elementSelectors = {
                By.cssSelector(".checkout-left-side"),
                By.cssSelector(".checkout-right-side"),
                By.cssSelector(".btn-place-order"),
                By.cssSelector(".btn-back-cart"),
                By.cssSelector("input[id='full-name']"),
                By.cssSelector("input[id='email']"),
                By.cssSelector("input[id='address']"),
                By.cssSelector("input[id='city']"),
                By.cssSelector("input[id='zip-code']"),
        };
        for (By selector : elementSelectors) {
            WebElement element = driver.findElement(selector);
            Assert.assertTrue(element.isDisplayed());
        }
    }

    @Test
    public void acceptsValidInformation() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));;
        preCondition();

        // Test
        for (int i = 0; i < inputSelectors.length; i++) {
            WebElement element = driver.findElement(inputSelectors[i]);
            element.sendKeys(validInformation[i]);
        }

        clickOrderButton();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("success-message")
                )
        );

        WebElement successMessageSection =
                driver.findElement(By.id("success-message"));

        WebElement heading =
                successMessageSection.findElement(By.tagName("h2"));

        Assert.assertEquals(heading.getText(), "Order Placed Successfully!");

        Assert.assertTrue(
                successMessageSection.getAttribute("class").contains("show")
        );

        WebElement continueShoppingButton =
                driver.findElement(By.cssSelector(".btn-continue-shopping"));

        Assert.assertTrue(continueShoppingButton.isDisplayed());
    }

    @Test
    public void navigateBackToCart() {
        preCondition();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        //Test
        WebElement backToCartButton = driver.findElement(By.cssSelector(".btn-back-cart"));
        backToCartButton.click();

        wait.until(ExpectedConditions.urlToBe(
                "https://surajkumar-ibm.github.io/Selenium-Miniproject-Application/cart.html"
        ));

        Assert.assertEquals(
                driver.getCurrentUrl(),
                "https://surajkumar-ibm.github.io/Selenium-Miniproject-Application/cart.html"
        );

    }

    @Test
    public void emailValidation() {
        preCondition();

        //Test
        for (int i = 0; i < inputSelectors.length; i++) {
            WebElement element = driver.findElement(inputSelectors[i]);

            if (validInformation[i].equals("rahul.sharma@example.com"))
                element.sendKeys("rahul.sharmaexamplecom");
            else
                element.sendKeys(validInformation[i]);
        }

        clickOrderButton();

        WebElement emailError = driver.findElement(By.cssSelector("#email-error"));
        Assert.assertTrue(
                emailError.getAttribute("class").contains("show")
        );
    }

    @Test
    public void requiredFieldValidation() {
        preCondition();

        //Test
        WebElement [] validationErrorElement = {
                driver.findElement(By.cssSelector("#name-error")),
                driver.findElement(By.cssSelector("#email-error")),
                driver.findElement(By.cssSelector("#address-error")),
                driver.findElement(By.cssSelector("#city-error")),
                driver.findElement(By.cssSelector("#zip-error"))

        };

        clickOrderButton();

        for(WebElement element : validationErrorElement) {
            Assert.assertTrue(element.isDisplayed());
        }
    }

    @Test
    public void costCalculationCheck() {
        preCondition();

        //Test
        String subtotalValue = driver.findElement(By.id("subtotal")).getText();
        String shippingValue = driver.findElement(By.id("shipping")).getText();
        String taxValue = driver.findElement(By.id("tax")).getText();
        String totalCostValue = driver.findElement(By.id("total-cost")).getText();

        double fetchedSubTotal = Double.parseDouble(subtotalValue.substring(1));
        double fetchedShipping = Double.parseDouble(shippingValue.substring(1));
        double fetchedTax = Double.parseDouble(taxValue.substring(1));
        double fetchedTotalCost = Double.parseDouble(totalCostValue.substring(1));

        double shipping = 10.0;
        double tax = 0.14 * fetchedSubTotal;
        double totalCost = shipping + tax + fetchedSubTotal;

        Assert.assertEquals(fetchedShipping, shipping, 0.01);
        Assert.assertEquals(fetchedTax, tax, 0.01);
        Assert.assertEquals(fetchedTotalCost, totalCost, 0.01);
    }

    @Test
    public void verifyPositiveTotalCost() {
        preCondition();

        //Test
        String totalCostValue = driver.findElement(By.id("total-cost")).getText();
        double fetchedTotalCost = Double.parseDouble(totalCostValue.substring(1));

        Assert.assertTrue(fetchedTotalCost > 0);
    }
}

