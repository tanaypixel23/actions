package commonTest;

import org.testng.annotations.BeforeSuite;

import java.io.File;

public class beforesuite {

    @BeforeSuite
    public void cleanAllureResults() {

        File folder = new File("target/allure-results");

        if(folder.exists()) {

            for(File file : folder.listFiles()) {
                file.delete();
            }
        }
    }
}
