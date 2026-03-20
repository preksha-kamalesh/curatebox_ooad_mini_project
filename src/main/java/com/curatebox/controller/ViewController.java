package com.curatebox.controller;

import com.curatebox.dto.CustomerPreferenceDTO;
import com.curatebox.model.Customer;
import com.curatebox.model.CustomerPreference;
import com.curatebox.model.PreferenceOption;
import com.curatebox.model.Subscription;
import com.curatebox.repository.PreferenceOptionRepository;
import com.curatebox.service.CustomerService;
import com.curatebox.service.SubscriptionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/customers/add")
    public String addCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customers/add";
    }

    @PostMapping("/customers/add")
    public String processAddCustomer(@ModelAttribute("customer") Customer customer, 
                                     @RequestParam("planType") String planType, 
                                     RedirectAttributes redirectAttributes) {
        customerService.createCustomer(customer, planType);
        redirectAttributes.addFlashAttribute("successMessage", "New customer created successfully.");
        return "redirect:/customers";
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
        List<CustomerPreference> currentPreferences = customerService.getCustomerPreferences(id);
        Map<Long, Boolean> selectedByOptionId = new HashMap<>();
        for (CustomerPreference currentPreference : currentPreferences) {
            if (currentPreference.getPreferenceOption() != null) {
                selectedByOptionId.put(currentPreference.getPreferenceOption().getPreferenceId(), currentPreference.isLike());
            }
        }

        PreferenceForm preferenceForm = new PreferenceForm();
        List<PreferenceChoice> choices = new ArrayList<>();
        for (PreferenceOption option : options) {
            PreferenceChoice choice = new PreferenceChoice();
            choice.setPreferenceOptionId(option.getPreferenceId());
            choice.setSelection(selectedByOptionId.getOrDefault(option.getPreferenceId(), true) ? "like" : "dislike");
            choices.add(choice);
        }
        preferenceForm.setPreferences(choices);

        model.addAttribute("customer", customerService.getCustomerById(id));
        model.addAttribute("options", options);
        model.addAttribute("preferenceForm", preferenceForm);
        return "customers/preferences";
    }

    @PostMapping("/customers/{id}/preferences")
    public String updatePreferences(@PathVariable Long id, @ModelAttribute PreferenceForm preferenceForm, RedirectAttributes redirectAttributes) {
        try {
            if (preferenceForm.getPreferences() == null || preferenceForm.getPreferences().isEmpty()) {
                throw new IllegalArgumentException("No preferences were submitted.");
            }

            List<CustomerPreferenceDTO> dtos = new ArrayList<>();
            for (PreferenceChoice choice : preferenceForm.getPreferences()) {
                if (choice.getPreferenceOptionId() == null) {
                    throw new IllegalArgumentException("Invalid preference option id.");
                }
                if (choice.getSelection() == null || (!"like".equalsIgnoreCase(choice.getSelection())
                        && !"dislike".equalsIgnoreCase(choice.getSelection()))) {
                    throw new IllegalArgumentException("Invalid preference selection.");
                }

                CustomerPreferenceDTO dto = new CustomerPreferenceDTO();
                dto.setPreferenceOptionId(choice.getPreferenceOptionId());
                dto.setLike("like".equalsIgnoreCase(choice.getSelection()));
                dtos.add(dto);
            }

            customerService.updatePreferences(id, dtos);
            redirectAttributes.addFlashAttribute("successMessage", "Preferences updated successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not update preferences. Please try again.");
        }

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
        try {
            Subscription subscription = subscriptionService.pauseSubscription(id);
            redirectAttributes.addFlashAttribute("successMessage", "Subscription paused successfully.");
            return "redirect:/customers/" + subscription.getCustomer().getCustomerId() + "/subscription";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/customers/" + subscriptionService.getSubscription(id).getCustomer().getCustomerId() + "/subscription";
        }
    }

    @PostMapping("/subscriptions/{id}/resume")
    public String resumeSubscription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Subscription subscription = subscriptionService.resumeSubscription(id);
            redirectAttributes.addFlashAttribute("successMessage", "Subscription resumed successfully.");
            return "redirect:/customers/" + subscription.getCustomer().getCustomerId() + "/subscription";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/customers/" + subscriptionService.getSubscription(id).getCustomer().getCustomerId() + "/subscription";
        }
    }

    @PostMapping("/subscriptions/{id}/cancel")
    public String cancelSubscription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Subscription subscription = subscriptionService.cancelSubscription(id);
            redirectAttributes.addFlashAttribute("successMessage", "Subscription cancelled successfully.");
            return "redirect:/customers/" + subscription.getCustomer().getCustomerId() + "/subscription";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/customers/" + subscriptionService.getSubscription(id).getCustomer().getCustomerId() + "/subscription";
        }
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
