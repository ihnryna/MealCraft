package org.l5g7.mealcraft.app.products;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    boolean existsByNameIgnoreCase(String name);
    long countByCreatedAtBetween(Date from, Date to);
}
