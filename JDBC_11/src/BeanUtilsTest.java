import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

/**
 * @author : mengmuzi
 * create at:  2019-06-13  21:04
 * @description:  任务11： JDBC_使用BeanUtils工具类操作JavaBean
 */
public class BeanUtilsTest {

    @Test
    public void testGetProperty() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Object object = new Student();
        System.out.println(object);

        BeanUtils.setProperty(object, "idCard", "370782199707196614");
        System.out.println(object);

        Object val = BeanUtils.getProperty(object,"idCard");
        System.out.println(val);


    }

    public void testSetProperty() throws InvocationTargetException, IllegalAccessException {

        Object object = new Student();

        BeanUtils.setProperty(object,"idCard","1213231431234");

        System.out.println(object);
    }


}
