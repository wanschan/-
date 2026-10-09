package DAO;

import utils.DBUtil;
import java.sql.*;
import java.util.*;

//DAO基类 封装通用的数据库操作
public class BaseDAO {

    /**
     * 通用查询：返回 List<Map<String, Object>>
     *   List<Map<String, Object>>
     *   └─ 外层：List（有序集合，可存放多条记录）
     *       └─ 内层：Map（键值对）
     *           ├─ Key: String 名
     *           └─ Value: Object 值
     *
     *
     * 适用于多表联查或不确定字段的场景
     */
    public List<Map<String, Object>> queryForList(String sql, Object... params) throws SQLException {  // 创建空List
        List<Map<String, Object>> result = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = DBUtil.executeQuery(sql, params);    //执行SQL查询，返回ResultSet（db结果集）
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();    //字段

            while (rs.next()) {//遍历
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    row.put(columnName, rs.getObject(i));
                }   //将这一行Map添加到List中
                result.add(row);
            }
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return result;
    }

    /**
     * 通用查询：返回单个 Map（第一条记录）
     */
    public Map<String, Object> queryForMap(String sql, Object... params) throws SQLException {
        List<Map<String, Object>> list = queryForList(sql, params);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 通用查询：返回单个值（如 COUNT(*)）
     */
    public Object queryForSingleValue(String sql, Object... params) throws SQLException {
        ResultSet rs = null;
        try {
            rs = DBUtil.executeQuery(sql, params);
            if (rs.next()) {
                return rs.getObject(1);
            }
            return null;
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * 通用更新：INSERT / UPDATE / DELETE
     * 返回受影响的行数
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        return DBUtil.executeUpdate(sql, params);
    }

    /**
     * 批量更新（事务内使用）
     */
    public int[] executeBatch(String sql, List<Object[]> batchParams) throws SQLException {
        return DBUtil.executeBatch(sql, batchParams);
    }


    //获取自增主键
    /**
     * 获取最后插入的ID（AUTO_INCREMENT）
     */
    public int getLastInsertId() throws SQLException {
        Object obj = queryForSingleValue("SELECT LAST_INSERT_ID()");
        return obj == null ? 0 : ((Number) obj).intValue();
    }
}