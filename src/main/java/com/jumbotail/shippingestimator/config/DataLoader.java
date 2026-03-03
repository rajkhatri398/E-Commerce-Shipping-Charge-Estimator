package com.jumbotail.shippingestimator.config;

import com.jumbotail.shippingestimator.model.Customer;
import com.jumbotail.shippingestimator.model.Product;
import com.jumbotail.shippingestimator.model.Seller;
import com.jumbotail.shippingestimator.model.Warehouse;
import com.jumbotail.shippingestimator.repository.CustomerRepository;
import com.jumbotail.shippingestimator.repository.ProductRepository;
import com.jumbotail.shippingestimator.repository.SellerRepository;
import com.jumbotail.shippingestimator.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Populates the H2 database with sample data on application startup.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public void run(String... args) {
        log.info("=== Loading sample data ===");
        loadWarehouses();
        loadSellers();
        loadProducts();
        loadCustomers();
        log.info("=== Sample data loaded successfully ===");
    }

    private void loadWarehouses() {
        warehouseRepository.save(Warehouse.builder()
                .name("BLR_Warehouse")
                .latitude(12.9716)
                .longitude(77.5946)
                .city("Bangalore")
                .state("Karnataka")
                .address("Electronic City, Bangalore")
                .capacityCbm(5000.0)
                .active(true)
                .build());

        warehouseRepository.save(Warehouse.builder()
                .name("MUMB_Warehouse")
                .latitude(19.0760)
                .longitude(72.8777)
                .city("Mumbai")
                .state("Maharashtra")
                .address("Andheri East, Mumbai")
                .capacityCbm(8000.0)
                .active(true)
                .build());

        warehouseRepository.save(Warehouse.builder()
                .name("DEL_Warehouse")
                .latitude(28.7041)
                .longitude(77.1025)
                .city("Delhi")
                .state("Delhi")
                .address("Okhla Industrial Area, Delhi")
                .capacityCbm(6000.0)
                .active(true)
                .build());

        warehouseRepository.save(Warehouse.builder()
                .name("HYD_Warehouse")
                .latitude(17.3850)
                .longitude(78.4867)
                .city("Hyderabad")
                .state("Telangana")
                .address("HITEC City, Hyderabad")
                .capacityCbm(4500.0)
                .active(true)
                .build());

        warehouseRepository.save(Warehouse.builder()
                .name("CHN_Warehouse")
                .latitude(13.0827)
                .longitude(80.2707)
                .city("Chennai")
                .state("Tamil Nadu")
                .address("Guindy, Chennai")
                .capacityCbm(3500.0)
                .active(true)
                .build());

        log.info("Loaded {} warehouses", warehouseRepository.count());
    }

    private void loadSellers() {
        sellerRepository.save(Seller.builder()
                .name("Nestle India Seller")
                .latitude(12.9352)
                .longitude(77.6245)
                .city("Bangalore")
                .state("Karnataka")
                .address("Koramangala, Bangalore")
                .phone("9876543210")
                .email("nestle.seller@example.com")
                .build());

        sellerRepository.save(Seller.builder()
                .name("Rice Wholesale Seller")
                .latitude(19.1136)
                .longitude(72.8697)
                .city("Mumbai")
                .state("Maharashtra")
                .address("Vashi, Navi Mumbai")
                .phone("9876543211")
                .email("rice.seller@example.com")
                .build());

        sellerRepository.save(Seller.builder()
                .name("Sugar Trading Seller")
                .latitude(28.6139)
                .longitude(77.2090)
                .city("Delhi")
                .state("Delhi")
                .address("Chandni Chowk, Delhi")
                .phone("9876543212")
                .email("sugar.seller@example.com")
                .build());

        sellerRepository.save(Seller.builder()
                .name("Spice World Seller")
                .latitude(17.4399)
                .longitude(78.4983)
                .city("Hyderabad")
                .state("Telangana")
                .address("Secunderabad, Hyderabad")
                .phone("9876543213")
                .email("spice.seller@example.com")
                .build());

        log.info("Loaded {} sellers", sellerRepository.count());
    }

    private void loadProducts() {
        // Nestle Seller (sellerId=1)
        productRepository.save(Product.builder()
                .name("Maggie 500g Packet")
                .sellerId(1L)
                .price(10.0)
                .weightKg(0.5)
                .lengthCm(10.0)
                .widthCm(10.0)
                .heightCm(10.0)
                .category("Instant Noodles")
                .description("Nestle Maggie instant noodles 500g pack")
                .build());

        productRepository.save(Product.builder()
                .name("Nescafe Coffee 200g")
                .sellerId(1L)
                .price(250.0)
                .weightKg(0.25)
                .lengthCm(8.0)
                .widthCm(8.0)
                .heightCm(15.0)
                .category("Beverages")
                .description("Nescafe Classic instant coffee 200g jar")
                .build());

        // Rice Seller (sellerId=2)
        productRepository.save(Product.builder()
                .name("Rice Bag 10Kg")
                .sellerId(2L)
                .price(500.0)
                .weightKg(10.0)
                .lengthCm(100.0)
                .widthCm(80.0)
                .heightCm(50.0)
                .category("Grains")
                .description("Premium Basmati rice 10kg bag")
                .build());

        productRepository.save(Product.builder()
                .name("Rice Bag 25Kg")
                .sellerId(2L)
                .price(1200.0)
                .weightKg(25.0)
                .lengthCm(120.0)
                .widthCm(90.0)
                .heightCm(60.0)
                .category("Grains")
                .description("Premium Basmati rice 25kg bag")
                .build());

        // Sugar Seller (sellerId=3)
        productRepository.save(Product.builder()
                .name("Sugar Bag 25Kg")
                .sellerId(3L)
                .price(700.0)
                .weightKg(25.0)
                .lengthCm(100.0)
                .widthCm(90.0)
                .heightCm(60.0)
                .category("Sugar")
                .description("Refined white sugar 25kg bag")
                .build());

        // Spice Seller (sellerId=4)
        productRepository.save(Product.builder()
                .name("Turmeric Powder 1Kg")
                .sellerId(4L)
                .price(180.0)
                .weightKg(1.0)
                .lengthCm(15.0)
                .widthCm(10.0)
                .heightCm(20.0)
                .category("Spices")
                .description("Pure turmeric powder 1kg pack")
                .build());

        productRepository.save(Product.builder()
                .name("Red Chilli Powder 5Kg")
                .sellerId(4L)
                .price(850.0)
                .weightKg(5.0)
                .lengthCm(40.0)
                .widthCm(25.0)
                .heightCm(30.0)
                .category("Spices")
                .description("Kashmiri red chilli powder 5kg pack")
                .build());

        log.info("Loaded {} products", productRepository.count());
    }

    private void loadCustomers() {
        customerRepository.save(Customer.builder()
                .name("Shree Kirana Store")
                .phone("9847123456")
                .latitude(11.2320)
                .longitude(23.4455)
                .address("MG Road, Belgaum")
                .city("Belgaum")
                .state("Karnataka")
                .pincode("590001")
                .build());

        customerRepository.save(Customer.builder()
                .name("Andheri Mini Mart")
                .phone("9101234567")
                .latitude(19.1197)
                .longitude(72.8464)
                .address("Andheri West, Mumbai")
                .city("Mumbai")
                .state("Maharashtra")
                .pincode("400058")
                .build());

        customerRepository.save(Customer.builder()
                .name("Rajesh General Store")
                .phone("9998765432")
                .latitude(28.6353)
                .longitude(77.2250)
                .address("Karol Bagh, Delhi")
                .city("Delhi")
                .state("Delhi")
                .pincode("110005")
                .build());

        customerRepository.save(Customer.builder()
                .name("Lakshmi Provision Store")
                .phone("9876001234")
                .latitude(13.0500)
                .longitude(80.2121)
                .address("T. Nagar, Chennai")
                .city("Chennai")
                .state("Tamil Nadu")
                .pincode("600017")
                .build());

        customerRepository.save(Customer.builder()
                .name("Ganesh Traders")
                .phone("9765432100")
                .latitude(17.4500)
                .longitude(78.3800)
                .address("Ameerpet, Hyderabad")
                .city("Hyderabad")
                .state("Telangana")
                .pincode("500016")
                .build());

        customerRepository.save(Customer.builder()
                .name("Punjab Kirana Corner")
                .phone("9812345678")
                .latitude(30.7333)
                .longitude(76.7794)
                .address("Sector 22, Chandigarh")
                .city("Chandigarh")
                .state("Punjab")
                .pincode("160022")
                .build());

        log.info("Loaded {} customers", customerRepository.count());
    }
}
