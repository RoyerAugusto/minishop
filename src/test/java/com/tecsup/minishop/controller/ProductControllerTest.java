package com.tecsup.minishop.controller;

import com.tecsup.minishop.dto.ProductRequest;
import com.tecsup.minishop.model.Product;
import com.tecsup.minishop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Test
    void create_debeGuardarProductoSinIdEnviadoPorCliente() {
        ProductService productService = mock(ProductService.class);

        when(productService.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductController controller = new ProductController(productService);

        ProductRequest request = new ProductRequest(
                "Laptop",
                2500.0,
                10);

        ResponseEntity<Product> response = controller.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getId());
        assertEquals("Laptop", response.getBody().getName());
        assertEquals(2500.0, response.getBody().getPrice());
        assertEquals(10, response.getBody().getStock());

        verify(productService, times(1)).save(any(Product.class));
    }
}
