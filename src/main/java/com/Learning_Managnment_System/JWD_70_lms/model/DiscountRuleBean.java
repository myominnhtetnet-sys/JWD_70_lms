package com.Learning_Managnment_System.JWD_70_lms.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DiscountRuleBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer discountRuleId;
    private Integer discountId;
    private String ruleTypeCode;
    private Integer courseId;
    private Integer categoryId;
    private Integer batchId;
    private Integer userId;
    private BigDecimal minAmount;
    private Integer daysBeforeStart;
    private LocalDateTime createdAt;

    // Default Constructor
    public DiscountRuleBean() {}

    // Parameterized Constructor
    public DiscountRuleBean(Integer discountRuleId, Integer discountId, String ruleTypeCode,
                            Integer courseId, Integer categoryId, Integer batchId, 
                            Integer userId, BigDecimal minAmount, Integer daysBeforeStart, 
                            LocalDateTime createdAt) {
        this.discountRuleId = discountRuleId;
        this.discountId = discountId;
        this.ruleTypeCode = ruleTypeCode;
        this.courseId = courseId;
        this.categoryId = categoryId;
        this.batchId = batchId;
        this.userId = userId;
        this.minAmount = minAmount;
        this.daysBeforeStart = daysBeforeStart;
        this.createdAt = createdAt;
    }

  

    public Integer getDiscountRuleId() {
        return discountRuleId;
    }

    public void setDiscountRuleId(Integer discountRuleId) {
        this.discountRuleId = discountRuleId;
    }

    public Integer getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Integer discountId) {
        this.discountId = discountId;
    }

    public String getRuleTypeCode() {
        return ruleTypeCode;
    }

    public void setRuleTypeCode(String ruleTypeCode) {
        this.ruleTypeCode = ruleTypeCode;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public Integer getDaysBeforeStart() {
        return daysBeforeStart;
    }

    public void setDaysBeforeStart(Integer daysBeforeStart) {
        this.daysBeforeStart = daysBeforeStart;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "DiscountRuleBean{" +
                "discountRuleId=" + discountRuleId +
                ", discountId=" + discountId +
                ", ruleTypeCode='" + ruleTypeCode + '\'' +
                ", courseId=" + courseId +
                ", categoryId=" + categoryId +
                ", batchId=" + batchId +
                ", userId=" + userId +
                ", minAmount=" + minAmount +
                ", daysBeforeStart=" + daysBeforeStart +
                ", createdAt=" + createdAt +
                '}';
    }
}