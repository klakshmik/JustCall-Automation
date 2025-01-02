package org.justcall.base;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.justcall.utility.TestRunPropertyReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BaseTest {
    public WebDriver driver;
    private String browser;
    public static final int DELAY_TEST_TIME = 2;
    private String testPlatform;

    @BeforeClass
    public void setupClass() {

        log.info("Started setupClass");

        initBrowser();

        testPlatform = System.getProperty("testPlatform");
        log.info("testPlatform: {}", testPlatform);

        if (StringUtils.isEmpty(testPlatform))
            testPlatform = "";

        switch (testPlatform) {
            case "LAMBDA":
                setupLambdaTestEnv();
                break;

            case "LOCAL":
            default:
                localSetUp();
        }

    }

    public void doLogin() {
        pause(DELAY_TEST_TIME);
        driver.get(TestRunPropertyReader.getPropertyMethod("url"));
        WebElement loginIcon = driver.findElement(By.cssSelector("a[class='new_menu_login login-link']"));
        loginIcon.click();
        WebElement usernameField = driver.findElement(By.cssSelector("input[id='form-email_id']"));
        usernameField.sendKeys(TestRunPropertyReader.getPropertyMethod("userName"));
        WebElement nextButton = driver.findElement(By.cssSelector("button[id='login-btn']"));
        nextButton.click();
        WebElement passwordField = driver.findElement(By.cssSelector("input[id='form-password']"));
        pause(DELAY_TEST_TIME);
        passwordField.sendKeys(TestRunPropertyReader.getPropertyMethod("password"));
        pause(DELAY_TEST_TIME);
        WebElement loginButton = driver.findElement(By.cssSelector("button[class='btn btn-color btn-green ']"));
        loginButton.click();
    }

    private void readProps() {
        String env = System.getProperty("env");
        if (StringUtils.isBlank(env)) {
            env = "testrun";
        }
        TestRunPropertyReader.propertiesName = env + ".properties";
    }

    public void pause(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException ex) {
            System.out.println("Interrupted exception occurring during thread.sleep call.");
        }
    }


    private void initBrowser() {
        browser = System.getProperty("browser");

        if (StringUtils.isBlank(browser)) {
            browser = "chrome";
        }
        log.info("browser: {}", browser);
    }

    public void setupLambdaTestEnv() {
        ChromeOptions browserOptions = getLambdaBrowswerOptions();
        readProps();
        URL url = null;
        try {
            url = new URL("https://hub.lambdatest.com/wd/hub");
            driver = new RemoteWebDriver(url, browserOptions);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.manage().window().maximize();
            doLogin();
        } catch (MalformedURLException e) {
            System.out.println("wrong url");
        }

    }

    private ChromeOptions getLambdaBrowswerOptions() {
        ChromeOptions browserOptions = new ChromeOptions();

        setOS(browserOptions);
        setBrowserVersion(browserOptions);

        HashMap<String, Object> ltOptions = new HashMap<>();
        ltOptions.put("username", "lakshmi29kumari");
        ltOptions.put("accessKey", "osXjthURJzBedY977MkcLYd6vGoOcVNpdMXPU3Z6TiFWZGkls7");
        ltOptions.put("video", true);
        ltOptions.put("build", "SeleniumBuild");
        ltOptions.put("project", "SeleniumProject");
        ltOptions.put("selenium_version", "4.0.0");
        ltOptions.put("w3c", true);
        browserOptions.setCapability("LT:Options", ltOptions);
        setBrowser(browserOptions);
        return browserOptions;
    }

    @AfterClass
    public void tearDown() {
        driver.close();
    }

    private void localSetUp() {

        browser = TestRunPropertyReader.getPropertyMethod("browser");
        if (browser.equalsIgnoreCase("safari")) {
            driver = new SafariDriver();
        } else if (browser.equalsIgnoreCase("chrome")) {
            driver = new ChromeDriver();
        }
        driver.get(TestRunPropertyReader.getPropertyMethod("url"));
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        pause(DELAY_TEST_TIME);
        doLogin();
    }

    private void setOS(ChromeOptions browserOptions) {
        String os = System.getProperty("os");
        log.info("os: {}.", os);
        if (StringUtils.isBlank(os)) {
            os = "Windows 10";
        }
        browserOptions.setPlatformName(os);
    }

    private void setBrowserVersion(ChromeOptions browserOptions) {
        String browserVersion = System.getProperty("browserVersion");
        log.info("browserVersion:{}", browserVersion);
        if (StringUtils.isBlank(browserVersion)) {
            browserVersion = "latest";
        }
        browserOptions.setBrowserVersion(browserVersion);
    }

    private void setBrowser(ChromeOptions browserOptions) {
        browserOptions.setCapability("browserName", browser);
    }

}

