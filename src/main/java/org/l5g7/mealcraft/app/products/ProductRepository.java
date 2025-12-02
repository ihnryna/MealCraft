package org.l5g7.mealcraft.app.products;

import org.l5g7.mealcraft.app.units.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    long countByCreatedAtBetween(Date from, Date to);
    List<Product> findByNameStartingWithIgnoreCase(String prefix);
    List<Product> findAllByOwnerUserIsNull();
    List<Product> findAllByOwnerUserIsNullOrOwnerUser_Id(Long id);
    List<Product> findAllByOwnerUserIsNullAndNameStartingWithIgnoreCase(String prefix);
    List<Product> findAllByNameStartingWithIgnoreCaseAndOwnerUserIsNullOrOwnerUser_Id(String prefix, Long ownerId);
    boolean existsByDefaultUnit(Unit defaultUnit);
}
