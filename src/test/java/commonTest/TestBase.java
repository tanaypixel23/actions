package commonTest;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.Parameters;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;

import java.time.Duration;

public class TestBase {

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeMethod
    @Parameters("browser")
    public void setupDriver(@Optional("chrome") String browser) {

        boolean isGitHubActions = System.getenv("GITHUB_ACTIONS") != null;

        switch (browser.toLowerCase()) {

            case "firefox":

                FirefoxOptions firefoxOptions = new FirefoxOptions();

                if (isGitHubActions) {
                    firefoxOptions.addArguments("--headless");
                    firefoxOptions.addArguments("--width=1920");
                    firefoxOptions.addArguments("--height=1080");
                }

                DriverFactory.setDriver(new FirefoxDriver(firefoxOptions));
                break;

            case "edge":

                EdgeOptions edgeOptions = new EdgeOptions();

                if (isGitHubActions) {
                    edgeOptions.addArguments("--headless=new");
                    edgeOptions.addArguments("--no-sandbox");
                    edgeOptions.addArguments("--disable-dev-shm-usage");
                    edgeOptions.addArguments("--disable-gpu");
                    edgeOptions.addArguments("--window-size=1920,1080");
                }

                DriverFactory.setDriver(new EdgeDriver(edgeOptions));
                break;

            case "chrome":
            default:

                ChromeOptions chromeOptions = new ChromeOptions();

                if (isGitHubActions) {
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--window-size=1920,1080");
                }

                DriverFactory.setDriver(new ChromeDriver(chromeOptions));
                break;
        }

        driver = DriverFactory.getDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        if (!isGitHubActions) {
            driver.manage().window().maximize();
        }

        driver.get("https://surajkumar-ibm.github.io/Selenium-Miniproject-Application/");
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
