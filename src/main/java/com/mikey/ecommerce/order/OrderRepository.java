package com.mikey.ecommerce.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

  @Query(
      """
        select distinct o from CustomerOrder o
        left join fetch o.items i
        left join fetch i.product
    """)
  List<CustomerOrder> findAllWithItems();

  @Query(
      """
        select o from CustomerOrder o
        left join fetch o.items i
        left join fetch i.product
        where o.id = :id
    """)
  Optional<CustomerOrder> findByIdWithItems(Long id);

  @Query(
      """
        select distinct o from CustomerOrder o
        left join fetch o.items i
        left join fetch i.product
        where lower(o.customerEmail) = lower(:customerEmail)
        order by o.createdAt desc
    """)
  List<CustomerOrder> findByCustomerEmailWithItems(String customerEmail);

  long countByStatus(OrderStatus status);

  @Query(
      """
       SELECT COALESCE(SUM(o.totalAmount), 0)
       FROM CustomerOrder o
       WHERE o.status = :status
       """)
  BigDecimal sumTotalAmountByStatus(OrderStatus status);
}
