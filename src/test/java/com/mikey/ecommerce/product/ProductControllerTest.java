package com.mikey.ecommerce.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.inventory.Inventory;
import com.mikey.ecommerce.inventory.InventoryRepository;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

  @Mock private ProductRepository productRepository;

  @Mock private InventoryRepository inventoryRepository;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();

    mockMvc =
        MockMvcBuilders.standaloneSetup(
                new ProductController(productRepository, inventoryRepository))
            .setValidator(validator)
            .build();

    objectMapper = new ObjectMapper();
  }

  @Test
  void findAll_shouldReturnPagedProducts() throws Exception {
    Product product =
        productWithId(1L, "Mechanical Keyboard", "RGB keyboard", new BigDecimal("1299.99"));

    when(productRepository.searchProducts(
            eq("keyboard"),
            eq(BigDecimal.ZERO),
            eq(new BigDecimal("999999999")),
            eq(""),
            eq(""),
            eq(""),
            any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(product)));

    mockMvc
        .perform(get("/api/products").param("search", "keyboard"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1))
        .andExpect(jsonPath("$.content[0].name").value("Mechanical Keyboard"))
        .andExpect(jsonPath("$.content[0].category").value("Peripherals"))
        .andExpect(jsonPath("$.content[0].subcategory").value("Keyboards"))
        .andExpect(jsonPath("$.content[0].brand").value("Logitech"))
        .andExpect(jsonPath("$.content[0].price").value(1299.99))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(1))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  void findById_shouldReturnProduct() throws Exception {
    Product product =
        productWithId(1L, "Mechanical Keyboard", "RGB keyboard", new BigDecimal("1299.99"));

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(inventoryRepository.findByProductId(1L))
        .thenReturn(Optional.of(new Inventory(product, 12)));

    mockMvc
        .perform(get("/api/products/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Mechanical Keyboard"))
        .andExpect(jsonPath("$.description").value("RGB keyboard"))
        .andExpect(jsonPath("$.category").value("Peripherals"))
        .andExpect(jsonPath("$.subcategory").value("Keyboards"))
        .andExpect(jsonPath("$.brand").value("Logitech"))
        .andExpect(jsonPath("$.price").value(1299.99))
        .andExpect(jsonPath("$.availableQuantity").value(12))
        .andExpect(jsonPath("$.stockStatus").value("IN_STOCK"));
  }

  @Test
  void create_shouldSaveProductAndInventory() throws Exception {
    ProductRequest request =
        new ProductRequest(
            "Mechanical Keyboard",
            "RGB keyboard",
            "Peripherals",
            "Keyboards",
            "Logitech",
            "https://example.com/keyboard.jpg",
            true,
            new BigDecimal("1299.99"),
            10);

    when(productRepository.save(any(Product.class)))
        .thenAnswer(
            invocation -> {
              Product product = invocation.getArgument(0);
              setField(product, "id", 1L);
              return product;
            });

    mockMvc
        .perform(
            post("/api/products")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Mechanical Keyboard"))
        .andExpect(jsonPath("$.category").value("Peripherals"))
        .andExpect(jsonPath("$.subcategory").value("Keyboards"))
        .andExpect(jsonPath("$.brand").value("Logitech"))
        .andExpect(jsonPath("$.price").value(1299.99))
        .andExpect(jsonPath("$.availableQuantity").value(10));

    ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);

    verify(inventoryRepository).save(inventoryCaptor.capture());

    assertThat(inventoryCaptor.getValue().getProduct().getName()).isEqualTo("Mechanical Keyboard");
    assertThat(inventoryCaptor.getValue().getQuantityAvailable()).isEqualTo(10);
  }

  @Test
  void create_shouldReturnBadRequestWhenNameIsBlank() throws Exception {
    ProductRequest request =
        new ProductRequest(
            "",
            "RGB keyboard",
            "Peripherals",
            "Keyboards",
            "Logitech",
            "https://example.com/keyboard.jpg",
            true,
            new BigDecimal("1299.99"),
            10);

    mockMvc
        .perform(
            post("/api/products")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    verify(productRepository, never()).save(any());
    verify(inventoryRepository, never()).save(any());
  }

  @Test
  void update_shouldUpdateExistingProduct() throws Exception {
    Product product =
        productWithId(1L, "Old Keyboard", "Old description", new BigDecimal("999.99"));

    ProductRequest request =
        new ProductRequest(
            "Mechanical Keyboard",
            "RGB keyboard",
            "Peripherals",
            "Keyboards",
            "Logitech",
            "https://example.com/keyboard.jpg",
            true,
            new BigDecimal("1299.99"),
            null);

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(productRepository.save(product)).thenReturn(product);
    when(inventoryRepository.findByProductId(1L))
        .thenReturn(Optional.of(new Inventory(product, 15)));

    mockMvc
        .perform(
            put("/api/products/1")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Mechanical Keyboard"))
        .andExpect(jsonPath("$.description").value("RGB keyboard"))
        .andExpect(jsonPath("$.category").value("Peripherals"))
        .andExpect(jsonPath("$.subcategory").value("Keyboards"))
        .andExpect(jsonPath("$.brand").value("Logitech"))
        .andExpect(jsonPath("$.price").value(1299.99))
        .andExpect(jsonPath("$.availableQuantity").value(15));
  }

  @Test
  void delete_shouldMarkProductInactiveWhenItExists() throws Exception {
    Product product =
        productWithId(1L, "Mechanical Keyboard", "RGB keyboard", new BigDecimal("1299.99"));

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(productRepository.save(product)).thenReturn(product);
    when(inventoryRepository.findByProductId(1L))
        .thenReturn(Optional.of(new Inventory(product, 10)));

    mockMvc
        .perform(delete("/api/products/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.active").value(false))
        .andExpect(jsonPath("$.stockStatus").value("INACTIVE"));

    verify(productRepository).save(product);
    assertThat(product.isActive()).isFalse();
  }

  @Test
  void delete_shouldThrowExceptionWhenProductDoesNotExist() {
    when(productRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> mockMvc.perform(delete("/api/products/99")))
        .hasRootCauseInstanceOf(ApiException.class)
        .hasMessageContaining("Product not found");

    verify(productRepository, never()).save(any());
  }

  private Product productWithId(Long id, String name, String description, BigDecimal price) {
    Product product =
        new Product(
            name,
            description,
            "Peripherals",
            "Keyboards",
            "Logitech",
            "https://example.com/product.jpg",
            true,
            price);

    setField(product, "id", id);
    return product;
  }

  private void setField(Object target, String fieldName, Object value) {
    try {
      Field field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      throw new IllegalStateException("Could not set " + fieldName + " for test", ex);
    }
  }
}
