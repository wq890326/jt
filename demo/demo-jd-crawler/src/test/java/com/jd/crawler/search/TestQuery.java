package com.jd.crawler.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestQuery {

    private ApplicationContext applicationContext;

    @Before
    public void init() {
        applicationContext = new ClassPathXmlApplicationContext("spring/*.xml");
    }

    /**
     * 词条搜索
     * 
     * @throws Exception
     */
    @Test
    public void testTermQuery() throws Exception {
        Query query = new TermQuery(new Term("title", "手机"));
        show(query);
    }

    /**
     * 范围搜索
     * 
     * @throws Exception
     */
    @Test
    public void testNumericRangeQuery() throws Exception {
        // 设置查询字段、最小值、最大值、最小值是否包含边界，最大值是否包含边界
        Query query = NumericRangeQuery.newLongRange("id", 1L, 100L, true, true);
        show(query);
    }

    /**
     * 匹配全部
     * 
     * @throws Exception
     */
    @Test
    public void testMatchAllDocsQuery() throws Exception {
        Query query = new MatchAllDocsQuery();
        show(query);
    }

    /**
     * 模糊搜索
     * 
     * @throws Exception
     */
    @Test
    public void testWildcardQuery() throws Exception {
        Query query = new WildcardQuery(new Term("title", "液*视"));
        show(query);
    }

    /**
     * 相似度搜索
     * 
     * @throws Exception
     */
    @Test
    public void testFuzzyQuery() throws Exception {
        Query query = new FuzzyQuery(new Term("title", "大视"), 2);
        show(query);
    }

    /**
     * 组合条件搜索
     * 
     * @throws Exception
     */
    @Test
    public void testBooleanQuery() throws Exception {
        BooleanQuery booleanQuery = new BooleanQuery();

        Query query1 = new TermQuery(new Term("title", "手机"));
        booleanQuery.add(query1, Occur.MUST);// 必须包含“手机”
        Query query2 = new TermQuery(new Term("title", "老人"));
        booleanQuery.add(query2, Occur.MUST_NOT);// 不能出现“老人”
        Query query3 = new TermQuery(new Term("title", "智能"));
        booleanQuery.add(query3, Occur.SHOULD);// 或者包含“智能”

        show(booleanQuery);
    }

    private void show(Query query) throws Exception {
        Directory directory = applicationContext.getBean(Directory.class);
        // 定义索引搜索对象
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(directory));

        // 搜索
        TopDocs topDocs = indexSearcher.search(query, 20);

        System.out.println("搜索到 " + topDocs.totalHits + " 条数据.");

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

}
