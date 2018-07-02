package com.jd.crawler.service;

import java.io.File;
import java.net.URI;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HttpService {

    private static final String CHARSET = "UTF-8";

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpService.class);

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private RequestConfig requestConfig;

    public String doGet(String url) throws Exception {
        return doGet(url, null, "UTF-8");
    }

    /**
     * 执行GET请求
     */
    public String doGet(String url, String encode) throws Exception {
        return doGet(url, null, encode);
    }

    /**
     * 执行GET请求
     */
    public String doGet(String url, Map<String, String> parapms, String encode) throws Exception {
        URI uri = null;
        if (null == parapms) {
            uri = URI.create(url);
        } else {
            // 设置参数
            URIBuilder builder = new URIBuilder(url);
            for (Map.Entry<String, String> entry : parapms.entrySet()) {
                builder.addParameter(entry.getKey(), entry.getValue());
            }
            uri = builder.build();
        }
        LOGGER.info("执行Http Get请求，URL：" + uri);
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            if(encode == null){
                encode = CHARSET;
            }
            return EntityUtils.toString(response.getEntity(), encode);
        } finally {
            response.close();
        }
    }

    /**
     * 下载文件
     * 
     * @param url 文件url
     * @param dest 目标目录
     * @throws Exception
     */
    public void downloadFile(String url, File dest) throws Exception {
        LOGGER.info("下载文件，URL：" + url);
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            FileUtils.writeByteArrayToFile(dest, IOUtils.toByteArray(response.getEntity().getContent()));
        } finally {
            response.close();
        }
    }

}
