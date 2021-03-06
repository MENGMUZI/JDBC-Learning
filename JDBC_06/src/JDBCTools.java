import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @author : mengmuzi
 * create at:  2019-06-12  17:28
 * @description: 任务6： JDBC_以面向对象的方式编写JDBC程序
 */
public class JDBCTools {

    /**
     * 执行 SQL 的方法
     * @param sql: insert , update 或 delete，而不包含 select
     */
    public static void update(String sql){
        Connection connection = null;
        Statement statement = null;
        try{
            // 1.获取数据库连接
            connection = getConnection();

            // 2.调用 Connection 对象的 createStatement() 方法获取 Statement 对象
            statement =  connection.createStatement();

            //3. 发送 SQL 语句： 调用 Statement 对象的 excuteUpdate(sql) 方法
            statement.executeUpdate(sql);

        } catch(Exception e){
            e.printStackTrace();

        }finally {
            // 4.关闭数据库资源： 由里向外关。
            releaseDB(null,statement,connection);
        }
    }

    /**
     * 释放数据库资源的方法
     */
    public static void releaseDB(ResultSet resultSet, Statement statement, Connection connection){

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取数据库连接
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getConnection() throws IOException, ClassNotFoundException, SQLException {
        // 0.读取 jdbc.properties
        /**
         * 1).属性文件对应 Java 中的 Properties 类
         * 2).可以通过类加载器加载bin目录（类路径下）的文件
         *
         */
        Properties properties = new Properties();
        InputStream inputStream = ReviewTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
        properties.load(inputStream);

        // 1.准备获取连接的4个字符串：user、password、jdbcUrl、driver
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String jdbcUrl = properties.getProperty("jdbcUrl");
        String driverClass = properties.getProperty("driver");

        // 2.加载驱动：Class.forName(driverClass)
        Class.forName(driverClass);

        // 3.调用
        Connection connection = DriverManager.getConnection(jdbcUrl,user,password);
        return connection;

    }


}
