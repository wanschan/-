package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库工具类
 * 功能：数据库连接、查询、更新、事务管理
 */
public class DBUtil {

    // ========== 1. ThreadLocal 管理 Connection ==========
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    // ========== 2. 获取数据库连接 ==========
    /**
     * 获取当前线程的数据库连接
     * 如果当前线程没有连接，则创建一个新的
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = threadLocal.get();
        if (conn == null || conn.isClosed()) {
            try {
                Class.forName(ComData.JDBC_DRIVER);
                conn = DriverManager.getConnection(ComData.DB_URL, ComData.USER, ComData.PASS);
                threadLocal.set(conn);
                System.out.println("✓ 数据库连接成功");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new SQLException("数据库驱动加载失败：" + e.getMessage());
            }
        }
        return conn;
    }

    // ========== 3. 事务管理 ==========
    /**
     * 开启事务
     */
    public static void beginTransaction() throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);
        System.out.println("▶ 事务已开启");
    }

    /**
     * 提交事务
     */
    public static void commit() throws SQLException {
        Connection conn = threadLocal.get();
        if (conn != null && !conn.isClosed()) {
            conn.commit();
            conn.setAutoCommit(true);
            System.out.println("✓ 事务已提交");
        }
    }

    /**
     * 回滚事务
     */
    public static void rollback() throws SQLException {
        Connection conn = threadLocal.get();
        if (conn != null && !conn.isClosed()) {
            conn.rollback();
            conn.setAutoCommit(true);
            System.out.println("✗ 事务已回滚");
        }
    }

    /**
     * 关闭连接（并移除 ThreadLocal）
     */
    public static void close() throws SQLException {
        Connection conn = threadLocal.get();
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
        threadLocal.remove();
        System.out.println("✓ 数据库连接已关闭");
    }

    // ========== 4. 查询方法 ==========
    /**
     * 通用查询（带参数）
     * 返回 ResultSet，调用者需要自行关闭
     *
     * 使用示例：
     * ResultSet rs = DBUtil.executeQuery("SELECT * FROM user WHERE username = ?", "admin");
     */
    public static ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeQuery();
    }

    /**
     * 通用查询（无参数）
     */
    public static ResultSet executeQuery(String sql) throws SQLException {
        return executeQuery(sql, new Object[]{});
    }

    /**
     * 查询返回 List<Object[]>（每行数据）
     */
    public static List<Object[]> queryForList(String sql, Object... params) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = executeQuery(sql, params);
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                result.add(row);
            }
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return result;
    }

    // ========== 5. 更新方法 ==========
    /**
     * 通用更新：INSERT / UPDATE / DELETE
     * 返回受影响的行数
     */
    public static int executeUpdate(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate();
        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // ========== 6. 批量操作 ==========
    /**
     * 批量更新（事务内使用）
     * 示例：
     * List<Object[]> batch = new ArrayList<>();
     * batch.add(new Object[]{"张三", 1});
     * batch.add(new Object[]{"李四", 2});
     * DBUtil.executeBatch("INSERT INTO student (name, class_id) VALUES (?, ?)", batch);
     */
    public static int[] executeBatch(String sql, List<Object[]> batchParams) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (Object[] params : batchParams) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                stmt.addBatch();
            }
            return stmt.executeBatch();
        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // ========== 7. 获取最后插入的ID ==========
    /**
     * 获取 AUTO_INCREMENT 生成的ID
     * 必须在 INSERT 之后调用，且在同一个连接中
     */
    public static int getLastInsertId() throws SQLException {
        String sql = "SELECT LAST_INSERT_ID()";
        ResultSet rs = null;
        try {
            rs = executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // ========== 8. 测试主方法 ==========
    public static void main(String[] args) {
        try {
            System.out.println("════════════════════════════════════════════════");
            System.out.println("   DBUtil 测试");
            System.out.println("════════════════════════════════════════════════");

            // 测试连接
            Connection conn = getConnection();
            System.out.println("✓ 连接测试通过");

            // 测试查询
            ResultSet rs = executeQuery("SELECT 1");
            if (rs.next()) {
                System.out.println("✓ 查询测试通过：1 = " + rs.getInt(1));
            }
            rs.close();

            // 测试事务
            System.out.println("\n--- 事务测试 ---");
            beginTransaction();
            System.out.println("   (模拟业务操作)");
            commit();

            close();
            System.out.println("\n✓ 所有测试通过！");

        } catch (SQLException e) {
            System.out.println("✗ 测试失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}