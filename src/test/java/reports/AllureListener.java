package reports;

import io.qameta.allure.Allure;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.FileInputStream;
import java.io.InputStream;

import static commonTest.captureScreenshot.captureScreenshot;

public class AllureListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {

        try {

            String screenshotPath =
                    captureScreenshot(
                            result.getMethod().getMethodName());

            InputStream is =
                    new FileInputStream(screenshotPath);

            Allure.addAttachment(
                    "Failure Screenshot",
                    "image/png",
                    is,
                    ".png");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}