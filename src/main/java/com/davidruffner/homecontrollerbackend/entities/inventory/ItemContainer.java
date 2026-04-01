package com.davidruffner.homecontrollerbackend.entities.inventory;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "item_container")
public class ItemContainer {
    @Id
    @Column(name = "container_id", nullable = false)
    private String containerId;

    @Column(name = "container_name", nullable = false)
    private String containerName;

    @OneToMany(mappedBy = "itemContainer")
    private java.util.List<Item> items;

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
