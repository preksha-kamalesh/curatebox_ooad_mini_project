package com.curatebox.config;

import com.curatebox.model.Admin;
import com.curatebox.model.Customer;
import com.curatebox.model.CustomerPreference;
import com.curatebox.model.PreferenceOption;
import com.curatebox.model.Product;
import com.curatebox.model.Subscription;
import com.curatebox.model.SubscriptionStatus;
import com.curatebox.model.Supplier;
import com.curatebox.repository.AdminRepository;
import com.curatebox.repository.CustomerPreferenceRepository;
import com.curatebox.repository.CustomerRepository;
import com.curatebox.repository.PreferenceOptionRepository;
import com.curatebox.repository.ProductRepository;
import com.curatebox.repository.SubscriptionRepository;
import com.curatebox.repository.SupplierRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PreferenceOptionRepository preferenceOptionRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final CustomerPreferenceRepository customerPreferenceRepository;
    private final AdminRepository adminRepository;

    public DataSeeder(
            PreferenceOptionRepository preferenceOptionRepository,
            SupplierRepository supplierRepository,
            ProductRepository productRepository,
            CustomerRepository customerRepository,
            SubscriptionRepository subscriptionRepository,
            CustomerPreferenceRepository customerPreferenceRepository,
            AdminRepository adminRepository) {
        this.preferenceOptionRepository = preferenceOptionRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.customerPreferenceRepository = customerPreferenceRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            return;
        }

        PreferenceOption nuts = saveOption("ALLERGEN", "nuts");
        PreferenceOption dairy = saveOption("ALLERGEN", "dairy");
        PreferenceOption spicy = saveOption("FLAVOR", "spicy");
        PreferenceOption organic = saveOption("BRAND", "organic");
        PreferenceOption sweet = saveOption("FLAVOR", "sweet");

        Supplier s1 = new Supplier();
        s1.setSupplierName("FreshFarm Supplies");
        s1.setContactEmail("contact@freshfarm.com");
        s1.setContactPhone("+1 (555) 123-4567");
        Supplier s2 = new Supplier();
        s2.setSupplierName("UrbanSnack Co");
        s2.setContactEmail("sales@urbansnack.com");
        s2.setContactPhone("+1 (555) 987-6543");
        supplierRepository.saveAll(List.of(s1, s2));

        productRepository.saveAll(List.of(
                product("Almond Mix", "Crunchy almond snack", "nuts", 12, s2),
                product("Organic Granola", "Healthy breakfast granola", "organic", 15, s1),
                product("Spicy Chips", "Chili potato crisps", "spicy", 8, s2),
                product("Fruit Bites", "Dried fruit cubes", "sweet", 18, s1),
                product("Dairy Cookies", "Milk butter cookies", "dairy", 9, s2),
                product("Organic Tea", "Herbal tea bags", "organic", 20, s1),
                product("Hot Salsa", "Extra spicy dip", "spicy", 11, s2),
                product("Sweet Crackers", "Honey glazed crackers", "sweet", 14, s1),
                product("Protein Bar", "Nut-free protein bar", "fitness", 22, s2)
        ));

        Customer c1 = customer("Ava", "Sharma", "ava@curatebox.com", "12 Palm Street");
        Customer c2 = customer("Noah", "Patel", "noah@curatebox.com", "88 Lake Road");
        Customer c3 = customer("Mia", "Reddy", "mia@curatebox.com", "22 Garden Ave");
        customerRepository.saveAll(List.of(c1, c2, c3));

        subscriptionRepository.saveAll(List.of(
                subscription(c1, "PREMIUM", SubscriptionStatus.ACTIVE),
                subscription(c2, "BASIC", SubscriptionStatus.ACTIVE),
                subscription(c3, "BASIC", SubscriptionStatus.PAUSED)
        ));

        customerPreferenceRepository.saveAll(List.of(
                pref(c1, organic, true), pref(c1, nuts, false), pref(c1, spicy, true),
                pref(c2, sweet, true), pref(c2, dairy, false), pref(c2, organic, true),
                pref(c3, spicy, false), pref(c3, nuts, false), pref(c3, sweet, true)
        ));

        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPasswordHash(BCrypt.hashpw("admin123", BCrypt.gensalt()));
        adminRepository.save(admin);
    }

    private PreferenceOption saveOption(String category, String value) {
        PreferenceOption p = new PreferenceOption();
        p.setCategory(category);
        p.setPreferenceValue(value);
        return preferenceOptionRepository.save(p);
    }

    private Product product(String name, String desc, String category, int stock, Supplier supplier) {
        Product p = new Product();
        p.setProductName(name);
        p.setDescription(desc);
        p.setCategory(category);
        p.setStockQuantity(stock);
        p.setSupplier(supplier);
        return p;
    }

    private Customer customer(String first, String last, String email, String address) {
        Customer c = new Customer();
        c.setFirstName(first);
        c.setLastName(last);
        c.setEmail(email);
        c.setShippingAddress(address);
        return c;
    }

    private Subscription subscription(Customer customer, String plan, SubscriptionStatus status) {
        Subscription s = new Subscription();
        s.setCustomer(customer);
        s.setPlanType(plan);
        s.setStatus(status);
        s.setStartDate(LocalDate.now().minusMonths(2));
        s.setEndDate(LocalDate.now().plusMonths(10));
        return s;
    }

    private CustomerPreference pref(Customer customer, PreferenceOption option, boolean isLike) {
        CustomerPreference cp = new CustomerPreference();
        cp.setCustomer(customer);
        cp.setPreferenceOption(option);
        cp.setLike(isLike);
        return cp;
    }
}
