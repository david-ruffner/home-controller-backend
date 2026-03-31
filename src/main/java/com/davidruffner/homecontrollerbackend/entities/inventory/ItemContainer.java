package com.davidruffner.homecontrollerbackend.entities.inventory;

import jakarta.persistence.*;

@Entity
@Table(name = "item_container")
public class ItemContainer {
    @Id
    @Column(name = "container_id", nullable = false)
    private String containerId;

    @Column(name = "container_name", nullable = false)
    private String containerName;

    @OneToOne(mappedBy = "itemContainer")
    private Item item;

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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
