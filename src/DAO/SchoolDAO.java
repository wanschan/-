package DAO;

import entity.School;
import java.sql.*;
import java.util.*;

/**
 * 学校表DAO
 * 对应表：school
 */
public class SchoolDAO extends BaseDAO {

    /**
     * 根据学校ID查询
     */
    public School queryById(int schoolId) throws SQLException {
        String sql = "SELECT school_id, name, introduction, min_score, quota, application_count, " +
                "province, level, create_time, update_time FROM school WHERE school_id = ?";
        Map<String, Object> map = queryForMap(sql, schoolId);
        if (map == null) {
            return null;
        }
        return mapToSchool(map);
    }

    /**
     * 根据学校名称查询
     */
    public School queryByName(String name) throws SQLException {
        String sql = "SELECT school_id, name, introduction, min_score, quota, application_count, " +
                "province, level, create_time, update_time FROM school WHERE name = ?";
        Map<String, Object> map = queryForMap(sql, name);
        if (map == null) {
            return null;
        }
        return mapToSchool(map);
    }

    /**
     * 查询所有学校
     */
    public List<School> queryAll() throws SQLException {
        String sql = "SELECT school_id, name, introduction, min_score, quota, application_count, " +
                "province, level, create_time, update_time FROM school ORDER BY school_id";
        List<Map<String, Object>> list = queryForList(sql);
        List<School> schools = new ArrayList<>();
        for (Map<String, Object> map : list) {
            schools.add(mapToSchool(map));
        }
        return schools;
    }

    /**
     * 按关键字搜索学校（名称或省份模糊匹配）
     */
    public List<School> searchByKeyword(String keyword) throws SQLException {
        String sql = "SELECT school_id, name, introduction, min_score, quota, application_count, " +
                "province, level, create_time, update_time FROM school " +
                "WHERE name LIKE ? OR province LIKE ? ORDER BY school_id";
        String like = "%" + keyword + "%";
        List<Map<String, Object>> list = queryForList(sql, like, like);
        List<School> schools = new ArrayList<>();
        for (Map<String, Object> map : list) {
            schools.add(mapToSchool(map));
        }
        return schools;
    }

    /**
     * 插入学校
     */
    public int insert(School school) throws SQLException {
        String sql = "INSERT INTO school (name, introduction, min_score, quota, application_count, province, level) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return executeUpdate(sql,
                school.getName(),
                school.getIntroduction(),
                school.getMinScore(),
                school.getQuota(),
                school.getApplicationCount() == null ? 0 : school.getApplicationCount(),
                school.getProvince(),
                school.getLevel());
    }

    /**
     * 更新学校信息
     */
    public int update(School school) throws SQLException {
        String sql = "UPDATE school SET name = ?, introduction = ?, min_score = ?, quota = ?, " +
                "province = ?, level = ? WHERE school_id = ?";
        return executeUpdate(sql,
                school.getName(),
                school.getIntroduction(),
                school.getMinScore(),
                school.getQuota(),
                school.getProvince(),
                school.getLevel(),
                school.getSchoolId());
    }

    /**
     * 更新报名人数 +1（报考时调用）
     */
    public int incrementApplicationCount(int schoolId) throws SQLException {
        String sql = "UPDATE school SET application_count = application_count + 1 WHERE school_id = ?";
        return executeUpdate(sql, schoolId);
    }

    /**
     * 更新报名人数 -1（取消报考时调用）
     */
    public int decrementApplicationCount(int schoolId) throws SQLException {
        String sql = "UPDATE school SET application_count = application_count - 1 WHERE school_id = ? AND application_count > 0";
        return executeUpdate(sql, schoolId);
    }

    /**
     * 删除学校
     */
    public int deleteById(int schoolId) throws SQLException {
        String sql = "DELETE FROM school WHERE school_id = ?";
        return executeUpdate(sql, schoolId);
    }

    // ========== 私有辅助方法 ==========
    private School mapToSchool(Map<String, Object> map) {
        School school = new School();
        school.setSchoolId((Integer) map.get("school_id"));
        school.setName((String) map.get("name"));
        school.setIntroduction((String) map.get("introduction"));
        school.setMinScore((Integer) map.get("min_score"));
        school.setQuota((Integer) map.get("quota"));
        school.setApplicationCount((Integer) map.get("application_count"));
        school.setProvince((String) map.get("province"));
        school.setLevel((String) map.get("level"));
        school.setCreateTime(map.get("create_time") == null ? null : map.get("create_time").toString());
        school.setUpdateTime(map.get("update_time") == null ? null : map.get("update_time").toString());
        return school;
    }
}