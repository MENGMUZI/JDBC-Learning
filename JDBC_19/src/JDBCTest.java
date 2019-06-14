import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author : mengmuzi
 * create at:  2019-06-14  17:01
 * @description:  任务19： JDBC_dbcp数据库连接池
 */
public class JDBCTest {

    /**
     * 使用 DBCP 数据库连接池 1. 加入 jar 包,依赖于 commens-pool.jar commens-logging.jar 2.
     * 创建数据库连接池 3. 为数据源实例指定必须的属性 4. 从数据源中获取数据库连接
     *
     * @throws SQLException
     */
    @Test
    public void testDBCP(){
        BasicDataSource dataSource = null;

        try{
            // 1.创建 DBCP 数据源实例
            dataSource = new BasicDataSource();

            // 2.为数据源实例指定必须的属性
            dataSource.setUsername("root");
            dataSource.setPassword("586189");
            dataSource.setUrl("jdbc:mysql://localhost:3306/girls");
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");


            // 3.指定数据源一些可选的属性
            // 1) 指定数据库连接池中初始化连接数的个数
            dataSource.setInitialSize(5);

            // 2) 指定最大的连接数,用一时刻向数据库申请的连接数

            dataSource.setMaxTotal(5);

            // 3) 指定最小连接数：在数据库连接池中保存的最少的空闲连接的数量
            dataSource.setMinIdle(5);

            // 4) 等待数据库连接池分配连接的最长时间。单位为毫秒，超出该时间将抛出异常
            dataSource.setMaxWaitMillis(1000 * 5);


            // 4.从数据源中获取数据库连接
            Connection connection = dataSource.getConnection();
            System.out.println(connection.getClass());

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 1. 加载 dbcp 的 properties 配置文件：配置文件中的键需要来自 BasicDataSourse的属性 2. 调用
     * BasicDataSourceFactory 的 createDataSource 方法创建 DataSource实例 3. 从 DataSource
     * 实例中获取数据库连接
     *
     * @throws Exception
     */
    @Test
    public void testDBCPWithDataSourceFactory() throws Exception {

        Properties properties = new Properties();
        InputStream inputStream = JDBCTools.class.getClassLoader().getResourceAsStream("dbcp.properties");
        properties.load(inputStream);

        final DataSource dataSource = BasicDataSourceFactory.createDataSource(properties);
        System.out.println(dataSource.getConnection());

        BasicDataSource basicDataSource = (BasicDataSource) dataSource;
        System.out.println(basicDataSource.getMaxWaitMillis());

        Connection connection = dataSource.getConnection();
        System.out.println(connection.getClass());

        connection = dataSource.getConnection();
        System.out.println(connection.getClass());

        connection = dataSource.getConnection();
        System.out.println(connection.getClass());

        connection = dataSource.getConnection();
        System.out.println(connection.getClass());

        Connection connection2 = dataSource.getConnection();
        System.out.println(">" + connection2.getClass());

        new Thread(()->{
            Connection conn;
            try{
                conn = dataSource.getConnection();
                System.out.println(conn.getClass());
            }catch (Exception e){
                e.printStackTrace();
            }
        },"t1").start();

        //暂停一会儿线程
        try{ TimeUnit.SECONDS.sleep(3);}catch(Exception e){e.printStackTrace();}

        connection2.close();

    }

}
