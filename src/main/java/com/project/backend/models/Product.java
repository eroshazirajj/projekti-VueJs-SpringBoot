package com.project.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Emri nuk mund të lihet bosh")
    private String name;

    @NotNull(message = "Çmimi është i detyrueshëm")
    @Min(value = 1, message = "Çmimi duhet të jetë të paktën 1€")
    private Double price;

    private String description;

    private String imageUrl;
    public String getImageUrl() { return imageUrl; }
public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @ManyToOne
@JoinColumn(name = "category_id")
private Category category;

    public Product() {}

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}