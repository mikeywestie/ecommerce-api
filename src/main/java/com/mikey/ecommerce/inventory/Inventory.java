package com.mikey.ecommerce.inventory;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.product.Product;
import jakarta.persistence.*;
import org.springframework.http.HttpStatus;

@Entity
@Table(name = "inventory")
public class Inventory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version private Long version;

  @OneToOne(optional = false)
  @JoinColumn(name = "product_id", nullable = false, unique = true)
  private Product product;

  @Column(nullable = false)
  private int quantityAvailable;

  protected Inventory() {}

  public Inventory(Product product, int quantityAvailable) {
    if (quantityAvailable < 0) {
      throw new ApiException("Quantity cannot be negative", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    this.product = product;
    this.quantityAvailable = quantityAvailable;
  }

  public Long getId() {
    return id;
  }

  public Long getVersion() {
    return version;
  }

  public Product getProduct() {
    return product;
  }

  public int getQuantityAvailable() {
    return quantityAvailable;
  }

  public void addStock(int quantity) {
    if (quantity < 0) {
      throw new ApiException("Quantity must be positive", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    this.quantityAvailable += quantity;
  }

  public void setQuantity(int quantity) {
    if (quantity < 0) {
      throw new ApiException("Quantity cannot be negative", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    this.quantityAvailable = quantity;
  }

  public void reserve(int quantity) {
    if (quantity <= 0) {
      throw new ApiException("Quantity must be greater than zero", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    if (quantityAvailable < quantity) {
      throw new ApiException(
          "Not enough stock for product: " + product.getName(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    quantityAvailable -= quantity;
  }
}
