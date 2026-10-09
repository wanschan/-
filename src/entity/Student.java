package entity;

/**
 * 学生实体类
 * 对应表：student
 */
public class Student {
    private Integer studentId;    // 学生ID（主键，内部使用）
    private String examNumber;    // 准考证号（唯一，自定义编码）
    private String name;          // 姓名
    private String gender;        // 性别
    private Integer score;        // 高考总分
    private String province;      // 省份
    private String createTime;    // 创建时间
    private String updateTime;    // 更新时间

    // 无参构造
    public Student() {}

    // 构造（不含studentId，用于插入）
    public Student(String examNumber, String name, String gender, Integer score, String province) {
        this.examNumber = examNumber;
        this.name = name;
        this.gender = gender;
        this.score = score;
        this.province = province;
    }

    // 全参构造
    public Student(Integer studentId, String examNumber, String name, String gender,
                   Integer score, String province, String createTime, String updateTime) {
        this.studentId = studentId;
        this.examNumber = examNumber;
        this.name = name;
        this.gender = gender;
        this.score = score;
        this.province = province;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // ========== Getter & Setter ==========
    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getExamNumber() {
        return examNumber;
    }

    public void setExamNumber(String examNumber) {
        this.examNumber = examNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    // ========== toString ==========
    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", examNumber='" + examNumber + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", score=" + score +
                ", province='" + province + '\'' +
                '}';
    }
}