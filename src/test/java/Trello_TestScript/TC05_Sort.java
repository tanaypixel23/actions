package Trello_TestScript;

import commonTest.TestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TC05_Sort extends TestBase {

    private void navigateToProducts() {
        driver.findElement(By.linkText("Products")).click();

        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("sort-select")
                )
        );
    }



    // Collect prices from ALL pages


    private List<Double> getAllPrices() {

        List<Double> allPrices = new ArrayList<>();

        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );

        while (true) {

            List<WebElement> priceElements =
                    driver.findElements(
                            By.cssSelector(".product-price-final")
                    );

            for (WebElement element : priceElements) {

                String priceText = element.getText()
                        .replace("$", "")
                        .trim();

                allPrices.add(Double.parseDouble(priceText));
            }

            // Get first product currently displayed
            String firstProduct =
                    driver.findElement(
                            By.cssSelector(".product-title")
                    ).getText().trim();

            WebElement nextButton =
                    driver.findElement(By.id("next-btn"));

            String currentPage =
                    driver.findElement(
                            By.cssSelector(".page-number.active")
                    ).getText().trim();

            // Stop if Next is disabled
            String disabled =
                    nextButton.getAttribute("disabled");

            String classes =
                    nextButton.getAttribute("class");

            if (disabled != null ||
                    (classes != null && classes.contains("disabled"))) {
                break;
            }

            nextButton.click();

            // Wait until the first product changes
            wait.until(driver -> {

                String newFirstProduct =
                        driver.findElement(
                                By.cssSelector(".product-title")
                        ).getText().trim();

                return !newFirstProduct.equals(firstProduct);
            });
        }

        return allPrices;
    }



    // Collect names from ALL pages

    private List<String> getAllNames() {

        List<String> allNames = new ArrayList<>();

        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );

        while (true) {

            List<WebElement> nameElements =
                    driver.findElements(
                            By.cssSelector(".product-title")
                    );

            for (WebElement element : nameElements) {
                allNames.add(element.getText().trim());
            }

            // First product on current page
            String firstProduct =
                    driver.findElement(
                            By.cssSelector(".product-title")
                    ).getText().trim();

            WebElement nextButton =
                    driver.findElement(By.id("next-btn"));

            String disabled =
                    nextButton.getAttribute("disabled");

            String classes =
                    nextButton.getAttribute("class");

            if (disabled != null ||
                    (classes != null && classes.contains("disabled"))) {
                break;
            }

            nextButton.click();

            // Wait for product list to change
            wait.until(driver -> {

                String newFirstProduct =
                        driver.findElement(
                                By.cssSelector(".product-title")
                        ).getText().trim();

                return !newFirstProduct.equals(firstProduct);
            });
        }

        return allNames;
    }


    // Collect ratings from ALL pages


    private List<Double> getAllRatings() {

        List<Double> allRatings = new ArrayList<>();

        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );

        while (true) {

            List<WebElement> ratingElements =
                    driver.findElements(
                            By.cssSelector(".rating-value")
                    );

            for (WebElement element : ratingElements) {

                String ratingText =
                        element.getText().trim();

                /*
                 * Example:
                 * (5.0)
                 * (4.4)
                 * (2.6)
                 */

                String numericRating =
                        ratingText.replaceAll("[^0-9.]", "");

                allRatings.add(
                        Double.parseDouble(numericRating)
                );
            }

            // First product on current page
            String firstProduct =
                    driver.findElement(
                            By.cssSelector(".product-title")
                    ).getText().trim();

            WebElement nextButton =
                    driver.findElement(By.id("next-btn"));

            String disabled =
                    nextButton.getAttribute("disabled");

            String classes =
                    nextButton.getAttribute("class");

            if (disabled != null ||
                    (classes != null && classes.contains("disabled"))) {
                break;
            }

            nextButton.click();

            // Wait for product list to change
            wait.until(driver -> {

                String newFirstProduct =
                        driver.findElement(
                                By.cssSelector(".product-title")
                        ).getText().trim();

                return !newFirstProduct.equals(firstProduct);
            });
        }

        return allRatings;
    }



    // 1. Price: Low to High


    @Test(priority = 1)
    public void sortByPriceLowToHigh() {

        navigateToProducts();

        Select sortDropdown =
                new Select(
                        driver.findElement(By.id("sort-select"))
                );

        sortDropdown.selectByValue("price-low-high");

        List<Double> actualPrices =
                getAllPrices();

        List<Double> expectedPrices =
                new ArrayList<>(actualPrices);

        Collections.sort(expectedPrices);

        Assert.assertEquals(
                actualPrices,
                expectedPrices,
                "Products are NOT sorted from low to high"
        );
    }


    // 2. Price: High to Low


    @Test(priority = 2)
    public void sortByPriceHighToLow() {

        navigateToProducts();

        Select sortDropdown =
                new Select(
                        driver.findElement(By.id("sort-select"))
                );

        sortDropdown.selectByValue("price-high-low");

        List<Double> actualPrices =
                getAllPrices();

        List<Double> expectedPrices =
                new ArrayList<>(actualPrices);

        expectedPrices.sort(Collections.reverseOrder());

        Assert.assertEquals(
                actualPrices,
                expectedPrices,
                "Products are NOT sorted from high to low"
        );
    }



    // 3. Name: A to Z


    @Test(priority = 3)
    public void sortByNameAToZ() {

        navigateToProducts();

        Select sortDropdown =
                new Select(
                        driver.findElement(By.id("sort-select"))
                );

        sortDropdown.selectByValue("name-a-z");

        List<String> actualNames =
                getAllNames();

        List<String> expectedNames =
                new ArrayList<>(actualNames);

        // Ignore upper/lower case differences
        expectedNames.sort(String.CASE_INSENSITIVE_ORDER);

        Assert.assertEquals(
                actualNames,
                expectedNames,
                "Products are NOT sorted from A to Z"
        );
    }



    // 4. Name: Z to A


    @Test(priority = 4)
    public void sortByNameZToA() {

        navigateToProducts();

        Select sortDropdown =
                new Select(
                        driver.findElement(By.id("sort-select"))
                );

        sortDropdown.selectByValue("name-z-a");

        List<String> actualNames =
                getAllNames();

        List<String> expectedNames =
                new ArrayList<>(actualNames);

        // Ignore upper/lower case differences
        expectedNames.sort(
                String.CASE_INSENSITIVE_ORDER.reversed()
        );

        Assert.assertEquals(
                actualNames,
                expectedNames,
                "Products are NOT sorted from Z to A"
        );
    }



    // 5. Rating: High to Low


    @Test(priority = 5)
    public void sortByRatingHighToLow() {

        navigateToProducts();

        Select sortDropdown =
                new Select(
                        driver.findElement(By.id("sort-select"))
                );

        sortDropdown.selectByValue("rating-high-low");

        List<Double> actualRatings =
                getAllRatings();

        List<Double> expectedRatings =
                new ArrayList<>(actualRatings);

        expectedRatings.sort(
                Collections.reverseOrder()
        );

        Assert.assertEquals(
                actualRatings,
                expectedRatings,
                "Products are NOT sorted from high to low based on rating"
        );
    }
}