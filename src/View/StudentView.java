package View;

import entity.User;
import entity.School;
import entity.Student;
import Service.school.SchoolQueryService;
import Service.student.StudentQueryService;
import Service.application.ApplicationService;
import utils.InputUtil;

import java.util.List;
import java.util.Map;

/**
 * 学生视图
 * 功能：查看成绩、查看学校、报考、查看报考记录、取消报考
 */
public class StudentView {
    private SchoolQueryService schoolQueryService = new SchoolQueryService();
    private StudentQueryService studentQueryService = new StudentQueryService();
    private ApplicationService applicationService = new ApplicationService();

    public void show(User user) {
        while (true) {
            printStudentMenu(user);
            int choice = InputUtil.readInt("请选择操作（输入数字）: ");

            switch (choice) {
                case 1:
                    showMyScore(user);
                    break;
                case 2:
                    showAllSchools();
                    break;
                case 3:
                    showSchoolDetail();
                    break;
                case 4:
                    searchSchools();
                    break;
                case 5:
                    applySchool(user);
                    break;
                case 6:
                    showMyApplications(user);
                    break;
                case 7:
                    cancelApplication(user);
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

    // ========== 学生菜单 ==========
    private void printStudentMenu(User user) {
        System.out.println("════════════════════════════════════════════════");
        System.out.println("                    学生菜单                      ");
        try {
            int count = applicationService.getApplicationCount(user.getStudentId(), user);
            System.out.println("   当前用户：" + user.getUsername() + "（已报考 " + count + "/10 所）");
        } catch (Exception e) {
            System.out.println("   当前用户：" + user.getUsername());
        }
        System.out.println("   角色：学生");
        System.out.println("════════════════════════════════════════════════");
        System.out.println("   1. 查看我的成绩");
        System.out.println("   2. 查看所有学校");
        System.out.println("   3. 查看学校详情（按ID搜索）");
        System.out.println("   4. 搜索学校（按关键字）");
        System.out.println("   5. 报考学校");
        System.out.println("   6. 查看我的报考记录");
        System.out.println("   7. 取消报考");
        System.out.println("   0. 退出登录");
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 1. 查看我的成绩 ==========
    private void showMyScore(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [我的成绩]");
        System.out.println("────────────────────────────────────────────────");

        try {
            int studentId = user.getStudentId();
            if (studentId == 0) {
                System.out.println("✗ 未关联学生信息，请联系管理员");
                return;
            }

            Student student = studentQueryService.getStudentInfo(studentId, user);
            System.out.println("   准考证号：" + student.getExamNumber());
            System.out.println("   姓名：" + student.getName());
            System.out.println("   性别：" + student.getGender());
            System.out.println("   省份：" + student.getProvince());
            System.out.println("   高考总分：" + student.getScore() + " 分");
        } catch (Exception e) {
            System.out.println("✗ 查询失败：" + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 2. 查看所有学校 ==========
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

            System.out.println("序号  学校名称        分数线  招生名额  已报名  层次  省份");
            System.out.println("────────────────────────────────────────────────");
            int i = 1;
            for (School s : schools) {
                System.out.printf("%-6d %-12s %-8d %-8d %-6d  %-6s %s\n",
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

    // ========== 3. 查看学校详情 ==========
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

    // ========== 4. 搜索学校 ==========
    private void searchSchools() {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [搜索学校]");
        System.out.println("────────────────────────────────────────────────");

        String keyword = InputUtil.readString("请输入关键字（学校名称/省份）: ");

        try {
            List<School> schools = schoolQueryService.searchSchools(keyword);
            if (schools.isEmpty()) {
                System.out.println("   未找到匹配的学校");
                return;
            }

            System.out.println("\n   ─── 搜索结果（共 " + schools.size() + " 所）───");
            for (School s : schools) {
                System.out.println("   " + s.getSchoolId() + ". " + s.getName() +
                        "（" + (s.getProvince() == null ? "-" : s.getProvince()) + "）" +
                        " 分数线：" + s.getMinScore());
            }
        } catch (Exception e) {
            System.out.println("✗ 搜索失败：" + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 5. 报考学校 ==========
    private void applySchool(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [报考学校]");
        System.out.println("────────────────────────────────────────────────");

        try {
            int studentId = user.getStudentId();
            int count = applicationService.getApplicationCount(studentId, user);
            System.out.println("   当前已报考：" + count + " 所（最多10所）");

            if (count >= 10) {
                System.out.println("✗ 您已报考10所学校，不能再报考更多");
                return;
            }

            int schoolId = InputUtil.readInt("请输入要报考的学校ID: ");

            // 确认报考
            School school = schoolQueryService.getSchoolById(schoolId);
            System.out.println("   学校名称：" + school.getName());
            System.out.println("   分数线：" + school.getMinScore() + " 分");

            if (!InputUtil.readConfirm("   确认报考该校？")) {
                System.out.println("已取消报考");
                return;
            }

            applicationService.applySchool(studentId, schoolId, user);
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 6. 查看我的报考记录 ==========
    private void showMyApplications(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [我的报考记录]");
        System.out.println("────────────────────────────────────────────────");

        try {
            int studentId = user.getStudentId();
            int count = applicationService.getApplicationCount(studentId, user);
            System.out.println("   已报考 " + count + " 所（最多10所）");

            if (count == 0) {
                System.out.println("   您还没有报考任何学校");
                return;
            }

            List<Map<String, Object>> apps = applicationService.getMyApplications(studentId, user);

            System.out.println("\n   序号  学校名称        分数线  已报名  报考时间");
            System.out.println("   ────────────────────────────────────────────────");
            int i = 1;
            for (Map<String, Object> app : apps) {
                System.out.printf("   %-6d %-12s %-8d %-6d  %s\n",
                        i++,
                        truncateString((String) app.get("school_name"), 12),
                        app.get("min_score"),
                        app.get("application_count"),
                        app.get("create_time")
                );
            }
            System.out.println("\n   剩余可报考：" + (10 - count) + " 所");
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 7. 取消报考 ==========
    private void cancelApplication(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [取消报考]");
        System.out.println("────────────────────────────────────────────────");

        try {
            int studentId = user.getStudentId();
            List<Map<String, Object>> apps = applicationService.getMyApplications(studentId, user);

            if (apps.isEmpty()) {
                System.out.println("   您还没有报考任何学校，无需取消");
                return;
            }

            System.out.println("   您的报考记录：");
            int i = 1;
            for (Map<String, Object> app : apps) {
                System.out.println("   " + i++ + ". " + app.get("school_name"));
            }

            int schoolId = InputUtil.readInt("请输入要取消报考的学校ID: ");

            // 确认取消
            School school = schoolQueryService.getSchoolById(schoolId);
            System.out.println("   学校：" + school.getName());

            if (!InputUtil.readConfirm("   确认取消报考该校？")) {
                System.out.println("已取消操作");
                return;
            }

            applicationService.cancelApplication(studentId, schoolId, user);
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