import com.sun.org.apache.regexp.internal.RE;
import org.junit.Test;

import java.sql.*;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

/**
 * @author : mengmuzi
 * create at:  2019-06-12  20:32
 * @description:  JDBC_利用反射及JDBC元数据编写通用的查询方法
 *
 *
 */
public class JDBCTest08 {
    /**
     * ResultSetMetaData 类
     * 1)what?
     * 是描述ResultSet的元数据对象，可用于获取关于 ResultSet 对象中列的类型和属性信息的对象
     * 2）how？
     *   a.得到ResultSetMetaData对象，调用 ResultSet 的 getMetaData（）方法
     *   b. ResultSetMetaData的常用方法
     *      int getColumnCount()：返回当前 ResultSet 对象中的列数。
     *      String getColumnLabel(int column)获取指定列的别名，其中从索引1开始
     * */
    @Test
    public void testResultSetMetaData(){

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            String sql="SELECT flow_id flowId,type,id_card idCard,"+
                    "exam_card examCard,student_name studentName,"+
                    "location,grade "+"FROM examstudent WHERE flow_id =?";
            //得到 ResultSet 对象
            connection = JDBCTools.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,5);
            resultSet = preparedStatement.executeQuery();

            //创建一个Map,存放键值对
            Map<String,Object> map = new Hashtable<String, Object>();

            //1.得到ResultSetMetaData对象
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while(resultSet.next()){
                //2.打印每一列的列名
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    //得到别名
                    String columnLabel = rsmd.getColumnLabel(i+1);
                    //得到每一个对应的值
                    Object columnValue = resultSet.getObject(columnLabel);
                    map.put(columnLabel,columnValue);
                }
            }
            System.out.println(map);

            //利用反射给对象属性赋值
            Class clazz = Student.class;

            Object object = clazz.newInstance();

            for(Map.Entry<String,Object> entry : map.entrySet()){
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();
                System.out.println(fieldName + ":" +fieldName);
                ReflectionUtils.setFieldValue(object,fieldName,fieldValue);
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            JDBCTools.releaseDB(resultSet,preparedStatement,connection);
        }

    }

    @Test
    public void testGet(){
        String sql = "SELECT id,name,email,birth"+
                " FROM customers WHERE id= ?";
        Customer customer = get(Customer.class,sql,5);
        System.out.println(customer);

        sql="SELECT flow_id flowId,type,id_card idCard,"+
                "exam_card examCard,student_name studentName,"+
                "location,grade "+"FROM examstudent WHERE flow_id =?";
        Student student=get(Student.class,sql,1);
        System.out.println(student);

    }
    /**
    * 主要步骤：
    *  1.先利用 SQL 进行查询，得到结果集
    *  2.利用反射创建实体类的对象：创建 Student 对象
    *  3.获取结果集的列的别名：idCard、studentName
    *  4.再获取结果集的每一列的值， 结合 3 得到一个 Map，键：列的别名，值：列的值：{flowId:5, type:6, idCard: xxx ……}
    *  5.再利用反射为 2 的对应的属性赋值：属性即为 Map 的键，值即为 Map 的值
    */

    public<T> T get(Class<T> clazz , String sql ,Object... args){
        T entity = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            // 1. 得到 ResultSet 对象
            connection = JDBCTools.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i <args.length ; i++) {
                preparedStatement.setObject(i+1,args[i]);
            }
            resultSet = preparedStatement.executeQuery();


            //2.ResultSetMetaData 对象
            ResultSetMetaData rsmd = resultSet.getMetaData();

            //3.创建一个 Map<String,Object> 对象
            // 键： SQL 查询的列的别名，值： 列的值
            Map<String,Object> map = new Hashtable<String, Object>();

            //4. 处理结果集，利用 ResultSetMetaData 填充3 对应的 Map 对象
            while(resultSet.next()){
                for(int i = 0; i < rsmd.getColumnCount(); i++){
                    String columnLabel = rsmd.getColumnLabel(i+1);
                    Object columnValue = resultSet.getObject(columnLabel);
                    map.put(columnLabel,columnValue);

                }
            }
            //5. 若Map 不为空 ，利用反射创建 clazz 对应的对象，
            if(map.size() > 0){
                entity = clazz.newInstance();
                //6. 遍历 Map 对象，利用反射为 Class 对象的属性赋值
                for(Map.Entry<String , Object> entry : map.entrySet()){
                    String fieldName = entry.getKey();
                    Object fieldValue = entry.getValue();
                    ReflectionUtils.setFieldValue(entity,fieldName,fieldValue);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            JDBCTools.releaseDB(resultSet,preparedStatement,connection);
        }

        return entity;
    }

    /*
     * 查询顾客类
     *
     * */
    public Customer getCustomer(String sql , Object... args){
        Customer customer = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = JDBCTools.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i+1, args[i]);
            }

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                customer = new Customer();
                customer.setId(resultSet.getInt(1));
                customer.setName(resultSet.getString(2));
                customer.setEmail(resultSet.getString(3));
                customer.setBirth(resultSet.getString(4));
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            JDBCTools.releaseDB(resultSet, preparedStatement, connection);
        }
        return customer;
    }

    /*
    * 查询学生类
    *
    * */
    public Student getStudent(String sql, Object... args){
        Student student = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = JDBCTools.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i <args.length ; i++) {
                preparedStatement.setObject(i+1,args[i]);
            }
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                student = new Student();
                student.setFlowId(resultSet.getInt(1));
                student.setType(resultSet.getInt(2));
                student.setIdCard(resultSet.getString(3));
                student.setExamCard(resultSet.getString(4));
                student.setStudentName(resultSet.getString(5));
                student.setLocation(resultSet.getString(6));
                student.setGrade(resultSet.getInt(7));

            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            JDBCTools.releaseDB(resultSet, preparedStatement, connection);
        }
        return student;

    }





    /**
     * 使用 preparedStatement 将有效的解决 SQL 注入问题
     */
    @Test
    public void testSQLInjection2() {
        String username="a' OR password=";
        String password=" OR '1'='1";

        // SELECT * FROM users WHERE username='a' OR password=' AND password =' OR '1'='1'

        String sql ="SELECT * FROM users WHERE username=? AND  password=? ";

        System.out.println(sql);

        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;

        try {
            connection=JDBCTools.getConnection();
            preparedStatement=(PreparedStatement) connection.prepareStatement(sql);

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);


            resultSet=preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("登陆成功");
            } else {
                System.out.println("用户名和密码不匹配或用户名不存在.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTools.releaseDB(resultSet, preparedStatement, connection);
        }

    }



    /**
     * SQL 注入
     */
    @Test
    public void testSQLInjection(){
        String username = null;
        String password = null;

        //String username="a' OR password=";
        //String password=" OR '1'='1";

        // SELECT * FROM users WHERE username='a' OR password=' AND password =' OR '1'='1'

        String sql ="SELECT * FROM users WHERE username='"+username+"' AND "+
                "password='"+password+"'";
        System.out.println(sql);

        Connection connection=null;
        Statement statement=null;
        ResultSet resultSet=null;

        try {
            connection=JDBCTools.getConnection();
            statement=connection.createStatement();
            resultSet=statement.executeQuery(sql);

            if (resultSet.next()) {
                System.out.println("登陆成功");
            } else {
                System.out.println("用户名和密码不匹配或用户名不存在.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTools.releaseDB(resultSet, statement, connection);
        }


    }


    @Test
    public void testPreparedStatement(){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            String sql =" INSERT INTO admin (id,username,password)"+ "VALUES(?,?,?) ";
            connection = JDBCTools.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,5);
            preparedStatement.setString(2,"xiaoqiang");
            preparedStatement.setString(3,"121324");

            preparedStatement.executeUpdate();

        }catch (Exception e){
            e.printStackTrace();

        }finally {
            //preparedStatement 是 Statement 的子接口，所以可以传入
            JDBCTools.releaseDB(null,preparedStatement,connection);
        }
    }


    /**
     * 利用PreparedStatement，向数据库中插入学生
     * */
    @Test
    public void testAddNewStudent(){
        Student student = getStudentFromConsole();
        addNewStudent2(student);
    }

    /**
     * 从控制台输入学生信息
     */
    private Student getStudentFromConsole(){
        Scanner scanner = new Scanner(System.in);
        Student student = new Student();
        System.out.print("FlowId：");
        student.setFlowId(scanner.nextInt());

        System.out.print("Type：");
        student.setType(scanner.nextInt());

        System.out.print("IdCard：");
        student.setIdCard(scanner.next());

        System.out.print("ExamCard：");
        student.setExamCard(scanner.next());

        System.out.print("StudentName：");
        student.setStudentName(scanner.next());

        System.out.print("Location：");
        student.setLocation(scanner.next());

        System.out.print("Grade：");
        student.setGrade(scanner.nextInt());

        return student;
    }
    public void addNewStudent2(Student student){
        String sql = "INSERT INTO examStudent(flowid,type,idcard,"+
                "examcard,studentname,location,grade)"+
                "VALUES(?,?,?,?,?,?,?)";
        JDBCTools.update(sql,student.getFlowId(),student.getType(),student.getIdCard(),
                student.getExamCard(),student.getStudentName(),student.getLocation(),
                student.getGrade());

    }

    public void addNewStudent(Student student) {
        // 1.准备一条SQL语句：
        String sql = "INSERT INTO examstudent " + "VALUES(" + student.getFlowId() + "," + student.getType() + ",'"
                + student.getIdCard() + "','" + student.getExamCard() + "','" + student.getStudentName() + "','"
                + student.getLocation() + "'," + student.getGrade() + ")";

        System.out.println(sql);
        // 2.调用 JDBCTools 类的 update(sql) 方法执行插入操作
        JDBCTools.update(sql);
    }




}
