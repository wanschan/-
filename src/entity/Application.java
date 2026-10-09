package entity;

/**
 * 报考记录实体类
 * 对应表：application
 */
public class Application {
    private Integer applicationId;   // 报考记录ID（主键）
    private Integer studentId;       // 学生ID（外键）
    private Integer schoolId;        // 学校ID（外键）
    private String createTime;       // 报考时间

    // 无参构造
    public Application() {}

    // 构造（不含applicationId，用于插入）
    public Application(Integer studentId, Integer schoolId) {
        this.studentId = studentId;
        this.schoolId = schoolId;
    }

    // 全参构造
    public Application(Integer applicationId, Integer studentId, Integer schoolId, String createTime) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.schoolId = schoolId;
        this.createTime = createTime;
    }

    // ========== Getter & Setter ==========
    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    // ========== toString ==========
    @Override
    public String toString() {
        return "Application{" +
                "applicationId=" + applicationId +
                ", studentId=" + studentId +
                ", schoolId=" + schoolId +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}