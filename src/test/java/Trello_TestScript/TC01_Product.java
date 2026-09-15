package Trello_TestScript;

import commonTest.TestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class TC01_Product extends TestBase {

    public void SearchProduct(String productName) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Products"))).click();

        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchinput")));

        searchBox.clear();
        searchBox.sendKeys(productName);
        boolean found = false;

        List<WebElement> productCards =
                driver.findElements(By.cssSelector("div.product-card"));

        for (WebElement card : productCards) {

            String title = card.findElement(
                            By.cssSelector("h3.product-title"))
                    .getText();

            if (title.toLowerCase().contains(productName.toLowerCase())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found, productName + "product was not found");
    }

    @Test
    public void SearchPartialKeyword() {
        SearchProduct("Eye");
    }

    @Test
    public void SearchFullKeyword() {
        SearchProduct("Mascara");
    }

    /*@Test
    public void dummy() {
        Assert.fail();
    }
    */




}


