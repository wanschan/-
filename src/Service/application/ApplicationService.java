package Service.application;

import DAO.ApplicationDAO;
import DAO.SchoolDAO;
import DAO.StudentDAO;
import entity.Student;
import entity.User;
import entity.Application;
import entity.School;
import utils.DBUtil;
import java.util.List;
import java.util.Map;

/**
 * 报考管理服务（学生专用）
 */
public class ApplicationService {
    private ApplicationDAO applicationDAO = new ApplicationDAO();
    private SchoolDAO schoolDAO = new SchoolDAO();
    private StudentDAO studentDAO = new StudentDAO();

    /**
     * 学生报考学校
     */
    public void applySchool(int studentId, int schoolId, User currentUser) throws Exception {
        // 1. 权限检查：只有学生可以报考
        if (!"student".equals(currentUser.getRole())) {
            throw new Exception("只有学生可以报考学校");
        }

        // 2. 校验是否为本人在操作
        if (currentUser.getStudentId() == null || currentUser.getStudentId() != studentId) {
            throw new Exception("您只能为自己报考学校");
        }

        // 3. 检查学生是否存在
        if (studentDAO.queryById(studentId) == null) {
            throw new Exception("学生不存在");
        }

        // 4. 检查学校是否存在
        School school = schoolDAO.queryById(schoolId);
        if (school == null) {
            throw new Exception("学校不存在");
        }

        // 5. 检查是否已报满10所
        int count = applicationDAO.countByStudentId(studentId);
        if (count >= 10) {
            throw new Exception("您已报考10所学校，不能再报考更多");
        }

        // 6. 检查是否已报过该校（防止重复）
        if (applicationDAO.exists(studentId, schoolId)) {
            throw new Exception("您已报考该学校，请勿重复报名");
        }

        // 7. 开启事务：插入报考记录 + 更新报名人数
        try {
            DBUtil.beginTransaction();

            // 7.1 插入报考记录
            Application application = new Application(studentId, schoolId);
            applicationDAO.insert(application);

            // 7.2 更新学校报名人数 +1
            schoolDAO.incrementApplicationCount(schoolId);

            DBUtil.commit();
            System.out.println("✓ 报考成功！当前已报考 " + (count + 1) + " 所学校");
        } catch (Exception e) {
            DBUtil.rollback();
            throw new Exception("报考失败：" + e.getMessage());
        } finally {
            DBUtil.close();
        }
    }

    /**
     * 学生取消报考
     */
    public void cancelApplication(int studentId, int schoolId, User currentUser) throws Exception {
        // 1. 权限检查
        if (!"student".equals(currentUser.getRole())) {
            throw new Exception("只有学生可以取消报考");
        }

        // 2. 校验是否为本人在操作
        if (currentUser.getStudentId() == null || currentUser.getStudentId() != studentId) {
            throw new Exception("您只能取消自己的报考");
        }

        // 3. 检查报考记录是否存在
        if (!applicationDAO.exists(studentId, schoolId)) {
            throw new Exception("您没有报考该学校");
        }

        // 4. 检查学校是否存在
        School school = schoolDAO.queryById(schoolId);
        if (school == null) {
            throw new Exception("学校不存在");
        }

        // 5. 开启事务：删除报考记录 + 更新报名人数 -1
        try {
            DBUtil.beginTransaction();

            // 5.1 删除报考记录
            applicationDAO.delete(studentId, schoolId);

            // 5.2 更新学校报名人数 -1
            schoolDAO.decrementApplicationCount(schoolId);

            DBUtil.commit();
            System.out.println("✓ 已取消报考：" + school.getName());
        } catch (Exception e) {
            DBUtil.rollback();
            throw new Exception("取消报考失败：" + e.getMessage());
        } finally {
            DBUtil.close();
        }
    }

    /**
     * 查看我的报考记录
     */
    public List<Map<String, Object>> getMyApplications(int studentId, User currentUser) throws Exception {
        // 权限检查：只能查看自己的
        if (!"student".equals(currentUser.getRole())) {
            throw new Exception("只有学生可以查看报考记录");
        }
        if (currentUser.getStudentId() == null || currentUser.getStudentId() != studentId) {
            throw new Exception("您只能查看自己的报考记录");
        }

        return applicationDAO.queryApplicationDetailsByStudent(studentId);
    }

    /**
     * 查看已报考数量
     */
    public int getApplicationCount(int studentId, User currentUser) throws Exception {
        if (!"student".equals(currentUser.getRole())) {
            throw new Exception("只有学生可以查询报考数量");
        }
        if (currentUser.getStudentId() == null || currentUser.getStudentId() != studentId) {
            throw new Exception("您只能查询自己的报考数量");
        }

        return applicationDAO.countByStudentId(studentId);
    }

    /**
     * 管理员查看所有报考记录
     */
    public List<Map<String, Object>> getAllApplications(User currentUser) throws Exception {
        String role = currentUser.getRole();
        if (!"admin".equals(role) && !"super_admin".equals(role)) {
            throw new Exception("权限不足，无法查看所有报考记录");
        }
        return applicationDAO.queryApplicationDetails();
    }

    /**
     * 管理员查看某学生的报考记录
     */
    public List<Map<String, Object>> getStudentApplications(int studentId, User currentUser) throws Exception {
        String role = currentUser.getRole();
        if (!"admin".equals(role) && !"super_admin".equals(role)) {
            throw new Exception("权限不足，无法查看其他学生的报考记录");
        }

        Student student = studentDAO.queryById(studentId);
        if (student == null) {
            throw new Exception("学生不存在");
        }

        return applicationDAO.queryApplicationDetailsByStudent(studentId);
    }
}