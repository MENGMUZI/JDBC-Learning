import org.apache.commons.dbutils.QueryRunner;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * @author : mengmuzi
 * create at:  2019-06-14  19:58
 * @description:  任务21： JDBC_使用DBUtils进行更新操作
 */
public class DBUtilsTest {

    @Test
    public void testQueryRunnerUpdate(){
        //1. 创建QueryRunner 的实现类
        QueryRunner queryRunner = new QueryRunner();

        //2. 使用其 update 方法
        String sql = "DELETE FROM customers "+"WHERE id IN(?,?)";

        Connection connection = null;
        try {
            connection = JDBCTools.getConnection();
            queryRunner.update(connection,sql,1,2);
            
        } catch (SQLException e) {
            
            e.printStackTrace();
        }finally {
            JDBCTools.releaseDB(null,null, connection);
        }


    }



}
