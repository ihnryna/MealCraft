package org.l5g7.mealcraft.app.products;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    long countByCreatedAtBetween(Date from, Date to);
    List<Product> findByNameStartingWithIgnoreCase(String prefix);
}
