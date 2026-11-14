package com.tecsup.minishop.controller;

import com.tecsup.minishop.model.Product;
import com.tecsup.minishop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIT {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ProductRepository productRepository;

        @BeforeEach
        void setUp() {
                productRepository.deleteAll();
        }

        @Test
        @DisplayName("POST debe registrar un producto y devolver 201")
        void shouldCreateProductAndReturn201() throws Exception {
                String json = """
                                {
                                    "name": "Webcam Logitech",
                                    "price": 250.00,
                                    "stock": 20
                                }
                                """;

                mockMvc.perform(post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").isNotEmpty())
                                .andExpect(jsonPath("$.name").value("Webcam Logitech"))
                                .andExpect(jsonPath("$.price").value(250.00))
                                .andExpect(jsonPath("$.stock").value(20));
        }

        @Test
        @DisplayName("GET debe devolver todos los productos")
        void shouldReturnAllProducts() throws Exception {
                productRepository.save(
                                Product.builder().name("Disco SSD")
                                                .price(180.00).stock(10).build());

                productRepository.save(
                                Product.builder().name("RAM 16GB")
                                                .price(120.00).stock(8).build());

                mockMvc.perform(get("/api/products"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].name").value("Disco SSD"))
                                .andExpect(jsonPath("$[1].name").value("RAM 16GB"));
        }

        @Test
        @DisplayName("GET debe devolver el producto por su ID")
        void shouldReturnProductById() throws Exception {
                Product saved = productRepository.save(
                                Product.builder().name("Impresora HP")
                                                .price(350.00).stock(4).build());

                mockMvc.perform(get("/api/products/" + saved.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Impresora HP"))
                                .andExpect(jsonPath("$.price").value(350.00));
        }

        @Test
        @DisplayName("GET debe devolver 500 cuando el ID no existe")
        void shouldReturn500WhenProductNotFound() throws Exception {
                mockMvc.perform(get("/api/products/9999"))
                                .andExpect(status().isInternalServerError());
        }
}
