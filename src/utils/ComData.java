package utils;

/**
 * 数据库配置常量
 */
public class ComData {
    // MySQL 驱动（低版本使用 com.mysql.jdbc.Driver）
    public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    // 数据库连接URL（改成你的数据库名）
    public static final String DB_URL = "jdbc:mysql://localhost:3306/college_admission_system?useSSL=false&characterEncoding=utf8";

    // 数据库用户名
    public static final String USER = "root";

    // 数据库密码（根据实际情况修改）
    public static final String PASS = "";
}