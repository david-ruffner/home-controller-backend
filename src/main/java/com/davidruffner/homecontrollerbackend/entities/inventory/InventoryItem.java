package com.davidruffner.homecontrollerbackend.entities.inventory;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "inventory_item")
public class InventoryItem {
    @Id
    @Column(name = "item_id", nullable = false)
    private String itemId;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "item_description")
    private String itemDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private InventoryRoom inventoryRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id")
    private ItemContainer itemContainer;

    @Column(name = "upc", nullable = false)
    private String upc;

    @Column(name = "current_quantity", nullable = false)
    private long quantity;

    @Column(name = "quantity_threshold")
    private long quantityThreshold;

    @Column(name = "notify_on_threshold", nullable = false)
    private Boolean notifyOnThreshold;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private InventoryCategory inventoryCategory;

    @Column(name = "is_favorite")
    private Boolean isFavorite;

    public InventoryItem() {
        this.itemId = UUID.randomUUID().toString();
    }

    public InventoryItem(String itemId) {
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public InventoryRoom getInventoryRoom() {
        return inventoryRoom;
    }

    public void setInventoryRoom(InventoryRoom inventoryRoom) {
        this.inventoryRoom = inventoryRoom;
    }

    public ItemContainer getItemContainer() {
        return itemContainer;
    }

    public void setItemContainer(ItemContainer itemContainer) {
        this.itemContainer = itemContainer;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getQuantityThreshold() {
        return quantityThreshold;
    }

    public void setQuantityThreshold(long quantityThreshold) {
        this.quantityThreshold = quantityThreshold;
    }

    public Boolean getNotifyOnThreshold() {
        return notifyOnThreshold;
    }

    public void setNotifyOnThreshold(Boolean notifyOnThreshold) {
        this.notifyOnThreshold = notifyOnThreshold;
    }

    public InventoryCategory getInventoryCategory() {
        return inventoryCategory;
    }

    public void setInventoryCategory(InventoryCategory inventoryCategory) {
        this.inventoryCategory = inventoryCategory;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }
}
