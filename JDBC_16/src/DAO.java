import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : mengmuzi
 * create at:  2019-06-13  16:54
 * @description:  任务12： JDBC_重构DAO查询方法&amp;完成DAO编写
 */
public class DAO {

    // INSERT,UPDATE，DELETE 操作都可以包含在其中
    public void update(String sql , Object... args){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try{
            connection = JDBCTools.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i+1,args[i]);

            }
            preparedStatement.executeUpdate();

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            JDBCTools.releaseDB(null,preparedStatement,connection);
        }
    }

    // 查询一条记录 返回对应的对象
    public<T> T get(Class<T> clazz, String sql, Object ... args){
        T entity = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            //1.获取Connection
            connection = JDBCTools.getConnection();
            //2.获取preparedStatement
            preparedStatement = connection.prepareStatement(sql);
            //3.填充占位符
            for (int i = 0; i <args.length ; i++) {
                preparedStatement.setObject(i+1,args[i]);
            }
            //4.进行查询，得到ResultSet
            resultSet = preparedStatement.executeQuery();
            //5.若 ResultSet 中有记录
            //准备一个 Map<String,Object> ：键：存放列的别名， 值：存放列的值
            if(resultSet.next()){
                Map<String,Object> map = new HashMap<String, Object>();
                // 6. 得到 ResultSetMetaData 对象
                ResultSetMetaData rsmd = resultSet.getMetaData();
                // 7. 处理 ResultSet 对象，把指针向下移动一个单位
                // 8. 由 ResultSetMetaData 对象得到结果集中有多少列
                int columnCount = rsmd.getColumnCount();
                // 9. 由 ResultSetMetaData 得到每一列的别名，由 ResultSet的到具体每一列的值
                for (int i = 0; i <columnCount ; i++) {
                    String columnLabel = rsmd.getColumnLabel(i+1);
                    Object columnValue = resultSet.getObject(columnLabel);
                    // 10. 填充 Map 对象
                    map.put(columnLabel,columnValue);
                }
                // 11. 用反射创建 Class 对应的对象
                entity = clazz.newInstance();
                // 12. 遍历 Map 对象，用反射填充对象的属性值：
                // 属性名为 Map 中的Key，属性值为 Map 中的 Value
                for(Map.Entry<String,Object> entry : map.entrySet()){
                    String propertyName = entry.getKey();
                    Object value = entry.getValue();

                    //ReflectionUtils.setFieldValue(entity,propertyName,value);
                    //采用BeanUtils
                    BeanUtils.setProperty(entity,propertyName,value);

                }
            }
        }catch(Exception e){

            e.printStackTrace();

        }finally{

            JDBCTools.releaseDB(resultSet,preparedStatement,connection);
        }
        return entity;

    }

    // 查询多条记录，返回对应的对象的集合
    public <T> List<T> getForList(Class<T> clazz, String sql, Object ... args){

        //创建List存放多个对象
        List<T> list = new ArrayList<T>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            // 1. 得到结果集
            connection = JDBCTools.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }

            resultSet = preparedStatement.executeQuery();

            // 2. 处理结果集，得到 Map 的 List，其中一个 Map 对象就是一条记录
            // Map 的 key 为 ResultSet 中列的别名，
            // Map 的 value 为列的值
           List< Map<String,Object>> mapList = handleResultSetToMapList(resultSet);

            // 3. 把 Map 的List 转为clazz 对应的对象的propertyName
            // 而 Map 的 value 即为 clazz 对应的对象的 propertyValue
            list = transferMapListToBeanList(clazz, mapList);

        }catch(Exception e){

            e.printStackTrace();

        }finally{
            JDBCTools.releaseDB(resultSet,preparedStatement,connection);
        }
        return null;

    }

    private <T> List<T> transferMapListToBeanList(Class<T> clazz, List<Map<String, Object>> mapList) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        // 12. 判断 List 是否为空集合，若不为空
        // 则遍历 List ，得到一个一个的Map对象，再把一个 Map 对象转为一个 Class
        // 参数对应的 Object 对象

        List<T> result = new ArrayList<>();

        T bean = null;
        if(mapList.size() > 0){
            for(Map<String,Object> entrymap : mapList){
                bean = clazz.newInstance();
                for(Map.Entry<String ,Object> entry : entrymap.entrySet()){
                    String propertyName = entry.getKey();
                    Object value = entry.getValue();

                    BeanUtils.setProperty(bean, propertyName, value);

                }
                //13.把object放入到list中
                result.add(bean);

            }
        }
        return result;

    }

    /**
     * 处理结果集，得到 Map 的一个List，其中一个Map 对象对应一条记录
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private List<Map<String, Object>> handleResultSetToMapList(ResultSet resultSet) throws SQLException {
        // 5. 准备一个List<Map<Sring,Object>>;
        // 键：存放列的名， 值：存放列的值 . 其中一个 Map 对应一条记录
        List<Map<String, Object>> mapList = new ArrayList();

        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        List<String> columnLabels = getColumnLabels(resultSet);

        Map<String, Object> map = null;
        // 7. 处理 ResultSet，使用 While 循环
        while (resultSet.next()) {
            map = new HashMap<>();

            for (String columnLabel : columnLabels) {
                Object colunValue = resultSet.getObject(columnLabel);

                map.put(columnLabel, colunValue);
            }
            // 11. 把一条记录的一个 Map 对象放入 5 准备好的 List 对象中
            mapList.add(map);

        }
        return mapList;



    }

    /**
     * 获取结果集的 ColumnLabel 对应的 List
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private List<String> getColumnLabels(ResultSet resultSet) throws SQLException {
        List<String> labels = new ArrayList();

        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
            labels.add(resultSetMetaData.getColumnLabel(i + 1));
        }
        return labels;
    }

    // 返回某条记录的某一个字段的值 或 一个统计的值(一共有多少条记录等)
    public <E> E getForValue(String sql, Object ... args){
        //1. 得到结果集
        // 1. 得到结果集，该结果集应该只有一行，且只有一列
        // 1. 得到结果集，该结果集应该只有一行，且只有一列
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;

        try {
            //1. 得到结果集
            connection=JDBCTools.getConnection();
            preparedStatement=connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i+1, args[i]);
            }
            resultSet=preparedStatement.executeQuery();
            if (resultSet.next()) {
                return (E) resultSet.getObject(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCTools.releaseDB(resultSet, preparedStatement, connection);

        }
        //取得结果
        return null;

    }
}
