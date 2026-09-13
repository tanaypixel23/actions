package commonTest;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;

import java.io.File;
import java.io.IOException;

public class captureScreenshot {

    public static String captureScreenshot(String testName) {

        String folder =
                System.getProperty("user.dir")
                        + "/Screenshots";

        new File(folder).mkdirs();

        TakesScreenshot ts =
                (TakesScreenshot) DriverFactory.getDriver();

        File source =
                ts.getScreenshotAs(OutputType.FILE);

        String destination =
                folder + "/" + testName + "_"
                        + Thread.currentThread().getId()
                        + ".png";

        try {
            FileHandler.copy(source, new File(destination));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return destination;
    }
}