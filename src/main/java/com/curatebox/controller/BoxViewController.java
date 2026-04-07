package com.curatebox.controller;

import com.curatebox.model.MonthlyBox;
import com.curatebox.service.BoxService;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/boxes")
public class BoxViewController {

    private final BoxService boxService;

    public BoxViewController(BoxService boxService) {
        this.boxService = boxService;
    }

    @GetMapping("/dashboard")
    public String boxDashboard(
            @RequestParam(required = false) Long customerId,
            Model model) {
        model.addAttribute("customerId", customerId);
        model.addAttribute("customerBoxes", customerId == null ? Collections.emptyList() : loadCustomerBoxes(customerId));
        return "boxes/dashboard";
    }

    @PostMapping("/dashboard/generate")
    public String generateMonthlyBoxes(@RequestParam String date, RedirectAttributes redirectAttributes) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            boxService.generateMonthlyBoxes(parsedDate);
            redirectAttributes.addFlashAttribute("successMessage", "Monthly boxes generated for " + parsedDate);
        } catch (DateTimeParseException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Date must be in yyyy-MM-dd format.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/boxes/dashboard";
    }

    @PostMapping("/dashboard/search")
    public String searchCustomerBoxes(@RequestParam Long customerId, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("customerId", customerId);
        return "redirect:/boxes/dashboard";
    }

    @PostMapping("/dashboard/status")
    public String updateShippingStatus(
            @RequestParam Long boxId,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        try {
            boxService.updateShippingStatus(boxId, status);
            redirectAttributes.addFlashAttribute("successMessage", "Shipping status updated successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/boxes/dashboard";
    }

    @PostMapping("/dashboard/ship")
    public String shipBox(@RequestParam Long boxId, RedirectAttributes redirectAttributes) {
        try {
            boxService.shipBox(boxId);
            redirectAttributes.addFlashAttribute("successMessage", "Box marked as shipped.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/boxes/dashboard";
    }

    private List<MonthlyBox> loadCustomerBoxes(Long customerId) {
        try {
            return boxService.getBoxesByCustomer(customerId);
        } catch (IllegalArgumentException ex) {
            return Collections.emptyList();
        }
    }
}
