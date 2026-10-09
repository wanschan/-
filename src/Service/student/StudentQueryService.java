package Service.student;

import DAO.StudentDAO;
import DAO.ApplicationDAO;
import entity.Student;
import entity.User;
import java.util.List;
import java.util.Map;

/**
 * 学生查询服务（所有角色均可使用，但权限不同）
 */
public class StudentQueryService {
    private StudentDAO studentDAO = new StudentDAO();
    private ApplicationDAO applicationDAO = new ApplicationDAO();

    /**
     * 查询学生个人信息（学生只能查自己，管理员/超管可查所有）
     */
    public Student getStudentInfo(int studentId, User currentUser) throws Exception {
        // 权限控制：普通学生只能查自己
        if ("student".equals(currentUser.getRole())) {
            if (currentUser.getStudentId() == null || currentUser.getStudentId() != studentId) {
                throw new Exception("您无权查看其他学生的信息");
            }
        }

        Student student = studentDAO.queryById(studentId);
        if (student == null) {
            throw new Exception("学生不存在");
        }
        return student;
    }

    /**
     * 查询所有学生（管理员/超管专用）
     */
    public List<Student> getAllStudents(User currentUser) throws Exception {
        // 权限检查：只有管理员和超管可以查看所有学生
        String role = currentUser.getRole();
        if (!"admin".equals(role) && !"super_admin".equals(role)) {
            throw new Exception("权限不足，无法查看所有学生");
        }
        return studentDAO.queryAll();
    }

    /**
     * 根据准考证号查询学生
     */
    public Student getStudentByExamNumber(String examNumber, User currentUser) throws Exception {
        Student student = studentDAO.queryByExamNumber(examNumber);
        if (student == null) {
            throw new Exception("学生不存在");
        }

        // 权限控制：普通学生只能查自己
        if ("student".equals(currentUser.getRole())) {
            if (currentUser.getStudentId() == null || currentUser.getStudentId() != student.getStudentId()) {
                throw new Exception("您无权查看其他学生的信息");
            }
        }

        return student;
    }

    /**
     * 获取学生报考统计（管理员用）
     */
    public Map<String, Object> getStudentApplicationStats(int studentId, User currentUser) throws Exception {
        // 权限检查
        if ("student".equals(currentUser.getRole())) {
            if (currentUser.getStudentId() == null || currentUser.getStudentId() != studentId) {
                throw new Exception("您无权查看其他学生的报考信息");
            }
        }

        Student student = studentDAO.queryById(studentId);
        if (student == null) {
            throw new Exception("学生不存在");
        }

        int count = applicationDAO.countByStudentId(studentId);
        List<Map<String, Object>> details = applicationDAO.queryApplicationDetailsByStudent(studentId);

        return Map.of(
                "student", student,
                "applicationCount", count,
                "applications", details
        );
    }
}