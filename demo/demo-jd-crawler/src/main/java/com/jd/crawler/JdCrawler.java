package com.jd.crawler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jd.pojo.Item;
import com.jd.service.ApiService;

@Component
public class JdCrawler implements Crawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdCrawler.class);

    @Autowired
    private ApiService apiService;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void start() throws Exception {
        Integer pages = getPages();
        LOGGER.info("总页数为: {}", pages);
        for (int i = 1; i <= pages; i++) {
            LOGGER.info("当前页是 {}/{}", i, pages);
            Collection<Item> items = getItems(i);
            // 保存数据库
            // 保存索引库
        }
    }

    private Integer getPages() throws Exception {
        String url = "http://list.jd.com/list.html?cat=9987,653,655";
        // 获取到页面数据
        String html = this.apiService.doGet(url);
        // 解析html
        Document document = Jsoup.parse(html);
        String count = document.select(".fp-text").text();
        return Integer.parseInt(StringUtils.substringAfter(count, "/"));
    }

    private Collection<Item> getItems(Integer page) throws Exception {
        String url = "http://list.jd.com/list.html?cat=9987,653,655&page=" + page;
        LOGGER.info("执行请求 URL = {}", url);
        // 获取到页面数据
        String html = this.apiService.doGet(url);
        // 解析html
        Document document = Jsoup.parse(html);
        Elements lis = document.select("#plist li");

        Map<Long, Item> items = new HashMap<Long, Item>();
        for (Element li : lis) {
            Long id = Long.valueOf(li.attr("data-sku"));
            String title = li.select(".p-name em").text();
            String image = li.select(".p-img img").attr("data-lazy-img");

            Item item = new Item();
            item.setId(id);
            item.setTitle(title);
            item.setImage(image);
            items.put(id, item);
        }

        // 获取商品卖点数据
        List<String> pids = new ArrayList<String>();
        for (Long id : items.keySet()) {
            pids.add("AD_" + id);
        }
        String adUrl = "http://ad.3.cn/ads/mgets?skuids=" + StringUtils.join(pids, ',');
        String adJson = this.apiService.doGet(adUrl);
        ArrayNode arrayNode = (ArrayNode) MAPPER.readTree(adJson);
        for (JsonNode jsonNode : arrayNode) {
            String id = StringUtils.substringAfter(jsonNode.get("id").asText(), "AD_");
            String ad = jsonNode.get("ad").asText();
            ad = Jsoup.parse(ad).text();
            items.get(Long.valueOf(id)).setSellPoint(ad);
        }

        // 获取商品的价格
        pids = new ArrayList<String>();
        for (Long id : items.keySet()) {
            pids.add("J_" + id);
        }
        String priceUrl = "http://p.3.cn/prices/mgets?type=1&skuIds=" + StringUtils.join(pids, ',');
        String priceJson = this.apiService.doGet(priceUrl);

        arrayNode = (ArrayNode) MAPPER.readTree(priceJson);
        for (JsonNode jsonNode : arrayNode) {
            String id = StringUtils.substringAfter(jsonNode.get("id").asText(), "J_");
            Long price = jsonNode.get("p").asLong();
            items.get(Long.valueOf(id)).setPrice(price);
        }
        
        // 获取商品描述
        for (Long id : items.keySet()) {
            String descUrl = "http://d.3.cn/desc/"+id;
            String jsonData = this.apiService.doGet(descUrl,"GBK");
            jsonData = StringUtils.substringAfter(jsonData, "showdesc(");
            jsonData = StringUtils.substringBeforeLast(jsonData, ")");
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            String desc = jsonNode.get("content").asText();
            items.get(id).setDesc(desc);
        }

        return items.values();

    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
