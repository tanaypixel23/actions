package Trello_TestScript;

import commonTest.TestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.openqa.selenium.JavascriptExecutor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Listeners(TC04_Filters.FilterReportListener.class)
public class TC04_Filters extends TestBase {

    // ─── Locators ────────────────────────────────────────────────────────────────
    private static final By PRODUCTS_NAV_LINK   = By.linkText("Products");
    private static final By CATEGORY_BEAUTY     = By.cssSelector("input[name='category'][value='beauty']");
    private static final By CATEGORY_ELECTRONICS = By.cssSelector("input[name='category'][value='electronics']");
    private static final By PRICE_0_50          = By.cssSelector("input[name='price'][value='0-50']");
    private static final By PRICE_50_100        = By.cssSelector("input[name='price'][value='50-100']");
    private static final By PRICE_1600_3200     = By.cssSelector("input[name='price'][value='1600-3200']");
    private static final By RATING_4_AND_ABOVE  = By.cssSelector("input[name='rating'][value='4-5']");
    private static final By APPLY_BUTTON        = By.id("apply-filters");
    private static final By CLEAR_BUTTON        = By.id("clear-filters");
    private static final By PRODUCT_CARDS       = By.cssSelector("div.product-card");
    private static final By PRODUCT_CATEGORY    = By.cssSelector("p.product-category");
    private static final By PRODUCT_PRICE_FINAL = By.cssSelector("span.product-price-final");
    private static final By RATING_VALUE        = By.cssSelector("span.rating-value");
    private static final By PRODUCT_COUNT_TEXT  = By.id("product-count-text");
    private static final By NO_PRODUCTS         = By.cssSelector(".no-products");
    private static final By FILTER_PANEL        = By.cssSelector("aside.filters-sidebar");

    // ─── Helper ───────────────────────────────────────────────────────────────────
    private static final long UI_SELECTION_DELAY_MS = 1000;

    /**
     * Click a filter and pause so the selection is visible in the browser.
     */
    private void selectFilter(By filterLocator) throws InterruptedException {
        driver.findElement(filterLocator).click();
        Thread.sleep(UI_SELECTION_DELAY_MS);
    }

    /**
     * Navigate to Products page and wait until product cards or a "no products"
     * message are present (backend has responded).
     */
    private void goToProductsPage() {
        driver.findElement(PRODUCTS_NAV_LINK).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(d -> !d.findElements(PRODUCT_CARDS).isEmpty()
                || !d.findElements(NO_PRODUCTS).isEmpty());
    }

    /**
     * Click Apply and wait for the products container to refresh.
     */
    private void applyFilters() throws InterruptedException {
        driver.findElement(APPLY_BUTTON).click();
        Thread.sleep(2000);
    }

    /**
     * Parse the final price text like "$29.99" into a double.
     */
    private double parsePrice(String priceText) {
        return Double.parseDouble(priceText.replace("$", "").trim());
    }

    /**
     * Parse the rating text like "(4.3)" into a double.
     */
    private double parseRating(String ratingText) {
        return Double.parseDouble(ratingText.replace("(", "").replace(")", "").trim());
    }

    // ─── TC04_TC1: Filter by Category (Beauty) ───────────────────────────────────

    /**
     * Verify that selecting the Beauty category checkbox and clicking Apply
     * shows only Beauty products and updates the product count correctly.
     *
     * Test Steps:
     *   1. Navigate to Products page.
     *   2. Select the Beauty checkbox under Category.
     *   3. Click Apply.
     * Expected:
     *   - All visible product cards belong to beauty / fragrances / skincare categories.
     *   - Product count text is updated and reflects the filtered count.
     */
    @Test
    public void filterByCategory() throws InterruptedException {
        goToProductsPage();

        // Note initial product count text before filtering
        String initialCountText = driver.findElement(PRODUCT_COUNT_TEXT).getText();

        // Select Beauty category and apply
        selectFilter(CATEGORY_BEAUTY);
        applyFilters();

        List<WebElement> cards = driver.findElements(PRODUCT_CARDS);
        Assert.assertFalse(cards.isEmpty(),
                "No products displayed after applying Beauty category filter.");

        // Every visible card must be a beauty-related category
        for (WebElement card : cards) {
            String category = card.findElement(PRODUCT_CATEGORY).getText().toLowerCase();
            boolean isBeautyRelated = category.contains("beauty")
                    || category.contains("fragrances")
                    || category.contains("skincare");
            Assert.assertTrue(isBeautyRelated,
                    "Non-beauty product found after filter: " + category);
        }

        // Product count text must have changed from the initial value
        String filteredCountText = driver.findElement(PRODUCT_COUNT_TEXT).getText();
        Assert.assertNotEquals(filteredCountText, initialCountText,
                "Product count text was not updated after applying filter.");
    }

    // ─── TC04_TC2: Filter by Price Range ($50–$100) ───────────────────────────────

    /**
     * Verify that selecting the $50-$100 price range shows only products within
     * that price range and updates the product count.
     *
     * Test Steps:
     *   1. Navigate to Products page.
     *   2. Select the $50-$100 price range radio button.
     *   3. Click Apply.
     * Expected:
     *   - All displayed products have a final price between $50 and $100 (inclusive).
     *   - Product count text updates to reflect filtered count.
     */
    @Test
    public void filterByPriceRange() throws InterruptedException {
        goToProductsPage();

        String initialCountText = driver.findElement(PRODUCT_COUNT_TEXT).getText();

        selectFilter(PRICE_50_100);
        applyFilters();

        List<WebElement> cards = driver.findElements(PRODUCT_CARDS);
        Assert.assertFalse(cards.isEmpty(),
                "No products displayed after applying $50-$100 price filter.");

        for (WebElement card : cards) {
            double price = parsePrice(card.findElement(PRODUCT_PRICE_FINAL).getText());
            Assert.assertTrue(price >= 50 && price <= 100,
                    "Product price $" + price + " is outside the $50-$100 range.");
        }

        String filteredCountText = driver.findElement(PRODUCT_COUNT_TEXT).getText();
        Assert.assertNotEquals(filteredCountText, initialCountText,
                "Product count text was not updated after applying price filter.");
    }

    // ─── TC04_TC3: Filter by Rating (4 Stars & Above) ────────────────────────────

    /**
     * Verify that selecting 4 Stars & Above under Rating shows only products
     * with rating >= 4 and updates the product count.
     *
     * Test Steps:
     *   1. Navigate to Products page.
     *   2. Select the 4 Stars & Above radio button under Rating.
     *   3. Click Apply.
     * Expected:
     *   - All displayed products have a rating of 4.0 or above.
     *   - Product count text updates to reflect filtered count.
     */
    @Test
    public void filterByRating() throws InterruptedException {
        goToProductsPage();

        String initialCountText = driver.findElement(PRODUCT_COUNT_TEXT).getText();

        selectFilter(RATING_4_AND_ABOVE);
        applyFilters();

        List<WebElement> cards = driver.findElements(PRODUCT_CARDS);
        Assert.assertFalse(cards.isEmpty(),
                "No products displayed after applying 4 Stars & Above rating filter.");

        for (WebElement card : cards) {
            double rating = parseRating(card.findElement(RATING_VALUE).getText());
            Assert.assertTrue(rating >= 4.0,
                    "Product with rating " + rating + " shown after 4 Stars & Above filter.");
        }

        String filteredCountText = driver.findElement(PRODUCT_COUNT_TEXT).getText();
        Assert.assertNotEquals(filteredCountText, initialCountText,
                "Product count text was not updated after applying rating filter.");
    }

    // ─── TC04_TC4: Apply Multiple Filters Simultaneously ─────────────────────────

    /**
     * Verify that applying Category = Beauty, Price = $0-$50, and Rating = 4 Stars
     * & Above together shows only products satisfying ALL three criteria.
     *
     * Test Steps:
     *   1. Navigate to Products page.
     *   2. Select Beauty under Category.
     *   3. Select $0-$50 under Price Range.
     *   4. Select 4 Stars & Above under Rating.
     *   5. Click Apply.
     * Expected:
     *   - Products shown match all three criteria simultaneously.
     *   - All three filter inputs remain checked after applying.
     *   - Product count text is updated.
     */
    @Test
    public void filterByMultipleCriteria() throws InterruptedException {
        goToProductsPage();

        selectFilter(CATEGORY_BEAUTY);
        selectFilter(PRICE_0_50);
        selectFilter(RATING_4_AND_ABOVE);
        applyFilters();

        // Filters must still be selected after Apply
        Assert.assertTrue(driver.findElement(CATEGORY_BEAUTY).isSelected(),
                "Beauty category checkbox was deselected after applying filters.");
        Assert.assertTrue(driver.findElement(PRICE_0_50).isSelected(),
                "Price $0-$50 radio was deselected after applying filters.");
        Assert.assertTrue(driver.findElement(RATING_4_AND_ABOVE).isSelected(),
                "Rating 4+ radio was deselected after applying filters.");

        List<WebElement> cards = driver.findElements(PRODUCT_CARDS);

        // It is valid for zero results when no products match all three criteria
        for (WebElement card : cards) {
            String category = card.findElement(PRODUCT_CATEGORY).getText().toLowerCase();
            boolean isBeautyRelated = category.contains("beauty")
                    || category.contains("fragrances")
                    || category.contains("skincare");
            Assert.assertTrue(isBeautyRelated,
                    "Non-beauty product shown in combined filter: " + category);

            double price = parsePrice(card.findElement(PRODUCT_PRICE_FINAL).getText());
            Assert.assertTrue(price >= 0 && price <= 50,
                    "Product price $" + price + " is outside $0-$50 in combined filter.");

            double rating = parseRating(card.findElement(RATING_VALUE).getText());
            Assert.assertTrue(rating >= 4.0,
                    "Product rating " + rating + " is below 4.0 in combined filter.");
        }

        // Product count text must be present and not blank
        String countText = driver.findElement(PRODUCT_COUNT_TEXT).getText();
        Assert.assertFalse(countText.isEmpty(),
                "Product count text is empty after applying multiple filters.");
    }

    // ─── TC04_TC5: Clear All Filters ─────────────────────────────────────────────

    /**
     * Verify that clicking Clear All resets all filters and restores the full
     * product listing.
     *
     * Test Steps:
     *   1. Navigate to Products page.
     *   2. Select Beauty, $0-$50, 4 Stars & Above, then Apply.
     *   3. Click Clear All.
     * Expected:
     *   - All checkboxes and radio buttons are deselected.
     *   - Products are no longer restricted; full catalogue is visible again.
     *   - Product count reflects the full (unfiltered) count.
     */
    @Test
    public void clearAllFilters() throws InterruptedException {
        goToProductsPage();

        // Capture full (unfiltered) count text before any filter
        String fullCountText = driver.findElement(PRODUCT_COUNT_TEXT).getText();

        // Apply filters to reduce the product list
        selectFilter(CATEGORY_BEAUTY);
        selectFilter(PRICE_0_50);
        selectFilter(RATING_4_AND_ABOVE);
        applyFilters();

        // Now clear all filters
        driver.findElement(CLEAR_BUTTON).click();
        Thread.sleep(2000);

        // All filter inputs must be unchecked
        Assert.assertFalse(driver.findElement(CATEGORY_BEAUTY).isSelected(),
                "Beauty checkbox is still selected after Clear All.");
        Assert.assertFalse(driver.findElement(PRICE_0_50).isSelected(),
                "Price radio is still selected after Clear All.");
        Assert.assertFalse(driver.findElement(RATING_4_AND_ABOVE).isSelected(),
                "Rating radio is still selected after Clear All.");

        // Products must be visible again
        List<WebElement> cards = driver.findElements(PRODUCT_CARDS);
        Assert.assertFalse(cards.isEmpty(),
                "No products displayed after Clear All.");

        // Product count must be restored to the full count
        String restoredCountText = driver.findElement(PRODUCT_COUNT_TEXT).getText();
        Assert.assertEquals(restoredCountText, fullCountText,
                "Product count was not restored to the full count after Clear All.");
    }

    // ─── TC04_TC6: Product Count Updates After Filter ─────────────────────────────

    /**
     * Verify that the displayed product count updates correctly to match the
     * number of filtered products shown on screen.
     *
     * Test Steps:
     *   1. Navigate to Products page and note the initial count.
     *   2. Select Beauty under Category.
     *   3. Click Apply.
     *   4. Read the updated count text and compare to visible card count.
     * Expected:
     *   - Count text format: "showing X-Y of Z products".
     *   - Z (total filtered) matches the number of cards rendered (on first page).
     *   - Count differs from the initial unfiltered count.
     */
    @Test
    public void productCountUpdatesAfterFilter() throws InterruptedException {
        goToProductsPage();

        String initialCountText = driver.findElement(PRODUCT_COUNT_TEXT).getText();

        selectFilter(CATEGORY_BEAUTY);
        applyFilters();

        String filteredCountText = driver.findElement(PRODUCT_COUNT_TEXT).getText();

        // Count text must have changed
        Assert.assertNotEquals(filteredCountText, initialCountText,
                "Product count text did not change after applying Beauty filter.");

        // Count text must follow the format "showing X-Y of Z products"
        Assert.assertTrue(filteredCountText.toLowerCase().contains("showing"),
                "Product count text format unexpected: " + filteredCountText);

        // The number of visible cards must be <= the per-page window shown in the text
        List<WebElement> cards = driver.findElements(PRODUCT_CARDS);
        Assert.assertFalse(cards.isEmpty(),
                "No product cards visible after applying Beauty filter.");

        // Extract the "X-Y" window from "showing X-Y of Z products"
        // and verify the visible card count matches the window size
        String[] parts = filteredCountText.toLowerCase().replace("showing", "").trim().split(" of ");
        String[] range = parts[0].trim().split("-");
        int displayedFrom = Integer.parseInt(range[0].trim());
        int displayedTo   = Integer.parseInt(range[1].trim());
        int expectedCardCount = displayedTo - displayedFrom + 1;

        Assert.assertEquals(cards.size(), expectedCardCount,
                "Visible card count (" + cards.size() + ") does not match count text range ("
                        + expectedCardCount + ").");
    }

    // ─── Additional filter test cases ─────────────────────────────────────────────

    /**
     * Verify that an impossible Beauty, $1600-$3200, and 4+ rating combination
     * displays the application's empty state and zero-results count.
     */
    @Test
    public void filterCombinationWithNoResults() throws InterruptedException {
        goToProductsPage();

        selectFilter(CATEGORY_BEAUTY);
        selectFilter(PRICE_1600_3200);
        selectFilter(RATING_4_AND_ABOVE);
        applyFilters();

        Assert.assertTrue(driver.findElement(CATEGORY_BEAUTY).isSelected(),
                "Beauty category was not selected.");
        Assert.assertTrue(driver.findElement(PRICE_1600_3200).isSelected(),
                "Price $1600-$3200 was not selected.");
        Assert.assertTrue(driver.findElement(RATING_4_AND_ABOVE).isSelected(),
                "Rating 4+ was not selected.");
        Assert.assertTrue(driver.findElements(PRODUCT_CARDS).isEmpty(),
                "Product cards were displayed for a combination with no expected results.");
        Assert.assertFalse(driver.findElements(NO_PRODUCTS).isEmpty(),
                "No-products empty-state message was not displayed.");
        Assert.assertTrue(driver.findElement(PRODUCT_COUNT_TEXT).getText().toLowerCase()
                        .contains("no product"),
                "Product count does not indicate zero results.");
    }

    /**
     * Verify that price and rating filters remain usable after scrolling down
     * the Products page and that matching products are displayed after Apply.
     */
    @Test
    public void applyFiltersAfterScrolling() throws InterruptedException {
        goToProductsPage();
        selectFilter(PRICE_0_50);
        selectFilter(RATING_4_AND_ABOVE);

        Assert.assertTrue(driver.findElement(PRICE_0_50).isSelected(),
                "Price $0-$50 was not selected after scrolling.");

        Assert.assertTrue(driver.findElement(RATING_4_AND_ABOVE).isSelected(),
                "Rating 4+ was not selected after scrolling.");

        driver.findElement(APPLY_BUTTON).click();
        Thread.sleep(2000);

        for (WebElement card : driver.findElements(PRODUCT_CARDS)) {
            double price = parsePrice(card.findElement(PRODUCT_PRICE_FINAL).getText());
            double rating = parseRating(card.findElement(RATING_VALUE).getText());

            Assert.assertTrue(price >= 0 && price <= 50,
                    "Product price $" + price + " is outside the $0-$50 range.");

            Assert.assertTrue(rating >= 4.0,
                    "Product rating " + rating + " is below 4.0.");
        }
    }
    /**
     * Verify that selecting Beauty and another category displays products from
     * either selected category and excludes unselected categories.
     */
    @Test
    public void filterByMultipleCategories() throws InterruptedException {
        goToProductsPage();

        selectFilter(CATEGORY_BEAUTY);
        selectFilter(CATEGORY_ELECTRONICS);
        applyFilters();

        Assert.assertTrue(driver.findElement(CATEGORY_BEAUTY).isSelected(),
                "Beauty category was not selected.");
        Assert.assertTrue(driver.findElement(CATEGORY_ELECTRONICS).isSelected(),
                "Electronics category was not selected.");

        List<WebElement> cards = driver.findElements(PRODUCT_CARDS);
        Assert.assertFalse(cards.isEmpty(),
                "No products displayed after selecting two categories.");
        for (WebElement card : cards) {
            String category = card.findElement(PRODUCT_CATEGORY).getText().toLowerCase();
            boolean isSelectedCategory = category.contains("beauty")
                    || category.contains("fragrances")
                    || category.contains("skincare")
                    || category.contains("electronics")
                    || category.contains("smartphones")
                    || category.contains("laptops")
                    || category.contains("tablets")
                    || category.contains("mobile-accessories");
            Assert.assertTrue(isSelectedCategory,
                    "Product from an unselected category was displayed: " + category);
        }
        Assert.assertFalse(driver.findElement(PRODUCT_COUNT_TEXT).getText().isEmpty(),
                "Product count was not updated.");
    }

    // ─── HTML Report Listener ─────────────────────────────────────────────────────

    /**
     * TestNG listener that collects pass / fail / skip results for every
     * @Test method in TC04_Filters and writes a self-contained HTML report
     * to  test-output/FilterTestReport.html  when the suite finishes.
     *
     * No extra Maven dependency is needed — ITestListener ships with TestNG.
     */
    public static class FilterReportListener implements ITestListener {

        // ── one row per test result ──────────────────────────────────────────
        private static class Row {
            final String name;
            final String status;       // PASS | FAIL | SKIP
            final String duration;     // e.g. "1.23 s"
            final String detail;       // failure message or empty string

            Row(String name, String status, String duration, String detail) {
                this.name     = name;
                this.status   = status;
                this.duration = duration;
                this.detail   = detail;
            }
        }

        private final List<Row>  rows       = new ArrayList<>();
        private       String     suiteName  = "TC04 Filters";
        private       String     startTime  = "";

        // ── lifecycle hooks ─────────────────────────────────────────────────

        @Override
        public void onStart(ITestContext ctx) {
            suiteName = ctx.getName();
            startTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        @Override
        public void onTestSuccess(ITestResult result) {
            rows.add(new Row(result.getName(), "PASS", durationOf(result), ""));
        }

        @Override
        public void onTestFailure(ITestResult result) {
            String msg = result.getThrowable() != null
                    ? escapeHtml(result.getThrowable().getMessage())
                    : "Unknown failure";
            rows.add(new Row(result.getName(), "FAIL", durationOf(result), msg));
        }

        @Override
        public void onTestSkipped(ITestResult result) {
            rows.add(new Row(result.getName(), "SKIP", durationOf(result), "Test was skipped"));
        }

        @Override
        public void onFinish(ITestContext ctx) {
            writeReport(ctx);
        }

        // ── helpers ─────────────────────────────────────────────────────────

        private String durationOf(ITestResult r) {
            long ms = r.getEndMillis() - r.getStartMillis();
            return String.format("%.2f s", ms / 1000.0);
        }

        private String escapeHtml(String raw) {
            if (raw == null) return "";
            return raw.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
        }

        // ── report writer ────────────────────────────────────────────────────

        private void writeReport(ITestContext ctx) {
            long passed  = rows.stream().filter(r -> r.status.equals("PASS")).count();
            long failed  = rows.stream().filter(r -> r.status.equals("FAIL")).count();
            long skipped = rows.stream().filter(r -> r.status.equals("SKIP")).count();
            long total   = rows.size();

            String finishTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n")
                    .append("<meta charset=\"UTF-8\"/>\n")
                    .append("<title>").append(suiteName).append(" – Test Report</title>\n")
                    .append("<style>\n")
                    .append("  body{font-family:Segoe UI,Arial,sans-serif;margin:0;background:#f4f6f9;color:#1f2328}\n")
                    .append("  header{background:#1a56db;color:#fff;padding:20px 32px}\n")
                    .append("  header h1{margin:0;font-size:1.5rem}\n")
                    .append("  header p{margin:4px 0 0;font-size:.85rem;opacity:.85}\n")
                    .append("  .summary{display:flex;gap:16px;padding:20px 32px}\n")
                    .append("  .card{flex:1;border-radius:8px;padding:16px 20px;text-align:center;color:#fff}\n")
                    .append("  .card h2{margin:0;font-size:2rem}\n")
                    .append("  .card p{margin:4px 0 0;font-size:.85rem}\n")
                    .append("  .total{background:#3b82f6}.pass{background:#16a34a}\n")
                    .append("  .fail{background:#dc2626}.skip{background:#d97706}\n")
                    .append("  .section{padding:0 32px 32px}\n")
                    .append("  table{width:100%;border-collapse:collapse;background:#fff;border-radius:8px;overflow:hidden;box-shadow:0 1px 4px rgba(0,0,0,.08)}\n")
                    .append("  th{background:#1a56db;color:#fff;text-align:left;padding:12px 16px;font-size:.85rem}\n")
                    .append("  td{padding:11px 16px;font-size:.875rem;border-bottom:1px solid #e5e7eb;vertical-align:top}\n")
                    .append("  tr:last-child td{border-bottom:none}\n")
                    .append("  tr:hover td{background:#f0f4ff}\n")
                    .append("  .badge{display:inline-block;padding:3px 10px;border-radius:12px;font-size:.78rem;font-weight:600}\n")
                    .append("  .PASS{background:#dcfce7;color:#15803d}\n")
                    .append("  .FAIL{background:#fee2e2;color:#b91c1c}\n")
                    .append("  .SKIP{background:#fef3c7;color:#b45309}\n")
                    .append("  .detail{font-size:.8rem;color:#6b7280;margin-top:4px;word-break:break-word}\n")
                    .append("  footer{text-align:center;padding:16px;font-size:.78rem;color:#9ca3af;border-top:1px solid #e5e7eb}\n")
                    .append("</style>\n</head>\n<body>\n")

                    // ── header ───────────────────────────────────────────────────
                    .append("<header>\n")
                    .append("  <h1>").append(escapeHtml(suiteName)).append(" – Filter Test Report</h1>\n")
                    .append("  <p>Started: ").append(startTime)
                    .append(" &nbsp;|&nbsp; Finished: ").append(finishTime).append("</p>\n")
                    .append("</header>\n")

                    // ── summary cards ────────────────────────────────────────────
                    .append("<div class=\"summary\">\n")
                    .append("  <div class=\"card total\"><h2>").append(total).append("</h2><p>Total</p></div>\n")
                    .append("  <div class=\"card pass\"><h2>").append(passed).append("</h2><p>Passed</p></div>\n")
                    .append("  <div class=\"card fail\"><h2>").append(failed).append("</h2><p>Failed</p></div>\n")
                    .append("  <div class=\"card skip\"><h2>").append(skipped).append("</h2><p>Skipped</p></div>\n")
                    .append("</div>\n")

                    // ── results table ─────────────────────────────────────────────
                    .append("<div class=\"section\">\n")
                    .append("<table>\n<thead><tr>")
                    .append("<th>#</th><th>Test Method</th><th>Status</th>")
                    .append("<th>Duration</th><th>Details</th>")
                    .append("</tr></thead>\n<tbody>\n");

            int idx = 1;
            for (Row row : rows) {
                sb.append("<tr>\n")
                        .append("  <td>").append(idx++).append("</td>\n")
                        .append("  <td>").append(escapeHtml(row.name)).append("</td>\n")
                        .append("  <td><span class=\"badge ").append(row.status).append("\">")
                        .append(row.status).append("</span></td>\n")
                        .append("  <td>").append(row.duration).append("</td>\n")
                        .append("  <td>")
                        .append(row.detail.isEmpty() ? "–" : "<span class=\"detail\">" + row.detail + "</span>")
                        .append("</td>\n</tr>\n");
            }

            sb.append("</tbody>\n</table>\n</div>\n")
                    .append("<footer>Generated by TC04_Filters &nbsp;|&nbsp; Mini Mart Regression Suite</footer>\n")
                    .append("</body>\n</html>");

            // ── write to disk ─────────────────────────────────────────────
            java.io.File outDir = new java.io.File("test-output");
            if (!outDir.exists()) outDir.mkdirs();
            java.io.File reportFile = new java.io.File(outDir, "FilterTestReport.html");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
                writer.write(sb.toString());
                System.out.println("\n[FilterReport] Report saved → " + reportFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("[FilterReport] Failed to write report: " + e.getMessage());
            }
        }
    }
}
