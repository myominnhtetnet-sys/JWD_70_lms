package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Discount_Bean {

    private Integer discountId;

    @NotBlank(message = "Discount name cannot be empty")
    private String name;
    private Integer usageLimitPerUser = 1;
    private String description;
    private String code;
    private String typeCode;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private Boolean isActive;
    private Integer usageLimit;
    private Boolean canCombineScholarship = false;
    private Integer usageCount;
    
    private Integer createdBy;

    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<DiscountRuleBean> rules;
}