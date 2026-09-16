package Trello_TestScript;

import commonTest.TestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;

public class TC06_CrossBrowser extends TestBase {

    private static final String BASE_URL     =
            "https://surajkumar-ibm.github.io/Selenium-Miniproject-Application/";
    private static final String CHECKOUT_URL =
            "https://surajkumar-ibm.github.io/Selenium-Miniproject-Application/checkout.html";

    // Locators for all checkout form fields and order summary
    private static final By FIELD_FULL_NAME = By.id("full-name");
    private static final By FIELD_EMAIL     = By.id("email");
    private static final By FIELD_ADDRESS   = By.id("address");
    private static final By FIELD_CITY      = By.id("city");
    private static final By FIELD_ZIP       = By.id("zip-code");
    private static final By ORDER_SUMMARY   = By.cssSelector(".order-summary-checkout");
    // SUBMIT_BUTTON reused as PLACE_ORDER_BTN in mobile tests — single declaration below
    private static final By SUBMIT_BUTTON   = By.id("btn-place-order");

    // Locators for ES module test
    // cart-counter ID confirmed from utils.js: document.getElementById('cart-counter')
    private static final By CART_COUNTER = By.id("cart-counter");
    private static final By ORDER_ITEMS  = By.id("order-items");

    /**
     * localStorage key confirmed from utils.js: localStorage.getItem('miniMartCart')
     * Item structure confirmed from checkout.js: title, price, discountPercentage, quantity
     */
    private static final String CART_STORAGE_KEY = "miniMartCart";
    private static final String CART_LOCALSTORAGE_JSON =
            "[{\"id\":1,\"title\":\"Mascara\",\"price\":9.99,\"discountPercentage\":0,\"quantity\":2}]";

    /**
     * Override TestBase @BeforeMethod — TC06 manages its own drivers per test,
     * so we do NOT want TestBase launching a Chrome window before each test here.
     */

    @BeforeMethod
    public void setupdriver() {
        // intentionally empty — each test below opens its own browser
    }

    /**
     * Override TestBase @AfterMethod — no shared driver to quit here.
     */

    @AfterMethod
    public void teardown() {
        // intentionally empty — each test quits its own driver in a finally block
    }

    /**
     * Verifies checkout form renders correctly on Google Chrome.
     */
    @Test
    public void verifyCheckoutFormOnChrome() {
        WebDriver chromeDriver = new ChromeDriver();
        try {
            chromeDriver.manage().window().maximize();
            chromeDriver.get(CHECKOUT_URL);
            verifyCheckoutFormElements(chromeDriver, "Chrome");
        } finally {
            chromeDriver.quit();
        }
    }

    /**
     * Verifies checkout form renders correctly on Microsoft Edge.
     */
    @Test
    public void verifyCheckoutFormOnEdge() {
        WebDriver edgeDriver = new EdgeDriver();
        try {
            edgeDriver.manage().window().maximize();
            edgeDriver.get(CHECKOUT_URL);
            verifyCheckoutFormElements(edgeDriver, "Edge");
        } finally {
            edgeDriver.quit();
        }
    }

    /**
     * Verifies checkout form renders correctly on Mozilla Firefox.
     * Requires Firefox to be installed on the machine.
     */
    @Test
    public void verifyCheckoutFormOnFirefox() {
        WebDriver firefoxDriver = new FirefoxDriver();
        try {
            firefoxDriver.manage().window().maximize();
            firefoxDriver.get(CHECKOUT_URL);
            verifyCheckoutFormElements(firefoxDriver, "Firefox");
        } finally {
            firefoxDriver.quit();
        }
    }

    // -------------------------------------------------------------------------
    // ES MODULE LOADING TESTS
    // -------------------------------------------------------------------------

    /**
     * Verifies ES module imports load without errors on Chrome.
     * Seeds localStorage with cart data, navigates to checkout.html,
     * checks browser console for JS errors, and verifies cart counter
     * and order summary populate correctly.
     */
    @Test
    public void verifyESModuleLoadingOnChrome() {
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);

        ChromeOptions options = new ChromeOptions();
        options.setCapability("goog:loggingPrefs", logPrefs);

        WebDriver chromeDriver = new ChromeDriver(options);
        try {
            chromeDriver.manage().window().maximize();
            seedCartAndVerifyModules(chromeDriver, "Chrome", true);
        } finally {
            chromeDriver.quit();
        }
    }

    /**
     * Verifies ES module imports load without errors on Edge.
     */
    @Test
    public void verifyESModuleLoadingOnEdge() {
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);

        EdgeOptions options = new EdgeOptions();
        options.setCapability("goog:loggingPrefs", logPrefs);

        WebDriver edgeDriver = new EdgeDriver(options);
        try {
            edgeDriver.manage().window().maximize();
            seedCartAndVerifyModules(edgeDriver, "Edge", true);
        } finally {
            edgeDriver.quit();
        }
    }

    /**
     * Verifies ES module imports load without errors on Firefox.
     * Note: Firefox does not expose browser console logs via WebDriver,
     * so JS error checking is skipped — only DOM-level checks are done.
     */
    @Test
    public void verifyESModuleLoadingOnFirefox() {
        WebDriver firefoxDriver = new FirefoxDriver();
        try {
            firefoxDriver.manage().window().maximize();
            seedCartAndVerifyModules(firefoxDriver, "Firefox", false);
        } finally {
            firefoxDriver.quit();
        }
    }

    /**
     * Seeds localStorage with a cart item, navigates to checkout.html,
     * then verifies:
     *  1. No JS module import/CORS errors in the browser console (Chrome/Edge only)
     *  2. Cart counter element is present and visible
     *  3. Order items section is populated (not showing "Your cart is empty")
     *
     * @param wd           the WebDriver instance for the browser under test
     * @param browserName  label used in assertion failure messages
     * @param checkLogs    true for Chrome/Edge (console logs accessible); false for Firefox
     */
    private void seedCartAndVerifyModules(WebDriver wd, String browserName, boolean checkLogs) {
        JavascriptExecutor js = (JavascriptExecutor) wd;
        WebDriverWait wait = new WebDriverWait(wd, Duration.ofSeconds(15));

        // Step 1: Navigate to BASE_URL first so localStorage is on the right origin
        wd.get(BASE_URL);

        // Step 2: Wait for homepage to fully load before touching localStorage
        wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete' ? 'done' : null;"));

        // Step 3: Seed cart data into localStorage under the correct key
        js.executeScript(
                "localStorage.setItem(arguments[0], arguments[1]);",
                CART_STORAGE_KEY, CART_LOCALSTORAGE_JSON
        );

        // Step 4: Verify the data was actually written before navigating away
        String written = (String) js.executeScript("return localStorage.getItem(arguments[0]);", CART_STORAGE_KEY);
        Assert.assertNotNull(written, "[" + browserName + "] Failed to seed cart data into localStorage");

        // Step 5: Navigate to checkout page — JS modules will now load with cart data
        wd.get(CHECKOUT_URL);

        // Step 6: Wait for page to be fully ready
        wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete' ? 'done' : null;"));

        // Step 7: Wait for #order-items to exist AND not contain the empty-cart message
        // This handles async JS module reads from localStorage
        wait.until(driver -> {
            List<org.openqa.selenium.WebElement> elements = driver.findElements(By.id("order-items"));
            if (elements.isEmpty()) return false;
            String text = elements.get(0).getText();
            return !text.contains("Your cart is empty") && !text.trim().isEmpty();
        });

        // Step 8: Check browser console for JS module import / CORS errors (Chrome & Edge)
        if (checkLogs) {
            List<LogEntry> logs = wd.manage().logs().get(LogType.BROWSER).getAll();
            for (LogEntry entry : logs) {
                String msg = entry.getMessage().toLowerCase();
                boolean isModuleError =
                        msg.contains("syntaxerror") ||
                                msg.contains("import")      ||
                                msg.contains("export")      ||
                                msg.contains("cors")        ||
                                msg.contains("failed to fetch") ||
                                msg.contains("cannot use import");

                Assert.assertFalse(
                        isModuleError,
                        "[" + browserName + "] JS module error found in console: " + entry.getMessage()
                );
            }
            System.out.println("[" + browserName + "] No JS module errors in console.");
        }

        // Step 9: Verify cart counter is visible on the page
        Assert.assertTrue(
                wd.findElement(CART_COUNTER).isDisplayed(),
                "[" + browserName + "] Cart counter is not visible"
        );

        // Step 10: Verify order summary is NOT showing "Your cart is empty"
        // (meaning localStorage data was read successfully by the JS module)
        String orderItemsText = wd.findElement(ORDER_ITEMS).getText();
        Assert.assertFalse(
                orderItemsText.contains("Your cart is empty"),
                "[" + browserName + "] Order summary still shows 'Your cart is empty' — " +
                        "localStorage was not read by the JS module"
        );

        System.out.println("[" + browserName + "] ES module loading verified successfully. " +
                "Order items: " + orderItemsText.trim());
    }

    // -------------------------------------------------------------------------
    // CHECKOUT FORM LAYOUT TESTS
    // -------------------------------------------------------------------------

    /**
     * Shared helper — asserts all required checkout form elements are
     * displayed correctly for a given browser name (used in assertion messages).
     */
    private void verifyCheckoutFormElements(WebDriver wd, String browserName) {

        Assert.assertTrue(
                wd.findElement(FIELD_FULL_NAME).isDisplayed(),
                "[" + browserName + "] Full Name field is not visible"
        );

        Assert.assertTrue(
                wd.findElement(FIELD_EMAIL).isDisplayed(),
                "[" + browserName + "] Email field is not visible"
        );

        Assert.assertTrue(
                wd.findElement(FIELD_ADDRESS).isDisplayed(),
                "[" + browserName + "] Address field is not visible"
        );

        Assert.assertTrue(
                wd.findElement(FIELD_CITY).isDisplayed(),
                "[" + browserName + "] City field is not visible"
        );

        Assert.assertTrue(
                wd.findElement(FIELD_ZIP).isDisplayed(),
                "[" + browserName + "] ZIP field is not visible"
        );

        Assert.assertTrue(
                wd.findElement(ORDER_SUMMARY).isDisplayed(),
                "[" + browserName + "] Order Summary section is not visible"
        );

        Assert.assertTrue(
                wd.findElement(SUBMIT_BUTTON).isDisplayed(),
                "[" + browserName + "] Submit button is not visible"
        );

        System.out.println("[" + browserName + "] Checkout form verified successfully.");
    }

    // -------------------------------------------------------------------------
    // RESPONSIVE GRID LAYOUT TESTS
    // -------------------------------------------------------------------------

    private static final String PRODUCTS_URL =
            "https://surajkumar-ibm.github.io/Selenium-Miniproject-Application/products.html";
    private static final By PRODUCTS_CONTAINER = By.id("products-container");
    private static final By PRODUCT_CARDS      = By.cssSelector("#products-container .product-card");
    private static final By PRODUCT_IMAGES     = By.cssSelector("#products-container .product-image");

    /**
     * Verifies product card grid adapts correctly at Desktop, Tablet, and Mobile
     * viewports on Chrome.
     * CSS breakpoints confirmed from style.css:
     *   ≥768px  : auto-fit/auto-fill minmax(250px,1fr) → 3–4 cols at desktop, 2–3 at tablet
     *   ≤767px  : repeat(2,1fr)     → exactly 2 cols
     *   ≤479px  : repeat(1,1fr)     → 1 col
     */
    @Test
    public void verifyResponsiveGridOnChrome() {
        WebDriver chromeDriver = new ChromeDriver();
        try {
            chromeDriver.manage().window().maximize();
            verifyResponsiveGrid(chromeDriver, "Chrome");
        } finally {
            chromeDriver.quit();
        }
    }

    /**
     * Verifies product card grid adapts correctly at all viewports on Edge.
     */
    @Test
    public void verifyResponsiveGridOnEdge() {
        WebDriver edgeDriver = new EdgeDriver();
        try {
            edgeDriver.manage().window().maximize();
            verifyResponsiveGrid(edgeDriver, "Edge");
        } finally {
            edgeDriver.quit();
        }
    }

    /**
     * Verifies product card grid adapts correctly at all viewports on Firefox.
     */
    @Test
    public void verifyResponsiveGridOnFirefox() {
        WebDriver firefoxDriver = new FirefoxDriver();
        try {
            firefoxDriver.manage().window().maximize();
            verifyResponsiveGrid(firefoxDriver, "Firefox");
        } finally {
            firefoxDriver.quit();
        }
    }

    /**
     * Core helper: resizes the viewport to Desktop → Tablet → Mobile L,
     * loads products at each size, then asserts column count and image integrity.
     */
    // Products per page confirmed from config.js: PRODUCTS_PER_PAGE = 12
    private static final int PRODUCTS_PER_PAGE = 12;

    private void verifyResponsiveGrid(WebDriver wd, String browserName) {
        JavascriptExecutor js = (JavascriptExecutor) wd;
        WebDriverWait wait = new WebDriverWait(wd, Duration.ofSeconds(30));

        // ── DESKTOP 1366×768 ──────────────────────────────────────────────────
        // Set viewport BEFORE navigating so CSS media queries apply from first paint
        wd.manage().window().setSize(new Dimension(1366, 768));
        wd.get(PRODUCTS_URL);
        // Wait until all 12 products per page are fully rendered in the DOM
        wait.until(driver ->
                driver.findElements(PRODUCT_CARDS).size() >= PRODUCTS_PER_PAGE
        );
        // Extra settle time for the grid layout engine to complete
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        int desktopCols = countColumns(wd, js);
        System.out.println("[" + browserName + "] Desktop (1366px) columns: " + desktopCols);
        Assert.assertTrue(
                desktopCols >= 3 && desktopCols <= 5,
                "[" + browserName + "] Desktop: expected 3–5 columns but got " + desktopCols
        );
        assertNoStretchedImages(wd, browserName, "Desktop");

        // ── TABLET 768×1024 ───────────────────────────────────────────────────
        // Resize then reload so media queries re-evaluate from initial load
        wd.manage().window().setSize(new Dimension(768, 1024));
        wd.navigate().refresh();
        wait.until(driver ->
                driver.findElements(PRODUCT_CARDS).size() >= PRODUCTS_PER_PAGE
        );
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        int tabletCols = countColumns(wd, js);
        System.out.println("[" + browserName + "] Tablet (768px) columns: " + tabletCols);
        Assert.assertTrue(
                tabletCols >= 2 && tabletCols <= 3,
                "[" + browserName + "] Tablet: expected 2–3 columns but got " + tabletCols
        );
        assertNoStretchedImages(wd, browserName, "Tablet");
        assertNoCardOverflow(wd, js, browserName, "Tablet");

        // ── MOBILE L 425×812 ──────────────────────────────────────────────────
        wd.manage().window().setSize(new Dimension(425, 812));
        wd.navigate().refresh();
        wait.until(driver ->
                driver.findElements(PRODUCT_CARDS).size() >= PRODUCTS_PER_PAGE
        );
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        int mobileCols = countColumns(wd, js);
        System.out.println("[" + browserName + "] Mobile (425px) columns: " + mobileCols);
        Assert.assertTrue(
                mobileCols >= 1 && mobileCols <= 2,
                "[" + browserName + "] Mobile: expected 1–2 columns but got " + mobileCols
        );
        assertNoStretchedImages(wd, browserName, "Mobile");

        System.out.println("[" + browserName + "] Responsive grid layout verified at all viewports.");
    }

    /**
     * Calculates how many columns are rendered in the product grid by comparing
     * the Y position of consecutive cards. Cards on the same row share the same Y.
     */
    private int countColumns(WebDriver wd, JavascriptExecutor js) {
        // Use the CSS Grid API directly — getComputedStyle().gridTemplateColumns
        // returns the resolved column track sizes (e.g. "250px 250px 250px 250px").
        // Counting the space-separated tokens gives the exact column count the
        // browser computed. This is synchronous and works identically on all browsers.
        String gridTemplateColumns = (String) js.executeScript(
                "var container = document.getElementById('products-container');" +
                        "if (!container) return '0';" +
                        "return window.getComputedStyle(container).gridTemplateColumns;"
        );

        if (gridTemplateColumns == null || gridTemplateColumns.equals("none") || gridTemplateColumns.equals("0")) {
            return 0;
        }

        // gridTemplateColumns looks like "250px 250px 250px 250px" — one token per column
        String[] tracks = gridTemplateColumns.trim().split("\\s+");
        return tracks.length;
    }

    /**
     * Asserts that no product image has a naturalWidth of 0 (broken image)
     * and that no image's rendered width exceeds its container width (stretched).
     */
    private void assertNoStretchedImages(WebDriver wd, String browserName, String viewport) {
        List<WebElement> images = wd.findElements(PRODUCT_IMAGES);
        Assert.assertFalse(images.isEmpty(),
                "[" + browserName + "][" + viewport + "] No product images found on page");

        JavascriptExecutor js = (JavascriptExecutor) wd;
        for (WebElement img : images) {
            double naturalWidth = ((Number) js.executeScript("return arguments[0].naturalWidth;", img)).doubleValue();
            Assert.assertTrue(
                    naturalWidth > 0,
                    "[" + browserName + "][" + viewport + "] Broken image detected (naturalWidth=0)"
            );

            double renderedWidth  = ((Number) js.executeScript("return arguments[0].getBoundingClientRect().width;", img)).doubleValue();
            double containerWidth = ((Number) js.executeScript(
                    "return arguments[0].parentElement.getBoundingClientRect().width;", img)).doubleValue();
            Assert.assertTrue(
                    renderedWidth <= containerWidth + 2.0, // 2px rounding tolerance
                    "[" + browserName + "][" + viewport + "] Image is wider than its container: " +
                            renderedWidth + "px > " + containerWidth + "px"
            );
        }
    }

    /**
     * Asserts that no product card overflows the products-container width.
     */
    private void assertNoCardOverflow(WebDriver wd, JavascriptExecutor js, String browserName, String viewport) {
        WebElement container = wd.findElement(PRODUCTS_CONTAINER);
        double containerRight = ((Number) js.executeScript(
                "return arguments[0].getBoundingClientRect().right;", container)).doubleValue();

        List<WebElement> cards = wd.findElements(PRODUCT_CARDS);
        for (WebElement card : cards) {
            double cardRight = ((Number) js.executeScript(
                    "return arguments[0].getBoundingClientRect().right;", card)).doubleValue();
            Assert.assertTrue(
                    cardRight <= containerRight + 2.0,
                    "[" + browserName + "][" + viewport + "] Card overflows container: " +
                            "card right=" + cardRight + "px, container right=" + containerRight + "px"
            );
        }
    }

    // =========================================================================
    // MOBILE CHECKOUT LAYOUT TESTS  (375×812 — iPhone SE/12)
    // =========================================================================

    // Mobile checkout locators (confirmed from checkout.html + style.css)
    // Note: PLACE_ORDER_BTN reuses SUBMIT_BUTTON — same element id="btn-place-order"
    private static final By PLACE_ORDER_BTN  = SUBMIT_BUTTON;
    private static final By BACK_TO_CART_BTN = By.cssSelector(".btn-back-cart");
    private static final By FORM_INPUTS      = By.cssSelector(".checkout-form .form-group input");

    /**
     * Verifies checkout form + order summary stack correctly at 375px on Chrome.
     */
    @Test
    public void verifyMobileCheckoutLayoutOnChrome() {
        WebDriver chromeDriver = new ChromeDriver();
        try {
            chromeDriver.manage().window().setSize(new Dimension(375, 812));
            verifyMobileCheckoutLayout(chromeDriver, "Chrome");
        } finally {
            chromeDriver.quit();
        }
    }

    /**
     * Verifies checkout form + order summary stack correctly at 375px on Edge.
     */
    @Test
    public void verifyMobileCheckoutLayoutOnEdge() {
        WebDriver edgeDriver = new EdgeDriver();
        try {
            edgeDriver.manage().window().setSize(new Dimension(375, 812));
            verifyMobileCheckoutLayout(edgeDriver, "Edge");
        } finally {
            edgeDriver.quit();
        }
    }

    /**
     * Verifies checkout form + order summary stack correctly at 375px on Firefox.
     */
    @Test
    public void verifyMobileCheckoutLayoutOnFirefox() {
        WebDriver firefoxDriver = new FirefoxDriver();
        try {
            firefoxDriver.manage().window().setSize(new Dimension(375, 812));
            verifyMobileCheckoutLayout(firefoxDriver, "Firefox");
        } finally {
            firefoxDriver.quit();
        }
    }

    /**
     * Core helper for mobile checkout layout verification.
     *
     * Checks (all confirmed against style.css breakpoints):
     *  1. Checkout container is flex-direction:column at ≤985px  → form and summary stack
     *  2. Each form input is full-width (width ≈ viewport width, no horizontal scroll)
     *  3. Order Summary appears BELOW the form (higher Y position)
     *  4. "Place Order" button is fully visible within the viewport
     *  5. "Back to Cart" link is fully visible within the viewport
     *  6. No horizontal scrollbar (scrollWidth ≤ viewport width)
     */
    private void verifyMobileCheckoutLayout(WebDriver wd, String browserName) {
        JavascriptExecutor js = (JavascriptExecutor) wd;
        WebDriverWait wait = new WebDriverWait(wd, Duration.ofSeconds(15));

        // Seed cart so the order summary is populated (not "Your cart is empty")
        wd.get(BASE_URL);
        wait.until(ExpectedConditions.jsReturnsValue(
                "return document.readyState === 'complete' ? 'done' : null;"));
        js.executeScript("localStorage.setItem(arguments[0], arguments[1]);",
                CART_STORAGE_KEY, CART_LOCALSTORAGE_JSON);

        // Navigate to checkout at 375px viewport
        wd.get(CHECKOUT_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(FIELD_FULL_NAME));

        // ── 1. Form and Order Summary stack vertically (flex-direction: column) ──
        // CSS confirmed: @media(max-width:985px) { .checkout-container { flex-direction: column } }
        String flexDirection = (String) js.executeScript(
                "return window.getComputedStyle(document.querySelector('.checkout-container')).flexDirection;"
        );
        Assert.assertEquals(flexDirection, "column",
                "[" + browserName + "] checkout-container should be flex-direction:column at 375px " +
                        "but got: " + flexDirection
        );

        // ── 2. Order Summary appears BELOW the form (higher Y) ────────────────
        double formBottom = ((Number) js.executeScript(
                "return document.querySelector('.checkout-left-side').getBoundingClientRect().bottom;"
        )).doubleValue();
        double summaryTop = ((Number) js.executeScript(
                "return document.querySelector('.checkout-right-side').getBoundingClientRect().top;"
        )).doubleValue();
        Assert.assertTrue(
                summaryTop >= formBottom - 2.0,
                "[" + browserName + "] Order Summary should appear BELOW the form at 375px. " +
                        "Form bottom=" + formBottom + " Summary top=" + summaryTop
        );
        System.out.println("[" + browserName + "] Form bottom=" + formBottom +
                ", Summary top=" + summaryTop + " — stacking verified.");

        // ── 3. All form inputs are full-width (no input wider than viewport) ──
        double viewportWidth = ((Number) js.executeScript("return window.innerWidth;")).doubleValue();
        List<WebElement> inputs = wd.findElements(FORM_INPUTS);
        Assert.assertFalse(inputs.isEmpty(),
                "[" + browserName + "] No form inputs found on checkout page");
        for (WebElement input : inputs) {
            double inputWidth = ((Number) js.executeScript(
                    "return arguments[0].getBoundingClientRect().width;", input
            )).doubleValue();
            Assert.assertTrue(
                    inputWidth <= viewportWidth + 2.0,
                    "[" + browserName + "] Form input is wider than viewport: " +
                            inputWidth + "px > " + viewportWidth + "px"
            );
        }
        System.out.println("[" + browserName + "] All " + inputs.size() +
                " form inputs are within viewport width (" + viewportWidth + "px).");

        // ── 4. No horizontal scrollbar ─────────────────────────────────────────
        double scrollWidth = ((Number) js.executeScript("return document.body.scrollWidth;")).doubleValue();
        Assert.assertTrue(
                scrollWidth <= viewportWidth + 2.0,
                "[" + browserName + "] Horizontal overflow detected: scrollWidth=" +
                        scrollWidth + "px > viewportWidth=" + viewportWidth + "px"
        );
        System.out.println("[" + browserName + "] No horizontal scroll. scrollWidth=" + scrollWidth);

        // ── 5. "Place Order" button is fully visible in viewport ───────────────
        js.executeScript("arguments[0].scrollIntoView({block:'center'});",
                wd.findElement(PLACE_ORDER_BTN));
        try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        double btnRight = ((Number) js.executeScript(
                "return document.getElementById('btn-place-order').getBoundingClientRect().right;"
        )).doubleValue();
        double btnLeft = ((Number) js.executeScript(
                "return document.getElementById('btn-place-order').getBoundingClientRect().left;"
        )).doubleValue();
        Assert.assertTrue(btnLeft >= 0,
                "[" + browserName + "] Place Order button is cut off on the left");
        Assert.assertTrue(btnRight <= viewportWidth + 2.0,
                "[" + browserName + "] Place Order button overflows viewport on the right: " +
                        btnRight + "px > " + viewportWidth + "px");
        System.out.println("[" + browserName + "] Place Order button visible: left=" +
                btnLeft + " right=" + btnRight);

        // ── 6. "Back to Cart" link is fully visible in viewport ─────────────────
        js.executeScript("arguments[0].scrollIntoView({block:'center'});",
                wd.findElement(BACK_TO_CART_BTN));
        try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        double backRight = ((Number) js.executeScript(
                "return document.querySelector('.btn-back-cart').getBoundingClientRect().right;"
        )).doubleValue();
        double backLeft = ((Number) js.executeScript(
                "return document.querySelector('.btn-back-cart').getBoundingClientRect().left;"
        )).doubleValue();
        Assert.assertTrue(backLeft >= 0,
                "[" + browserName + "] Back to Cart button is cut off on the left");
        Assert.assertTrue(backRight <= viewportWidth + 2.0,
                "[" + browserName + "] Back to Cart button overflows viewport: " +
                        backRight + "px > " + viewportWidth + "px");
        System.out.println("[" + browserName + "] Back to Cart button visible: left=" +
                backLeft + " right=" + backRight);

        System.out.println("[" + browserName + "] Mobile checkout layout (375px) fully verified.");
    }
}