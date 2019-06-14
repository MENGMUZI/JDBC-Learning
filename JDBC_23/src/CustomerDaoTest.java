import org.junit.Test;

import java.sql.Connection;

import static org.junit.Assert.fail;

/**
 * @author : mengmuzi
 * create at:  2019-06-15  02:11
 * @description:
 */
public class CustomerDaoTest {

    CustomerDao customerDao = new CustomerDao();

    @Test
    public void testBatch() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetForValue() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetForList() {
        fail("Not yet implemented");
    }

    @Test
    public void testGet(){
        Connection connection = null;
        try{
            connection = JDBCTools.getConnection();
            String sql="SELECT id,name,email,birth FROM customers"+
                    " WHERE id= ?";
            Customer customer = (Customer) customerDao.get(connection,sql,5);
            System.out.println(customer);


        }catch(Exception e){

            e.printStackTrace();

        }finally{

            JDBCTools.releaseDB(null,null,connection);
        }
    }



    @Test
    public void testUpdate() {
        fail("Not yet implemented");
    }

}
