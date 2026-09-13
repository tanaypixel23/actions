package Trello_TestScript;

import commonTest.TestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class TC01_Product extends TestBase {

    @Test
    public void verifySearchProduct() {

        clickUntilLoaded(
                By.linkText("Products")
        );
        WebElement searchBox = driver.findElement(By.xpath("//input[@id='searchinput']"));
        searchBox.sendKeys("mascara");
        boolean found = false;

        List<WebElement> productCards =
                driver.findElements(By.cssSelector("div.product-card"));

        for (WebElement card : productCards) {

            String title = card.findElement(
                            By.cssSelector("h3.product-title"))
                    .getText();

            if (title.toLowerCase().contains("mascara")) {
                found = true;
                break;
            }
        }
        Assert.assertFalse(found, "Mascara product was not found");
    }

    @Test
    public void SearchPartialKeyword() {

        clickUntilLoaded(
                By.linkText("Products")
        );
        WebElement searchBox = driver.findElement(By.xpath("//input[@id='searchinput']"));
        searchBox.sendKeys("eye");

        boolean found = false;

        List<WebElement> productCards =
                driver.findElements(By.cssSelector("div.product-card"));

        for (WebElement card : productCards) {

            String title = card.findElement(
                            By.cssSelector("h3.product-title"))
                    .getText();

            if (title.toLowerCase().contains("eye")) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found, "Eye product was not found");
    }
}


