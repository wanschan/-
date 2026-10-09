package entity;

/**
 * 用户实体类
 * 对应表：user
 */
public class User {
    private Integer userId;      // 用户ID（主键）
    private String username;     // 用户名（登录用）
    private String password;     // 密码
    private String role;         // 角色：student / admin / super_admin
    private Integer studentId;   // 关联的学生ID（学生角色时必填）

    // 无参构造
    public User() {}

    // 全参构造（不含userId，用于插入）
    public User(String username, String password, String role, Integer studentId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.studentId = studentId;
    }

    // 全参构造（含userId）
    public User(Integer userId, String username, String password, String role, Integer studentId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.studentId = studentId;
    }

    // ========== Getter & Setter ==========
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    // ========== toString ==========
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", studentId=" + studentId +
                '}';
    }
}

//User{userId=1, username='john_doe', role='student', studentId=1001}