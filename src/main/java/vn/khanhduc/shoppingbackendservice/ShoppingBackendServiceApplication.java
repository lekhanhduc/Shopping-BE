package vn.khanhduc.shoppingbackendservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ShoppingBackendServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingBackendServiceApplication.class, args);
    }

}
