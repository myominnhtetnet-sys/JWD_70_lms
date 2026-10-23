package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Data;
import java.sql.Timestamp;

public class SettingsBean {

    @Data
    public static class PaymentMethodBean {
        private int paymentMethodId;
        private String name;
        private String description;
        private int isActive;
        private Timestamp createdAt;
    }

    @Data
    public static class DiscountTypeBean {
        private String typeCode;
        private String name;
        private String description;
    }

    @Data
    public static class DiscountRuleTypeBean {
        private String ruleTypeCode;
        private String name;
        private String appliesTo;
        private String description;
    }
}