package com.crawler;

import org.jsoup.select.Elements;

import com.brucezee.jspider.Page;
import com.brucezee.jspider.Request;
import com.brucezee.jspider.Result;
import com.brucezee.jspider.processor.PageProcessor;

public class HelloWorldSample {
	public static void main(String[] args) {
        //create, config and start
//        Spider.create()                                                 //创建爬虫实例
//                .addStartRequests("https://www.baidu.com")           //添加起始url
//                .setPageProcessor(new HelloWorldPageProcessor())        //设置页面解析�?
//                .start();                                               //�?始抓�?
    }

    public static class HelloWorldPageProcessor implements PageProcessor {
        @Override
        public Result process(Request request, Page page) {
            Result result = new Result();

            //解析HTML采用jsoup框架，详见：https://jsoup.org/

            //解析页面标题
            result.put("xzxl_xl_nr", page.document().getElementsByClass("xzxl_xl_nr").html());

            //获取页面上的新的链接地址
            Elements elements = page.document().select("a");        //获取�?有a标签
            for (int i = 0; i < elements.size(); i++) {
                String url = elements.get(i).absUrl("href");    //获取绝对url
                if (url != null && url.contains("credit.fuzhou.gov.cn/qyxy/qyxyfw")) {
                    page.addTargetRequest(url);     //获取新url添加到任务队�?
                }
            }

            return result;
        }
    }
}
