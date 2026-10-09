package Service.student;

import DAO.StudentDAO;
import DAO.ApplicationDAO;
import entity.User;
import entity.Student;
import utils.DBUtil;

/**
 * 成绩管理服务（仅超级管理员专用）
 */
public class ScoreManageService {
    private StudentDAO studentDAO = new StudentDAO();
    private ApplicationDAO applicationDAO = new ApplicationDAO();

    /**
     * 修改学生成绩（仅超管）
     */
    public void updateScore(int studentId, int newScore, User currentUser) throws Exception {
        // 1. 权限检查：仅 super_admin
        if (currentUser == null || !"super_admin".equals(currentUser.getRole())) {
            throw new Exception("权限不足，只有超级管理员可以修改成绩");
        }

        // 2. 检查学生是否存在
        Student student = studentDAO.queryById(studentId);
        if (student == null) {
            throw new Exception("学生不存在");
        }

        // 3. 成绩范围校验（0-750分，可根据实际情况调整）
        if (newScore < 0 || newScore > 750) {
            throw new Exception("成绩范围应在 0-750 分之间");
        }

        // 4. 执行更新
        int oldScore = student.getScore();
        int affected = studentDAO.updateScore(studentId, newScore);
        if (affected <= 0) {
            throw new Exception("成绩修改失败，请稍后重试");
        }

        System.out.println("✓ 学生 [" + student.getName() + "] 成绩已从 " + oldScore + " 修改为 " + newScore);
    }

    /**
     * 批量修改成绩（仅超管，按准考证号）
     */
    public void updateScoreByExamNumber(String examNumber, int newScore, User currentUser) throws Exception {
        if (currentUser == null || !"super_admin".equals(currentUser.getRole())) {
            throw new Exception("权限不足，只有超级管理员可以修改成绩");
        }

        Student student = studentDAO.queryByExamNumber(examNumber);
        if (student == null) {
            throw new Exception("准考证号不存在");
        }

        updateScore(student.getStudentId(), newScore, currentUser);
    }

    /**
     * 获取学生当前成绩（仅超管可查看所有，学生只能看自己）
     */
    public Student getStudentScore(int studentId, User currentUser) throws Exception {
        if ("student".equals(currentUser.getRole())) {
            if (currentUser.getStudentId() == null || currentUser.getStudentId() != studentId) {
                throw new Exception("您无权查看其他学生的成绩");
            }
        }

        Student student = studentDAO.queryById(studentId);
        if (student == null) {
            throw new Exception("学生不存在");
        }
        return student;
    }
}