package com.jd.crawler;

import java.io.File;
import java.util.Map;

import com.jd.crawler.service.HttpService;
import com.jd.crawler.service.PropertieService;


/**
 * 图片下载
 */
public class ImageDownloadCrawler implements Crawler {

	/**
	 * key：原始图片地址 
	 * value:新图片地址
	 */
	private Map<String, String> urlMapping;

	public ImageDownloadCrawler(Map<String, String> urlMapping) {
		this.urlMapping = urlMapping;
	}

	@Override
	public void run() {
		if (this.urlMapping == null || this.urlMapping.isEmpty()) {
			return;
		}
		HttpService httpService = Main.applicationContext.getBean(HttpService.class);
		PropertieService propertieService =  Main.applicationContext.getBean(PropertieService.class);
		for (Map.Entry<String, String> entry : this.urlMapping.entrySet()) {
			try {
				httpService.downloadFile(entry.getKey(), new File(propertieService.IMAGE_DIR + entry.getValue()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
