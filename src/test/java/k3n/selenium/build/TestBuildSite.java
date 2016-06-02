package k3n.selenium.build;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.browserlaunchers.CapabilityType;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;

public class TestBuildSite {

    private static final String SEARCH_TXT = "search_txt";

    private static final String SITE = "www.build.com";

    private static final String QUANTITY_FIELD = "qtyselected";

    private DesiredCapabilities capabilities = null;

    private WebDriver webDriver = null;

    @Before
    public void setUp() throws Exception {
        capabilities = new DesiredCapabilities();
        System.setProperty("phantomjs.binary.path", "phantomjs.exe");

        Proxy proxy = new Proxy();
        proxy.setProxyType(ProxyType.MANUAL);
        proxy.setHttpProxy("10.12.134.32:8080");
        proxy.setSslProxy("10.12.134.32:8080");

        capabilities.setCapability(CapabilityType.PROXY, proxy);
        webDriver = new PhantomJSDriver(capabilities);
    }

    @Test
    public void test() throws Exception {
        webDriver.get(SITE);

        waitForPage(60, "Home Page Loaded.png", true);

        addItemToCart("Suede Kohler K­6626­6U", 1, true);
        addItemToCart("Cashmere Kohler K­6626­6U", 1, true);
        addItemToCart("Kohler K­6066­ST", 2, true);

        goToCartAndThenCheckout();

    }

    private void goToCartAndThenCheckout() throws Exception {
        WebElement shoppingCartButton = webDriver.findElement(By.xpath("//*[@id='main-container']/header/div[2]/div/a"));
        shoppingCartButton.click();

        waitForPage(10, "Shopping Cart.png", true);

        WebElement checkoutButton = webDriver.findElement(By.xpath("//*[@id='cartNav']/a"));
        checkoutButton.click();
        waitForPage(10, "Checkout Page.png", true);

        WebElement guestLoginSubmit = webDriver.findElement(By.id("guestLoginSubmit"));
        guestLoginSubmit.click();
        waitForPage(10, "Guest Checkout.png", true);
    }

    private void addItemToCart(String itemName, int quantity, boolean captureScreenshot) throws Exception {
        WebElement searchBox = webDriver.findElement(By.id(SEARCH_TXT));
        searchBox.sendKeys(itemName);
        searchBox.submit();
        waitForPage(10, itemName + ".png", captureScreenshot);

        WebElement quantityField = webDriver.findElement(By.id(QUANTITY_FIELD));
        quantityField.sendKeys(quantity + "");
        quantityField.submit();
    }

    private void waitForPage(long seconds, String fileName, boolean captureScreenshot) throws IOException {
        capture(webDriver, fileName);
        (new WebDriverWait(webDriver, seconds)).until(ExpectedConditions.presenceOfElementLocated(By.id(SEARCH_TXT)));
        if (captureScreenshot) {
            capture(webDriver, fileName);
        }
    }

    private WebElement findA(WebDriver webDriver, String linkText) {
        WebElement a = webDriver.findElement(By.linkText(linkText));
        return a;
    }

    private void focus(WebDriver webDriver, WebElement webElement) throws IOException {
        Actions action1 = new Actions(webDriver);
        action1.moveToElement(webElement).build().perform();
        capture(webDriver, webElement.getText() + ".png");
    }

    private void capture(WebDriver webDriver, String fileName) throws IOException {
        File screenshotFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(screenshotFile, new File(fileName));
    }

}
