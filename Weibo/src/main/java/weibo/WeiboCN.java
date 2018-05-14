package weibo;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.Set;

public class WeiboCN {

    /**
     * 根据起始时间抓取文章
     *
     * @param startTime
     * @param endTime
     * @return
     * @throws Exception
     */
    public static String getArtical(Long startTime, Long endTime) throws Exception {

        //driver.close();
//        driver = new HtmlUnitDriver();
//        driver.get("https://m.weibo.cn/u/1295674790");
//        System.out.println(driver.getPageSource());
//        FirefoxProfile profile = new FirefoxProfile();
//        profile.setPreference("general.useragent.override", "some UAstring");
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\huangwei\\Desktop\\微博\\chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        driver.get("https://weibo.com/p/1005051295674790/wenzhang");

        Thread.sleep(10000);
        // 获取 网页的 title
        // System.out.println(driver.getPageSource());

        String mainTitle = driver.getTitle();
        List<WebElement> elementList = driver.findElements(By.partialLinkText("题源报刊阅读"));
        // WebElement elements = ((ChromeDriver) driver).findElementById("Pl_Official_Headerv6__1");

        JavascriptExecutor executor = (JavascriptExecutor) driver;


        for (WebElement webElement : elementList) {

            System.out.println(webElement.getAttribute("href"));
            System.out.println(webElement.getText());
            String title = webElement.getText();
            String url = webElement.getAttribute("href");
            webElement.click();


            //test


            Thread.sleep(5000);

            switchToWindow(title, driver);
            WebElement time = driver.findElement(By.className("time"));
            System.out.println(time.getText());
            int index = time.getText().indexOf("发布于");
            String currentTime = time.getText().substring(index + 4, time.getText().length());
            Long current = DateFormatUitl.getTimeStamp(currentTime, "yyyy-MM-dd HH:mm:ss");
            if (startTime < current || endTime > current) {
                break;
            }

            //获取文章内容
//            List<WebElement> webElementText = driver.findElements(By.tagName("p"));
//            for (int i = 0; i < webElementText.size(); i++) {
//                System.out.println(webElementText.get(i).getText());
//            }
//
//            switchToWindow(mainTitle, driver);
//            executor.executeScript("arguments[0].scrollIntoView();", webElement);
            //  driver.close();
            //switchToWindow(mainTitle, driver);
            // openNewWindow(driver, url, title);
        }

        // driver.quit();

        return null;
//        if (result.contains("SUB")) {
//            return result;
//        } else {
//            throw new Exception("weibo login failed");
//        }
    }

    public static WebDriver openNewWindow(WebDriver driver, String url, String titleName) {
        Actions action = new Actions(driver);
        action.sendKeys(Keys.CONTROL + "t").perform();
        switchToWindow(titleName, driver);
        driver.get(url);
        return driver;
    }

    public static boolean switchToWindow(String windowTitle, WebDriver dr) {
        boolean flag = false;
        try {
            //将页面上所有的windowshandle放在入set集合当中
            String currentHandle = dr.getWindowHandle();
            Set<String> handles = dr.getWindowHandles();
            for (String s : handles) {
                if (s.equals(currentHandle))
                    continue;
                else {
                    dr.switchTo().window(s);
                    //和当前的窗口进行比较如果相同就切换到windowhandle
                    //判断title是否和handles当前的窗口相同
                    if (dr.getTitle().contains(windowTitle)) {
                        flag = true;
                        System.out.println("Switch to window: "
                                + windowTitle + " successfully!");
                        break;
                    } else
                        continue;
                }
            }
        } catch (Exception e) {
            System.out.printf("Window: " + windowTitle
                    + " cound not found!", e.fillInStackTrace());
            flag = false;
        }
        flag = true;
        return flag;
    }

    public static String concatCookie(HtmlUnitDriver driver) {
        Set<Cookie> cookieSet = driver.manage().getCookies();
        StringBuilder sb = new StringBuilder();
        for (Cookie cookie : cookieSet) {
            sb.append(cookie.getName() + "=" + cookie.getValue() + ";");
        }
        String result = sb.toString();
        return result;
    }
}
