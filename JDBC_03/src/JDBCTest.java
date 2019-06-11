
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @author : mengmuzi
 * create at:  2019-06-12  02:16
 * @description: 任务3： JDBC_通过Statement执行更新操作
 */
public class JDBCTest{

    /**
     * 通用的更新的方法：包括 INSERT、UPDATE、DELETE
     * 版本 1.
     *
     */
   @Test
    public void update(String sql){
        Connection connection = null;
        Statement statement = null;
        try{
            connection = getConnection2();
            statement = connection.createStatement();
            statement.executeUpdate(sql);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(statement != null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }


    }
    /**
     * 通过JDBC 向指定的数据库插入一条数据
     * 1. Statement: 用于执行SQL 语句的对象
     * 1) 通过Connection 的 createStatement()来获取
     * 2）通过executeUpdate(sql) 可以执行SQL语句。
     * 3) 传入的 SQL 可以是 INSERT、UPDATE、DELETE，但不能是 SELECT
     *
     * 2. Connection、Statement 都是应用程序和数据库服务器的连接资源，使用后一定要关闭
     *    需要在 finally 中关闭 Connection 和 Statement 对象
     *
     * 3. 关闭的顺序是：先关闭后获取的，即先关闭 Statement 后关闭 Connection
     */

    @Test
    public void testStatement() throws Exception {
        //1.获取数据库连接
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection2();

            //3.准备插入的SQL语句
//          String sql = "INSERT INTO admin VALUES('3','cuihua','8866');";
//          String sql = "DELETE FROM admin WHERE id ='3'";
            String sql = "UPDATE admin SET username='xiaohu' WHERE id ='3'";

            //4.执行插入
            //1）获取操作SQL语句的Statement对象，通过调用Connection的createStatement（）方法来获取。
            statement = conn.createStatement();

            //2）调用Statement对象的executeUpdate（sql）执行SQL语句进行插入
            statement.executeUpdate(sql);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //5）关闭Statement对象
            try {
                if(statement != null){
                    statement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                //2.关闭数据库连接
                if(conn != null){
                    conn.close();
                }
            }

        }
    }

    /**
     * DriverManager 是驱动类的管理类
     * 1）可以重载的getConnection（）方法获取数据库连接，较为方便。
     * 2）可以同时管理多个驱动程序，调用方法时传入的参数不同，即返回不同的数据库连接。
     *
     */
    @Test
    public void testGetConnection2() throws SQLException, IOException, ClassNotFoundException {
        System.out.println(this.getConnection2());
    }

    public Connection getConnection2() throws IOException, ClassNotFoundException, SQLException {
        //1.准备连接数据库的四个字符串
        String driverClass = null;
        String jdbcUrl = null;
        String user = null;
        String password = null;

        //1)创建properties对象
        Properties properties = new Properties();

        //2)创建jdbc.properties 对应的输入流
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("jdbc.properties");

        //3)加载2)对应的输入流
        properties.load(inputStream);

        //4). 具体决定user、password 等 4个字符串
        driverClass = properties.getProperty("driver");
        jdbcUrl = properties.getProperty("jdbcUrl");
        user=properties.getProperty("user");
        password=properties.getProperty("password");

        //2.加载数据库驱动程序（对应的 Driver 实现类中有注册驱动的静态代码块。）
        Class.forName(driverClass);

        //3.通过DriverManager的getConnection()方法获取数据库连接
        return DriverManager.getConnection(jdbcUrl,user,password);

    }

    /*@Test
    public void testDriverManager() throws SQLException, IOException, ClassNotFoundException {
        // 1.准备连接数据库的4个字符串。
        //驱动的全类名
        String driverClass="com.mysql.jdbc.Driver";
        //JDBC url
        String jdbcUrl="jdbc:mysql://localhost:3306/jdbc";
        //user
        String user="root";
        //password
        String password="root";

        // 2. 加载数据库驱动程序(对应的Driver 驱动中有对应的静态代码快)。
        //DriverManager.registerDriver(Class.forName(driverClass).newInstance());
        Class.forName(driverClass);

        // 3. 通过 DriverManager 的 getConnection() 方法获取数据库连接。
        Connection connection= DriverManager.getConnection(jdbcUrl, user, password);

        System.out.println(connection);
    }






    *//**
     * Driver是一个接口：数据库厂商必须提供实现的接口，从中获取数据库连接。
     * 缺点：是和mysql耦合太高，换一个数据库则不行
     *
     * 1.加入Mysql数据驱动
     *  1）解压mysql压缩包
     *  2）当前目录下新建lib目录
     *  3）把mysql-connector-java-5.1.39-bin.jar 复制到新建的lib目录下
     *  4）add to buildpath 加入到类路径下
     *
     *//*
    @Test
    public void test() throws SQLException {
        //1.创建一个Driver实现类的对象
        Driver driver = new com.mysql.jdbc.Driver();

        //2.准备连接数据库的基本信息：url,us,password
        //要加上这句?useUnicode=true&characterEncoding=utf8不然报异常
        //Unknown initial character set index '255' received from server. Initial client character set can be forced via the 'characterEncoding' property.
        String url = "jdbc:mysql://localhost:3306/girls?useUnicode=true&characterEncoding=utf8";
        Properties info = new Properties();
        info.put("user","root");
        info.put("password","586189");

        //3.调用Driver接口的driver.connect(url,info)获取数据库连接
        Connection connection = driver.connect(url,info);
        System.out.println(connection);


    }

    *//**
     * 编写一个通用的方法，在不修改程序的情况下，可以获取任何数据库连接，
     * 解决方案：把数据库的驱动Driver实现类的全类名、url、us、password放入一个配置文件中，
     *  通过修改数据库配置文件来实现具体的数据库的接解耦
     *
     *//*
    public Connection getConnection() throws Exception{
        String driverClass = null;
        String jdbcUrl = null;
        String user = null;
        String password = null;

        //读取类路径下的jdbc.properties文件

        //实现获取在classpath路径下的资源文件的输入流
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        driverClass = properties.getProperty("driver");
        jdbcUrl = properties.getProperty("jdbcUrl");
        user = properties.getProperty("user");
        password = properties.getProperty("password");


        //通过反射创建Driver对象
        Driver driver = (Driver)Class.forName(driverClass).newInstance();

        Properties info = new Properties();
        info.put("user",user);
        info.put("password",password);

        //通过Driver的connect 方法获取数据库连接。
        Connection connection = driver.connect(jdbcUrl,info);
        return connection;


    }
    @Test
    public void testGetConnection() throws Exception{
        System.out.println(this.getConnection());
    }*/

}
