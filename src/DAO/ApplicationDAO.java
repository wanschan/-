package DAO;

import entity.Application;
import java.sql.*;
import java.util.*;

/**
 * 报考记录表DAO
 * 对应表：application
 */
public class ApplicationDAO extends BaseDAO {

    /**
     * 根据报考记录ID查询
     */
    public Application queryById(int applicationId) throws SQLException {
        String sql = "SELECT application_id, student_id, school_id, create_time FROM application WHERE application_id = ?";
        Map<String, Object> map = queryForMap(sql, applicationId);
        if (map == null) {
            return null;
        }
        return mapToApplication(map);
    }

    /**
     * 查询某学生的所有报考记录
     */
    public List<Application> queryByStudentId(int studentId) throws SQLException {
        String sql = "SELECT application_id, student_id, school_id, create_time FROM application " +
                "WHERE student_id = ? ORDER BY create_time";
        List<Map<String, Object>> list = queryForList(sql, studentId);
        List<Application> applications = new ArrayList<>();
        for (Map<String, Object> map : list) {
            applications.add(mapToApplication(map));
        }
        return applications;
    }

    /**
     * 查询某学校的所有报考记录
     */
    public List<Application> queryBySchoolId(int schoolId) throws SQLException {
        String sql = "SELECT application_id, student_id, school_id, create_time FROM application " +
                "WHERE school_id = ? ORDER BY create_time";
        List<Map<String, Object>> list = queryForList(sql, schoolId);
        List<Application> applications = new ArrayList<>();
        for (Map<String, Object> map : list) {
            applications.add(mapToApplication(map));
        }
        return applications;
    }

    /**
     * 查询某学生的报考数量
     */
    public int countByStudentId(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM application WHERE student_id = ?";
        Object obj = queryForSingleValue(sql, studentId);
        return obj == null ? 0 : ((Number) obj).intValue();
    }

    /**
     * 查询某学校的报考人数
     */
    public int countBySchoolId(int schoolId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM application WHERE school_id = ?";
        Object obj = queryForSingleValue(sql, schoolId);
        return obj == null ? 0 : ((Number) obj).intValue();
    }

    /**
     * 检查某学生是否已报考某学校
     */
    public boolean exists(int studentId, int schoolId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM application WHERE student_id = ? AND school_id = ?";
        Object obj = queryForSingleValue(sql, studentId, schoolId);
        return obj != null && ((Number) obj).intValue() > 0;
    }

    /**
     * 插入报考记录
     */
    public int insert(Application application) throws SQLException {
        String sql = "INSERT INTO application (student_id, school_id) VALUES (?, ?)";
        return executeUpdate(sql, application.getStudentId(), application.getSchoolId());
    }

    /**
     * 删除报考记录（取消报考）
     */
    public int delete(int studentId, int schoolId) throws SQLException {
        String sql = "DELETE FROM application WHERE student_id = ? AND school_id = ?";
        return executeUpdate(sql, studentId, schoolId);
    }

    /**
     * 删除某学生的所有报考记录（删除学生时调用）
     */
    public int deleteByStudentId(int studentId) throws SQLException {
        String sql = "DELETE FROM application WHERE student_id = ?";
        return executeUpdate(sql, studentId);
    }

    /**
     * 删除某学校的所有报考记录（删除学校时Service层手动调用）
     */
    public int deleteBySchoolId(int schoolId) throws SQLException {
        String sql = "DELETE FROM application WHERE school_id = ?";
        return executeUpdate(sql, schoolId);
    }

    /**
     * 查询报考详情（联表查询：学生名 + 学校名）
     * 返回 List<Map>，包含：application_id, student_name, exam_number, score, school_name, min_score, create_time
     */
    public List<Map<String, Object>> queryApplicationDetails() throws SQLException {
        String sql = "SELECT " +
                "a.application_id, " +
                "s.name AS student_name, " +
                "s.exam_number, " +
                "s.score, " +
                "sch.name AS school_name, " +
                "sch.min_score, " +
                "a.create_time " +
                "FROM application a " +
                "JOIN student s ON a.student_id = s.student_id " +
                "JOIN school sch ON a.school_id = sch.school_id " +
                "ORDER BY a.create_time DESC";
        return queryForList(sql);
    }

    /**
     * 查询某学生的报考详情（联表查询）
     */
    public List<Map<String, Object>> queryApplicationDetailsByStudent(int studentId) throws SQLException {
        String sql = "SELECT " +
                "a.application_id, " +
                "s.name AS student_name, " +
                "s.exam_number, " +
                "s.score, " +
                "sch.name AS school_name, " +
                "sch.min_score, " +
                "sch.quota, " +
                "sch.application_count, " +
                "sch.introduction, " +
                "a.create_time " +
                "FROM application a " +
                "JOIN student s ON a.student_id = s.student_id " +
                "JOIN school sch ON a.school_id = sch.school_id " +
                "WHERE a.student_id = ? " +
                "ORDER BY a.create_time";
        return queryForList(sql, studentId);
    }

    /**
     * 查询某学校的报考学生名单
     */
    public List<Map<String, Object>> queryStudentsBySchool(int schoolId) throws SQLException {
        String sql = "SELECT " +
                "a.application_id, " +
                "s.student_id, " +
                "s.exam_number, " +
                "s.name AS student_name, " +
                "s.score, " +
                "s.province, " +
                "a.create_time " +
                "FROM application a " +
                "JOIN student s ON a.student_id = s.student_id " +
                "WHERE a.school_id = ? " +
                "ORDER BY s.score DESC";
        return queryForList(sql, schoolId);
    }

    /**
     * 各学校报名人数统计
     */
    public List<Map<String, Object>> querySchoolStatistics() throws SQLException {
        String sql = "SELECT " +
                "sch.school_id, " +
                "sch.name AS school_name, " +
                "sch.quota, " +
                "sch.application_count, " +
                "sch.min_score, " +
                "sch.level, " +
                "sch.province " +
                "FROM school sch " +
                "ORDER BY sch.application_count DESC";
        return queryForList(sql);
    }

    // ========== 私有辅助方法 ==========
    private Application mapToApplication(Map<String, Object> map) {
        Application application = new Application();
        application.setApplicationId((Integer) map.get("application_id"));
        application.setStudentId((Integer) map.get("student_id"));
        application.setSchoolId((Integer) map.get("school_id"));
        application.setCreateTime(map.get("create_time") == null ? null : map.get("create_time").toString());
        return application;
    }
}