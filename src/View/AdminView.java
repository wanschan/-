package View;

import entity.User;
import entity.School;
import entity.Student;
import Service.school.SchoolQueryService;
import Service.school.SchoolManageService;
import Service.student.StudentQueryService;
import Service.application.ApplicationService;
import utils.InputUtil;

import java.util.List;
import java.util.Map;

/**
 * 管理员视图
 * 功能：查看学校、管理学校、查看学生、查看报考统计
 */
public class AdminView {
    private SchoolQueryService schoolQueryService = new SchoolQueryService();
    private SchoolManageService schoolManageService = new SchoolManageService();
    private StudentQueryService studentQueryService = new StudentQueryService();
    private ApplicationService applicationService = new ApplicationService();

    public void show(User user) {
        while (true) {
            printAdminMenu(user);
            int choice = InputUtil.readInt("请选择操作（输入数字）: ");    //InputUtil有readInt等方法

            switch (choice) {
                case 1:
                    showAllSchools();
                    break;
                case 2:
                    showSchoolDetail();
                    break;
                case 3:
                    addSchool(user);
                    break;
                case 4:
                    updateSchool(user);
                    break;
                case 5:
                    deleteSchool(user);
                    break;
                case 6:
                    showAllStudents(user);
                    break;
                case 7:
                    showSchoolStudents();
                    break;
                case 8:
                    showSchoolStatistics();
                    break;
                case 0:
                    System.out.println("已退出登录");
                    return;
                default:
                    System.out.println("✗ 无效选择，请重新输入");
            }
            InputUtil.pressEnterToContinue();
        }
    }

    // ========== 管理员菜单 ==========
    private void printAdminMenu(User user) {
        System.out.println("════════════════════════════════════════════════");
        System.out.println("                    管理员菜单                    ");
        System.out.println("   当前用户：" + user.getUsername());
        System.out.println("   角色：管理员");
        System.out.println("════════════════════════════════════════════════");
        System.out.println("   1. 查看所有学校");
        System.out.println("   2. 查看学校详情");
        System.out.println("   3. 新增学校");
        System.out.println("   4. 修改学校信息");
        System.out.println("   5. 删除学校");
        System.out.println("   6. 查看所有学生（含成绩）");
        System.out.println("   7. 查看某学校的报考学生名单");
        System.out.println("   8. 查看各学校报名人数统计");
        System.out.println("   0. 退出登录");
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 1. 查看所有学校 ==========
    private void showAllSchools() {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [学校列表]");
        System.out.println("────────────────────────────────────────────────");

        try {
            List<School> schools = schoolQueryService.getAllSchools();
            if (schools.isEmpty()) {
                System.out.println("   暂无学校数据");
                return;
            }

            System.out.println("序号  学校名称         分数线    招生名额    已报名      层次      省份");
            System.out.println("────────────────────────────────────────────────");
            int i = 1;
            for (School s : schools) {
                System.out.printf("%-6d %-15s %-8d %-8d %-6d  %-6s %s\n",
                        i++,
                        truncateString(s.getName(), 12),
                        s.getMinScore(),
                        s.getQuota(),
                        s.getApplicationCount(),
                        s.getLevel() == null ? "-" : s.getLevel(),
                        s.getProvince() == null ? "-" : s.getProvince()
                );
            }
            System.out.println("共 " + schools.size() + " 所学校");
        } catch (Exception e) {
            System.out.println("✗ 查询失败：" + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 2. 查看学校详情 ==========
    private void showSchoolDetail() {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [学校详情]");
        System.out.println("────────────────────────────────────────────────");

        int schoolId = InputUtil.readInt("请输入学校ID: ");

        try {
            School s = schoolQueryService.getSchoolById(schoolId);
            System.out.println("\n   ─── " + s.getName() + " ───");
            System.out.println("   层次：" + (s.getLevel() == null ? "未设置" : s.getLevel()));
            System.out.println("   所在省份：" + (s.getProvince() == null ? "未设置" : s.getProvince()));
            System.out.println("   录取分数线：" + s.getMinScore() + " 分");
            System.out.println("   计划招生名额：" + s.getQuota() + " 人");
            System.out.println("   已报名人数：" + s.getApplicationCount() + " 人");
            System.out.println("   学校简介：" + (s.getIntroduction() == null ? "暂无" : s.getIntroduction()));
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 3. 新增学校 ==========
    private void addSchool(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [新增学校]");
        System.out.println("────────────────────────────────────────────────");

        try {
            String name = InputUtil.readString("请输入学校名称: ");
            String province = InputUtil.readString("请输入所在省份: ");
            String level = InputUtil.readString("请输入学校层次（985/211/双一流/普通）: ");
            int minScore = InputUtil.readInt("请输入录取分数线: ");
            int quota = InputUtil.readInt("请输入计划招生名额: ");
            String introduction = InputUtil.readString("请输入学校简介: ");

            School school = new School();
            school.setName(name);
            school.setProvince(province);
            school.setLevel(level);
            school.setMinScore(minScore);
            school.setQuota(quota);
            school.setIntroduction(introduction);

            System.out.println("\n   ⚠ 确认新增：");
            System.out.println("   学校名称：" + name);
            System.out.println("   省份：" + province);
            System.out.println("   层次：" + level);
            System.out.println("   分数线：" + minScore);

            if (!InputUtil.readConfirm("   确认新增该校？")) {
                System.out.println("已取消");
                return;
            }

            schoolManageService.addSchool(school, user);
            System.out.println("✓ 学校添加成功！");
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 4. 修改学校信息 ==========
    private void updateSchool(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [修改学校信息]");
        System.out.println("────────────────────────────────────────────────");

        try {
            int schoolId = InputUtil.readInt("请输入要修改的学校ID: ");
            School school = schoolQueryService.getSchoolById(schoolId);

            System.out.println("\n   ─── " + school.getName() + "（当前信息）───");
            System.out.println("   分数线：" + school.getMinScore());
            System.out.println("   招生名额：" + school.getQuota());
            System.out.println("   简介：" + (school.getIntroduction() == null ? "无" : school.getIntroduction()));

            String name = InputUtil.readString("请输入新的学校名称（不修改直接回车）: ");
            String province = InputUtil.readString("请输入新的省份（不修改直接回车）: ");
            String level = InputUtil.readString("请输入新的层次（不修改直接回车）: ");
            String minScoreStr = InputUtil.readString("请输入新的分数线（不修改直接回车）: ");
            String quotaStr = InputUtil.readString("请输入新的招生名额（不修改直接回车）: ");
            String introduction = InputUtil.readString("请输入新的简介（不修改直接回车）: ");

            if (!name.isEmpty()) school.setName(name);
            if (!province.isEmpty()) school.setProvince(province);
            if (!level.isEmpty()) school.setLevel(level);
            if (!minScoreStr.isEmpty()) school.setMinScore(Integer.parseInt(minScoreStr));
            if (!quotaStr.isEmpty()) school.setQuota(Integer.parseInt(quotaStr));
            if (!introduction.isEmpty()) school.setIntroduction(introduction);

            if (!InputUtil.readConfirm("   确认修改该校信息？")) {
                System.out.println("已取消");
                return;
            }

            schoolManageService.updateSchool(school, user);
            System.out.println("✓ 学校信息更新成功！");
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 5. 删除学校 ==========
    private void deleteSchool(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [删除学校]");
        System.out.println("────────────────────────────────────────────────");

        try {
            int schoolId = InputUtil.readInt("请输入要删除的学校ID: ");
            School school = schoolQueryService.getSchoolById(schoolId);

            System.out.println("\n   ─── 确认删除 ───");
            System.out.println("   学校名称：" + school.getName());
            System.out.println("   已报名人数：" + school.getApplicationCount() + " 人");

            if (school.getApplicationCount() > 0) {
                System.out.println("   ⚠ 警告：该校有 " + school.getApplicationCount() + " 名学生报考，删除后将同时删除所有报考记录！");
            }

            if (!InputUtil.readConfirm("   确认删除该校？")) {
                System.out.println("已取消");
                return;
            }

            schoolManageService.deleteSchool(schoolId, user);
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 6. 查看所有学生 ==========
    private void showAllStudents(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [学生列表]");
        System.out.println("────────────────────────────────────────────────");

        try {
            List<Student> students = studentQueryService.getAllStudents(user);
            if (students.isEmpty()) {
                System.out.println("   暂无学生数据");
                return;
            }

            System.out.println("序号  准考证号    姓名  性别  高考成绩  省份");
            System.out.println("────────────────────────────────────────────────");
            int i = 1;
            for (Student s : students) {
                System.out.printf("%-6d %-10s %-6s %-4s %-8d %s\n",
                        i++,
                        s.getExamNumber(),
                        s.getName(),
                        s.getGender(),
                        s.getScore(),
                        s.getProvince() == null ? "-" : s.getProvince()
                );
            }
            System.out.println("共 " + students.size() + " 名学生");
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 7. 查看某学校的报考学生名单 ==========
    private void showSchoolStudents() {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [报考学生名单]");
        System.out.println("────────────────────────────────────────────────");

        int schoolId = InputUtil.readInt("请输入学校ID: ");

        try {
            List<Map<String, Object>> students = schoolQueryService.getStudentsBySchool(schoolId);
            if (students.isEmpty()) {
                System.out.println("   暂无学生报考该校");
                return;
            }

            School school = schoolQueryService.getSchoolById(schoolId);
            System.out.println("\n   ─── " + school.getName() + "（报考学生 " + students.size() + " 人）───");
            System.out.println("   序号  准考证号    姓名  高考成绩  报考时间");
            System.out.println("   ──────────────────────────────────────────────");
            int i = 1;
            for (Map<String, Object> s : students) {
                System.out.printf("   %-6d %-10s %-6s %-8d %s\n",
                        i++,
                        s.get("exam_number"),
                        s.get("student_name"),
                        s.get("score"),
                        s.get("create_time")
                );
            }
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 8. 查看各学校报名人数统计 ==========
    private void showSchoolStatistics() {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [各学校报名人数统计]");
        System.out.println("────────────────────────────────────────────────");

        try {
            List<Map<String, Object>> stats = schoolQueryService.getSchoolStatistics();
            if (stats.isEmpty()) {
                System.out.println("   暂无数据");
                return;
            }

            System.out.println("序号  学校名称        计划招生  实际报名  报录比");
            System.out.println("────────────────────────────────────────────────");
            int i = 1;
            for (Map<String, Object> stat : stats) {
                int quota = (Integer) stat.get("quota");
                int count = (Integer) stat.get("application_count");
                double ratio = quota > 0 ? (double) count / quota * 100 : 0;
                System.out.printf("%-6d %-12s %-8d %-8d  %.1f%%\n",
                        i++,
                        truncateString((String) stat.get("school_name"), 12),
                        quota,
                        count,
                        ratio
                );
            }
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 辅助方法 ==========
    private String truncateString(String str, int maxLen) {
        if (str == null) return "-";
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen - 2) + "..";
    }
}