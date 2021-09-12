package Apache.console;

import Apache.database.VehicleBase;
import Apache.objects.Vehicle;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

class NextPartScraper {

    private static WebDriver driver;

    private static String year;
    private static String make;
    private static String model;
    private static String engine;

    private static List<WebElement> yearList = new ArrayList<>();
    private static List<WebElement> makeList = new ArrayList<>();
    private static List<WebElement> modelList = new ArrayList<>();
    private static List<WebElement> engineList = new ArrayList<>();

    private static String START_YEAR;
    private static String START_MAKE;
    private static int PAUSE;

    public static boolean startBrowser() {
        try {
            System.out.println("Starting browser...");
            System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--window-size=1200x900");
            driver = new ChromeDriver(options);
            Console.printSuccess("Browser started");
            return true;
        } catch (Exception e) {
            Console.printError("Failed to start browser");
            return false;
        }
    }

    public static boolean login(String username, String password) {
        try {
            driver.get("https://nexpart.com/login.php");
            System.out.println("Sending username...");
            waitForVisibility(By.id("username")).sendKeys(username);
            System.out.println("Sending password...");
            driver.findElement(By.id("password")).sendKeys(password);
            System.out.println("Logging in...");
            driver.findElement(By.id("btnLoginID")).click();
            waitForVisibility(By.id("catYearBox"));
            Console.printSuccess("Successfully logged in");
            return true;
        } catch (Exception e) {
            Console.printError("Failed to authenticate login");
            return false;
        }
    }

    private static int yearIndex;
    private static int makeCursor;
    private static boolean firstYear;

    public static boolean commenceScrape(String startYear, String startMake, int msPause) {
        START_YEAR = startYear;
        START_MAKE = startMake;
        PAUSE = msPause;

        selectYearTab();
        yearIndex = 0;
        firstYear = true;
        try {
            while (yearIndex < 93) {
                scrapeYear(yearIndex);
                yearIndex++;
            }
            return true;
        } catch (Exception e) {
            Console.printError("Failure occurred at " + year + " " + make + " " + model);
            driver.quit();
            driver = null;
            return false;
        }
    }

    private static void scrapeYear(int cursor) throws InterruptedException {
        Thread.sleep(PAUSE);
        loadYearList();

        WebElement yearElement;

        yearElement = yearList.get(cursor);
        year = yearElement.getText();

        if(Integer.parseInt(year) > Integer.parseInt(START_YEAR)){
            return;
        }

        yearElement.click();
        Thread.sleep(PAUSE);
        loadMakeList();
        int size = makeList.size();
        for (makeCursor = 0; makeCursor < size; makeCursor++)
            scrapeMake();
        selectYearTab();
    }


    private static void scrapeMake() throws InterruptedException {
        Thread.sleep(PAUSE);
        loadMakeList();

        WebElement makeElement;

        makeElement = makeList.get(makeCursor);
        make = makeElement.getText();

        if(firstYear){

            while(!make.equalsIgnoreCase(START_MAKE)){
                makeCursor++;
                makeElement = makeList.get(makeCursor);
                make = makeElement.getText();
            }
            firstYear = false;
        }

        makeElement.click();
        Thread.sleep(PAUSE);
        loadModelList();

        int size = modelList.size();
        for (int cursor2 = 0; cursor2 < size; cursor2++)
            scrapeModel(cursor2);
        selectMakeTab();
    }

    private static void scrapeModel(int cursor) throws InterruptedException {
        Thread.sleep(PAUSE);
        loadModelList();
        WebElement modelElement = modelList.get(cursor);
        model = modelElement.getText();
        modelElement.click();
        Thread.sleep(PAUSE);
        loadEngineList();


        int size = engineList.size();
        for (int cursor2 = 0; cursor2 < size; cursor2++)
            scrapeEngine(cursor2);
        selectModelTab();
    }

    private static void scrapeEngine(int cursor) throws InterruptedException {
        Thread.sleep(PAUSE);
        WebElement engineElement = engineList.get(cursor);
        engine = engineElement.getText();
        engine = engine.replaceAll("[\n\r]", "");
        engine = engine.trim();
        if (!engine.equalsIgnoreCase("I Don't Know"))
            VehicleBase.addVehicle(new Vehicle(year, make, model, engine));
    }

    private static WebElement waitForVisibility(By by) throws TimeoutException {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }


    private static void loadYearList() {
        WebElement catYearList = waitForVisibility(By.id("catYearList"));
        yearList = new ArrayList<>();
        List<WebElement> tempYearList = catYearList.findElements(By.tagName("li"));
        for (WebElement element : tempYearList)
            if (element.getText().length() > 3)
                yearList.add(element);
    }

    private static void loadMakeList() {
        WebElement makeContainer = waitForVisibility(By.id("makeContainer"));
        makeList = new ArrayList<>();
        makeList.addAll(makeContainer.findElements(By.tagName("li")));
    }

    private static void loadModelList() {
        WebElement modelContainer = waitForVisibility(By.id("modelContainer"));
        modelList = new ArrayList<>();
        modelList.addAll(modelContainer.findElements(By.tagName("li")));
    }

    private static void loadEngineList() {
        WebElement engineContainer = waitForVisibility(By.id("engineContainer"));
        engineList = new ArrayList<>();
        engineList.addAll(engineContainer.findElements(By.tagName("li")));
    }

    private static void selectYearTab() {
        driver.findElement(By.id("catYearBox")).click();
    }

    private static void selectMakeTab() {
        driver.findElement(By.id("catMakeBox")).click();
    }

    private static void selectModelTab() {
        driver.findElement(By.id("catModelBox")).click();
    }

}

