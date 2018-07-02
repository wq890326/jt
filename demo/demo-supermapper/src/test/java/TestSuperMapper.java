import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.github.abel533.mapperhelper.MapperHelper;
import com.supermapper.base.mapper.SysMapper;
import com.supermapper.mapper.UserMapper;
import com.supermapper.pojo.User;


public class TestSuperMapper {
	public static void main(String[] args) throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession session = sessionFactory.openSession(true);

        // 创建一个MapperHelper
        MapperHelper mapperHelper = new MapperHelper();
        // 主键自增回写方法,默认值MYSQL,详细说明请看文档
        mapperHelper.setIDENTITY("MYSQL");
        
        // 注册通用Mapper接口
        mapperHelper.registerMapper(SysMapper.class);
        // 配置完成后，执行下面的操作
        mapperHelper.processConfiguration(session.getConfiguration());
        // OK - mapperHelper的任务已经完成，可以不管了
        
        UserMapper mapper = session.getMapper(UserMapper.class);
		
		List<User> uList = mapper.select(null);
		for(User u : uList){
			System.out.println(u);
		}
	}
}
