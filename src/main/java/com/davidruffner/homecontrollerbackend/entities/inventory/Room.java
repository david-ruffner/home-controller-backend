package com.davidruffner.homecontrollerbackend.entities.inventory;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "room")
public class Room {
    @Id
    @Column(name = "room_id")
    private String roomId;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "group_id", nullable = false)
    private String groupId;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Item> items = new ArrayList<>();

    public Room() {
        this.roomId = UUID.randomUUID().toString();
    }

    public Room(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public List<Item> getItems() {
        return items;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
