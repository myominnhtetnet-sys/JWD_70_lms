package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.Learning_Managnment_System.JWD_70_lms.Service.DiscountService;
import com.Learning_Managnment_System.JWD_70_lms.model.Discount_Bean;

import java.util.List;
import java.util.Map;

@Controller // Changed to @Controller so it can return HTML views as well as JSON
@RequestMapping("/api/v1/discounts")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    // --- Thymeleaf Web View Endpoint ---
    // Access this via browser at: /api/v1/discounts/view
    @GetMapping("/view")
    public String getDiscountsPageView(Model model) {
        try {
            List<Discount_Bean> discounts = discountService.getAllDiscounts();
            model.addAttribute("discounts", discounts);
        } catch (Exception e) {
            System.err.println("Failed to fetch discounts for view:");
            e.printStackTrace();
            model.addAttribute("errorMessage", "Could not load discounts.");
        }
        // This explicitly returns "discounts-list" to resolve your Thymeleaf error
        // Make sure discounts-list.html exists in src/main/resources/templates/
        return "discount-list";
    }

    // --- REST API Endpoints (Annotated with @ResponseBody to return JSON) ---

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createDiscount(@Validated @RequestBody Discount_Bean discount) {
        try {
            if (discount == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Discount data is required.")
                );
            }

            Discount_Bean created = discountService.createDiscount(discount);
            
            if (created == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        Map.of("message", "Failed to create discount.")
                );
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Failed to create discount. Database constraint violation (e.g., duplicate entry).")
            );
        } catch (Exception e) {
            System.err.println("Failed to create discount:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "An unexpected error occurred while creating the discount.")
            );
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getDiscountById(@PathVariable Integer id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Invalid discount ID.")
            );
        }

        try {
            Discount_Bean discount = discountService.getDiscountById(id);
            if (discount == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(discount);
        } catch (Exception e) {
            System.err.println("Failed to fetch discount by ID:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "An unexpected error occurred while fetching the discount.")
            );
        }
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<?> getAllDiscounts() {
        try {
            List<Discount_Bean> discounts = discountService.getAllDiscounts();
            return ResponseEntity.ok(discounts);
        } catch (Exception e) {
            System.err.println("Failed to fetch all discounts:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "An unexpected error occurred while fetching discounts.")
            );
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateDiscount(@PathVariable Integer id, @Validated @RequestBody Discount_Bean discount) {
        try {
            if (id == null || id <= 0 || discount == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Invalid discount ID or request data.")
                );
            }

            discount.setDiscountId(id);
            Discount_Bean updated = discountService.updateDiscount(id, discount);
            
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Failed to update discount. Check constraints or unique fields.")
            );
        } catch (Exception e) {
            System.err.println("Failed to update discount:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "An unexpected error occurred while updating the discount.")
            );
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteDiscount(@PathVariable Integer id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Invalid discount ID.")
            );
        }

        try {
            boolean deleted = discountService.deleteDiscount(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("message", "Cannot delete discount because it is currently referenced by other records (e.g., discount rules).")
            );
        } catch (Exception e) {
            System.err.println("Failed to delete discount:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "An unexpected error occurred while deleting the discount.")
            );
        }
    }
}