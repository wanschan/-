package Service.school;

import DAO.SchoolDAO;
import DAO.ApplicationDAO;
import entity.School;
import entity.User;
import utils.DBUtil;

/**
 * 学校管理服务（管理员/超管专用）
 */
public class SchoolManageService {
    private SchoolDAO schoolDAO = new SchoolDAO();
    private ApplicationDAO applicationDAO = new ApplicationDAO();

    /**
     * 新增学校
     */
    public void addSchool(School school, User currentUser) throws Exception {
        // 1. 权限检查
        checkAdminOrSuperAdmin(currentUser);

        // 2. 校验学校名称是否已存在
        School exist = schoolDAO.queryByName(school.getName());
        if (exist != null) {
            throw new Exception("学校名称已存在");
        }

        // 3. 插入学校（报名人数默认为0）
        school.setApplicationCount(0);
        schoolDAO.insert(school);
    }

    /**
     * 修改学校信息
     */
    public void updateSchool(School school, User currentUser) throws Exception {
        // 1. 权限检查
        checkAdminOrSuperAdmin(currentUser);

        // 2. 检查学校是否存在
        School exist = schoolDAO.queryById(school.getSchoolId());
        if (exist == null) {
            throw new Exception("学校不存在");
        }

        // 3. 如果修改了名称，检查新名称是否被其他学校占用
        if (!exist.getName().equals(school.getName())) {
            School nameExist = schoolDAO.queryByName(school.getName());
            if (nameExist != null && nameExist.getSchoolId() != school.getSchoolId()) {
                throw new Exception("学校名称已被其他学校使用");
            }
        }

        // 4. 更新学校信息（报名人数保持不变）
        school.setApplicationCount(exist.getApplicationCount());
        schoolDAO.update(school);
    }

    /**
     * 删除学校（Service层手动删除报考记录）
     */
    public void deleteSchool(int schoolId, User currentUser) throws Exception {
        // 1. 权限检查
        checkAdminOrSuperAdmin(currentUser);

        // 2. 检查学校是否存在
        School school = schoolDAO.queryById(schoolId);
        if (school == null) {
            throw new Exception("学校不存在");
        }

        // 3. 开启事务：先删报考记录，再删学校
        try {
            DBUtil.beginTransaction();

            // 3.1 删除该学校的所有报考记录
            int deletedCount = applicationDAO.deleteBySchoolId(schoolId);
            System.out.println("已删除 " + deletedCount + " 条关联的报考记录");

            // 3.2 删除学校
            schoolDAO.deleteById(schoolId);

            DBUtil.commit();
            System.out.println("✓ 学校 [" + school.getName() + "] 删除成功");
        } catch (Exception e) {
            DBUtil.rollback();
            throw new Exception("删除学校失败：" + e.getMessage());
        } finally {
            DBUtil.close();
        }
    }

    // ========== 私有权限检查方法 ==========
    private void checkAdminOrSuperAdmin(User user) throws Exception {
        if (user == null) {
            throw new Exception("请先登录");
        }
        String role = user.getRole();
        if (!"admin".equals(role) && !"super_admin".equals(role)) {
            throw new Exception("权限不足，需要管理员或超级管理员权限");
        }
    }
}