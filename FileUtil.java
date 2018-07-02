import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class FileUtil {
	@Test	//生成当前天的目录，没有目录创建day01，有了编写最后一天的dayxx
	public void create() throws IOException{
		String srcDir = "D:\\tonyVideo\\17\\jt";
		String tempFile = "D:\\tonyVideo\\课堂笔记.docx";
		
		File dir = new File(srcDir);
		File[] fs = dir.listFiles();
		if(null==fs){	//空目录，则创建day01，拷贝word文档
			srcDir += "\\day01";
			FileUtil.makeDir(srcDir);
			FileUtils.copyFile(new File(tempFile), new File(srcDir+"\\课堂笔记.docx"));
		}else{
			Integer days = fs.length+1;
			srcDir += "\\day" + String.format("%02d", days);
			FileUtil.makeDir(srcDir);
			FileUtils.copyFile(new File(tempFile), new File(srcDir+"\\课堂笔记.docx"));
		}
	}
	
	@Test	//拷贝代码
	public void cpcode() throws FileNotFoundException{
		String srcDir = "D:\\javaws\\17";
		String descDir = "D:\\tonyVideo\\17\\jt";
		descDir += "\\" + this.getDays(descDir) + "\\code";
		
		deleteAllFilesOfDir(new File(descDir));
		FileUtil.makeDir(descDir);
		
		File jtdir = new File(srcDir);
		File[] fs = jtdir.listFiles();
		for(File _dir : fs){
			String dirName = _dir.getName();
			if(dirName.startsWith("jt-")){
				copy(srcDir+"\\"+dirName, descDir+"\\"+dirName);
				System.out.println(dirName+"目录下的文件拷贝完成!");
			}
		}
		System.out.println("\n全部完成!");
	}
	
	//获取最大天数
	public String getDays(String srcDir){
		return this.getDays(srcDir, 0);
	}
	//获取下一天天数
	public String getNextDays(String srcDir){
		return this.getDays(srcDir, 1);
	}
	
	public String getDays(String srcDir, Integer addValue){
		File dir = new File(srcDir);
		File[] fs = dir.listFiles();
		if(null==fs){	//空目录，则创建day01，拷贝word文档
			return "day01";
		}else{
			Integer days = fs.length + addValue;
			return "day" + String.format("%02d", days);
		}
	}
	
	//删除目录
	public static void deleteAllFilesOfDir(File path) {  
	    if (!path.exists())  
	        return;  
	    if (path.isFile()) {  
	        path.delete();  
	        return;  
	    }  
	    File[] files = path.listFiles();  
	    for (int i = 0; i < files.length; i++) {  
	        deleteAllFilesOfDir(files[i]);  
	    }  
	    path.delete();
	}  

	// 路径中的多层目录,如果不存在,则建立(mkdir－只可建最后一层目录)
	public static synchronized void makeDir(String dirPath) throws FileNotFoundException {
		String s = "";

		dirPath = dirPath.replaceAll("\\t", "/t"); // replace tab key
		dirPath = dirPath.replaceAll("\\\\", "/");
		String[] aPath = dirPath.split("/");
		for (int i = 0; i < aPath.length; i++) {
			s = s + aPath[i] + "/";
			// System.out.println(s);
			File d = new File(s);
			if (!d.exists()) {
				d.mkdir();
			}
		}
	}
	
	private static void copy(String src, String des) {  
        File file1=new File(src);  
        if(file1.exists()){
        	File[] fs=file1.listFiles();  
        	File file2=new File(des);  
        	if(!file2.exists()){  
        		file2.mkdirs();  
        	}  
        	for (File f : fs) {  
        		if(f.isFile()){  
        			if(!f.getName().startsWith(".")){
        				fileCopy(f.getPath(),des+"\\"+f.getName()); //调用文件拷贝的方法
        			}
        		}else if(f.isDirectory()){  
        			if(!f.getName().equals("target") && !f.getName().startsWith(".")){
        				copy(f.getPath(),des+"\\"+f.getName());
        			}
        		}  
        	}  
        }
    }  
  
    /** 
     * 文件拷贝的方法 
     */  
    public static void fileCopy(String src, String des) {  
      
        BufferedReader br=null;  
        PrintStream ps=null;  
          
        try {  
            br=new BufferedReader(new InputStreamReader(new FileInputStream(src)));  
            ps=new PrintStream(new FileOutputStream(des));  
            String s=null;  
            while((s=br.readLine())!=null){  
                ps.println(s);  
                ps.flush();  
            }  
              
        } catch (FileNotFoundException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }finally{  
              
                try {  
                    if(br!=null)  br.close();  
                    if(ps!=null)  ps.close();  
                } catch (IOException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }  
                  
        }  
          
          
    }  	
}