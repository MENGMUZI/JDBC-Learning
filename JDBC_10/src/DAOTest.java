import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.fail;

/**
 * @author : mengmuzi
 * create at:  2019-06-13  17:39
 * @description:
 */
public class DAOTest {
    DAO dao = new DAO();
    @Test
    public void testUpdate(){
        String sql="INSERT INTO customers(name,"+
                "email,birth) VALUES(?,?,?)";
        dao.update(sql, "XiaoMing","xiaoming@atguigu.com",
                new Date(new java.util.Date().getTime()));
    }

    @Test
    public void testGet() {
        String sql="SELECT flow_id flowId,type,exam_card examCard,"+
                "id_card idCard,student_name studentName,location,"+
                "grade From examstudent WHERE flow_id = ?";

        Student student=dao.get(Student.class,sql,1);
        System.out.println(student);
    }


    @Test
    public void testGetForList() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetForValue() {
        fail("Not yet implemented");
    }

}
