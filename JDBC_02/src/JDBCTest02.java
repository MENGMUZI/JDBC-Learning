import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @author : mengmuzi
 * create at:  2019-06-11  17:10
 * @description: 任务2： JDBC_通过 DriverManager 获取数据库连接
 */
public class JDBCTest02 {

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

    @Test
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






    /**
     * Driver是一个接口：数据库厂商必须提供实现的接口，从中获取数据库连接。
     * 缺点：是和mysql耦合太高，换一个数据库则不行
     *
     * 1.加入Mysql数据驱动
     *  1）解压mysql压缩包
     *  2）当前目录下新建lib目录
     *  3）把mysql-connector-java-5.1.39-bin.jar 复制到新建的lib目录下
     *  4）add to buildpath 加入到类路径下
     *
     */
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

    /**
     * 编写一个通用的方法，在不修改程序的情况下，可以获取任何数据库连接，
     * 解决方案：把数据库的驱动Driver实现类的全类名、url、us、password放入一个配置文件中，
     *  通过修改数据库配置文件来实现具体的数据库的接解耦
     *
     */
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
    }


}
