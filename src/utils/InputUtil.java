package utils;

import java.util.Scanner;

/**
 * 输入工具类
 * 功能：封装键盘输入操作，提供类型安全的输入方法
 */
public class InputUtil {

    private static Scanner scanner = new Scanner(System.in);

    // ========== 1. 字符串输入 ==========
    /**
     * 读取字符串（不允许为空）
     * @param prompt 提示信息
     * @return 输入的字符串（trim后）
     */
    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("✗ 输入不能为空，请重新输入");
        }
    }

    /**
     * 读取字符串（允许为空）
     * @param prompt 提示信息
     * @return 输入的字符串（trim后），空字符串返回 ""
     */
    public static String readStringAllowEmpty(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input;
    }

    /**
     * 读取字符串（带默认值）
     * @param prompt 提示信息
     * @param defaultValue 默认值
     * @return 输入的内容，如果直接回车则返回默认值
     */
    public static String readStringWithDefault(String prompt, String defaultValue) {
        System.out.print(prompt + "（默认：" + defaultValue + "）：");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    // ========== 2. 整数输入 ==========
    /**
     * 读取整数
     * @param prompt 提示信息
     * @return 输入的整数
     */
    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("✗ 输入不能为空，请重新输入");
                    continue;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("✗ 输入无效，请输入数字");
            }
        }
    }

    /**
     * 读取整数（带默认值）
     * @param prompt 提示信息
     * @param defaultValue 默认值
     * @return 输入的整数，如果直接回车则返回默认值
     */
    public static int readIntWithDefault(String prompt, int defaultValue) {
        while (true) {
            try {
                System.out.print(prompt + "（默认：" + defaultValue + "）：");
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    return defaultValue;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("✗ 输入无效，请输入数字");
            }
        }
    }

    /**
     * 读取整数（范围限制）
     * @param prompt 提示信息
     * @param min 最小值（包含）
     * @param max 最大值（包含）
     * @return 输入的整数
     */
    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt + "（" + min + "-" + max + "）：");
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("✗ 输入不能为空，请重新输入");
                    continue;
                }
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("✗ 输入超出范围，请在 " + min + "-" + max + " 之间选择");
            } catch (NumberFormatException e) {
                System.out.println("✗ 输入无效，请输入数字");
            }
        }
    }

    // ========== 3. 长整数输入 ==========
    /**
     * 读取长整数
     */
    public static long readLong(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("✗ 输入不能为空，请重新输入");
                    continue;
                }
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("✗ 输入无效，请输入数字");
            }
        }
    }

    // ========== 4. 浮点数输入 ==========
    /**
     * 读取双精度浮点数
     */
    public static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("✗ 输入不能为空，请重新输入");
                    continue;
                }
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("✗ 输入无效，请输入数字");
            }
        }
    }

    // ========== 5. 确认输入（y/n） ==========
    /**
     * 读取确认（y/n）
     * @param prompt 提示信息
     * @return true 表示 y/yes，false 表示 n/no
     */
    public static boolean readConfirm(String prompt) {
        while (true) {
            System.out.print(prompt + "（y/n）：");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            }
            System.out.println("✗ 输入无效，请输入 y 或 n");
        }
    }

    /**
     * 读取确认（带默认值）
     * @param prompt 提示信息
     * @param defaultValue 默认值
     * @return true 表示 y/yes，false 表示 n/no
     */
    public static boolean readConfirmWithDefault(String prompt, boolean defaultValue) {
        while (true) {
            String defaultStr = defaultValue ? "y" : "n";
            System.out.print(prompt + "（y/n，默认：" + defaultStr + "）：");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty()) {
                return defaultValue;
            }
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            }
            System.out.println("✗ 输入无效，请输入 y 或 n");
        }
    }

    // ========== 6. 菜单选择 ==========
    /**
     * 读取菜单选择（返回 int）
     */
    public static int readMenuChoice(String prompt, int min, int max) {
        return readIntInRange(prompt, min, max);
    }

    // ========== 7. 按回车继续 ==========
    /**
     * 按回车继续
     */
    public static void pressEnterToContinue() {
        System.out.print("\n按回车键继续...");
        scanner.nextLine();
    }

    /**
     * 按回车继续（带自定义消息）
     */
    public static void pressEnterToContinue(String message) {
        System.out.print(message);
        scanner.nextLine();
    }
}