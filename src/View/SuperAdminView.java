package View;

import entity.User;
import entity.Student;
import Service.school.SchoolQueryService;
import Service.school.SchoolManageService;
import Service.student.StudentQueryService;
import Service.student.ScoreManageService;
import Service.application.ApplicationService;
import Service.user.UserManageService;
import utils.InputUtil;

import java.util.List;
import java.util.Map;

/**
 * 超级管理员视图
 * 功能：学校管理、成绩管理、用户管理、查看所有报考记录
 */
public class SuperAdminView {
    private SchoolQueryService schoolQueryService = new SchoolQueryService();
    private SchoolManageService schoolManageService = new SchoolManageService();
    private StudentQueryService studentQueryService = new StudentQueryService();
    private ScoreManageService scoreManageService = new ScoreManageService();
    private ApplicationService applicationService = new ApplicationService();
    private UserManageService userManageService = new UserManageService();

    public void show(User user) {
        while (true) {
            printSuperAdminMenu(user);
            int choice = InputUtil.readInt("请选择操作（输入数字）: ");

            switch (choice) {
                case 1:
                    showAllSchools();
                    break;
                case 2:
                    addSchool(user);
                    break;
                case 3:
                    updateSchool(user);
                    break;
                case 4:
                    deleteSchool(user);
                    break;
                case 5:
                    showAllStudents(user);
                    break;
                case 6:
                    updateStudentScore(user);
                    break;
                case 7:
                    showAllApplications(user);
                    break;
                case 8:
                    userManagement(user);
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

    // ========== 超级管理员菜单 ==========
    private void printSuperAdminMenu(User user) {
        System.out.println("════════════════════════════════════════════════");
        System.out.println("                  超级管理员菜单                   ");
        System.out.println("   当前用户：" + user.getUsername());
        System.out.println("   角色：超级管理员（最高权限）");
        System.out.println("════════════════════════════════════════════════");
        System.out.println("   1. 查看所有学校");
        System.out.println("   2. 新增学校");
        System.out.println("   3. 修改学校信息");
        System.out.println("   4. 删除学校");
        System.out.println("   5. 查看所有学生及成绩");
        System.out.println("   6. 修改学生成绩");
        System.out.println("   7. 查看所有报考记录");
        System.out.println("   8. 用户管理");
        System.out.println("   0. 退出登录");
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 1. 查看所有学校 ==========
    private void showAllSchools() {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [学校列表]");
        System.out.println("────────────────────────────────────────────────");

        try {
            List<entity.School> schools = schoolQueryService.getAllSchools();
            if (schools.isEmpty()) {
                System.out.println("   暂无学校数据");
                return;
            }

            System.out.println("序号  学校名称         分数线    招生名额    已报名      层次      省份");
            System.out.println("────────────────────────────────────────────────");
            int i = 1;
            for (entity.School s : schools) {
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
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 2. 新增学校 ==========
    private void addSchool(User user) {
        // 复用 AdminView 的逻辑，但这里简化直接写
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

            entity.School school = new entity.School();
            school.setName(name);
            school.setProvince(province);
            school.setLevel(level);
            school.setMinScore(minScore);
            school.setQuota(quota);
            school.setIntroduction(introduction);

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

    // ========== 3. 修改学校信息 ==========
    private void updateSchool(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [修改学校信息]");
        System.out.println("────────────────────────────────────────────────");

        try {
            int schoolId = InputUtil.readInt("请输入要修改的学校ID: ");
            entity.School school = schoolQueryService.getSchoolById(schoolId);

            System.out.println("\n   ─── " + school.getName() + "（当前信息）───");
            System.out.println("   分数线：" + school.getMinScore());
            System.out.println("   招生名额：" + school.getQuota());

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

    // ========== 4. 删除学校 ==========
    private void deleteSchool(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [删除学校]");
        System.out.println("────────────────────────────────────────────────");

        try {
            int schoolId = InputUtil.readInt("请输入要删除的学校ID: ");
            entity.School school = schoolQueryService.getSchoolById(schoolId);

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

    // ========== 5. 查看所有学生及成绩 ==========
    private void showAllStudents(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [学生列表及成绩]");
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

    // ========== 6. 修改学生成绩（超管专属） ==========
    private void updateStudentScore(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [修改学生成绩]");
        System.out.println("────────────────────────────────────────────────");

        try {
            String examNumber = InputUtil.readString("请输入学生准考证号: ");
            Student student = studentQueryService.getStudentByExamNumber(examNumber, user);

            System.out.println("\n   ─── 当前学生信息 ───");
            System.out.println("   准考证号：" + student.getExamNumber());
            System.out.println("   姓名：" + student.getName());
            System.out.println("   当前成绩：" + student.getScore() + " 分");

            int newScore = InputUtil.readInt("   请输入新的成绩: ");

            if (!InputUtil.readConfirm("   ⚠ 确认将成绩从 " + student.getScore() + " 修改为 " + newScore + " 分？")) {
                System.out.println("已取消");
                return;
            }

            scoreManageService.updateScore(student.getStudentId(), newScore, user);
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 7. 查看所有报考记录 ==========
    private void showAllApplications(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [所有报考记录]");
        System.out.println("────────────────────────────────────────────────");

        try {
            List<Map<String, Object>> apps = applicationService.getAllApplications(user);
            if (apps.isEmpty()) {
                System.out.println("   暂无报考记录");
                return;
            }

            System.out.println("序号  准考证号    学生姓名  高考成绩  报考学校        报考时间");
            System.out.println("────────────────────────────────────────────────────────────");
            int i = 1;
            for (Map<String, Object> app : apps) {
                System.out.printf("%-6d %-10s %-6s %-8d %-12s %s\n",
                        i++,
                        app.get("exam_number"),
                        app.get("student_name"),
                        app.get("score"),
                        truncateString((String) app.get("school_name"), 12),
                        app.get("create_time")
                );
            }
            System.out.println("共 " + apps.size() + " 条报考记录");
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 8. 用户管理（超管专属） ==========
    private void userManagement(User user) {
        while (true) {
            System.out.println("────────────────────────────────────────────────");
            System.out.println("   [用户管理]");
            System.out.println("────────────────────────────────────────────────");
            System.out.println("   1. 查看所有用户");
            System.out.println("   2. 新增用户");
            System.out.println("   3. 修改用户角色");
            System.out.println("   4. 重置用户密码");
            System.out.println("   5. 删除用户");
            System.out.println("   0. 返回上级菜单");
            System.out.println("────────────────────────────────────────────────");

            int choice = InputUtil.readInt("请选择操作（输入数字）: ");

            switch (choice) {
                case 1:
                    showAllUsers(user);
                    break;
                case 2:
                    createUser(user);
                    break;
                case 3:
                    updateUserRole(user);
                    break;
                case 4:
                    resetUserPassword(user);
                    break;
                case 5:
                    deleteUser(user);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("✗ 无效选择");
            }
            InputUtil.pressEnterToContinue();
        }
    }

    private void showAllUsers(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [用户列表]");
        System.out.println("────────────────────────────────────────────────");

        try {
            List<User> users = userManageService.getAllUsers(user);
            System.out.println("用户ID  用户名           角色          关联学生");
            System.out.println("────────────────────────────────────────────────");
            for (User u : users) {
                System.out.printf("%-8d %-14s %-12s %s\n",
                        u.getUserId(),
                        u.getUsername(),
                        getRoleName(u.getRole()),
                        u.getStudentId() == null ? "无" : u.getStudentId().toString()
                );
            }
            System.out.println("共 " + users.size() + " 个用户");
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    private void createUser(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [新增用户]");
        System.out.println("────────────────────────────────────────────────");

        try {
            String username = InputUtil.readString("请输入用户名: ");
            String password = InputUtil.readString("请输入密码（至少6位）: ");
            if (password.length() < 6) {
                System.out.println("✗ 密码长度不能少于6位");
                return;
            }

            System.out.println("   角色：");
            System.out.println("   1. student（学生）");
            System.out.println("   2. admin（管理员）");
            System.out.println("   3. super_admin（超级管理员）");
            int roleChoice = InputUtil.readInt("请选择角色（1-3）: ");
            String role;
            Integer studentId = null;
            switch (roleChoice) {
                case 1:
                    role = "student";
                    studentId = InputUtil.readInt("请输入关联的学生ID（可输入0跳过）: ");
                    if (studentId == 0) studentId = null;
                    break;
                case 2:
                    role = "admin";
                    break;
                case 3:
                    role = "super_admin";
                    break;
                default:
                    System.out.println("✗ 无效选择");
                    return;
            }

            if (!InputUtil.readConfirm("   确认创建用户 " + username + "？")) {
                System.out.println("已取消");
                return;
            }

            userManageService.createUser(username, password, role, studentId, user);
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    private void updateUserRole(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [修改用户角色]");
        System.out.println("────────────────────────────────────────────────");

        try {
            String username = InputUtil.readString("请输入要修改角色的用户名: ");

            System.out.println("   角色：");
            System.out.println("   1. student（学生）");
            System.out.println("   2. admin（管理员）");
            System.out.println("   3. super_admin（超级管理员）");
            int roleChoice = InputUtil.readInt("请选择新角色（1-3）: ");
            String newRole;
            switch (roleChoice) {
                case 1:
                    newRole = "student";
                    break;
                case 2:
                    newRole = "admin";
                    break;
                case 3:
                    newRole = "super_admin";
                    break;
                default:
                    System.out.println("✗ 无效选择");
                    return;
            }

            if (!InputUtil.readConfirm("   确认将用户 " + username + " 角色修改为 " + getRoleName(newRole) + "？")) {
                System.out.println("已取消");
                return;
            }

            userManageService.updateUserRole(username, newRole, user);
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    private void resetUserPassword(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [重置用户密码]");
        System.out.println("────────────────────────────────────────────────");

        try {
            String username = InputUtil.readString("请输入要重置密码的用户名: ");
            String newPassword = InputUtil.readString("请输入新密码（至少6位）: ");
            if (newPassword.length() < 6) {
                System.out.println("✗ 密码长度不能少于6位");
                return;
            }

            if (!InputUtil.readConfirm("   确认重置用户 " + username + " 的密码？")) {
                System.out.println("已取消");
                return;
            }

            userManageService.resetPassword(username, newPassword, user);
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    private void deleteUser(User user) {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [删除用户]");
        System.out.println("────────────────────────────────────────────────");

        try {
            String username = InputUtil.readString("请输入要删除的用户名: ");

            if (!InputUtil.readConfirm("   ⚠ 确认删除用户 " + username + "？此操作不可恢复！")) {
                System.out.println("已取消");
                return;
            }

            userManageService.deleteUser(username, user);
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 辅助方法 ==========
    private String getRoleName(String role) {
        switch (role) {
            case "student": return "学生";
            case "admin": return "管理员";
            case "super_admin": return "超级管理员";
            default: return role;
        }
    }

    private String truncateString(String str, int maxLen) {
        if (str == null) return "-";
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen - 2) + "..";
    }
}