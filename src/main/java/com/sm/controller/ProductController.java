package com.sm.controller;
import com.sm.model.Product;
import com.sm.model.repo.ProductRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductRepository productRepository;

	@PostMapping
	public Product create(@RequestBody Product product) {
		return productRepository.save(product);
	}

	@GetMapping
	public List<Product> findAll() {
		return productRepository.findAll();
	}

	@SneakyThrows
	@GetMapping("/{id}")
	public Product findById(@PathVariable Long id) {
		return productRepository.findById(id).orElseThrow(() -> new Exception("Product is not available"));
	}
}

