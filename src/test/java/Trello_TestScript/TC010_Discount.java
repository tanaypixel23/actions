package Trello_TestScript;

import commonTest.TestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class TC010_Discount extends TestBase {

    @Test
    public void verifyDiscountfeature(){

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Products"))).click();

        driver.findElements(By.className("btn-add-to-cart"))
                .get(0)
                .click();
        driver.findElement(By.cssSelector("a[href='cart.html']")).click();
        driver.findElement(By.xpath("//button[text()='Checkout']")).click();

        driver.findElement(By.id("full-name")).sendKeys("Tanay Pande");
        driver.findElement(By.id("email")).sendKeys("tanay@test.com");
        driver.findElement(By.id("address")).sendKeys("Bangalore");
        driver.findElement(By.id("city")).sendKeys("Bangalore");
        driver.findElement(By.id("zip-code")).sendKeys("560001");

        double beforeTotal = Double.parseDouble(
                driver.findElement(By.id("total-cost"))
                        .getText()
                        .replace("$", "")
        );

        driver.findElement(
                By.cssSelector("button[data-discount='60']")
        ).click();

        double afterTotal = Double.parseDouble(
                driver.findElement(By.id("total-cost"))
                        .getText()
                        .replace("$", "")
        );

        Assert.assertTrue(afterTotal < beforeTotal,
                "Total cost should decrease after applying discount");

    }

    @Test
    public void multipleDiscount(){

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Products"))).click();

        driver.findElements(By.className("btn-add-to-cart"))
                .get(0)
                .click();
        driver.findElement(By.cssSelector("a[href='cart.html']")).click();
        driver.findElement(By.xpath("//button[text()='Checkout']")).click();

        driver.findElement(By.id("full-name")).sendKeys("Tanay Pande");
        driver.findElement(By.id("email")).sendKeys("tanay@test.com");
        driver.findElement(By.id("address")).sendKeys("Bangalore");
        driver.findElement(By.id("city")).sendKeys("Bangalore");
        driver.findElement(By.id("zip-code")).sendKeys("560001");

        driver.findElement(
                By.cssSelector("button[data-discount='60']")
        ).click();

        driver.findElement(
                By.cssSelector("button[data-discount='50']")
        ).click();

        double afterTotal = Double.parseDouble(
                driver.findElement(By.id("total-cost"))
                        .getText()
                        .replace("$", "")
        );

        Assert.assertTrue(afterTotal > 0,
                "Total cost should be positive after applying multiple discounts");

    }
}
