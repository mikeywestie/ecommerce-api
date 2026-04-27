package com.mikey.ecommerce.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    @Query("""
        select distinct o from CustomerOrder o
        left join fetch o.items i
        left join fetch i.product
    """)
    List<CustomerOrder> findAllWithItems();

    @Query("""
        select o from CustomerOrder o
        left join fetch o.items i
        left join fetch i.product
        where o.id = :id
    """)
    Optional<CustomerOrder> findByIdWithItems(Long id);
}