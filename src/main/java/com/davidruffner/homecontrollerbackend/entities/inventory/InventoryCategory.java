package com.davidruffner.homecontrollerbackend.entities.inventory;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "inventory_category")
public class InventoryCategory {
    @Id
    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @OneToMany(mappedBy = "inventoryCategory", fetch = FetchType.LAZY)
    private List<InventoryItem> items = new ArrayList<>();

    public InventoryCategory() {
        this.categoryId = UUID.randomUUID().toString();
    }

    public InventoryCategory(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public List<InventoryItem> getItems() {
        return items;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
