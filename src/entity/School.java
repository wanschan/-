package entity;

/**
 * 学校实体类
 * 对应表：school
 */
public class School {
    private Integer schoolId;           // 学校ID（主键）
    private String name;               // 学校名称
    private String introduction;       // 学校简介
    private Integer minScore;          // 录取分数线（管理员设置）
    private Integer quota;             // 计划招生名额（仅展示用）
    private Integer applicationCount;  // 实际报名人数（系统自动维护）
    private String province;           // 所在省份
    private String level;              // 学校层次：985/211/双一流/普通
    private String createTime;         // 创建时间
    private String updateTime;         // 更新时间

    // 无参构造
    public School() {}

    // 构造（不含schoolId，用于插入）
    public School(String name, String introduction, Integer minScore, Integer quota,
                  Integer applicationCount, String province, String level) {
        this.name = name;
        this.introduction = introduction;
        this.minScore = minScore;
        this.quota = quota;
        this.applicationCount = applicationCount;
        this.province = province;
        this.level = level;
    }

    // 全参构造
    public School(Integer schoolId, String name, String introduction, Integer minScore,
                  Integer quota, Integer applicationCount, String province, String level,
                  String createTime, String updateTime) {
        this.schoolId = schoolId;
        this.name = name;
        this.introduction = introduction;
        this.minScore = minScore;
        this.quota = quota;
        this.applicationCount = applicationCount;
        this.province = province;
        this.level = level;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // ========== Getter & Setter ==========
    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Integer getMinScore() {
        return minScore;
    }

    public void setMinScore(Integer minScore) {
        this.minScore = minScore;
    }

    public Integer getQuota() {
        return quota;
    }

    public void setQuota(Integer quota) {
        this.quota = quota;
    }

    public Integer getApplicationCount() {
        return applicationCount;
    }

    public void setApplicationCount(Integer applicationCount) {
        this.applicationCount = applicationCount;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
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
    @Override   // 重写toString方法
    public String toString() {
        return "School{" +
                "schoolId=" + schoolId +
                ", name='" + name + '\'' +
                ", minScore=" + minScore +
                ", quota=" + quota +
                ", applicationCount=" + applicationCount +
                ", level='" + level + '\'' +
                '}';
    }
}