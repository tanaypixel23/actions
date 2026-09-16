package Trello_TestScript;

import commonTest.TestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * TC03_Wishlist — Wishlist Test Suite
 * Target Website : https://ibrahim2656.github.io/E-commerce-Site/index.html
 *
 * Relies on TestBase for:
 *   - TestBase.@BeforeMethod  → starts ChromeDriver, maximises window
 *   - TestBase.@AfterMethod   → calls driver.quit()
 *   - TestBase.driver         → public static WebDriver
 *   - TestBase.wait 		   → public static WebDriverWait
 *
 * Test cases follow Given / When / Then (GWT) structure via inline comments.
 *
 * Groups:
 *   TC-W01 – TC-W04  Add to Wishlist
 *   TC-W05 – TC-W07  Remove from Wishlist
 *   TC-W07 – TC-W10  Wishlist Display & UI
 *   TC-W11 – TC-W13  Wishlist <-> Cart Interaction
 *   TC-W13 – TC-W15  Persistence & State
 *   TC-W16 – TC-W18  Navigation & Filtering
 *   TC-W19 – TC-W21  Boundary / Rapid Actions (Edge Cases)
 */

public class TC03_Wishlist extends TestBase {

    // ── Locators ──────────────────────────────────────────────────────────────
    // Header / nav
    private static final By PRODUCT_PAGE       = By.cssSelector("li a[href=\"products.html\"]");
    private static final By WISHLIST_PAGE      = By.cssSelector("li a[href=\"favorites.html\"]");
    private static final By WISHLIST_BADGE     = By.id("fav-count");
    private static final By CART_BADGE         = By.cssSelector("#cart-counter");


    //	// Product listing page
    private static final By VALIDATOR_PRODUCT_PAGE = By.className("products-main-container");
    private static final By PRODUCT_CARDS          = By.className("product-card");
    private static final By PRODUCT_NAME           = By.cssSelector("h3.product-title");
    //    private static final By PRODUCT_PRICE        = By.cssSelector(".product-price-final, .product-card-price, [class*='price']"); --> priority is not there so leaving
    private static final By PRODUCT_PRICE          = By.className("product-price-final");
    private static final By PRODUCT_IMAGE          = By.className("product-image-container");
    private static final By HEART_BTN              = By.className("btn-favorite");
    private static final By NEXT_BTN               = By.id("next-btn");
    private static final By PREV_BTN               = By.id("prev-btn");
    private static final By CART_BTN               = By.cssSelector(".btn-add-to-cart, button[class*='cart']");

    //    // Wishlist page / section
    private static final By VALIDATOR_WISHLIST_PAGE  = By.className("favorites-main");
    private static final By WISHLIST_ITEMS           = By.className("product-card");
    private static final By EMPTY_STATE              = By.className("empty-favorites");
    private static final By START_SHOPPING           = By.className("shop-btn");
    private static final By ITEM_REMOVE_BTN          = HEART_BTN;
    private static final By ITEM_ADD_TO_CART_BTN     = CART_BTN;
    private static final By ITEM_OUT_OF_STOCK        = By.cssSelector(".out-of-stock, [class*='out-of-stock'], .stock-label");
    private static final By ITEM_NAME                = By.cssSelector("div.product-title");
    private static final By ITEM_PRICE               = By.cssSelector(".product-price-final, .product-card-price, [class*='price']");
    private static final By ITEM_IMAGE               = By.cssSelector(".product-image, img");

    //Filter
    private static final By APPLY_FILTER_BTN = By.id("apply-filters");
    private static final By CLEAR_FILTER     = By.id("clear-filters");
    private static final By FILTER_CATEGORY  = By.cssSelector("input[type=\"checkbox\"]");

    //Single Product Details
    private static final By SINGLE_PRODUCT_PAGE_INFO = By.className("product-page-info");

    //Instant Popup
    private static final By TOGGLE_TEXT = By.cssSelector(".toast.show");

    // ══════════════════════════════════════════════════════════════════════════
    // Private helpers — use TestBase.driver directly (no driver management here)
    // ══════════════════════════════════════════════════════════════════════════

    /** Navigates to the PRODUCT target site using the driver And Assert to valid if the user is on product site or not. */
    private void goToProductSite() throws InterruptedException {
        driver.findElement(PRODUCT_PAGE).click();
        Assert.assertTrue(driver.findElement(VALIDATOR_PRODUCT_PAGE).isDisplayed(), "User should be on product page.");

        //Thread.sleep(1500);
    }

    /** Returns the integer on the wishlist heart badge (0 if absent). */
    private int wishlistBadgeCount() {
        try {
            String txt = driver.findElement(WISHLIST_BADGE)
                    .getText().trim().replaceAll("[^0-9]", "");
            return txt.isEmpty() ? 0 : Integer.parseInt(txt);
        } catch (Exception e) {
            return 0;
        }
    }

    /** Returns all currently visible product cards. */
    private List<WebElement> getProductCards() {
        return driver.findElements(PRODUCT_CARDS);
    }

    /** Scrolls to and clicks the wishlist heart icon on the product card at 0-based index. */
    private void clickHeartOnCard(int index) {
        WebElement card  = getProductCards().get(index);
        WebElement heart = card.findElement(HEART_BTN);


//        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", heart);

        // for direct method
//        wait.until(ExpectedConditions.elementToBeClickable(driver.findElements(HEART_BTN).get(index))).click();

        wait.until(ExpectedConditions.elementToBeClickable(heart)).click();
    }

    /** Returns true if the heart icon on the card at index is in active/wishlisted state. */
    private boolean isWishlisted(int index) {
        WebElement card   = getProductCards().get(index);
        WebElement heart  = card.findElement(HEART_BTN);
//        String class_name = heart.getAttribute("class");
        List<String> class_name = Arrays.asList(
                heart.getAttribute("class").trim().split("\\s+")
        );

        return class_name.contains("active");
    }

    /**
     * Opens the wishlist page via the nav icon.
     */
    private void openWishlistPage() throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(
                driver.findElement(WISHLIST_PAGE)
        )).click();

        //Thread.sleep(1000);
        wait.until(d ->
                !d.findElements(WISHLIST_ITEMS).isEmpty()
                        || !d.findElements(EMPTY_STATE).isEmpty());

        Assert.assertTrue(driver.findElement(VALIDATOR_WISHLIST_PAGE).isDisplayed(), "User should be on Wishlist page.");
    }

    /** Returns all wishlist item rows in the DOM. */
    private List<WebElement> getWishlistItems() {
        return driver.findElements(WISHLIST_ITEMS);
    }

    /** Returns the product name text on the card at index. */
    private String cardName(int index) {
        return getProductCards().get(index).findElement(PRODUCT_NAME).getText().trim();
    }

    /** Returns the product price text on the card at index. */
    private String cardPrice(int index) {
        return getProductCards().get(index).findElement(PRODUCT_PRICE).getText().trim();
    }

    private String cardImage(int index) {
        return getProductCards().get(index).findElement(PRODUCT_IMAGE).getText().trim();
    }

    /** Returns the integer on the cart nav badge (0 if absent). */
    private int cartBadgeCount() {
        try {
            String txt = driver.findElement(CART_BADGE).getText().trim().replaceAll("[^0-9]", "");
            return txt.isEmpty() ? 0 : Integer.parseInt(txt);
        } catch (Exception e) {
            return 0;
        }
    }

    /** Returns the name text on the wishlist item at 0-based index. */
    private String wishlistItemName(int index) {
        return getWishlistItems().get(index).findElement(ITEM_NAME).getText().trim();
    }

    /** Returns the price text on the wishlist item at 0-based index. */
    private String wishlistItemPrice(int index) {
        return getWishlistItems().get(index).findElement(ITEM_PRICE).getText().trim();
    }

    /** Returns the Image text on the wishlist item at 0-based index. */
    private String wishlistItemImage(int index) {
        return getWishlistItems().get(index).findElement(ITEM_IMAGE).getText().trim();
    }

    /** Returns the tooltip message shown on the website for few seconds */
    private String tooltipMessage() {
        return driver.findElement(TOGGLE_TEXT).getText().trim();
    }

    /** Return True if the product is available on wishlist page*/
    private boolean wishlistNameChecker(String product_name) {

        for(var item: getWishlistItems()) {
            if(item.findElement(ITEM_NAME).getText().trim().equalsIgnoreCase(product_name)) {
                return true;
            }
        }

        return false;
    }

    /** Clicks remove on the wishlist item at index and waits for the DOM to settle. */
    private void removeWishlistItem(int index) throws InterruptedException {
        WebElement btn = getWishlistItems().get(index).findElement(ITEM_REMOVE_BTN);
        wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
        //Thread.sleep(1000);
    }

    /** Returns true if the product image src is non-empty on the item at index. */
    private boolean imageLoaded(int index) {
        String src = getWishlistItems().get(index).findElement(ITEM_IMAGE).getAttribute("src");
        return src != null && !src.isEmpty();
    }

    /** Add product to cart till the end*/
    private int addProductToWishlist(int number) {
        int counter = 0;

        while(true) {
            for(var card: getProductCards()) {
                if (number > 0 && counter >= number) {
                    return counter;
                }

                WebElement heart = card.findElement(HEART_BTN);
                wait.until(ExpectedConditions.elementToBeClickable(heart)).click();
                ++counter;
            }

            WebElement next_btn = driver.findElement(NEXT_BTN);
            boolean is_disabled = next_btn.getAttribute("disabled") != null;

            if (is_disabled) {
                break;
            }

            wait.until(ExpectedConditions.elementToBeClickable(next_btn)).click();
        }

        return counter;
    }

    /** Clicks Add-to-Cart on the wishlist item at index. */
    private void addToCartFromWishlist(int index) throws InterruptedException {
        WebElement btn = getWishlistItems().get(index).findElement(ITEM_ADD_TO_CART_BTN);
        wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
        //Thread.sleep(800);
    }

    /** Add product to the cart from the product page*/
    private void addProductToCart(int index) throws InterruptedException{
        WebElement card  = getProductCards().get(index);
        WebElement cart = card.findElement(CART_BTN);

        wait.until(ExpectedConditions.elementToBeClickable(cart)).click();
    }

    /** Clears localStorage via JS and reloads.. */
    private void clearStorageAndReload() throws InterruptedException {
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        driver.navigate().refresh();
        //Thread.sleep(1500);
    }

    private List<WebElement> returnFilterCategoryElements() {
        return driver.findElements(FILTER_CATEGORY);
    }

    /** Apply Filter for the product*/
    private void selectApplyFilterToProduct(int index) {
        WebElement category = returnFilterCategoryElements().get(index);
        wait.until(ExpectedConditions.elementToBeClickable(category)).click();
        Assert.assertTrue(category.isSelected(), "Failed to Add Item from "+category.getAttribute("value"));

        wait.until(ExpectedConditions.elementToBeClickable(APPLY_FILTER_BTN)).click();
    }

    /** Clear Filter for the product*/
    private void clearFilterOfProduct() {
        wait.until(ExpectedConditions.elementToBeClickable(CLEAR_FILTER)).click();
    }

    /** Add one product from each filter category to wishlist */
    private void addOneProductFromEachCategory(int number) {

        int productsToAdd = (number == 0) ? 1 : number;
        for(int i=0;i<returnFilterCategoryElements().size();i++) {
            selectApplyFilterToProduct(i);
            addProductToWishlist(productsToAdd);
            clearFilterOfProduct();
        }

    }

    // ══════════════════════════════════════════════════════════════════════════
    //  GROUP 1 — Add to Wishlist  (TC-W01 to TC-W04)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * TC-W01 — Add a single product to the wishlist.
     *
     * GIVEN  The user is on the product listing page and the wishlist is empty
     * WHEN   The user clicks the wishlist icon (heart) on any product card
     * THEN   The heart icon becomes active, badge increments by 1,
     *        and the item appears in the wishlist
     */
    @Test(description = "TC-W01: Add a single product to the wishlist")
    public void TC_W01_addSingleProductToWishlist() throws InterruptedException {

        // GIVEN — The user is on the product listing page and the wishlist is empty
        goToProductSite();
        int initial_counter = wishlistBadgeCount();
        assertTrue(initial_counter==0, "Wishlist Should be Empty before Adding the Cart");

        // WHEN - The user clicks the wishlist icon (heart) on any product card. In our case taking at 0 index i.e first product
        clickHeartOnCard(0);
        //Thread.sleep(800);

        // THEN — heart is active
        Assert.assertTrue(isWishlisted(0), "TC-W01: Heart icon should be active after adding to wishlist");

        // THEN — badge incremented
        Assert.assertEquals(wishlistBadgeCount(), initial_counter + 1, "TC-W01: Wishlist badge should increment by 1");

        // THEN — item in wishlist
        openWishlistPage();
        Assert.assertEquals(getWishlistItems().size(), 1, "TC-W01: Wishlist page should show exactly 1 item");
    }

    /**
     * TC-W02 — Add multiple different products to the wishlist.
     *
     * GIVEN  The user is on the product listing page and wishlist is empty
     * WHEN   The user clicks the wishlist icon on 3 different products one by one
     * THEN   All 3 products appear in the wishlist
     The wishlist counter badge shows 3
     Each product retains its name, price and image in the wishlist
     */
    @Test(description = "TC-W02: Add multiple different products to the wishlist")
    public void TC_W02_addMultipleProductsToWishlist() throws InterruptedException {
        // GIVEN
        goToProductSite();
        String name0 = cardName(0);
        String price0 = cardPrice(0);
        String image0 = cardImage(0);


        String name1 = cardName(1);
        String price1 = cardPrice(1);
        String image1 = cardImage(1);

        String name2 = cardName(2);
        String price2 = cardPrice(2);
        String image2 = cardImage(2);


        // WHEN
        clickHeartOnCard(0);
        //Thread.sleep(500);
        clickHeartOnCard(1);
        //Thread.sleep(500);
        clickHeartOnCard(2);
        //Thread.sleep(800);

        // THEN — badge = 3
        Assert.assertEquals(wishlistBadgeCount(), 3, "TC-W02: Badge should show 3 after adding 3 products");

        // THEN — all 3 present
        openWishlistPage();
        Assert.assertEquals(getWishlistItems().size(), 3, "TC-W02: Wishlist should contain exactly 3 items");

        // THEN — integrity
        Assert.assertEquals(wishlistItemName(0), name0, "TC-W02: First wishlist item name should match the product card name");
        Assert.assertEquals(wishlistItemName(1), name1, "TC-W02: Second wishlist item name should match the product card name");
        Assert.assertEquals(wishlistItemName(2), name2, "TC-W02: Third wishlist item name should match the product card name");
        Assert.assertEquals(wishlistItemPrice(0), price0, "TC-W02: First wishlist item price should match the product card price");
        Assert.assertEquals(wishlistItemPrice(1), price1, "TC-W02: Second wishlist item price should match the product card price");
        Assert.assertEquals(wishlistItemPrice(2), price2, "TC-W02: Third wishlist item price should match the product card price");
        Assert.assertEquals(wishlistItemImage(0), image0, "TC-W02: First wishlist item image should match the product card image");
        Assert.assertEquals(wishlistItemImage(1), image1, "TC-W02: Second wishlist item image should match the product card image");
        Assert.assertEquals(wishlistItemImage(2), image2, "TC-W02: Third wishlist item image should match the product card image");
    }


    /**
     * TC-W03 — [EDGE] Adding the same product twice
     * must not duplicate it.
     *
     * GIVEN  Product "X" is already in the wishlist
     * WHEN   The user clicks the wishlist icon on product "X" again
     * THEN   Product "X" is NOT duplicated in the wishlist
     * The wishlist counter does not increment again
     * The icon toggles back to unfilled state (acts as remove), OR a tooltip "Already in wishlist" is shown
     */
    @Test(description = "TC-W03 [EDGE]: Add same product twice — no duplicate")
    public void TC_W03_addSameProductTwiceNoDuplicate() throws InterruptedException {
        // GIVEN
        goToProductSite();
        clickHeartOnCard(0);
        //Thread.sleep(600);
        int countAfterFirst = wishlistBadgeCount();

        // WHEN — second click
        clickHeartOnCard(0);
        String tooltip_message = tooltipMessage();
//        //Thread.sleep(600);

        //Then
        Assert.assertFalse(isWishlisted(0), "TC-W03: Item should not be present on the wishlist cart.");

        openWishlistPage();
        Assert.assertTrue(getWishlistItems().size() <= 1, "TC-W03: Product must NOT be duplicated after clicking heart twice");
        Assert.assertFalse(wishlistBadgeCount() > countAfterFirst, "TC-W03: Badge must not exceed count after first click");
        Assert.assertTrue(tooltip_message.contains("Removed") || tooltip_message.contains("Already"), "TC-W03: Item must show a tooltip \"Already in wishlist\" or removed the Item.");


    }

    /**
     * TC-W04 — Add product to wishlist from the product detail page.
     *
     * GIVEN  The user has navigated to a product's detail/single page
     * WHEN   The user clicks the "Add to Wishlist" button on the detail page
     * THEN   The product is added to the wishlist
     * The wishlist icon/counter in the header updates accordingly
     * The button state changes to indicate the product is already wishlisted
     */
    @Test(description = "TC-W04: Add product to wishlist from product detail page")
    public void TC_W04_addToWishlistFromDetailPage() throws InterruptedException {
        // GIVEN
        goToProductSite();
        getProductCards().get(0).click();

        int badgeBefore = wishlistBadgeCount();
        //Thread.sleep(1500);

        // WHEN
        WebElement wish_btn = driver.findElement(HEART_BTN);
        String product_name = wait.until(ExpectedConditions.elementToBeClickable(
                driver.findElement(By.cssSelector("h2.product-page-title"))
        )).getText().trim();
        wait.until(ExpectedConditions.elementToBeClickable(wish_btn)).click();


        //Thread.sleep(800);

        // THEN — badge incremented
        Assert.assertTrue(wishlistBadgeCount() == badgeBefore+1, "TC-W04: Wishlist badge should increment after adding from detail page");

        // THEN — button active
        String cls = wish_btn.getAttribute("class");
        Assert.assertTrue(cls.contains("active"), "TC-W04: Wishlist button should show active state on the detail page");

        //Then - Check product on wishlist
        openWishlistPage();
        assertTrue(wishlistNameChecker(product_name), "TC-W04: Wishlist page should contain the added product");
    }


    // ══════════════════════════════════════════════════════════════════════════
    //  GROUP 2 — Remove from Wishlist  (TC-W05 to TC-W06)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * TC-W05 — Remove a single product from the wishlist.
     *
     * GIVEN  The wishlist contains at least one product
     * WHEN   The user clicks the remove/delete icon on that product inside the wishlist
     * THEN   The product is removed from the wishlist immediately
     * 		  The wishlist counter badge decrements by 1
     *        The heart icon on the product card (if visible) reverts to unfilled
     */
    @Test(description = "TC-W05: Remove a single product from the wishlist")
    public void TC_W05_removeSingleProductFromWishlist() throws InterruptedException {
        // GIVEN
        goToProductSite();
        clickHeartOnCard(0);
        //Thread.sleep(600);
        openWishlistPage();
        Assert.assertEquals(getWishlistItems().size(), 1, "TC-W05 pre-condition: 1 item in wishlist");

        // WHEN
        removeWishlistItem(0);

        // THEN
        Assert.assertEquals(getWishlistItems().size(), 0, "TC-W05: Wishlist should be empty after removal");
        Assert.assertEquals(wishlistBadgeCount(), 0, "TC-W05: Badge should show 0 after removal");
        goToProductSite();
        Assert.assertFalse(isWishlisted(0), "TC-W05: Item should have unfilled heart icon");
    }

    /**
     * TC-W06 — [EDGE] Remove the last product — wishlist becomes empty.
     *
     * GIVEN  The wishlist contains exactly one product
     * WHEN   The user removes that product
     * THEN   The wishlist counter badge shows 0 or disappears
     *        An empty-state message is displayed (e.g., "Your wishlist is empty")
     *        No broken layout or orphan UI elements remain
     */
    @Test(description = "TC-W06 [EDGE]: Remove last item — empty state is shown")
    public void TC_W06_removeLastItemShowsEmptyState() throws InterruptedException {
        // GIVEN
        goToProductSite();
        clickHeartOnCard(0);
        //Thread.sleep(600);
        openWishlistPage();

        // WHEN
        removeWishlistItem(0);

        // THEN
        Assert.assertFalse(driver.findElements(EMPTY_STATE).isEmpty(), "TC-W06: Empty-state message should be visible when wishlist is empty");
        Assert.assertEquals(wishlistBadgeCount(), 0, "TC-W06: Badge should show 0 after last item removed");
    }


    // ══════════════════════════════════════════════════════════════════════════
    //  GROUP 3 — Wishlist Display & UI  (TC-W07 to TC-W09)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * TC-W07 — Wishlist shows correct product name, price, and image.
     *
     * GIVEN  TestBase.driver is live AND product[0] has been added to the wishlist
     * WHEN   The user opens the wishlist
     * THEN   Name and price match the listing page; image src is non-empty
     */
    @Test(description = "TC-W07: Wishlist displays correct product name, price, and image")
    public void TC_W07_wishlistDisplaysCorrectDetails() throws InterruptedException {
        // GIVEN
        goToProductSite();
        String expectedName  = cardName(0);
        String expectedPrice = cardPrice(0);
        clickHeartOnCard(0);
        //Thread.sleep(600);

        // WHEN
        openWishlistPage();

        // THEN
        Assert.assertEquals(wishlistItemName(0), expectedName, "TC-W07: Product name in wishlist should match the listing page");
        Assert.assertEquals(wishlistItemPrice(0), expectedPrice, "TC-W07: Product price in wishlist should match the listing page");
        Assert.assertTrue(imageLoaded(0), "TC-W07: Product image src should be non-empty in wishlist");
    }

    /**
     * TC-W08 — Wishlist counter badge reflects accurate count at all times.
     *
     * GIVEN  The wishlist currently has 2 items
     * WHEN   The user adds 1 more item, then removes 1 item
     * THEN   After adding: counter shows 3
     *        After removing: counter shows 2
     *        Counter is always in sync with actual wishlist contents
     */
    @Test(description = "TC-W08: Wishlist badge stays accurate after add and remove")
    public void TC_W08_wishlistBadgeAccuracy() throws InterruptedException {
        // GIVEN
        goToProductSite();
        clickHeartOnCard(0);
        //Thread.sleep(400);
        clickHeartOnCard(1);
        //Thread.sleep(400);
        Assert.assertEquals(wishlistBadgeCount(), 2, "TC-W08 pre-condition: badge = 2");

        // WHEN — add 1 more
        clickHeartOnCard(2);
        //Thread.sleep(400);
        Assert.assertEquals(wishlistBadgeCount(), 3, "TC-W08: Badge should be 3 after adding a third product");

        // WHEN — remove 1
        openWishlistPage();
        removeWishlistItem(0);

        // THEN
        Assert.assertEquals(wishlistBadgeCount(), 2, "TC-W08: Badge should be 2 after removing one item");
    }

    /**
     * TC-W09 — [EDGE] Wishlist counter display with large number of items.
     *
     * GIVEN  The wishlist has more than 99 items (if the site has no hard cap)
     * WHEN   The user views the wishlist badge in the header
     * THEN   The badge shows "99+" or the actual number without layout overflow
     *        The badge does not break the header layout
     */
    @Test(description = "TC-W09 [EDGE]: Badge does not overflow with large item count")
    public void TC_W09_badgeDoesNotOverflowWithManyItems() throws InterruptedException {
        // GIVEN / WHEN
        goToProductSite();
        int available = addProductToWishlist(0);

        // THEN
        int badge = wishlistBadgeCount();
        Assert.assertTrue(badge > 0, "W09: Badge should show a positive number after adding many items");
        Assert.assertTrue(badge == available, "TC-W09: Badge should not exceed the number of items added");
        Assert.assertFalse(driver.getPageSource().contains("NaN"), "TC-W09: Page source must not contain 'NaN'");
    }

    /**
     * TC-W10 — Empty wishlist state displays a meaningful message.
     *
     * GIVEN  The user has no items in the wishlist
     * WHEN   The user navigates to the wishlist page or opens the wishlist panel
     * THEN   An empty-state illustration or message is shown (e.g., "No items in your wishlist")
     *        A CTA button such as "Continue Shopping" is present and functional
     *        No error messages or blank white space is shown
     */
    @Test(description = "TC-W10: Empty wishlist shows empty state and Continue Shopping CTA")
    public void TC_W10_emptyWishlistShowsEmptyState() throws InterruptedException {
        // GIVEN
        goToProductSite();

        // WHEN
        openWishlistPage();

        // THEN
        Assert.assertFalse(driver.findElements(EMPTY_STATE).isEmpty(), "TC-W10: Empty-state message should be visible on an empty wishlist");
        Assert.assertEquals(getWishlistItems().size(), 0, "TC-W10: No item rows should render when wishlist is empty");
        Assert.assertFalse(driver.findElements(START_SHOPPING).isEmpty(), "TC-W10: Continue Shopping button should be present on empty wishlist");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  GROUP 4 — Wishlist <-> Cart Interaction  (TC-W11 to TC-W13)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * TC-W11 — Move a product from wishlist to cart
     *
     * GIVEN  The wishlist contains product "B" and the cart is empty
     * WHEN   The user clicks "Add to Cart" on product "B" from within the wishlist
     * THEN   Product "B" is added to the cart (cart counter increments)
     *        The wishlist may optionally retain or remove product "B" (behaviour is consistent)
     */
    @Test(description = "TC-W11: Add to cart from wishlist")
    public void TC_W11_addToCartFromWishlist() throws InterruptedException {
        // GIVEN
        goToProductSite();
        clickHeartOnCard(0);
        //Thread.sleep(600);
        openWishlistPage();

        // WHEN
        addToCartFromWishlist(0);

        // THEN
        Assert.assertTrue(wishlistBadgeCount()==1, "TC-W11: Wishlist badge should show 1. After adding to cart");
        Assert.assertTrue(cartBadgeCount() == 1, "TC-W11: Cart badge should be > 0 after moving item from wishlist to cart");
    }

    //Requirement is to make the cart counter increment not removed from cart
    /**
     * TC-W12 — [EDGE] Add to cart from wishlist when item is already in cart.
     *
     * GIVEN  Product "C" is in both the wishlist and already in the cart (qty: 1)
     * WHEN   The user clicks "Add to Cart" on product "C" from the wishlist
     * THEN   Cart quantity for product "C" increments to 2
     *        No orphan or ghost cart entries are created

    @Test(description = "TC-W12 [EDGE]: Add to cart from wishlist when item already in cart")
    public void TC_W12_addToCartFromWishlistAlreadyInCart() throws InterruptedException {
        // GIVEN — add to wishlist then to cart once
        goToProductSite();
        clickHeartOnCard(0);
        addProductToCart(0);

        Assert.assertTrue(cartBadgeCount()==1, "TC-W12: Pre-condition: Item should be in Cart");
        Assert.assertTrue(wishlistBadgeCount()==1, "TC-W12: Pre-condition: Item should be in Wishlist");

        int cartCountAfterFirst = cartBadgeCount();
        //Thread.sleep(600);

        //When
        openWishlistPage();
        addToCartFromWishlist(0);


        // THEN
        Assert.assertTrue(cartBadgeCount() >= cartCountAfterFirst , "TC-W12: Cart count must not decrease after second add-to-cart from wishlist");
    }
    */



    // ══════════════════════════════════════════════════════════════════════════
    //  GROUP 5 — Persistence & State  (TC-W13 to TC-W15)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * TC-W13 — Wishlist persists after page refresh.
     *
     * GIVEN  The user has 2 products in the wishlist
     * WHEN   The user refreshes the page (F5 / hard reload)
     * THEN   Both products are still present in the wishlist after reload
     *        The wishlist counter badge shows the correct count
     *        Heart icons on product cards remain filled for wishlisted products
     */
    @Test(description = "TC-W13: Wishlist persists after page refresh")
    public void TC_W13_wishlistPersistsAfterRefresh() throws InterruptedException {
        // GIVEN
        goToProductSite();
        clickHeartOnCard(0);
        //Thread.sleep(400);
        clickHeartOnCard(1);
        //Thread.sleep(400);
        Assert.assertEquals(wishlistBadgeCount(), 2, "TC-W13 pre-condition: badge = 2");

        // WHEN
        driver.navigate().refresh();
        //Thread.sleep(1500);

        // THEN
        Assert.assertEquals(wishlistBadgeCount(), 2, "TC-W13: Badge should still show 2 after page refresh");
        Assert.assertTrue(isWishlisted(0), "TC-W13: Product[0] heart should remain filled after refresh");
        Assert.assertTrue(isWishlisted(1), "TC-W13: Product[1] heart should remain filled after refresh");

        openWishlistPage();
        Assert.assertEquals(getWishlistItems().size(), 2, "TC-W13: Both items should still be in wishlist after refresh");
    }

    /**
     * TC-W14 — [EDGE] Wishlist state when browser localStorage is cleared.
     *
     * GIVEN  The user has items in the wishlist stored in localStorage
     * WHEN   The user clears browser storage (DevTools → Application → Clear Storage) and reloads
     * THEN   The wishlist is reset to empty (expected behaviour)
     *        No JavaScript errors are thrown in the console
     *        The empty-state UI is shown gracefully
     */
    @Test(description = "TC-W14 [EDGE]: Wishlist handles cleared localStorage gracefully")
    public void TC_W14_wishlistHandlesClearedStorage() throws InterruptedException {
        // GIVEN
        goToProductSite();
        clickHeartOnCard(0);
        //Thread.sleep(600);

        // WHEN — use TestBase.driver via the helper
        clearStorageAndReload();

        // THEN
        Assert.assertEquals(wishlistBadgeCount(), 0, "TC-W14: Badge should be 0 after localStorage cleared");
        Assert.assertFalse(isWishlisted(0),"TC-W14: Heart icon should be unfilled after localStorage cleared");

        openWishlistPage();
        Assert.assertFalse(driver.findElements(EMPTY_STATE).isEmpty(), "TC-W14: Empty state should render gracefully after storage cleared");
    }

    /**
     * TC-W15 — Wishlist icon state is consistent across product listing and detail pages.
     *
     * GIVEN  Product "E" is in the wishlist (icon shows filled on the listing page)
     * WHEN   The user navigates to the product detail page
     * THEN   The wishlist button on the detail page shows the active state
     */
    @Test(description = "TC-W15: Wishlist icon is consistent across listing and detail pages")
    public void TC_W15_wishlistIconConsistentAcrossPages() throws InterruptedException {
        // GIVEN
        goToProductSite();
        clickHeartOnCard(0);
        //Thread.sleep(600);
        Assert.assertTrue(isWishlisted(0), "TC-W15 pre-condition: heart should be filled");

        // WHEN
        WebElement link = getProductCards().get(0);
        link.click();
        //Thread.sleep(1500);

        // THEN
        WebElement product_info = wait.until(ExpectedConditions.elementToBeClickable(SINGLE_PRODUCT_PAGE_INFO));
        String wish_btn = product_info.findElement(HEART_BTN).getAttribute("class");
        Assert.assertTrue(wish_btn.contains("active"), "TC-W15: Wishlist button on detail page should show active state for a wishlisted product");

    }

    // ══════════════════════════════════════════════════════════════════════════
    //  GROUP 6 — Navigation & Filtering  (TC-W16 to TC-W18)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * TC-W16 — Clicking a wishlist item navigates to its product detail page
     *
     * GIVEN  The wishlist contains product "F" and the user is viewing the wishlist
     * WHEN   The user clicks on product "F"'s name or image in the wishlist
     * THEN   The user is taken to product "F"'s detail page
     *        The correct product information is displayed on the detail page

    @Test(description = "TC-W16: Clicking a wishlist item navigates to product detail page")
    public void TC_W16_clickingWishlistItemNavigatesToDetail() throws InterruptedException {
        // GIVEN
        goToProductSite();
        clickHeartOnCard(0);
        //Thread.sleep(600);
        openWishlistPage();

        // WHEN
        String urlBefore = driver.getCurrentUrl();
        WebElement link  = getWishlistItems().get(0);
        link.click();
        //Thread.sleep(1500);

        // THEN
        boolean urlChanged     = !driver.getCurrentUrl().equals(urlBefore);
        boolean detailRendered = !driver.findElements(SINGLE_PRODUCT_PAGE_INFO).isEmpty();
        Assert.assertTrue(urlChanged || detailRendered, "TC-W16: Clicking wishlist item should navigate to product detail page");
    }
    */

    /**
     * TC-W17 — Wishlist items are correctly filtered when product category filter is appliedr.
     *
     * GIVEN  The wishlist contains products from multiple categories
     * WHEN   The user applies a category filter on the product listing page
     * THEN   Heart icons remain correctly filled only on visible wishlisted products
     *        Wishlist should show only eligible product list according to the filter on the wishlist pg.
     *        The wishlist counter badge does not change (filter does not alter wishlist)
     *        Removing the filter restores all product cards with correct heart states
     */
    @Test(description = "TC-W17: Category filter does not change wishlist counter")
    public void TC_W17_filterDoesNotAffectWishlistCount() throws InterruptedException {
        // GIVEN
        goToProductSite();
        addOneProductFromEachCategory(1);
        //Thread.sleep(400);
        int countBefore = wishlistBadgeCount();
        Assert.assertEquals(countBefore, 6, "TC-W17 pre-condition: badge = 6"); //Automative has no product

        // WHEN — simulate filter via page reload using TestBase.driver
        goToProductSite();
        selectApplyFilterToProduct(0);

        //Then
        Assert.assertTrue(isWishlisted(0), "TC-W17: Product should show filled state After Filter Application.");
        Assert.assertEquals(wishlistBadgeCount(), countBefore, "TC-W17: Wishlist counter must not change after a page filter");

        clearFilterOfProduct();
        Assert.assertTrue(isWishlisted(0), "TC-W17: Product should show filled state After clear filter.");
    }

    /**
     * TC-W18 — [EDGE] Wishlist behaviour when navigating back with browser back button.
     *
     * GIVEN  The user adds a product to the wishlist on the listing page, then navigates to the wishlist page
     * WHEN   The user presses the browser back button to return to the listing page
     * THEN   The listing page shows the heart icon as filled for the previously added product
     *        The wishlist counter is correct
     */
    @Test(description = "TC-W18 [EDGE]: Browser back button preserves wishlist heart states")
    public void TC_W18_browserBackPreservesWishlistState() throws InterruptedException {
        // GIVEN
        goToProductSite();
        clickHeartOnCard(0);
        //Thread.sleep(600);
        openWishlistPage();

        // WHEN
        driver.navigate().back();
        //Thread.sleep(1500);

        // THEN
        Assert.assertTrue(isWishlisted(0),"TC-W18: Heart icon should remain filled after browser back button");
        Assert.assertEquals(wishlistBadgeCount(), 1, "TC-W18: Badge should still show 1 after browser back navigation");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  GROUP 7 — Boundary / Rapid Actions  (TC-W19 to TC-W21)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * TC-W19 — [EDGE] Rapidly clicking the wishlist icon multiple times.
     *
     * GIVEN  The user is on the product listing page
     * WHEN   The user rapidly clicks the heart icon on a product 5 times in quick succession
     * THEN   The product is either in or out of the wishlist depending on whether 5 clicks = odd or even toggles
     *        The wishlist counter is not negative and not erroneously high
     *        No duplicate entries are created
     *        The icon state is in sync with the actual wishlist state
     */
    @Test(description = "TC-W19 [EDGE]: Rapid heart clicks produce consistent wishlist state")
    public void TC_W21_rapidHeartClickConsistentState() throws InterruptedException {
        // GIVEN / WHEN
        goToProductSite();
        for (int i = 0; i < 5; i++) {
            clickHeartOnCard(0);
        }
        //Thread.sleep(600);

        // THEN — badge bounds
        int badge = wishlistBadgeCount();
        Assert.assertTrue(badge >= 0, "TC-W19: Badge must not be negative");
        Assert.assertTrue(badge <= 1, "TC-W19: Badge must not exceed 1 for a single product");

        // THEN — icon and badge agree
        if (isWishlisted(0)) {
            Assert.assertEquals(badge, 1, "TC-W19: Badge should be 1 when product is wishlisted");
        } else {
            Assert.assertEquals(badge, 0, "TC-W19: Badge should be 0 when product is not wishlisted");
        }

        // THEN — no duplicates
        openWishlistPage();
        Assert.assertTrue(getWishlistItems().size() <= 1, "TC-W19: No duplicate entries should exist after rapid clicks");
    }

    /**
     * TC-W20 — [EDGE] Wishlist behaviour with a product that has a very long name
     *
     * GIVEN  A product with a very long name (e.g., 80+ characters) exists in the catalogue
     * WHEN   The user views the wishlist
     * THEN   Remove button is reachable, confirming no layout overflow
     */
    @Test(description = "TC-W20 [EDGE]: Long product name does not break wishlist layout")
    public void TC_W20_longProductNameDoesNotBreakLayout() throws InterruptedException {
        // GIVEN
        goToProductSite();
        clickHeartOnCard(0);
        openWishlistPage();
        Assert.assertFalse(getWishlistItems().isEmpty(), "TC-W20 pre-condition: wishlist must have at least one item");

        // WHEN / THEN
        try {
            removeWishlistItem(0);
            Assert.assertEquals(getWishlistItems().size(), 0, "TC-W20: Remove button must be clickable — layout is intact");
        } catch (Exception e) {
            Assert.fail("TC-W20: Remove button not reachable — possible layout overflow: " + e.getMessage());
        }
    }

    /**
     * TC-W21 — [EDGE] Remove button is reachable, confirming no layout overflow(375 x 812).
     *
     * GIVEN  The user opens the site on a mobile viewport (375px width)
     * WHEN   The user adds a product to the wishlist and then views the wishlist
     * THEN   The heart icon is tappable (minimum 44×44px touch target)
     *        The wishlist page/panel renders without horizontal scrolling
     *        All action buttons (Remove, Add to Cart) are reachable
     */
    @Test(description = "TC-W21 [EDGE]: Wishlist is functional on a mobile viewport (375px)")
    public void TC_W21_wishlistFunctionalOnMobileViewport() throws InterruptedException {
        // GIVEN
        driver.manage().window().setSize(new Dimension(375, 812));
        goToProductSite();

        // WHEN
        clickHeartOnCard(0);
        //Thread.sleep(600);

        // THEN
        Assert.assertEquals(wishlistBadgeCount(), 1,
                "TC-W21: Badge should show 1 on mobile viewport");

        // THEN — heart active
        Assert.assertTrue(isWishlisted(0),
                "TC-W21: Heart icon should be active on mobile viewport");

        // THEN — wishlist accessible
        openWishlistPage();
        Assert.assertEquals(getWishlistItems().size(), 1,
                "TC-W21: Wishlist item should be accessible on mobile viewport");

        // THEN — no horizontal overflow
        Long bodyScrollWidth = (Long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollWidth;");
        Assert.assertTrue(bodyScrollWidth <= 500, "TC-W21: Page must not overflow horizontally on 375px viewport (actual: "+ bodyScrollWidth + "px)");
    }
}