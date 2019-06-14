import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author : mengmuzi
 * create at:  2019-06-15  02:05
 * @description: 任务23： JDBC_使用DBUtils编写通用的DAO
 */

/**
 * 使用 QueryRunner 提供其具体的实现
 * @author mengmuzi
 *
 * @param <T>：子类需传入的泛型类型
 */
public class JDBCDAOImpl<T> implements DAO<T>{

    private QueryRunner queryRunner = null;
    private Class<T> type;

    public JDBCDAOImpl() {
        queryRunner = new QueryRunner();
        type = ReflectionUtils.getSuperGenericType(getClass());


    }

    public void update(Connection connection, String sql, Object... args) {

    }

    public T get(Connection connection, String sql, Object... args) throws SQLException {

        return queryRunner.query(connection,sql,new BeanHandler<T>(type),args);
    }

    public List<T> getForList(Connection connection, String sql, Object... args) {
        return null;
    }

    public <E> E getForValue(Connection connection, String sql, Object... args) {
        return null;
    }

    public void batch(Connection connection, String sql, Object[]... args) {

    }
}
