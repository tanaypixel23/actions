package commonTest;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.*;

public class TestBase {

    protected WebDriver driver;

    @Parameters("browser")
    @BeforeMethod
    public void setupDriver(@Optional("chrome") String browser) {

        switch (browser.toLowerCase()) {
            case "firefox":
                DriverFactory.setDriver(new FirefoxDriver());
                break;

            case "edge":
                DriverFactory.setDriver(new EdgeDriver());
                break;

            default:
                DriverFactory.setDriver(new ChromeDriver());
        }

        driver = DriverFactory.getDriver();

        driver.manage().window().maximize();
        driver.get(
                "https://surajkumar-ibm.github.io/Selenium-Miniproject-Application/"
        );
    }

    @AfterMethod
    public void tearDown() {
        DriverFactory.quitDriver();
    }


    @Attachment(value = "Failure Screenshot",
            type = "image/png")
    public byte[] takeScreenshot() {

        return ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BYTES);
    }

}
