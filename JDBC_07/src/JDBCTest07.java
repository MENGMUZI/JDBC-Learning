import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

/**
 * @author : mengmuzi
 * create at:  2019-06-12  20:32
 * @description: 任务7： JDBC_PreparedStatement
 *
 *
 * 1.为什么要用PreparedStatement?
 *   1)使用Statement需要进行拼写SQL语句，很繁琐
 *   2)可以有效的防止SQL注入（对于 Java 而言，要防范 SQL 注入，只要用 PreparedStatement
 *   取代 Statement 就可以了对于 Java 而言，要防范 SQL 注入，只要用 PreparedStatement 取代 Statement 就可以了）
 *
 * 2.使用PreparedStatement ==》是Statement的子接口，可以传入带占位符的SQL语句，提供了补充占位符变量方法。
 *
 * 1）创建PreparedStatement
 * preparedStatement=(PreparedStatement) connection.prepareStatement(sql);
 *
 * 2）调用preparedStatement的setXXX（）方法，索引从1开始
 *
 * 3）执行SQL语句：executeQuery（）或者使用executeUpdate（）注意：执行时不需要再传入SQL语句
 *
 */
public class JDBCTest07 {


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
