package commonTest;

import io.qameta.allure.Attachment;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.io.FileHandler;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;

public class TestBase {
    public static WebDriver driver;
    public static File website;

    @BeforeMethod
    public void setupdriver() throws InterruptedException {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://surajkumar-ibm.github.io/Selenium-Miniproject-Application/");
    }

    @Attachment(value = "Failure Screenshot",
            type = "image/png")
    public byte[] takeScreenshot() {

        return ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BYTES);
    }

    @BeforeSuite
    public void cleanAllureResults() {

        File folder = new File("target/allure-results");

        if(folder.exists()) {

            for(File file : folder.listFiles()) {
                file.delete();
            }
        }
    }

    @AfterMethod
    public void teardown(){
        driver.quit();
    }

    public static void clickUntilLoaded(By clickLocator) {

        for (int i = 0; i < 5; i++) {

            driver.findElement(clickLocator).click();

            try {
                Thread.sleep(2000);

                if (driver.findElements(By.cssSelector(".no-products")).isEmpty()) {
                    return; // Success
                }

                System.out.println("Backend did not load, retrying again...");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        throw new RuntimeException("Backend did not load after 5 attempts.");
    }

    public String captureScreenshot(String testName) {

        String folder =
                System.getProperty("user.dir")
                        + "/Screenshots";

        new File(folder).mkdirs();

        TakesScreenshot ts = (TakesScreenshot) driver;

        File source =
                ts.getScreenshotAs(OutputType.FILE);

        String destination =
                folder + "/" + testName + ".png";

        try {
            FileHandler.copy(source,
                    new File(destination));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return destination;
    }

}
