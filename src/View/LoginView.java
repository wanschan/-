package View;

import entity.User;
import Service.auth.AuthService;
import utils.InputUtil;

/**
 * 登录视图
 * 功能：登录、注册、退出
 */
public class LoginView {
    private AuthService authService = new AuthService();
    private StudentView studentView = new StudentView();
    private AdminView adminView = new AdminView();
    private SuperAdminView superAdminView = new SuperAdminView();

    public void show() {
        while (true) {
            printLoginMenu();
            int choice = InputUtil.readInt("请选择: ");

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 0:
                    System.out.println("\n感谢使用高考志愿填报模拟系统，再见\n                                   河北省教育考试院");
                    System.exit(0);
                    break;
                default:
                    System.out.println("✗ 无效选择，请重新输入");
                    InputUtil.pressEnterToContinue();
            }
        }
    }

    // ========== 登录菜单 ==========
    private void printLoginMenu() {
        System.out.println("════════════════════════════════════════════════\n");
        System.out.println("高考志愿填报模拟                      河北省教育考试院" );
        System.out.println("College Application Simulation           v26.1.0\n");

        System.out.println("════════════════════════════════════════════════");
        System.out.println("   1. 登录");
        System.out.println("   2. 注册（学生账号）\n");
        System.out.println("   0. 退出系统");
        System.out.println("────────────────────────────────────────────────");
    }

    // ========== 登录 ==========
    private void login() {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [用户登录]");
        System.out.println("────────────────────────────────────────────────");

        String username = InputUtil.readString("请输入用户名: ");
        String password = InputUtil.readString("            提示：学生账户初始密码是用户名+准考证号后四位\n请输入密码: ");

        try {
            User user = authService.login(username, password);

            System.out.println("✓ 登录成功！欢迎，" + username + "（" + getRoleName(user.getRole()) + "）");
            System.out.println("────────────────────────────────────────────────");
            InputUtil.pressEnterToContinue();

            // 根据角色跳转
            switch (user.getRole()) {
                case "student":
                    studentView.show(user);
                    break;
                case "admin":
                    adminView.show(user);
                    break;
                case "super_admin":
                    superAdminView.show(user);
                    break;
                default:
                    System.out.println("✗ 未知角色，请联系管理员");
            }
        } catch (Exception e) {
            System.out.println("✗ 登录失败：" + e.getMessage());
            InputUtil.pressEnterToContinue();
        }
    }

    // ========== 注册 ==========
    private void register() {
        System.out.println("────────────────────────────────────────────────");
        System.out.println("   [学生账号注册]");
        System.out.println("────────────────────────────────────────────────");

        System.out.println("   ─── 填写个人信息 ───");
        String examNumber = InputUtil.readString("请输入准考证号: ");
        String name = InputUtil.readString("请输入姓名: ");
        String gender = InputUtil.readString("请输入性别（男/女）: ");
        String province = InputUtil.readString("请输入省份: ");

        System.out.println("\n   ─── 设置登录账号 ───");
        String username = InputUtil.readString("请输入用户名: ");
        String password = InputUtil.readString("请输入密码: ");
        String confirmPwd = InputUtil.readString("请确认密码: ");

        // 密码一致性校验
        if (!password.equals(confirmPwd)) {
            System.out.println("✗ 两次密码输入不一致，注册失败！");
            InputUtil.pressEnterToContinue();
            return;
        }

        // 注册信息确认
        System.out.println("\n   ⚠ 注册信息确认：");
        System.out.println("   准考证号：" + examNumber);
        System.out.println("   姓名：" + name);
        System.out.println("   性别：" + gender);
        System.out.println("   省份：" + province);
        System.out.println("   用户名：" + username);

        if (!InputUtil.readConfirm("   确认提交？")) {
            System.out.println("已取消注册");
            InputUtil.pressEnterToContinue();
            return;
        }

        try {
            authService.register(examNumber, name, gender, province, username, password);
            System.out.println("\n✓ 注册成功！请使用用户名 \"" + username + "\" 登录");
        } catch (Exception e) {
            System.out.println("✗ 注册失败：" + e.getMessage());
        }
        InputUtil.pressEnterToContinue();
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
}