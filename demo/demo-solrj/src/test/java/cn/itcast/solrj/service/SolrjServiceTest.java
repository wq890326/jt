package com.solrj.service;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.junit.Before;
import org.junit.Test;

import com.solrj.pojo.Foo;
import com.solrj.service.SolrjService;

public class SolrjServiceTest {

    private SolrjService solrjService;
    
    private HttpSolrServer httpSolrServer;

    @Before
    public void setUp() throws Exception {
        // 在url中指定core名称：jt
        String url = "http://solr.jt.com/jt";
        HttpSolrServer httpSolrServer = new HttpSolrServer(url); //定义solr的server
        httpSolrServer.setParser(new XMLResponseParser()); // 设置响应解析器
        httpSolrServer.setMaxRetries(1); // 设置重试次数，推荐设置为1
        httpSolrServer.setConnectionTimeout(500); // 建立连接的最长时间

        this.httpSolrServer = httpSolrServer;
        solrjService = new SolrjService(httpSolrServer);
    }

    @Test
    public void testAdd() throws Exception {
        Foo foo = new Foo();
        foo.setId(1425955126820L);
        foo.setTitle("new - 轻量级Java EE企业应用实战（第3版）：Struts2＋Spring3＋Hibernate整合开发（附CD光盘）");

        this.solrjService.add(foo);
    }

    @Test
    public void testDelete() throws Exception {
        this.solrjService.delete(Arrays.asList("1433151614240"));
    }

    @Test
    public void testSearch() throws Exception {
        List<Foo> foos = this.solrjService.search("java", 1, 10);
        for (Foo foo : foos) {
            System.out.println(foo);
        }
    }
    
    @Test
    public void testDeleteByQuery() throws Exception{
        httpSolrServer.deleteByQuery("title:本色（精装图文版）");
        httpSolrServer.commit();
    }

}
