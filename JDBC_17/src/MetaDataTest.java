/**
 * @author : mengmuzi
 * create at:  2019-06-14  01:38
 * @description:  任务13： JDBC_JDBC的元数据
 */

import org.junit.Test;

import java.sql.*;

/**
 * 1.Java 通过JDBC获得连接以后，得到一个Connection 对象，可以从这个对象获得有关数据库管理系统的各种信息，
 *   包括数据库中的各个表，表中的各个列，数据类型，触发器，存储过程等各方面的信息。根据这些信息，JDBC可以访
 *   问一个实现事先并不了解的数据库。
 *
 * 2.获取这些信息的方法都是在DatabaseMetaData类的对象上实现的，而DataBaseMetaData对象是在Connection对象上获得的。
 *
 * */

public class MetaDataTest {

    /**
     * ResultSetMetaData: 描述结果集中的元数据，
     * 可以得到结果集中的基本信息：结果集中有哪些列，列名，列的别名等。
     * 结合反射可以写出通用的查询方法。
     */
    @Test
    public void testResultSetMetaData(){
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;

        try{
            connection = JDBCTools.getConnection();
            String sql="SELECT id,name,email,birth "+"FROM customers";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            //1. 得到 ResultSetMetaData 对象
            ResultSetMetaData resultSetMetaData=resultSet.getMetaData();

            //2. 得到列的个数
            int columnCount=resultSetMetaData.getColumnCount();
            System.out.println(columnCount);

            for (int i = 0; i <columnCount ; i++) {
                //3. 得到列名
                String columnName = resultSetMetaData.getColumnName(i+1);

                //4. 得到列的别名
                String columnLabel=resultSetMetaData.getColumnLabel(i+1);

                System.out.println(columnName+" ,"+columnLabel);
            }


        }catch(Exception e){

            e.printStackTrace();

        }finally{

            JDBCTools.releaseDB(resultSet, preparedStatement, connection);
        }



    }

    /**
     * DatabaseMetaData 是描述 数据库 的元数据对象
     * 可以由 Connection 得到
     * 了解
     */
    @Test
    public void testDatabaseMetaData(){
        Connection connection=null;
        ResultSet resultSet=null;

        try{

            connection=JDBCTools.getConnection();
            DatabaseMetaData data=(DatabaseMetaData) connection.getMetaData();

            // 1.可以得到数据库本身的一些信息
            int version = data.getDatabaseMajorVersion();
            System.out.println(version);

            // 2.得到用户的用户名
            String user=data.getUserName();
            System.out.println(user);

            // 3.得到 Mysql 中有哪些数据库
            resultSet = data.getCatalogs();
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1));
            }


        }catch(Exception e){

            e.printStackTrace();

        }finally{
            JDBCTools.releaseDB(resultSet, null, connection);
        }
    }


}
