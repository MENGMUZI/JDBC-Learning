import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.Test;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author : mengmuzi
 * create at:  2019-06-14  19:26
 * @description: 任务20： JDBC_C3P0数据库连接池
 */
public class JDBCTest {

    @Test
    public void testJDBCTools() throws SQLException {

        Connection connection = JDBCTools.getConnection();
        System.out.println(connection);
    }


    /**
     * 1. 创建 C3P0-config.xml文件，参考文档Appendix B：Configuation
     * 2. 创建 ComboPooledDataSource 实例
     * 3. DataSource dataSource=new ComboPooledDataSource("helloC3P0");
     * 4. 从 DataSource 中获取数据库连接
     * @throws Exception
     */
    @Test
    public void testC3P0WithConfigFile() throws SQLException {
        DataSource dataSource = new ComboPooledDataSource("helloC3P0");
        System.out.println(dataSource.getConnection());
        ComboPooledDataSource comboPooledDataSource = (ComboPooledDataSource) dataSource;
        System.out.println(comboPooledDataSource.getMaxStatements());
    }

    @Test
    public void testC3P0() throws PropertyVetoException, SQLException {
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass("com.mysql.jdbc.Driver");
        comboPooledDataSource.setJdbcUrl("jdbc:mysql:///girls");
        comboPooledDataSource.setUser("root");
        comboPooledDataSource.setPassword("586189");

        System.out.println(comboPooledDataSource.getConnection());
    }


}
