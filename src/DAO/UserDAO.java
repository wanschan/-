package DAO;

import entity.User;
import java.sql.*;
import java.util.*;

/**
 * 用户表DAO
 * 对应表：user
 */
public class UserDAO extends BaseDAO {

    /**
     * 根据用户名查询用户（登录用）
     */
    public User queryByUsername(String username) throws SQLException {
        String sql = "SELECT user_id, username, password, role, student_id FROM user WHERE username = ?";
        Map<String, Object> map = queryForMap(sql, username);
        if (map == null) {
            return null;
        }
        return mapToUser(map);
    }

    /**
     * 根据用户ID查询用户
     */
    public User queryById(int userId) throws SQLException {
        String sql = "SELECT user_id, username, password, role, student_id FROM user WHERE user_id = ?";
        Map<String, Object> map = queryForMap(sql, userId);
        if (map == null) {
            return null;
        }
        return mapToUser(map);
    }

    /**
     * 根据学生ID查询用户（用于通过准考证号查登录账号）
     */
    public User queryByStudentId(int studentId) throws SQLException {
        String sql = "SELECT user_id, username, password, role, student_id FROM user WHERE student_id = ?";
        Map<String, Object> map = queryForMap(sql, studentId);
        if (map == null) {
            return null;
        }
        return mapToUser(map);
    }

    /**
     * 查询所有用户
     */
    public List<User> queryAll() throws SQLException {
        String sql = "SELECT user_id, username, password, role, student_id FROM user";
        List<Map<String, Object>> list = queryForList(sql);
        List<User> users = new ArrayList<>();
        for (Map<String, Object> map : list) {
            users.add(mapToUser(map));
        }
        return users;
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
        Object obj = queryForSingleValue(sql, username);
        return obj != null && ((Number) obj).intValue() > 0;
    }

    /**
     * 插入用户
     */
    public int insert(User user) throws SQLException {
        String sql = "INSERT INTO user (username, password, role, student_id) VALUES (?, ?, ?, ?)";
        return executeUpdate(sql, user.getUsername(), user.getPassword(),
                user.getRole(), user.getStudentId());
    }

    /**
     * 更新用户角色
     */
    public int updateRole(String username, String newRole) throws SQLException {
        String sql = "UPDATE user SET role = ? WHERE username = ?";
        return executeUpdate(sql, newRole, username);
    }

    /**
     * 更新密码
     */
    public int updatePassword(String username, String newPassword) throws SQLException {
        String sql = "UPDATE user SET password = ? WHERE username = ?";
        return executeUpdate(sql, newPassword, username);
    }

    /**
     * 删除用户
     */
    public int deleteByUsername(String username) throws SQLException {
        String sql = "DELETE FROM user WHERE username = ?";
        return executeUpdate(sql, username);
    }

    /**
     * 删除用户（按ID）
     */
    public int deleteById(int userId) throws SQLException {
        String sql = "DELETE FROM user WHERE user_id = ?";
        return executeUpdate(sql, userId);
    }

    // ========== 私有辅助方法 ==========
    private User mapToUser(Map<String, Object> map) {
        User user = new User();
        user.setUserId((Integer) map.get("user_id"));
        user.setUsername((String) map.get("username"));
        user.setPassword((String) map.get("password"));
        user.setRole((String) map.get("role"));
        Object studentIdObj = map.get("student_id");
        user.setStudentId(studentIdObj == null ? null : (Integer) studentIdObj);
        return user;
    }
}