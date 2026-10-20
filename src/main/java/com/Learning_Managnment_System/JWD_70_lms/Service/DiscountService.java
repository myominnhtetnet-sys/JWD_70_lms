package com.Learning_Managnment_System.JWD_70_lms.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.DiscountRespority;
import com.Learning_Managnment_System.JWD_70_lms.Repository.DiscountRuleRespority;
import com.Learning_Managnment_System.JWD_70_lms.model.DiscountRuleBean;
import com.Learning_Managnment_System.JWD_70_lms.model.Discount_Bean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountService {

    private final DiscountRespority discountRepository;
    private final DiscountRuleRespority discountRuleRepository;

    public DiscountService(DiscountRespority discountRepository, DiscountRuleRespority discountRuleRepository) {
        this.discountRepository = discountRepository;
        this.discountRuleRepository = discountRuleRepository;
    }

    // ==========================================
    // DISCOUNT METHODS
    // ==========================================

    public Discount_Bean createDiscount(Discount_Bean discount) {
        return discountRepository.saveDiscount(discount);
    }

    public Discount_Bean getDiscountById(Integer id) {
        return discountRepository.getDiscountById(id);
    }

    public List<Discount_Bean> getAllDiscounts() {
        return discountRepository.getAllDiscounts();
    }

    public Discount_Bean updateDiscount(Integer id, Discount_Bean discount) {
        return discountRepository.updateDiscount(id, discount);
    }

    public boolean deleteDiscount(Integer id) {
        return discountRepository.deleteDiscount(id);
    }

    // ==========================================
    // DISCOUNT RULE METHODS
    // ==========================================

    public List<DiscountRuleBean> getAllDiscountRules() {
        return discountRuleRepository.getAllDiscountRules();
    }

    public DiscountRuleBean createDiscountRule(DiscountRuleBean rule) {
        return discountRuleRepository.createDiscountRule(rule);
    }

    public DiscountRuleBean getDiscountRuleById(Integer id) {
        return discountRuleRepository.getDiscountRuleById(id);
    }

    public List<DiscountRuleBean> getRulesByDiscountId(Integer discountId) {
        return discountRuleRepository.getRulesByDiscountId(discountId);
    }

    public DiscountRuleBean updateDiscountRule(Integer id, DiscountRuleBean rule) {
        return discountRuleRepository.updateDiscountRule(id, rule);
    }

    public boolean deleteDiscountRule(Integer id) {
        return discountRuleRepository.deleteItemsByRuleId(id);
    }
}