import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * @author : mengmuzi
 * create at:  2019-06-14  16:23
 * @description:  任务18： JDBC_批量处理
 */
public class JDBCTest {

    /**
     * 向 Oracle 或 Mysql 的 customers 数据表中插入 100,000 条记录, 测试如何插入，用时最短。
     *  1. 使用 Statement
     */
    @Test
    public void testBatchWithStatement(){
        Connection connection = null;
        Statement statement = null;
        String sql = null;

        try{
            connection = JDBCTools.getConnection();
            JDBCTools.beginTx(connection);
            statement = connection.createStatement();

            long begin = System.currentTimeMillis();
            Date date = new Date(new java.util.Date().getTime());

            for (int i = 0; i <10000 ; i++) {
                sql = "INSERT INTO customers VALUES(" + (i + 1) + ",'name_" + i + "','" + date + "')";
                statement.executeUpdate(sql);
            }

            long end = System.currentTimeMillis();
            System.out.println("Time:" + (end - begin));
            JDBCTools.commit(connection);
        }catch(Exception e){

            e.printStackTrace();
            JDBCTools.rollback(connection);
        }finally{
            JDBCTools.releaseDB(null,statement,connection);
        }
    }

    /**
     * 2.使用 PreparedStatement
     * */
    @Test
    public void testBatchWithPreparedStatement(){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String sql = null;

        try{
            connection = JDBCTools.getConnection();
            JDBCTools.beginTx(connection);
            sql = "INSERT INTO customers VALUES(?,?,?)";

            preparedStatement = connection.prepareStatement(sql);
            Date date = new Date(new java.util.Date().getTime());

            long begin = System.currentTimeMillis();
            for (int i = 0; i <10000 ; i++) {
                preparedStatement.setInt(1,i+1);
                preparedStatement.setString(2, "name_"+i);
                preparedStatement.setDate(3, date);

                preparedStatement.executeUpdate();
            }

            long end = System.currentTimeMillis();
            System.out.println("Time:" + (end - begin));
            JDBCTools.commit(connection);

        }catch(Exception e){

            e.printStackTrace();
            JDBCTools.rollback(connection);

        }finally{

            JDBCTools.releaseDB(null,preparedStatement,connection);

        }
    }

    @Test
    public void testBatch(){

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String sql = null;

        try{
            connection = JDBCTools.getConnection();
            JDBCTools.beginTx(connection);
            sql = "INSERT INTO customers VALUES(?,?,?)";

            preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
            Date date = new Date(new java.util.Date().getTime());

            long begin = System.currentTimeMillis();

            for (int i = 0; i < 100000; i++) {
                preparedStatement.setInt(1, i + 1);
                preparedStatement.setString(2, "name_" + i);
                preparedStatement.setDate(3, date);

                //积攒SQL
                preparedStatement.addBatch();
                // 当积攒到一定程度，就统一的执行一次，并且清空先前积攒的 SQL
                if( (i+1) % 300 == 0){
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }

            }
            // 若总条数不是批量数值的整数倍，则还需要额外的执行一次
            if( (10000%300) != 0){
               preparedStatement.executeBatch();
               preparedStatement.clearBatch();

            }
            long end = System.currentTimeMillis();
            System.out.println("Time:" + (end - begin));
            JDBCTools.commit(connection);
        }catch(Exception e){

            e.printStackTrace();
            JDBCTools.rollback(connection);

        }finally{
            JDBCTools.releaseDB(null, preparedStatement, connection);
        }

    }
}
