package com.Learning_Managnment_System.JWD_70_lms.Controller;

import com.Learning_Managnment_System.JWD_70_lms.Service.DiscountService;
import com.Learning_Managnment_System.JWD_70_lms.model.DiscountRuleBean;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/discount-rules")
public class DiscountRuleController {

    private final DiscountService discountService;

    public DiscountRuleController(DiscountService discountService) {
        this.discountService = discountService;
    }

    // Get all discount rules
    @GetMapping
    public ResponseEntity<List<DiscountRuleBean>> getAllDiscountRules() {
        List<DiscountRuleBean> rules = discountService.getAllDiscountRules();
        return ResponseEntity.ok(rules);
    }

    // Create a discount rule
    @PostMapping
    public ResponseEntity<?> createDiscountRule(@RequestBody DiscountRuleBean rule) {
        try {
            if (rule == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Rule data is required.")
                );
            }

            Integer discountId = rule.getDiscountId();
            if (discountId == null || discountId <= 0) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Invalid discountId. Please select a discount.")
                );
            }

            DiscountRuleBean created = discountService.createDiscountRule(rule);
            if (created == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        Map.of("message", "Failed to create discount rule.")
                );
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Failed to create rule. Check foreign key references.")
            );
        } catch (Exception e) {
            System.err.println("Failed to create discount rule:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "An unexpected error occurred.")
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscountRuleBean> getRuleById(@PathVariable Integer id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        DiscountRuleBean rule = discountService.getDiscountRuleById(id);
        if (rule == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(rule);
    }

    // Get rules belonging to a specific discount
    @GetMapping("/discount/{discountId}")
    public ResponseEntity<?> getRulesByDiscountId(@PathVariable Integer discountId) {
        if (discountId == null || discountId <= 0) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Invalid discount ID.")
            );
        }

        List<DiscountRuleBean> rules = discountService.getRulesByDiscountId(discountId);
        return ResponseEntity.ok(rules);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiscountRule(
            @PathVariable Integer id,
            @RequestBody DiscountRuleBean rule) {

        try {
            if (id == null || id <= 0 || rule == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Invalid rule ID or request data.")
                );
            }

            Integer discountId = rule.getDiscountId();
            if (discountId == null || discountId <= 0) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Invalid discountId. Please select a discount.")
                );
            }

            rule.setDiscountRuleId(id);
            DiscountRuleBean updated = discountService.updateDiscountRule(id, rule);

            if (updated == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("message", "Discount rule with ID " + id + " not found for update.")
                );
            }

            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Failed to update rule due to database constraints.")
            );
        } catch (Exception e) {
            System.err.println("Failed to update discount rule:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "Failed to update discount rule.")
            );
        }
    }

    // Delete a discount rule (Improved with JSON error response instead of blank 404)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiscountRule(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Invalid rule ID.")
                );
            }

            boolean deleted = discountService.deleteDiscountRule(id);

            if (deleted) {
                return ResponseEntity.noContent().build(); // 204 No Content on success
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("message", "Discount rule with ID " + id + " was not found in the database.")
            );

        } catch (Exception e) {
            System.err.println("Failed to delete discount rule ID " + id + ":");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "Server error while deleting discount rule: " + e.getMessage())
            );
        }
    }
}