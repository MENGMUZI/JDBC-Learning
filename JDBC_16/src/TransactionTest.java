import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author : mengmuzi
 * create at:  2019-06-14  14:05
 * @description:  任务16： JDBC_处理事务
 */
public class TransactionTest {

    /**
     * tom 给 jerry 汇款 500元
     *
     * 关于事务
     * 1. 如果多个操作，每个操作使用的是自己的单独的连接，则无法保证事务
     * 2. 具体步骤
     * 1). 事务开始前，开始事务：取消Connection的默认提交行为
     *     connection.setAutoCommit(false);
     * 2). 如果事务的操作都成功，则提交事务：connection.commit()
     * 3). 回滚事务：若出现异常，则在 catch 块中回滚事务；
     */

    //如果多个操作，每个操作使用的是自己的单独的连接，则无法保证事务
    @Test
    public void testTransaction01(){

        DAO dao = new DAO();
        String sql = "UPDATE users SET banlance ="+
					"banlance -500 WHERE id=1";
        dao.update(sql);

        //如果出现了异常，如何使得数据回滚
        int i = 10/0;

        sql="UPDATE users SET banlance = "+
				"banlance + 500 WHERE id=2";
		dao.update(sql);


    }


    @Test
    public void testTransaction02(){
        Connection connection = null;

        try{
            // 开始事务：取消默认提交
            connection = JDBCTools.getConnection();
            connection.setAutoCommit(false);

            String sql="UPDATE users SET banlance ="+
                    "banlance -500 WHERE id=1";
            update(connection, sql);
            int i = 10/0;
            System.out.println(i);

            sql="UPDATE users SET banlance = "+
                    "banlance + 500 WHERE id=2";
            update(connection, sql);

            //提交事务
            connection.commit();

        }catch(Exception e){
            e.printStackTrace();
            //回滚事务
            try{
                connection.rollback();
            }catch (Exception e1){
                e1.printStackTrace();
            }

        }finally{
            JDBCTools.releaseDB(null,null,connection);
        }

    }

    public void update(Connection connection,String sql,Object ... args){

        PreparedStatement preparedStatement = null;

        try{
            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i <args.length ; i++) {
                preparedStatement.setObject(i+1,args[i]);
            }
            preparedStatement.executeUpdate();


        }catch(Exception e){

            e.printStackTrace();

        }finally{

            JDBCTools.releaseDB(null,preparedStatement,null);

        }


    }

}
