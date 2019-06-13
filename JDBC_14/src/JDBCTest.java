import org.junit.Test;

import java.sql.*;

/**
 * @author : mengmuzi
 * create at:  2019-06-14  01:59
 * @description: 任务14： JDBC_获取插入记录的主键值
 */
public class JDBCTest {

    /**
     * 取得数据库自动生成的主键
     */
    @Test
    public void testGetKeyValue(){

        Connection connection=null;
        PreparedStatement preparedStatement=null;

        try{
            connection=JDBCTools.getConnection();
            String sql="INSERT INTO customers(name,email,birth)"+
                    "VALUES(?,?,?)";

            //preparedStatement = connection.prepareStatement(sql);
            //使用重载的 prepareStatement(sql,flag)
            //来生成 PreparedStatement 对象
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, "ABCDE");
            preparedStatement.setString(2, "abcd@gmail.com");
            preparedStatement.setDate(3,new Date(new java.util.Date().getTime()));

            preparedStatement.executeUpdate();

            //通过 getGeneratedKeys() 获取包含了新生成的主键的 ResultSet 对象
            //在 ResultSet 中只有一列 GENERATED_KEY ，用于存放新生成的主键值
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()){
                System.out.println(resultSet.getObject(1));
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{

        }
    }

}
