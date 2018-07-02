package jsoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJD {

	private static final Logger log = Logger.getLogger(TestJD.class);
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@Test	//访问商品详情页面，抓取商品标题
	public void test() throws IOException{
		//创建Jsoup访问链接，得到html源代码
		String url = "https://item.jd.com/3499302.html";
		Document doc = Jsoup.connect(url).get();
		
		//利用选择器快速定位元素，就可以获取其值	.itemInfo-wrap 
		Elements eles = doc.select("#itemInfo #name h1");	//找到一个元素集合
		for(Element ele : eles){
			String title = ele.text();		//当前元素的text值
			System.out.println(title);
		}
		
		//抓取价格
		String urlPrice = "http://p.3.cn/prices/mgets?skuIds=J_3499302&type=1";
		String jsonPriceObject = Jsoup.connect(urlPrice).ignoreContentType(true).execute().body();
		JsonNode jsonNode = MAPPER.readTree(jsonPriceObject);
		//返回json是一个数组，获取数组的第一个元素
		Long price = jsonNode.get(0).get("p").asLong();
		System.out.println(price);
	}
	
	@Test //主要分类有1183
	public void classLevel3() throws IOException{
		List<String> classLevel3List = this.getClassLevel3();
	}
	
	//第一步：获取所有是三级分类 1286个三级分类
	public List<String> getClassLevel3() throws IOException{
		List<String> classLevel3 = new ArrayList<String>();
		//请求所有分类页面
		String url = "https://www.jd.com/allSort.aspx";
		Document doc = Jsoup.connect(url).get();
		Elements eles = doc.select("dd a");	//多个标签时中间用空格隔开
		for(Element ele :eles){
			String name = ele.text();		//获取三级分类名称
			String href = ele.attr("href");	//获取三级分类的链接，包含三级分类id值
			
			System.out.println("三级分类名称："+name+" 链接："+href);
			
			//不是所有的分类都符合规则，对于特殊分类要单独处理。主流：http://list.jd.com/list.html?cat=1713,4855,4859
			if(href.startsWith("//list.jd.com/list.html?cat=")){	//过滤数据
				classLevel3.add("http:"+href);	//补请求协议，否则调用时报错
			}
		}
		
		return classLevel3;
	}
	
	@Test
	public void testPage() throws IOException{
		Integer page = this.getPages("https://list.jd.com/list.html?cat=9987,653,655");
		System.out.println(page);
	}
	
	//抓取某个分类下商品列表的页数，参数分类url
	public Integer getPages(String classUrl) throws IOException{
		Integer page = 0;
		Document doc = Jsoup.connect(classUrl).get();
		Elements eles = doc.select(".fp-text i");
		for(Element ele : eles){
			page = Integer.valueOf(ele.text());
		}
		return page;
	}
	
	@Test
	public void testItems() throws IOException{
		this.getItemUrlByPage("https://list.jd.com/list.html?cat=9987,653,655&page=1");
	}
	
	//抓取某个分类下某页的商品id
	//https://list.jd.com/list.html?cat=9987,653,655&page=1
	public List<String> getItemUrlByPage(String classUrlPage) throws IOException{
		List<String> itemIdUrl = new ArrayList<String>();
		Document doc = Jsoup.connect(classUrlPage).get();
		Elements eles = doc.select(".gl-item .p-img a");
		for(Element ele : eles){
			String url = ele.attr("href");
			System.out.println(url);
			itemIdUrl.add("http:"+url);
		}
		System.out.println("当前分类当前页面有多少个商品："+eles.size());
		return itemIdUrl;
	}
	
	@Test
	public void testItem() throws IOException{
		String url = "http://item.jd.com/3499302.html";
		Item item = this.getItem(url);
	}
	
	//从商品详情页抓取商品详细信息 http://item.jd.com/3499302.html
	public Item getItem(String url) throws IOException{
		Item item = new Item();
		
		//抓取商品的标题
		Document doc = Jsoup.connect(url).get();
		//利用选择器快速定位元素，就可以获取其值	.itemInfo-wrap 
		Elements eles = doc.select("#itemInfo #name h1");	//找到一个元素集合
		String title = "";
		for(Element ele : eles){
			title = ele.text();		//当前元素的text值
		}
		item.setId(Long.valueOf(url.replace("http://item.jd.com/", "").replace(".html", "")));
		item.setTitle(title);
		//item.setCid(cid);	从前面记录留下商品分类的id
		
		//获取价格
		String urlPrice = "http://p.3.cn/prices/mgets?skuIds=J_"+item.getId()+"&type=1";
		try{
			String jsonPriceObject = Jsoup.connect(urlPrice).ignoreContentType(true).execute().body();
			JsonNode jsonNodePrice = MAPPER.readTree(jsonPriceObject);
			//返回json是一个数组，获取数组的第一个元素
			Long price = jsonNodePrice.get(0).get("p").asLong();
			item.setPrice(price);
		}catch(Exception e){
			item.setPrice(-1L);
		}
		//获取卖点 http://ad.3.cn/ads/mgets?skuids=AD_1411013,AD_1411014
		String urlSellPoint = "http://ad.3.cn/ads/mgets?skuids=AD_"+item.getId();
		try{
			String jsonSellPointObject = Jsoup.connect(urlSellPoint).ignoreContentType(true).execute().body();
			JsonNode jsonNodeSellPoint = MAPPER.readTree(jsonSellPointObject);
			//返回json是一个数组，获取数组的第一个元素
			String sellPoint = jsonNodeSellPoint.get(0).get("ad").asText();
			item.setSellPoint(sellPoint);
		}catch(Exception e){
			item.setSellPoint("error");
		}
		
		//图片
		String image = "";
		Elements elesImage = doc.select("#spec-n1 #spec-img");
		for(Element ele :elesImage){
			image = ele.attr("data-origin");
		}
		item.setImage(image);
		
		//商品详情
		String urlDesc = "http://d.3.cn/desc/"+item.getId();
		try{
			String jsonpDesc = Jsoup.connect(urlDesc).ignoreContentType(true).execute().body();
			String jsonDesc = jsonpDesc.substring(9, jsonpDesc.length()-1);
			JsonNode descJsonNode = MAPPER.readTree(jsonDesc);
			String desc = descJsonNode.get("content").asText();
			item.setItemDesc(desc);
		}catch(Exception e){
			item.setItemDesc("error");
		}
		
		return item;
	}
	
	
	@Test	//得到三级分类，得到分类分页链接，得到所有商品id
	public void makeLogs() throws IOException{
		Integer rowNum = 100;
		//先清空数据库表，否则itemId冲突
		File dir = new File("./logs");
		if(dir.isDirectory()){
			dir.mkdirs();
		}
		
		int i=0;
		//断点续爬
		i = this.getFlagValue(dir.getPath());
		
		for(String catUrl:this.getClassLevel3()){
			StringBuilder buf = new StringBuilder();
			List<Item> itemList = new ArrayList<Item>();
			Integer endPage = this.getPages(catUrl);
			log.info(catUrl+"("+endPage+")");
			for(int page=1;page<=endPage;page++){
				for(String itemUrl: this.getItemUrlByPage(catUrl+"&page="+page)){
					i++;
					log.info(i);
					try{
						itemList.add(this.getItem(itemUrl));
						
						//如100条记录形成一个文件
						if(i % rowNum == 0){
							this.write(dir+"/"+i+".log", MAPPER.writeValueAsString(itemList));
							this.write(dir+"/fv.lock",i+"");		//断点续爬标识，标识爬到哪里。中断后，此作为最后完成到的数量，下次从此数量开始
							buf.delete(0, buf.length());	//清空
						}
					}catch(Exception e){
						System.out.println("抓取失败:"+itemUrl);
					}
				}
			}
			
		}
	}
	
	//标识值文件：获取目前下，最后一个文件 fv.lock
	public Integer getFlagValue(String dir) throws IOException{
		File file=new File(dir+"/fv.lock");
        if(file.isFile() && file.exists()){ //判断文件是否存在
            InputStreamReader read = new InputStreamReader(new FileInputStream(file));//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while((lineTxt = bufferedReader.readLine()) != null){
                return Integer.valueOf(lineTxt);
            }
            read.close();
            return 0;		//文件内容为空设置默认值
		}else{
		    return 0;		//文件不存在设置默认值
		}
	}
	
	//写文件，覆盖现有问题
	public void write(String fileName, String content) throws IOException{
		File file = new File(fileName);
		FileOutputStream fos = new FileOutputStream(file);
		
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");   
	       osw.write(content);
		try{
			osw.flush();
			osw.close();
			fos.close();
		}finally{
			try {
				if (osw != null) {
					osw.close();
			    }
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}
	}
}
