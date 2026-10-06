# Java 语句示例集锦

本文档提供了一系列 Java 编程语言的常见语句示例，涵盖了基础语法、控制结构、面向对象、集合操作及异常处理等核心内容。

## 1. 基础语法与数据类型

### 变量声明与赋值

```java
import java.util.Scanner;

public class IOExample {
    public static void main(String[] rgs) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入您的名字: ");
        String userName = scanner.nextLine();
        System.out.println("您好, " + userName + "!");
        scanner.close();
    }
}
```


