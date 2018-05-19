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

import java.io.File;
import java.util.List;
import java.util.Set;

public class WeiboCN {

    /**
     * 根据起始时间抓取微博文章
     *
     * @param startTime
     * @param endTime
     * @return
     * @throws Exception
     */
    public static String getArtical(Long startTime, Long endTime, String savePath) throws Exception {

        //driver.close();
//        driver = new HtmlUnitDriver();
//        driver.get("https://m.weibo.cn/u/1295674790");
//        System.out.println(driver.getPageSource());
//        FirefoxProfile profile = new FirefoxProfile();
//        profile.setPreference("general.useragent.override", "some UAstring");
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\huangwei\\Desktop\\微博\\chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        driver.get("https://weibo.com/p/1005051295674790/wenzhang");

        JavascriptExecutor executor = (JavascriptExecutor) driver;

        Thread.sleep(10000);
        // 获取 网页的 title
        // System.out.println(driver.getPageSource());

        String mainTitle = driver.getTitle();

        //如果有下一页执行抓取数据
        while (driver.findElements(By.partialLinkText("下一页")).size() > 0) {
            //找出所以calss为w_autocut的标签
            List<WebElement> elementList = driver.findElements(By.className("W_autocut"));
            for (WebElement element : elementList) {
                //找出所以标签为a的元素
                executor.executeScript("window.scrollTo(arguments[0],arguments[1]);", 0, element.getLocation().y - 50);
                if (element.getTagName().equals("a")) {
                    //找出标签内容为题源报刊阅读
                    if (element.getText().indexOf("题源报刊") > -1) {
                        String title = element.getText();
                        //滚动到当前元素 由于微博有遮罩层  Y-100

                        //点开新页面
                        element.click();
                        Thread.sleep(5000);

                        //跳到新页面
                        switchToWindow(title, driver);

                        //进行页面操作
                        try {
                            WebElement time = driver.findElement(By.className("time"));

                            System.out.println(time.getText());
                            int index = time.getText().indexOf("发布于");
                            String currentTime = time.getText().substring(index + 4, time.getText().length());


                            Long current = DateFormatUitl.getTimeStamp(currentTime, "yyyy-MM-dd HH:mm:ss");
                            //时间判断
                            //如果还是时间已经大于当前时间  那么已经抓取完毕
                            if (startTime > current) {
                                System.out.println("数据抓取完毕!");
                                driver.quit();
                                return null;
                            }
                            //如果时间大于结束时间 说明还没有到所要抓取的区间  不进行文章抓取
                            if (current > endTime) {

                            } else {

                                //创建文件
                                String fileTitle = title.replaceAll(" ", "");
                                fileTitle = fileTitle.substring(fileTitle.indexOf("|") + 1, fileTitle.indexOf("（"));
                                String currentDate = currentTime.substring(0, currentTime.indexOf(" "));
                                //文件创建有问题
                                //fileTitle.substring("")
                                String filePath = savePath + "\\" + fileTitle + currentDate + ".txt";
                                File currentFile = new File(filePath);
                                if (!FileUtil.createFile(currentFile)) {
                                    System.out.println("文件创建失败");
                                    return null;
                                }

                                //写入标题和时间
                                FileUtil.fileChaseFW(filePath, title);
                                //换行
                                FileUtil.fileChaseFW(filePath, "\r\n");
                                FileUtil.fileChaseFW(filePath, time.getText());
                                FileUtil.fileChaseFW(filePath, "\r\n");
                                //获取文章内容
                                //写入txt
                                List<WebElement> webElementText = driver.findElements(By.tagName("p"));
                                for (int i = 0; i < webElementText.size(); i++) {
                                    FileUtil.fileChaseFW(filePath, webElementText.get(i).getText());
                                    FileUtil.fileChaseFW(filePath, "\r\n");
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("该页面数据抓取失败");
                        }

                        switchToWindow(mainTitle, driver);
                    }
                }

                //    executor.executeScript("arguments[0].scrollIntoView();", element);
            }
            closeOtherWindow(driver, driver.getWindowHandle());

            driver.findElements(By.partialLinkText("下一页")).get(0).click();
            Thread.sleep(10000);

        }
        return null;

    }


    /**
     * 关闭其他窗口
     *
     * @param driver
     * @param wiindowHandle
     */
    public static void closeOtherWindow(WebDriver driver, String wiindowHandle) {
        try {
            //将页面上所有的windowshandle放在入set集合当中
            Set<String> handles = driver.getWindowHandles();
            for (String s : handles) {
                if (!s.equals(wiindowHandle)) {
                    driver.switchTo().window(s);
                    driver.close();
                    driver.switchTo().window(wiindowHandle);
                }
            }
        } catch (Exception e) {
            System.out.println("关闭失败");
        }


    }

    /**
     * 打开新窗口
     *
     * @param driver
     * @param url
     * @param titleName
     * @return
     */
    public static WebDriver openNewWindow(WebDriver driver, String url, String titleName) {
        Actions action = new Actions(driver);
        action.sendKeys(Keys.CONTROL + "t").perform();
        switchToWindow(titleName, driver);
        driver.get(url);
        return driver;
    }

    /**
     * 切换窗口
     *
     * @param windowTitle
     * @param dr
     * @return
     */
    public static boolean switchToWindow(String windowTitle, WebDriver dr) {
        boolean flag = false;
        try {
            //将页面上所有的windowshandle放在入set集合当中
            String currentHandle = dr.getWindowHandle();
            Set<String> handles = dr.getWindowHandles();
            for (String s : handles) {
                if (s.equals(currentHandle)) {
                    continue;
                } else {
                    dr.switchTo().window(s);
                    //和当前的窗口进行比较如果相同就切换到windowhandle
                    //判断title是否和handles当前的窗口相同
                    if (dr.getTitle().contains(windowTitle)) {
                        flag = true;
                        System.out.println("Switch to window: "
                                + windowTitle + " successfully!");
                        break;
                    } else {
                        continue;
                    }
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
