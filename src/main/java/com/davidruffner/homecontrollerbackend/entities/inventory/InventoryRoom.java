package com.davidruffner.homecontrollerbackend.entities.inventory;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "inventory_room")
public class InventoryRoom {
    @Id
    @Column(name = "room_id")
    private String roomId;

    @Column(name = "room_name")
    private String roomName;

    @OneToMany(mappedBy = "inventoryRoom", fetch = FetchType.LAZY)
    private List<InventoryItem> items = new ArrayList<>();

    public InventoryRoom() {
        this.roomId = UUID.randomUUID().toString();
    }

    public InventoryRoom(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public List<InventoryItem> getItems() {
        return items;
    }

    public void setItems(List<InventoryItem> items) {
        this.items = items;
    }

    public void addItem(InventoryItem item) {
        this.items.add(item);
    }

    public void removeItem(InventoryItem item) {
        this.items.remove(item);
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
