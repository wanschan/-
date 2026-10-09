package DAO;

import entity.Student;
import java.sql.*;
import java.util.*;

/**
 * 学生表DAO
 * 对应表：student
 */
public class StudentDAO extends BaseDAO {

    /**
     * 根据学生ID查询
     */
    public Student queryById(int studentId) throws SQLException {
        String sql = "SELECT student_id, exam_number, name, gender, score, province, create_time, update_time " +
                "FROM student WHERE student_id = ?";
        Map<String, Object> map = queryForMap(sql, studentId);
        if (map == null) {
            return null;
        }
        return mapToStudent(map);
    }

    /**
     * 根据准考证号查询
     */
    public Student queryByExamNumber(String examNumber) throws SQLException {
        String sql = "SELECT student_id, exam_number, name, gender, score, province, create_time, update_time " +
                "FROM student WHERE exam_number = ?";
        Map<String, Object> map = queryForMap(sql, examNumber);
        if (map == null) {
            return null;
        }
        return mapToStudent(map);
    }

    /**
     * 查询所有学生
     */
    public List<Student> queryAll() throws SQLException {
        String sql = "SELECT student_id, exam_number, name, gender, score, province, create_time, update_time " +
                "FROM student ORDER BY student_id";
        List<Map<String, Object>> list = queryForList(sql);
        List<Student> students = new ArrayList<>();
        for (Map<String, Object> map : list) {
            students.add(mapToStudent(map));
        }
        return students;
    }

    /**
     * 检查准考证号是否存在
     */
    public boolean existsByExamNumber(String examNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM student WHERE exam_number = ?";
        Object obj = queryForSingleValue(sql, examNumber);
        return obj != null && ((Number) obj).intValue() > 0;
    }

    /**
     * 插入学生
     */
    public int insert(Student student) throws SQLException {
        String sql = "INSERT INTO student (exam_number, name, gender, score, province) VALUES (?, ?, ?, ?, ?)";
        return executeUpdate(sql,
                student.getExamNumber(),
                student.getName(),
                student.getGender(),
                student.getScore(),
                student.getProvince());
    }

    /**
     * 更新学生成绩（超管专用）
     */
    public int updateScore(int studentId, int newScore) throws SQLException {
        String sql = "UPDATE student SET score = ? WHERE student_id = ?";
        return executeUpdate(sql, newScore, studentId);
    }

    /**
     * 更新学生信息（不含成绩）
     */
    public int update(Student student) throws SQLException {
        String sql = "UPDATE student SET name = ?, gender = ?, province = ? WHERE student_id = ?";
        return executeUpdate(sql,
                student.getName(),
                student.getGender(),
                student.getProvince(),
                student.getStudentId());
    }

    /**
     * 删除学生
     */
    public int deleteById(int studentId) throws SQLException {
        String sql = "DELETE FROM student WHERE student_id = ?";
        return executeUpdate(sql, studentId);
    }

    // ========== 私有辅助方法 ==========
    private Student mapToStudent(Map<String, Object> map) {
        Student student = new Student();
        student.setStudentId((Integer) map.get("student_id"));
        student.setExamNumber((String) map.get("exam_number"));
        student.setName((String) map.get("name"));
        student.setGender((String) map.get("gender"));
        student.setScore((Integer) map.get("score"));
        student.setProvince((String) map.get("province"));
        student.setCreateTime(map.get("create_time") == null ? null : map.get("create_time").toString());
        student.setUpdateTime(map.get("update_time") == null ? null : map.get("update_time").toString());
        return student;
    }
}