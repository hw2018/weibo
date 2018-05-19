package weibo;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

/**
 * 该登录算法适用时间: 2017-6-2 —— ?
 * 利用WebCollector和获取的cookie爬取新浪微博并抽取数据
 *
 * @author hu
 */
public class WeiboCrawler extends BreadthCrawler {

    String cookie;

    public WeiboCrawler(String crawlPath) throws Exception {
        super(crawlPath, false);
        /* 获取新浪微博的cookie，账号密码以明文形式传输，请使用小号 */
        String startTime = "2018-05-01 00:00:00";
        String endTime = "2018-06-01 00:00:00";
        WeiboCN.getArtical(DateFormatUitl.getTimeStamp(startTime, "yyyy-MM-dd HH:mm:ss"), DateFormatUitl.getTimeStamp(endTime, "yyyy-MM-dd HH:mm:ss"), "C:\\Users\\huangwei\\Desktop\\微博\\题源报刊");

        //设置线程数
        setThreads(3);
        //设置每个线程的爬取间隔
        getConf().setExecuteInterval(1000);
    }

    @Override
    public Page getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest(crawlDatum);
        request.setCookie(cookie);
        return request.responsePage();
    }

    public void visit(Page page, CrawlDatums next) {
        System.out.println(new String(page.content()));
//        int pageNum = Integer.valueOf(page.meta("pageNum"));
//        /* 抽取微博 */
//        Elements weibos = page.select("div.c[id]");
//        for (Element weibo : weibos) {
//            System.out.println("第" + pageNum + "页\t" + weibo.text());
//        }
    }

    public static void main(String[] args) throws Exception {
        WeiboCrawler crawler = new WeiboCrawler("crawl_weibo");

    }

}