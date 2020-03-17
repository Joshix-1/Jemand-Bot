package Jemand;

//import org.openqa.selenium.OutputType;
//import org.openqa.selenium.TakesScreenshot;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class WebScreenshotter {
        private static int      DISPLAY_NUMBER  = 99;
        private static String   XVFB            = "/usr/dingd/bin/xvfb";
        private static String   XVFB_COMMAND    = XVFB + " :" + DISPLAY_NUMBER;
        private static String   URL;
        private static String   RESULT_FILENAME;

        public WebScreenshotter(String URL) {
            String date = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_DATE_TIME);
            this.URL = URL;
            RESULT_FILENAME = func.filepathof("screenshot_" + date + ".png");
        }

        public File  makeScreen() throws IOException {
            //System.setProperty("webdriver.chrome.driver", func.filepathof("chromedriver.exe"));
            //System.out.println(0);
            //WebDriver driver = new RemoteWebDriver(new URL("http://185.7.199.94:9515"), new ChromeOptions());
            //System.out.println(1);
            //driver.manage().window().maximize();
            //driver.get(URL);
            //System.out.println(2);
            //File scrFile = ( (TakesScreenshot) driver ).getScreenshotAs(OutputType.FILE);
            //copyFile(scrFile, new File(RESULT_FILENAME));
            //driver.close();
            //return scrFile;
            return new File(func.filepathof("tmp/ws"));
        }

}
