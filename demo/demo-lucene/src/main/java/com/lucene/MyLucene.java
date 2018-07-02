package com.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
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
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class MyLucene {
	public static void main(String[] args) throws IOException {
		//将商品数据转化为文档对象
		Document doc = new Document();
		doc.add(new LongField("id",562379L,Store.YES));
		doc.add(new TextField("title","京淘电商，三星 W999 黑色 电信3G手机 双卡双待双通",Store.YES));
		doc.add(new TextField("sellPoint","下单送12000毫安移动电源！双3.5英寸魔焕炫屏，以非凡视野纵观天下时局，尊崇翻盖设计，张弛中，尽显从容气度！",Store.YES));
		doc.add(new LongField("price",4299000L,Store.YES));
		doc.add(new LongField("num",99999,Store.YES));
		doc.add(new StringField("image","http://image.jt.com/jd/d2ac340e728d4c6181e763e772a9944a.jpg",Store.YES));
		
		//索引位置
		Directory dir = FSDirectory.open(new File("index"));	//在工程目录中创建文件
		//定义分词器（标准分词器）
		Analyzer analyzer = new StandardAnalyzer();
		//定义索引配置
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
		//设置是追加还是新建，OpenMode.APPEND在原有索引上追加，OpenMode.APPEND删除原索引新建，默认APPEND方式。
		config.setOpenMode(OpenMode.CREATE);
		//定义索引对象
		IndexWriter indexWriter = new IndexWriter(dir, config);
		//写入数据
		indexWriter.addDocument(doc);
		//关闭
		indexWriter.close();
	}
	
    /**
     * 使用IK分词器写入索引
     * 
     * @throws Exception
     */
    @Test
    public void testIndex2() throws Exception {
        // 将商品数据转化为文档对象
        Document document = new Document();// 商品数据
        document.add(new LongField("id", 562379L, Store.YES));
        document.add(new TextField("title", "京淘电商，三星 W999 黑色 电信3G手机 双卡双待双通 京淘电商", Store.YES));
        document.add(new TextField("sellPoint", "下单送12000毫安移动电源！双3.5英寸魔焕炫屏，以非凡视野纵观天下时局，尊崇翻盖设计，张弛中，尽显从容气度！",Store.YES));
        document.add(new LongField("price", 299000L, Store.YES));
        document.add(new LongField("num", 99999L, Store.YES));
        document.add(new StringField("image","http://image.jt.com/jd/4ef8861cf6854de9889f3db9b24dc371.jpg", Store.YES));

        // 索引位置
        Directory directory = FSDirectory.open(new File("index"));
        // 定义分词器(IK分词器)
        Analyzer analyzer = new IKAnalyzer();

        // 定义索引配置
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
        indexWriterConfig.setOpenMode(OpenMode.CREATE);
        // 定义索引对象
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        // 写入数据
        indexWriter.addDocument(document);
        // 关闭
        indexWriter.close();
    }

    @Test
    public void testQuery() throws Exception {
        // 定义索引位置
        Directory directory = FSDirectory.open(new File("index"));

        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(directory));
        // 构造查询对象，词条搜索
        Query query = new TermQuery(new Term("title", "三"));

        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("搜索结果总结：" + topDocs.totalHits);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            System.out.println("得分：" + scoreDoc.score);
            // 通过文档id查询文档数据
            Document doc = indexSearcher.doc(scoreDoc.doc);

            System.out.println("商品ID：" + doc.get("id"));
            System.out.println("商品标题：" + doc.get("title"));
            System.out.println("商品卖点：" + doc.get("sellPoint"));
            System.out.println("商品价格：" + doc.get("price"));
            System.out.println("商品图片：" + doc.get("image"));

        }
    }
    
    @Test
    public void testQueryParser() throws Exception {
        // 定义索引位置
        Directory directory = FSDirectory.open(new File("index"));

        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(directory));

        Analyzer analyzer = new IKAnalyzer(); // 定义分词器(标准分词器)
        QueryParser parser = new QueryParser("title", analyzer); // 定义查询分析器
        Query query = parser.parse("三星"); // 构造查询对象，分词查询

        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("搜索结果总结：" + topDocs.totalHits);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            System.out.println("得分：" + scoreDoc.score);
            // 通过文档id查询文档数据
            Document doc = indexSearcher.doc(scoreDoc.doc);

            System.out.println("商品ID：" + doc.get("id"));
            System.out.println("商品标题：" + doc.get("title"));
            System.out.println("商品卖点：" + doc.get("sellPoint"));
            System.out.println("商品价格：" + doc.get("price"));
            System.out.println("商品图片：" + doc.get("image"));

        }
    }

    //展示信息
    private void showQuery(Query query) throws Exception {
        // 定义索引位置
        Directory directory = FSDirectory.open(new File("index"));

        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(directory));

        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("搜索结果总结：" + topDocs.totalHits);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            System.out.println("得分：" + scoreDoc.score);
            // 通过文档id查询文档数据
            Document doc = indexSearcher.doc(scoreDoc.doc);

            System.out.println("商品ID：" + doc.get("id"));
            System.out.println("商品标题：" + doc.get("title"));
            System.out.println("商品卖点：" + doc.get("sellPoint"));
            System.out.println("商品价格：" + doc.get("price"));
            System.out.println("商品图片：" + doc.get("image"));

        }
    }
    
    
    @Test
    public void testTermQuery() throws Exception {
        Query query = new TermQuery(new Term("title", "三星"));
        showQuery(query);
    }
    
    /**
     * 循环准备100条测试数据
     * 
     * @throws Exception
     */
    @Test
    public void testIndex3() throws Exception {
        // 索引位置
        Directory directory = FSDirectory.open(new File("index"));
        // 定义分词器(标准分词器)
        Analyzer analyzer = new IKAnalyzer();

        // 定义索引配置
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
        indexWriterConfig.setOpenMode(OpenMode.CREATE);

        // 定义索引对象
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

        for (int i = 0; i < 100; i++) {
            // 将商品数据转化为文档对象
            Document document = new Document();// 商品数据
            document.add(new LongField("id", i, Store.YES));
            document.add(new TextField("title", "苹果（Apple）iPhone 6 (A1586) 16GB 金色 移动联通电信4G手机  " + i, Store.YES));
            document.add(new TextField("sellPoint",
                    "下单送12000毫安移动电源！双3.5英寸魔焕炫屏，以非凡视野纵观天下时局，尊崇翻盖设计，张弛中，尽显从容气度！" + i, Store.YES));
            document.add(new LongField("price", 100 * i, Store.YES));
            document.add(new LongField("num", 99999L, Store.YES));
            document.add(new StringField("image",
                    "http://image.taotao.com/jd/4ef8861cf6854de9889f3db9b24dc371.jpg", Store.YES));

            // 写入数据
            indexWriter.addDocument(document);
        }

        // 关闭
        indexWriter.close();
    }
    
    /**
     * 范围搜索
     * 
     * @throws Exception
     */
    @Test
    public void testNumericRangeQuery() throws Exception {
        // 设置查询字段、最小值、最大值、最小值是否包含边界，最大值是否包含边界
        Query query = NumericRangeQuery.newLongRange("id", 1L, 10L, false, true);
        showQuery(query);
    }

    /**
     * 匹配全部
     * 
     * @throws Exception
     */
    @Test
    public void testMatchAllDocsQuery() throws Exception {
        Query query = new MatchAllDocsQuery();
        showQuery(query);
    }
    
    /**
     * 模糊搜索
     * 
     * @throws Exception
     */
    @Test
    public void testWildcardQuery() throws Exception {
        Query query = new WildcardQuery(new Term("title", "2*"));
        showQuery(query);
    }
    
    /**
     * 相识度搜索
     * 
     * @throws Exception
     */
    @Test
    public void testFuzzyQuery() throws Exception {
        Query query = new FuzzyQuery(new Term("title", "2"));
        showQuery(query);
    }
    
    /**
     * 组合搜索
     * 
     * @throws Exception
     */
    @Test
    public void testBooleanQuery() throws Exception {
        BooleanQuery query = new BooleanQuery();
        //必须包含“苹果”
        query.add(new TermQuery(new Term("title", "苹果")), Occur.MUST);
        //不能包含apple，不区分大小写
        query.add(new FuzzyQuery(new Term("title", "apple")), Occur.SHOULD);
        showQuery(query);
    }
}
