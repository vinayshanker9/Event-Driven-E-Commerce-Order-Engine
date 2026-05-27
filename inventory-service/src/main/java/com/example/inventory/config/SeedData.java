package com.example.inventory.config;

import com.example.inventory.domain.StockItem;
import com.example.inventory.domain.StockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedData {
    @Bean
    CommandLineRunner seedInventory(StockRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new StockItem("SKU-RED-SHIRT", 25));
                repository.save(new StockItem("SKU-BLUE-JEANS", 15));
                repository.save(new StockItem("SKU-SNEAKERS", 10));
            }
        };
    }
}
