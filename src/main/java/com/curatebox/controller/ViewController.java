package com.curatebox.controller;

import com.curatebox.dto.CustomerPreferenceDTO;
import com.curatebox.model.Customer;
import com.curatebox.model.PreferenceOption;
import com.curatebox.model.Subscription;
import com.curatebox.repository.PreferenceOptionRepository;
import com.curatebox.service.CustomerService;
import com.curatebox.service.SubscriptionService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class ViewController {

    private final CustomerService customerService;
    private final SubscriptionService subscriptionService;
    private final PreferenceOptionRepository preferenceOptionRepository;

    public ViewController(
            CustomerService customerService,
            SubscriptionService subscriptionService,
            PreferenceOptionRepository preferenceOptionRepository) {
        this.customerService = customerService;
        this.subscriptionService = subscriptionService;
        this.preferenceOptionRepository = preferenceOptionRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/customers")
    public String customerList(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        return "customers/list";
    }

    @GetMapping("/customers/{id}/edit")
    public String editCustomer(@PathVariable Long id, Model model) {
        model.addAttribute("customer", customerService.getCustomerById(id));
        return "customers/edit";
    }

    @PostMapping("/customers/{id}/edit")
    public String updateCustomer(@PathVariable Long id, @ModelAttribute("customer") Customer customer, RedirectAttributes redirectAttributes) {
        customerService.updateProfile(id, customer.getFirstName(), customer.getLastName(), customer.getEmail(), customer.getShippingAddress());
        redirectAttributes.addFlashAttribute("successMessage", "Customer profile updated successfully.");
        return "redirect:/customers";
    }

    @GetMapping("/customers/{id}/preferences")
    public String customerPreferences(@PathVariable Long id, Model model) {
        List<PreferenceOption> options = preferenceOptionRepository.findAll();
        model.addAttribute("customer", customerService.getCustomerById(id));
        model.addAttribute("options", options);
        return "customers/preferences";
    }

    @PostMapping("/customers/{id}/preferences")
    public String updatePreferences(@PathVariable Long id, @ModelAttribute PreferenceForm preferenceForm, RedirectAttributes redirectAttributes) {
        List<CustomerPreferenceDTO> dtos = new ArrayList<>();
        for (PreferenceChoice choice : preferenceForm.getPreferences()) {
            CustomerPreferenceDTO dto = new CustomerPreferenceDTO();
            dto.setPreferenceOptionId(choice.getPreferenceOptionId());
            dto.setLike("like".equalsIgnoreCase(choice.getSelection()));
            dtos.add(dto);
        }
        customerService.updatePreferences(id, dtos);
        redirectAttributes.addFlashAttribute("successMessage", "Preferences updated successfully.");
        return "redirect:/customers/" + id + "/preferences";
    }

    @GetMapping("/customers/{id}/subscription")
    public String subscriptionStatus(@PathVariable Long id, Model model) {
        Subscription subscription = subscriptionService.getSubscriptionByCustomer(id);
        model.addAttribute("subscription", subscription);
        return "subscriptions/status";
    }

    @PostMapping("/subscriptions/{id}/pause")
    public String pauseSubscription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Subscription subscription = subscriptionService.pauseSubscription(id);
        redirectAttributes.addFlashAttribute("successMessage", "Subscription paused successfully.");
        return "redirect:/customers/" + subscription.getCustomer().getCustomerId() + "/subscription";
    }

    @PostMapping("/subscriptions/{id}/resume")
    public String resumeSubscription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Subscription subscription = subscriptionService.resumeSubscription(id);
        redirectAttributes.addFlashAttribute("successMessage", "Subscription resumed successfully.");
        return "redirect:/customers/" + subscription.getCustomer().getCustomerId() + "/subscription";
    }

    @PostMapping("/subscriptions/{id}/cancel")
    public String cancelSubscription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Subscription subscription = subscriptionService.cancelSubscription(id);
        redirectAttributes.addFlashAttribute("successMessage", "Subscription cancelled successfully.");
        return "redirect:/customers/" + subscription.getCustomer().getCustomerId() + "/subscription";
    }

    public static class PreferenceForm {
        private List<PreferenceChoice> preferences = new ArrayList<>();

        public List<PreferenceChoice> getPreferences() {
            return preferences;
        }

        public void setPreferences(List<PreferenceChoice> preferences) {
            this.preferences = preferences;
        }
    }

    public static class PreferenceChoice {
        private Long preferenceOptionId;
        private String selection;

        public Long getPreferenceOptionId() {
            return preferenceOptionId;
        }

        public void setPreferenceOptionId(Long preferenceOptionId) {
            this.preferenceOptionId = preferenceOptionId;
        }

        public String getSelection() {
            return selection;
        }

        public void setSelection(String selection) {
            this.selection = selection;
        }
    }
}
