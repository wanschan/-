package Service.school;

import DAO.SchoolDAO;
import DAO.ApplicationDAO;
import entity.School;
import java.util.List;
import java.util.Map;

/**
 * 学校查询服务（所有角色均可使用）
 */
public class SchoolQueryService {
    private SchoolDAO schoolDAO = new SchoolDAO();
    private ApplicationDAO applicationDAO = new ApplicationDAO();

    /**
     * 获取所有学校列表
     */
    public List<School> getAllSchools() throws Exception {
        return schoolDAO.queryAll();
    }

    /**
     * 根据ID获取学校详情
     */
    public School getSchoolById(int schoolId) throws Exception {
        School school = schoolDAO.queryById(schoolId);
        if (school == null) {
            throw new Exception("学校不存在");
        }
        return school;
    }

    /**
     * 搜索学校（按名称或省份模糊匹配）
     */
    public List<School> searchSchools(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            return schoolDAO.queryAll();
        }
        return schoolDAO.searchByKeyword(keyword.trim());
    }

    /**
     * 获取各学校报名人数统计（管理员/超管用）
     */
    public List<Map<String, Object>> getSchoolStatistics() throws Exception {
        return applicationDAO.querySchoolStatistics();
    }

    /**
     * 获取某学校的报名学生名单（管理员/超管用）
     */
    public List<Map<String, Object>> getStudentsBySchool(int schoolId) throws Exception {
        School school = schoolDAO.queryById(schoolId);
        if (school == null) {
            throw new Exception("学校不存在");
        }
        return applicationDAO.queryStudentsBySchool(schoolId);
    }
}