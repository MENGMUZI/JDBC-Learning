import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;

/**
 * @author : mengmuzi
 * create at:  2019-06-14  02:19
 * @description: 任务15： JDBC_处理BLOB
 */
public class JDBCTest {
    /**
     * 读取blob数据：
     * 1. 使用getBlob 方法读取到 Blob 对象
     * 2. 调用 Blob 的 getBinaryStream 方法得到输入流。再使用 IO 操作即可。
     */
    @Test
    public void readBlob() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            connection = JDBCTools.getConnection();
            String sql = "SELECT id,name,email,birth,picture " + "FROM customers WHERE id=12";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                int id=resultSet.getInt(1);
                String name=resultSet.getString(2);
                String email=resultSet.getString(3);

                System.out.println(id+","+name+","+email);

                Blob picture = resultSet.getBlob(5);
                InputStream inputStream = picture.getBinaryStream();
                OutputStream outputStream = new FileOutputStream("flower.jpg");

                byte[] buffer = new byte[1024];
                int len = 0;
                while((len = inputStream.read(buffer)) != -1){
                    outputStream.write(buffer,0,len);
                }
                outputStream.close();
                inputStream.close();

            }
        }catch(Exception e){

            e.printStackTrace();

        }finally{

            JDBCTools.releaseDB(resultSet, preparedStatement, connection);
        }

    }







    /**
     * 插入 BLOB 类型的数据必须使用 PreparedStatement，因为 BLOB 类型 的数据无法使用字符串拼写。 调用 setBlob(int
     * index,InputStream inputStream);
     */
    @Test
    public void testInsertBlob(){

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = JDBCTools.getConnection();
            String sql = "INSERT INTO customers(name,email,birth,picture)" + "VALUES(?,?,?,?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "wang");
            preparedStatement.setString(2, "ivan@gmail.com");
            preparedStatement.setDate(3, new Date(new java.util.Date().getTime()));

            InputStream inputStream = new FileInputStream("123.jpg");
            preparedStatement.setBlob(4,inputStream);

            preparedStatement.executeUpdate();


        }catch(Exception e){

            e.printStackTrace();

        }finally{
            JDBCTools.releaseDB(null, preparedStatement, connection);
        }





    }

}
