package Service.auth;

import DAO.UserDAO;
import DAO.StudentDAO;
import entity.User;
import entity.Student;
import utils.DBUtil;

/**
 * 认证服务：登录 + 注册
 */
public class AuthService {
    private UserDAO userDAO = new UserDAO();
    private StudentDAO studentDAO = new StudentDAO();

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的User对象
     * @throws Exception 登录失败时抛出异常
     */
    public User login(String username, String password) throws Exception {
        // 1. 查询用户
        User user = userDAO.queryByUsername(username);
        if (user == null) {
            throw new Exception("用户名不存在");
        }

        // 2. 验证密码
        if (!user.getPassword().equals(password)) {
            throw new Exception("密码错误");
        }

        return user;
    }

    /**
     * 学生注册
     * @param examNumber 准考证号
     * @param name 姓名
     * @param gender 性别
     * @param province 省份
     * @param username 用户名
     * @param password 密码
     * @throws Exception 注册失败时抛出异常
     */
    public void register(String examNumber, String name, String gender,
                         String province, String username, String password) throws Exception {
        // 1. 校验用户名是否已存在
        if (userDAO.existsByUsername(username)) {
            throw new Exception("用户名已被占用，请换一个");
        }

        // 2. 校验准考证号是否已存在
        if (studentDAO.existsByExamNumber(examNumber)) {
            throw new Exception("准考证号已被注册，请核对后重试");
        }

        // 3. 密码长度校验
        if (password.length() < 6) {
            throw new Exception("密码长度不能少于6位");
        }

        // 4. 开启事务（插入student + 插入user）
        try {
            DBUtil.beginTransaction();

            // 4.1 插入学生表（成绩默认为0）
            Student student = new Student(examNumber, name, gender, 0, province);
            studentDAO.insert(student);

            // 4.2 获取刚生成的 student_id
            int studentId = studentDAO.getLastInsertId();

            // 4.3 插入用户表（角色固定为 student）
            User user = new User(username, password, "student", studentId);
            userDAO.insert(user);

            DBUtil.commit();
        } catch (Exception e) {
            DBUtil.rollback();
            throw e;
        } finally {
            DBUtil.close();
        }
    }

    /**
     * 通过准考证号查找对应的User（用于登录时支持准考证号登录）
     */
    public User loginByExamNumber(String examNumber, String password) throws Exception {
        // 1. 通过准考证号查学生
        Student student = studentDAO.queryByExamNumber(examNumber);
        if (student == null) {
            throw new Exception("准考证号不存在");
        }

        // 2. 通过 student_id 查用户
        User user = userDAO.queryByStudentId(student.getStudentId());
        if (user == null) {
            throw new Exception("该学生尚未注册账号，请先注册");
        }

        // 3. 验证密码
        if (!user.getPassword().equals(password)) {
            throw new Exception("密码错误");
        }

        return user;
    }
}