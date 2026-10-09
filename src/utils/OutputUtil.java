package utils;

import java.util.List;
import java.util.Map;

/**
 * 输出工具类
 * 功能：格式化输出，美化控制台显示
 */
public class OutputUtil {

    // ========== 1. 分隔线 ==========
    /**
     * 打印单分隔线（40个字符）
     */
    public static void printSeparator() {
        System.out.println("────────────────────────────────────────────────────────────");
    }

    /**
     * 打印单分隔线（自定义长度）
     */
    public static void printSeparator(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("─");
        }
        System.out.println(sb.toString());
    }

    /**
     * 打印双分隔线
     */
    public static void printDoubleSeparator() {
        System.out.println("════════════════════════════════════════════════════════════");
    }

    /**
     * 打印短分隔线
     */
    public static void printShortSeparator() {
        System.out.println("───────────────────────────────────────────────────");
    }

    // ========== 2. 标题 ==========
    /**
     * 打印标题（带双分隔线）
     */
    public static void printTitle(String title) {
        printDoubleSeparator();
        System.out.println("   ★★★ " + title + " ★★★");
        printDoubleSeparator();
    }

    /**
     * 打印小标题（带单分隔线）
     */
    public static void printSubTitle(String title) {
        printSeparator();
        System.out.println("   【" + title + "】");
        printSeparator();
    }

    // ========== 3. 带图标的消息 ==========
    /**
     * 打印成功消息（绿色对勾）
     */
    public static void printSuccess(String message) {
        System.out.println("   ✅ " + message);
    }

    /**
     * 打印错误消息（红色叉号）
     */
    public static void printError(String message) {
        System.out.println("   ❌ " + message);
    }

    /**
     * 打印警告消息（黄色感叹号）
     */
    public static void printWarning(String message) {
        System.out.println("   ⚠️ " + message);
    }

    /**
     * 打印信息消息（蓝色信息）
     */
    public static void printInfo(String message) {
        System.out.println("   ℹ️ " + message);
    }

    /**
     * 打印普通消息
     */
    public static void printMessage(String message) {
        System.out.println("   " + message);
    }

    // ========== 4. 表格输出 ==========
    /**
     * 打印表格（简单版）
     * @param headers 表头
     * @param data 数据行
     * @param widths 列宽
     */
    public static void printTable(String[] headers, List<Object[]> data, int[] widths) {
        if (headers == null || data == null) {
            return;
        }

        // 打印表头
        printTableRow(headers, widths);
        printTableSeparator(widths);

        // 打印数据
        for (Object[] row : data) {
            printTableRow(row, widths);
        }
    }

    /**
     * 打印表格（带Map数据）
     * @param headers 表头
     * @param data 数据行（List<Map>）
     * @param keys Map的key数组
     * @param widths 列宽
     */
    public static void printTableFromMap(String[] headers, List<Map<String, Object>> data,
                                         String[] keys, int[] widths) {
        if (headers == null || data == null || keys == null) {
            return;
        }

        // 打印表头
        printTableRow(headers, widths);
        printTableSeparator(widths);

        // 打印数据
        for (Map<String, Object> row : data) {
            Object[] rowData = new Object[keys.length];
            for (int i = 0; i < keys.length; i++) {
                rowData[i] = row.get(keys[i]);
            }
            printTableRow(rowData, widths);
        }
    }

    /**
     * 打印表格的一行
     */
    private static void printTableRow(Object[] columns, int[] widths) {
        if (columns == null || widths == null || columns.length != widths.length) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            String value = columns[i] == null ? "-" : columns[i].toString();
            sb.append(alignLeft(value, widths[i]));
            sb.append("  ");
        }
        System.out.println(sb.toString());
    }

    /**
     * 打印表格分隔线
     */
    private static void printTableSeparator(int[] widths) {
        StringBuilder sb = new StringBuilder();
        for (int width : widths) {
            for (int i = 0; i < width + 2; i++) {
                sb.append("─");
            }
        }
        System.out.println(sb.toString());
    }

    // ========== 5. 对齐工具 ==========
    /**
     * 左对齐
     */
    public static String alignLeft(String str, int width) {
        if (str == null) str = "-";
        if (str.length() >= width) {
            return str.substring(0, width);
        }
        return String.format("%-" + width + "s", str);
    }

    /**
     * 右对齐
     */
    public static String alignRight(String str, int width) {
        if (str == null) str = "-";
        if (str.length() >= width) {
            return str.substring(0, width);
        }
        return String.format("%" + width + "s", str);
    }

    /**
     * 居中对齐
     */
    public static String alignCenter(String str, int width) {
        if (str == null) str = "-";
        if (str.length() >= width) {
            return str.substring(0, width);
        }
        int left = (width - str.length()) / 2;
        int right = width - str.length() - left;
        return String.format("%" + left + "s%s%" + right + "s", "", str, "");
    }

    /**
     * 截断字符串
     */
    public static String truncate(String str, int maxLen) {
        if (str == null) return "-";
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen - 2) + "..";
    }

    // ========== 6. 空行 ==========
    /**
     * 打印空行
     */
    public static void printEmptyLine() {
        System.out.println();
    }

    /**
     * 打印指定数量的空行
     */
    public static void printEmptyLines(int count) {
        for (int i = 0; i < count; i++) {
            System.out.println();
        }
    }

    // ========== 7. 列表输出 ==========
    /**
     * 打印序号列表
     * @param items 列表项
     */
    public static void printNumberedList(List<String> items) {
        if (items == null || items.isEmpty()) {
            printInfo("列表为空");
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            System.out.printf("   %d. %s\n", i + 1, items.get(i));
        }
    }

    /**
     * 打印键值对
     */
    public static void printKeyValue(String key, String value) {
        System.out.printf("   %s：%s\n", key, value == null ? "-" : value);
    }

    /**
     * 打印键值对（带缩进）
     */
    public static void printKeyValueIndent(String key, String value, int indent) {
        String prefix = " ".repeat(indent);
        System.out.printf("%s%s：%s\n", prefix, key, value == null ? "-" : value);
    }

    // ========== 8. 进度/状态提示 ==========
    /**
     * 打印加载中...
     */
    public static void printLoading(String message) {
        System.out.print("   ⏳ " + message + "...");
    }

    /**
     * 打印完成
     */
    public static void printDone() {
        System.out.println(" ✅ 完成");
    }
}