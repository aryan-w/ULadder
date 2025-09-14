package pages;

import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

public class SearchPage extends StartupPage {

    public SearchPage(WebDriver driver) {
        super(driver);
    }

    private By searchBar = By.cssSelector("input#search");
    private By searchButton = By.cssSelector("button#search_button");
    private By searchedProductTitle = By.xpath("//div[contains(@class,'product-title')]/span[@class='name']");
    private By wishIcon = By.xpath("(//span[contains(@class,'wishicon')])[1]");
    private By wishlistCount = By.cssSelector("div#shortlist-badge span:nth-child(2)");
    private By viewProductButton = By.xpath("//a[text()='View Product']");
    private By productTitle = By.xpath("//div[contains(@id,'product-title-container')]/h1[@class='product-title']");
    private By priceFilter = By.xpath("(//li[@data-group=\"price\"])[1]");
    private By priceRange = By.xpath("(//label[@class=\"range\"])[1]");
    private By productPrice = By.cssSelector("div.price-number span.pricing");
    private By inStockOnlyCheckBox = By.cssSelector("div[data-option-key=\"In Stock Only\"] label");
    private By headerWishlistIcon = By.cssSelector("div#shortlist-badge ");
    private By wishlistPageProductName = By.cssSelector("a.product-title-block div span.name");
    private By addToCompareButton = By.xpath("(//a[text()='Add to Compare'])[1]");
    private By widgetProduct = By.xpath("//div[contains(@class,\"widget-product-card\") and @data-vid]");

    private int beforeWishlistCount;
    private String wishlistProductName, actualProductName;

    // --- Actions ---
    public void searchForProduct(String productName) {
        driver.findElement(searchBar).sendKeys(productName);
        driver.findElement(searchButton).click();
    }

    public boolean verifySearchedProduct(String expectedProductName) {
        return driver.findElements(searchedProductTitle).stream()
                .map(WebElement::getText)
                .peek(p -> System.out.println("Found: " + p))
                .anyMatch(p -> p.contains(expectedProductName));
    }

    public void addItemToWishlist() {
        beforeWishlistCount = Integer.parseInt(driver.findElement(wishlistCount).getText());
        wishlistProductName = driver.findElements(searchedProductTitle).get(0).getText();
        System.out.println("Wishlist product: " + wishlistProductName);
        driver.findElement(wishIcon).click();
        sleep(2000);
    }

    public boolean verifyProductAddedToWishlist() {
        int afterCount = Integer.parseInt(driver.findElement(wishlistCount).getText());
        return afterCount > beforeWishlistCount;
    }

    public void clickOnViewProduct() {
        WebElement firstProduct = driver.findElements(searchedProductTitle).get(0);
        actualProductName = firstProduct.getText();
        System.out.println("Product Name: " + actualProductName);
        new Actions(driver).moveToElement(firstProduct).click(driver.findElements(viewProductButton).get(0)).perform();
    }

    public boolean verifyProductDetailsOnProductInfoPage() {
        String expected = driver.findElement(productTitle).getText();
        System.out.println("Expected: " + expected);
        return expected.trim().equals(actualProductName.trim());
    }

    public void applyPriceFilter() {
        new Actions(driver).moveToElement(driver.findElement(priceFilter)).click().perform();
        driver.findElement(priceRange).click();
    }

    public boolean verifyProductPriceLiesInRange() {
        String[] range = driver.findElement(priceRange).getText().replace("₹", "").replace(",", "").split("-");
        int min = Integer.parseInt(range[0].trim());
        int max = Integer.parseInt(range[1].trim());

        sleep(2000);
        driver.findElement(inStockOnlyCheckBox).click();

        return driver.findElements(productPrice).stream()
                .map(WebElement::getText)
                .map(s -> s.replace("Starting From ₹", "").replace(",", "").trim())
                .mapToInt(Integer::parseInt)
                .allMatch(price -> price >= min && price <= max);
    }

    public boolean verifyWishlistedProductDetails() {
        driver.findElement(headerWishlistIcon).click();
        String expected = driver.findElement(wishlistPageProductName).getText();
        return expected.trim().equals(wishlistProductName.trim());
    }

    public void clickOnAddToCompareButton() {
        new Actions(driver).moveToElement(driver.findElements(searchedProductTitle).get(0))
                .click(driver.findElement(addToCompareButton)).perform();
    }

    public boolean verifyProductIsPresentInCompareList() {
        return driver.findElement(widgetProduct).isDisplayed();
    }

    // --- Utility ---
    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
