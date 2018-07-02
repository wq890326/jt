package com.jd.crawler.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLEncoder;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class TestSearch {

    private ApplicationContext applicationContext;

    @Before
    public void init() {
        applicationContext = new ClassPathXmlApplicationContext("spring/*.xml");
    }

    /**
     * 基本搜索
     * 
     * @throws Exception
     */
    @Test
    public void search1() throws Exception {
        Directory directory = applicationContext.getBean(Directory.class);
        // 定义索引搜索对象
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(directory));

        // 搜索关键字
        String keyWords = "iphone";

        // 构建查询对象
        Query query = new QueryParser("title", new IKAnalyzer()).parse(keyWords);

        // 搜索
        TopDocs topDocs = indexSearcher.search(query, Integer.MAX_VALUE);

        System.out.println("“" + keyWords + "” 搜索到 " + topDocs.totalHits + " 条数据.");

        for (ScoreDoc doc : topDocs.scoreDocs) {
            System.out.println("得分：" + doc.score);
            Document document = indexSearcher.doc(doc.doc);
            System.out.println("ID: " + document.get("id"));
            System.out.println("Title: " + document.get("title"));
            System.out.println("SellPoint: " + document.get("sellPoint"));
            System.out.println("Price: " + document.get("price"));
            System.out.println("---------------");
        }
    }

    /**
     * 分页搜索
     * 
     * @throws Exception
     */
    @Test
    public void search2() throws Exception {
        Directory directory = applicationContext.getBean(Directory.class);
        // 定义索引搜索对象
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(directory));

        // 搜索关键字
        String keyWords = "手机";

        // 分页信息
        Integer page = 3;
        Integer pageSize = 20;
        Integer start = (page - 1) * pageSize;
        Integer end = start + pageSize;

        // 构建查询对象
        Query query = new QueryParser("title", new IKAnalyzer()).parse(keyWords);

        // 搜索
        TopDocs topDocs = indexSearcher.search(query, end); // 根据end查询

        Integer totalPage = ((topDocs.totalHits / pageSize) == 0) ? topDocs.totalHits / pageSize
                : ((topDocs.totalHits / pageSize) + 1);
        System.out.println("“" + keyWords + "” 搜索到 " + topDocs.totalHits + " 条数据. 页数：" + page + "/"
                + totalPage);

        ScoreDoc[] docs = topDocs.scoreDocs;
        for (int i = start; i < end; i++) {
            ScoreDoc doc = docs[i];
            System.out.println("得分：" + doc.score);
            Document document = indexSearcher.doc(doc.doc);
            System.out.println("ID: " + document.get("id"));
            System.out.println("Title: " + document.get("title"));
            System.out.println("SellPoint: " + document.get("sellPoint"));
            System.out.println("Price: " + document.get("price"));
            System.out.println("---------------");
        }

    }

    /**
     * 分页并且高亮显示
     * 
     * @throws Exception
     */
    @Test
    public void search3() throws Exception {
        Directory directory = applicationContext.getBean(Directory.class);
        // 定义索引搜索对象
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(directory));

        // 搜索关键字
        String keyWords = "手机";

        // 分页信息
        Integer page = 2;
        Integer pageSize = 50;
        Integer start = (page - 1) * pageSize;
        Integer end = start + pageSize;

        Analyzer analyzer = new IKAnalyzer();

        // 构建查询对象
        Query query = new QueryParser("title", analyzer).parse(keyWords);

        // 定义高亮组件
        Formatter formatter = new SimpleHTMLFormatter("<span class='red'>", "</span>");
        // 构建Scorer,用于选取最佳切片
        Scorer scorer = new QueryScorer(query);
        // 实例化Highlighter组件
        Highlighter highlighter = new Highlighter(formatter, scorer);
        // 构建Fragmenter对象,用于文档切片
        highlighter.setTextFragmenter(new SimpleFragmenter(100));
        highlighter.setEncoder(new SimpleHTMLEncoder());

        // 搜索
        TopDocs topDocs = indexSearcher.search(query, end); // 根据end查询

        Integer totalPage = ((topDocs.totalHits / pageSize) == 0) ? topDocs.totalHits / pageSize
                : ((topDocs.totalHits / pageSize) + 1);
        System.out.println("“" + keyWords + "” 搜索到 " + topDocs.totalHits + " 条数据. 页数：" + page + "/"
                + totalPage);

        ScoreDoc[] docs = topDocs.scoreDocs;
        for (int i = start; i < end; i++) {
            ScoreDoc doc = docs[i];
            System.out.println("得分：" + doc.score);
            Document document = indexSearcher.doc(doc.doc);
            System.out.println("ID: " + document.get("id"));
            String title = document.get("title");
            System.out.println("Title: " + title);
            title = highlighter.getBestFragment(analyzer, "title", title);

            System.out.println("Title(高亮): " + title);
            System.out.println("SellPoint: " + document.get("sellPoint"));
            System.out.println("Price: " + document.get("price"));
            System.out.println("---------------");
        }

    }

}
