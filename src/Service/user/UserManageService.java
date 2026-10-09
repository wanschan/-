package Service.user;

import DAO.UserDAO;
import DAO.StudentDAO;
import entity.User;
import utils.DBUtil;
import java.util.List;

/**
 * 用户管理服务（仅超级管理员专用）
 */
public class UserManageService {
    private UserDAO userDAO = new UserDAO();
    private StudentDAO studentDAO = new StudentDAO();

    /**
     * 查询所有用户
     */
    public List<User> getAllUsers(User currentUser) throws Exception {
        // 权限检查：仅 super_admin
        if (currentUser == null || !"super_admin".equals(currentUser.getRole())) {
            throw new Exception("权限不足，只有超级管理员可以管理用户");
        }
        return userDAO.queryAll();
    }

    /**
     * 修改用户角色
     */
    public void updateUserRole(String username, String newRole, User currentUser) throws Exception {
        // 1. 权限检查
        if (currentUser == null || !"super_admin".equals(currentUser.getRole())) {
            throw new Exception("权限不足，只有超级管理员可以修改用户角色");
        }

        // 2. 不能修改自己的角色
        if (username.equals(currentUser.getUsername())) {
            throw new Exception("不能修改自己的角色");
        }

        // 3. 校验用户是否存在
        User user = userDAO.queryByUsername(username);
        if (user == null) {
            throw new Exception("用户不存在");
        }

        // 4. 校验角色是否合法
        if (!"student".equals(newRole) && !"admin".equals(newRole) && !"super_admin".equals(newRole)) {
            throw new Exception("角色不合法，可选值：student / admin / super_admin");
        }

        // 5. 执行更新
        int affected = userDAO.updateRole(username, newRole);
        if (affected <= 0) {
            throw new Exception("修改角色失败");
        }

        System.out.println("✓ 用户 [" + username + "] 角色已修改为: " + newRole);
    }

    /**
     * 创建用户（超管手动创建）
     * @param username 用户名
     * @param password 密码
     * @param role 角色
     * @param studentId 关联的学生ID（如果是学生角色则必填）
     */
    public void createUser(String username, String password, String role, Integer studentId, User currentUser) throws Exception {
        // 1. 权限检查
        if (currentUser == null || !"super_admin".equals(currentUser.getRole())) {
            throw new Exception("权限不足，只有超级管理员可以创建用户");
        }

        // 2. 校验用户名是否已存在
        if (userDAO.existsByUsername(username)) {
            throw new Exception("用户名已存在");
        }

        // 3. 校验角色
        if (!"student".equals(role) && !"admin".equals(role) && !"super_admin".equals(role)) {
            throw new Exception("角色不合法，可选值：student / admin / super_admin");
        }

        // 4. 如果是学生角色，校验 student_id 是否有效
        if ("student".equals(role)) {
            if (studentId == null) {
                throw new Exception("学生角色必须关联学生ID");
            }
            if (studentDAO.queryById(studentId) == null) {
                throw new Exception("学生ID不存在");
            }
            // 检查该学生是否已有账号
            if (userDAO.queryByStudentId(studentId) != null) {
                throw new Exception("该学生已有关联账号，不能重复创建");
            }
        }

        // 5. 插入用户
        User user = new User(username, password, role, studentId);
        userDAO.insert(user);
        System.out.println("✓ 用户 [" + username + "] 创建成功，角色: " + role);
    }

    /**
     * 删除用户
     */
    public void deleteUser(String username, User currentUser) throws Exception {
        // 1. 权限检查
        if (currentUser == null || !"super_admin".equals(currentUser.getRole())) {
            throw new Exception("权限不足，只有超级管理员可以删除用户");
        }

        // 2. 不能删除自己
        if (username.equals(currentUser.getUsername())) {
            throw new Exception("不能删除自己的账号");
        }

        // 3. 校验用户是否存在
        User user = userDAO.queryByUsername(username);
        if (user == null) {
            throw new Exception("用户不存在");
        }

        // 4. 执行删除
        int affected = userDAO.deleteByUsername(username);
        if (affected <= 0) {
            throw new Exception("删除用户失败");
        }

        System.out.println("✓ 用户 [" + username + "] 已删除");
    }

    /**
     * 修改密码（管理员/超管重置密码）
     */
    public void resetPassword(String username, String newPassword, User currentUser) throws Exception {
        if (currentUser == null || !"super_admin".equals(currentUser.getRole()) && !"admin".equals(currentUser.getRole())) {
            throw new Exception("权限不足");
        }

        if (newPassword.length() < 6) {
            throw new Exception("密码长度不能少于6位");
        }

        User user = userDAO.queryByUsername(username);
        if (user == null) {
            throw new Exception("用户不存在");
        }

        userDAO.updatePassword(username, newPassword);
        System.out.println("✓ 用户 [" + username + "] 密码已重置");
    }

    /**
     * 根据用户名查询用户
     */
    public User getUserByUsername(String username, User currentUser) throws Exception {
        if (currentUser == null || !"super_admin".equals(currentUser.getRole())) {
            throw new Exception("权限不足");
        }
        return userDAO.queryByUsername(username);
    }
}