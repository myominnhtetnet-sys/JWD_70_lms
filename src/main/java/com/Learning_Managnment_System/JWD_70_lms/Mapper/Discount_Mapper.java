package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.Learning_Managnment_System.JWD_70_lms.model.Discount_Bean;

@Mapper
public interface Discount_Mapper {

    List<Discount_Bean> selectAllDiscounts();

    Discount_Bean selectDiscountById(
            @Param("discountId") Integer discountId
    );

    Discount_Bean selectDiscountWithRulesByCode(
            @Param("code") String code
    );

    int insertDiscount(
            Discount_Bean discount
    );

    int updateDiscount(
            Discount_Bean discount
    );

    int deleteDiscount(
            @Param("discountId") Integer discountId
    );
}