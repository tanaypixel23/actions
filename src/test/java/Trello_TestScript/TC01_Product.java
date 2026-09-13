package Trello_TestScript;

import commonTest.TestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class TC01_Product extends TestBase {

    public void SearchProduct(String productName) {

        driver.findElement(By.linkText("Products")).click();

        WebElement searchBox = driver.findElement(By.xpath("//input[@id='searchinput']"));
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

    @Test
    public void dummy() {
        Assert.fail();
    }
}


