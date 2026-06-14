package com.mikey.ecommerce.inventory;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
  Optional<Inventory> findByProductId(Long productId);

  @Query(
      """
       SELECT COUNT(i)
       FROM Inventory i
       WHERE i.quantityAvailable <= :threshold
       """)
  long countLowStock(int threshold);
}
